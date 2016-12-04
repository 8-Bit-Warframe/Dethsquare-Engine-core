package com.ezardlabs.dethsquare.multiplayer;

import org.bitlet.weupnp.NameValueHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

class UPnPManager {
	private static final String IP = "239.255.255.250";
	private static final int PORT = 1900;
	private static final String[] SEARCH_TYPES = {"urn:schemas-upnp-org:device:InternetGatewayDevice:1",
			"urn:schemas-upnp-org:service:WANIPConnection:1",
			"urn:schemas-upnp-org:service:WANPPPConnection:1"};
	private static List<InetAddress> localAddresses;
	private static String location;
	private static String baseUrl;
	private static ArrayList<String[]> services = new ArrayList<>();

	public enum Protocol {
		TCP,
		UDP
	}

	private static List<InetAddress> getLocalInetAddresses() {
		List<InetAddress> arrayIPAddress = new ArrayList<>();

		// Get all network interfaces
		Enumeration<NetworkInterface> networkInterfaces;
		try {
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			return arrayIPAddress;
		}

		if (networkInterfaces == null) return arrayIPAddress;

		// For every suitable network interface, get all IP addresses
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface card = networkInterfaces.nextElement();

			try {
				// skip devices, not suitable to search gateways for
				if (card.isLoopback() || card.isPointToPoint() || card.isVirtual() || !card.isUp())
					continue;
			} catch (SocketException e) {
				continue;
			}

			Enumeration<InetAddress> addresses = card.getInetAddresses();

			while (addresses.hasMoreElements()) {
				InetAddress inetAddress = addresses.nextElement();
				int index = arrayIPAddress.size();

				if (!Inet4Address.class.isInstance(inetAddress)) continue;

				arrayIPAddress.add(index, inetAddress);
			}
		}

		return arrayIPAddress;
	}

	private static class DiscoveryThread extends Thread {
		private final InetAddress address;

		private DiscoveryThread(InetAddress address) {
			this.address = address;
		}

		@Override
		public void run() {
			System.out.println("Searching for UPnP devices from " + address);
			try {
				DatagramSocket socket = new DatagramSocket(0, address);
				socket.setSoTimeout(3000);
				outer:
				for (String searchType : SEARCH_TYPES) {
					String message =
							"M-SEARCH * HTTP/1.1\r\nHOST: " + IP + ":" + PORT + "\r\nST: " +
									searchType + "\r\nMAN: \"ssdp:discover\"\r\nMX: 3\r\n\r\n";
					byte[] messageBytes = message.getBytes();
					DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length,
							InetAddress.getByName(IP), PORT);
					socket.send(packet);

					int count = -1;

					boolean waitingPacket = true;
					while (waitingPacket) {
						count++;
						DatagramPacket receivePacket = new DatagramPacket(new byte[1536], 1536);
						try {
							socket.receive(receivePacket);
							byte[] receivedData = new byte[receivePacket.getLength()];
							System.arraycopy(receivePacket.getData(), 0, receivedData, 0,
									receivePacket.getLength());

							String data = new String(receivedData);

							StringTokenizer st = new StringTokenizer(data, "\n");

							while (st.hasMoreTokens()) {
								String line = st.nextToken().trim();

								if (line.isEmpty()) continue;

								if (line.startsWith("HTTP/1.") || line.startsWith("NOTIFY *"))
									continue;

								String key = line.substring(0, line.indexOf(':'));
								String value = line.length() > key.length() + 1 ? line
										.substring(key.length() + 1) : null;

								key = key.trim();
								if (value != null) {
									value = value.trim();
								}

								if (key.compareToIgnoreCase("location") == 0) {
									location = value;
								}
							}

							System.out.println("UPnP location: " + location);

							URLConnection conn = new URL(location).openConnection();

							DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
							DocumentBuilder builder = factory.newDocumentBuilder();
							Document doc = builder.parse(conn.getInputStream());

							Element root = doc.getDocumentElement();
							parseXML(root);

							if (baseUrl == null) {
								URL url = new URL(location);
								baseUrl = url.getProtocol() + "://" + url.getHost() + ":" +
										url.getPort();
								System.out.println(
										"No explicit base URL was found; setting it to " + baseUrl);
							}
						} catch (SocketTimeoutException ste) {
							System.err.println(
									"Timed out waiting for UPnP discovery response: " + count);
							waitingPacket = false;
						} catch (ParserConfigurationException | SAXException e) {
							e.printStackTrace();
						}
						if (!services.isEmpty()) {
							break outer;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	static void discover() {
		localAddresses = getLocalInetAddresses();
		ArrayList<DiscoveryThread> discoveryThreads = new ArrayList<>(localAddresses.size());
		for (InetAddress address : localAddresses) {
			DiscoveryThread discoveryThread = new DiscoveryThread(address);
			discoveryThreads.add(discoveryThread);
			discoveryThread.start();
		}
		for (DiscoveryThread discoveryThread : discoveryThreads) {
			try {
				discoveryThread.join();
			} catch (InterruptedException ignored) {
			}
		}
	}

	private static void parseXML(Node element) {
		switch (element.getNodeName()) {
			case "root":
				NodeList children = element.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					Node child = children.item(i);
					if ("URLBase".equals(child.getNodeName())) {
						baseUrl = child.getTextContent();
					} else if ("device".equals(child.getNodeName())) {
						parseXML(child);
					}
				}
				break;
			case "deviceList":
				children = element.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					if ("device".equals(children.item(i).getNodeName())) {
						parseXML(children.item(i));
					}
				}
				break;
			case "device":
				children = element.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					if ("serviceList".equals(children.item(i).getNodeName()) ||
							"deviceList".equals(children.item(i).getNodeName())) {
						parseXML(children.item(i));
					}
				}
				break;
			case "serviceList":
				children = element.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					if ("service".equals(children.item(i).getNodeName())) {
						parseXML(children.item(i));
					}
				}
				break;
			case "service":
				children = element.getChildNodes();
				outer:
				for (int i = 0; i < children.getLength(); i++) {
					Node child = children.item(i);
					if ("serviceType".equals(child.getNodeName()) &&
							Arrays.asList(SEARCH_TYPES).contains(child.getTextContent())) {
						for (int j = 0; j < children.getLength(); j++) {
							Node child2 = children.item(j);
							if ("controlURL".equals(child2.getNodeName())) {
								services.add(new String[]{child.getTextContent(),
										child2.getTextContent()});
								break outer;
							}
						}
					}
				}
				break;
			default:
				break;
		}
	}

	private static void sendSOAPMessage(String action, String service, String url,
			Map<String, String> args) {
		System.out.println(url);
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		sb.append("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
				"s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">");
		sb.append("<s:Body>");
		sb.append("<m:").append(action).append(" xmlns:m=\"").append(service).append("\">");
		for (String s : args.keySet()) {
			sb.append("<").append(s).append(">").append(args.get(s)).append("</").append(s)
			  .append(">");
		}
		sb.append("</m:").append(action).append(">");
		sb.append("</s:Body>");
		sb.append("</s:Envelope>");
		String message = sb.toString();

		try {
			URL postUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) postUrl.openConnection();

			conn.setRequestMethod("POST");
			conn.setReadTimeout(5000);
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "text/xml");
			conn.setRequestProperty("SOAPAction", "\"" + service + "#" + action + "\"");
			conn.setRequestProperty("Connection", "Close");

			System.out.println("Message: " + message);

			byte[] messageBytes = message.getBytes();

			conn.setRequestProperty("Content-Length", String.valueOf(messageBytes.length));

			conn.getOutputStream().write(messageBytes);

			Map<String, String> nameValue = new HashMap<>();
			XMLReader parser = XMLReaderFactory.createXMLReader();
			parser.setContentHandler(new NameValueHandler(nameValue));
			if (conn.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
				try {
					// attempt to parse the error message
					parser.parse(new InputSource(conn.getErrorStream()));
				} catch (SAXException e) {
					// ignore the exception
					// FIXME We probably need to find a better way to return
					// significant information when we reach this point
				}
				conn.disconnect();
				System.out.println(nameValue);
			} else {
				parser.parse(new InputSource(conn.getInputStream()));
				conn.disconnect();
				System.out.println(nameValue);
			}

			conn.disconnect();
		} catch (IOException | SAXException e) {
			e.printStackTrace();
		}
	}

	static void addPortMapping(int port, Protocol protocol, String description) {
		System.out.println("Services: " + services);
		HashMap<String, String> args = new HashMap<>();
		args.put("NewRemoteHost", "");
		args.put("NewExternalPort", String.valueOf(port));
		args.put("NewProtocol", protocol.toString());
		args.put("NewInternalPort", String.valueOf(port));
		try {
			args.put("NewInternalClient", InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		args.put("NewEnabled", "1");
		args.put("NewPortMappingDescription", description == null ? "" : description);
		args.put("NewLeaseDuration", "0");

		for (String[] s : services) {
			sendSOAPMessage("AddPortMapping", s[0], baseUrl + s[1], args);
		}
	}
}

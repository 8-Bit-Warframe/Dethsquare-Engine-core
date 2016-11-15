package com.ezardlabs.dethsquare.multiplayer;

import com.ezardlabs.dethsquare.GameObject;
import com.ezardlabs.dethsquare.Time;
import com.ezardlabs.dethsquare.Vector2;
import com.ezardlabs.dethsquare.multiplayer.Network.NetworkStateChangeListener.State;
import com.ezardlabs.dethsquare.prefabs.PrefabManager;
import com.ezardlabs.dethsquare.util.GameListeners;
import com.ezardlabs.dethsquare.util.GameListeners.UpdateListener;

import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.PortMappingEntry;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;

public class Network {
	static {
		// Add hook into game loop
//		GameListeners.addUpdateListener(Network::update);
	}

	private static UpdateListener updateListener;
	private static NetworkStateChangeListener listener;

	private static InetAddress[] addresses;
	private static int[] ports;
	private static UDPWriter udpOut;
	private static UDPReader udpIn;
	private static TCPWriter[] tcpOut;
	private static ServerSocket tcpIn;

	private static int myPort = 2828;

	// TODO change back to -1 then make it work in singleplayer
	private static int playerId = 0;
	private static boolean host;

	//TODO change back to -1 then make it work in singleplayer
	private static int networkIdCounter = 0;

	public static int getPlayerId() {
		return playerId;
	}

	static int getNewNetworkId() {
		if (networkIdCounter == -1) throw new Error("Network ID counter has not been setup yet");
		return networkIdCounter++;
	}

	public static boolean isHost() {
		return host;
	}

	private static void configureUPnP() throws ParserConfigurationException, SAXException, IOException {
		GatewayDiscover discover = new GatewayDiscover();
		System.out.println("Looking for Gateway Devices");
		discover.discover();
		GatewayDevice d = discover.getValidGateway();

		PortMappingEntry pme = new PortMappingEntry();
		if (!d.getSpecificPortMappingEntry(myPort, "UDP", pme)) {
			System.out.println("UDP mapping does not already exist");
			System.out.println("Adding UDP port mapping: " +
					d.addPortMapping(myPort, myPort, d.getLocalAddress().getHostAddress(), "UDP",
							"Lost Sector UDP"));
		} else {
			System.out.println("UDP mapping already exists");
		}

		pme = new PortMappingEntry();
		if (!d.getSpecificPortMappingEntry(myPort + 1, "TCP", pme)) {
			System.out.println("TCP mapping does not already exist");
			System.out.println("Adding TCP port mapping: " +
					d.addPortMapping(myPort + 1, myPort + 1, d.getLocalAddress().getHostAddress(),
							"TCP", "Lost Sector TCP"));
		} else {
			System.out.println("TCP mapping already exists");
		}
	}

	public static void findGame(NetworkStateChangeListener listener) {
		Network.listener = listener;
		new TCPServer().start();
		MatchmakingThread mt = new MatchmakingThread();
		updateListener = () -> checkIfGameFound(mt);
		GameListeners.addUpdateListener(updateListener);
		mt.start();
		listener.onNetworkStateChanged(State.MATCHMAKING_SEARCHING);
	}

	private static void checkIfGameFound(MatchmakingThread mt) {
		if (mt.data != null) {
			listener.onNetworkStateChanged(State.MATCHMAKING_FOUND);
			JSONObject data = new JSONObject(mt.data);
			playerId = data.getInt("id");
			host = data.getBoolean("host");

			JSONArray peers = data.getJSONArray("peers");

			addresses = new InetAddress[peers.length()];
			ports = new int[peers.length()];
			tcpOut = new TCPWriter[peers.length()];

			listener.onNetworkStateChanged(State.GAME_CONNECTING);

			for (int i = 0; i < peers.length(); i++) {
				JSONObject player = peers.getJSONObject(i);
				try {
					addresses[i] = InetAddress.getByName(player.getString("address"));
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ports[i] = player.getInt("port");

				try {
					tcpOut[i] = new TCPWriter(new Socket(addresses[i], ports[i] + 1));
					tcpOut[i].start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				DatagramSocket socket = new DatagramSocket(myPort);
				udpOut = new UDPWriter(socket);
				udpOut.start();
				udpIn = new UDPReader(socket);
				udpIn.start();
			} catch (SocketException e) {
				e.printStackTrace();
			}

			networkIdCounter = playerId * (Integer.MAX_VALUE / 4) + 1;

			listener.onNetworkStateChanged(State.GAME_CONNECTED);

			GameListeners.removeUpdateListener(updateListener);
			GameListeners.addUpdateListener(Network::update);
		}
	}

	private static void update() {
		if (Time.frameCount % 2 == 0) {
			ByteBuffer data = ByteBuffer.allocate(NetworkBehaviour.totalSize + (NetworkBehaviour
					.myNetworkBehaviours.size() * 8));
			for (NetworkBehaviour nb : NetworkBehaviour.myNetworkBehaviours.values()) {
				data.putInt(nb.getNetworkId());
				data.putInt(nb.getSize());
				data.put(nb.onSend());
			}
			udpOut.sendMessage(data.array());
		}
		synchronized (udpIn.udpMessages) {
			while (udpIn.udpMessages.size() > 0) {
				int count = 0;
				ByteBuffer data = ByteBuffer.wrap(udpIn.udpMessages.remove(0));
				NetworkBehaviour nb;
				while (count < data.capacity()) {
					data.position(count);
					int networkId = data.getInt(count);
					if (networkId == 0) break;
					int size = data.getInt(count + 4);
					nb = NetworkBehaviour.otherNetworkBehaviours.get(networkId);
					if (nb != null) {
						nb.onReceive(data, count + 8);
					}
					count += size + 8;
				}
			}
		}
	}

	private static class MatchmakingThread extends Thread {
		private String data;

		MatchmakingThread() {
			super("MatchmakingThread");
		}

		@Override
		public void run() {
			byte[] buffer = String.valueOf(myPort).getBytes();
			try (DatagramSocket s = new DatagramSocket(myPort)) {
				s.send(new DatagramPacket(buffer, buffer.length,
						InetAddress.getByName("8bitwarframe.com"), 3000));
				DatagramPacket p = new DatagramPacket(new byte[1024], 1024);
				s.receive(p);
				data = new String(p.getData());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static class UDPReader extends Thread {
		private final DatagramSocket socket;
		final ArrayList<byte[]> udpMessages = new ArrayList<>();

		UDPReader(DatagramSocket socket) {
			super("UDPReader");
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				DatagramPacket packet = new DatagramPacket(new byte[4096], 4096);
				while (true) {
					socket.receive(packet);
					synchronized (udpMessages) {
						udpMessages.add(packet.getData());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static class UDPWriter extends Thread {
		private final DatagramSocket socket;
		private final ArrayList<byte[]> messages = new ArrayList<>();
		private final DatagramPacket[] packets = new DatagramPacket[addresses.length];

		UDPWriter(DatagramSocket socket) {
			super("UDPWriter");
			this.socket = socket;
			for (int i = 0; i < packets.length; i++) {
				packets[i] = new DatagramPacket(new byte[0], 0, addresses[i], ports[i]);
			}
		}

		@Override
		public void run() {
			try {
				while (true) {
					synchronized (messages) {
						messages.wait();
						while (messages.size() > 0) {
							byte[] message = messages.remove(0);
							for (int i = 0; i < addresses.length; i++) {
								packets[i].setData(message);
								socket.send(packets[i]);
							}
						}
					}
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}

		void sendMessage(byte[] message) {
			synchronized (messages) {
				messages.add(message);
				messages.notify();
			}
		}
	}

	private static class TCPServer extends Thread {

		TCPServer() {
			super("TCPServer");
		}

		@Override
		public void run() {
			try (ServerSocket ss = new ServerSocket(myPort + 1)) {
				while (true) {
					new TCPReader(ss.accept()).start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static class TCPReader extends Thread {
		private final Socket socket;

		TCPReader(Socket socket) {
			super("TCPReader");
			this.socket = socket;
		}

		@Override
		public void run() {
			try (BufferedReader in = new BufferedReader(
					new InputStreamReader(socket.getInputStream()))) {
				socket.setKeepAlive(true);
				while (socket.isConnected()) {
					String command = in.readLine();
					if (command != null) {
						if (command.equals("instantiate")) {
							processInstantiation(in);
						} else if (command.equals("destroy")) {
							processDestruction(in);
						} else {
							System.out.println("Unknown command:" + command);
						}
					}
				}
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static class TCPWriter extends Thread {
		private final Socket socket;
		private final ArrayList<String> messages = new ArrayList<>();

		TCPWriter(Socket socket) {
			super("TCPWriter");
			this.socket = socket;
		}

		@Override
		public void run() {
			try (BufferedWriter out = new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream()))) {
				while (true) {
					synchronized (messages) {
						messages.wait();
						while (messages.size() > 0) {
							String s = messages.remove(0);
							out.write(s);
							out.flush();
						}
					}
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}

		void sendMessage(String message) {
			synchronized (messages) {
				messages.add(message);
				messages.notify();
			}
		}
	}

	public static GameObject instantiate(String prefabName, Vector2 position) {
		GameObject gameObject = PrefabManager.loadPrefab(prefabName);
		gameObject.networkId = getNewNetworkId();
		if (tcpOut != null) {
			List<NetworkBehaviour> networkBehaviours = gameObject.getComponentsOfType(NetworkBehaviour.class);
			HashMap<String, Integer> networkIds = new HashMap<>();
			for (NetworkBehaviour nb : networkBehaviours) {
				networkIds.put(nb.getClass().getCanonicalName(), nb.getNetworkId());
			}
			StringBuilder sb = new StringBuilder();
			sb.append("instantiate").append(System.lineSeparator());
			if (PrefabManager.prefabExists(prefabName + "_other")) {
				sb.append(prefabName).append("_other").append(System.lineSeparator());
			} else {
				sb.append(prefabName).append(System.lineSeparator());
			}
			sb.append(gameObject.networkId).append(System.lineSeparator());
			sb.append(position.x).append(System.lineSeparator());
			sb.append(position.y).append(System.lineSeparator());
			sb.append(playerId).append(System.lineSeparator());
			for (String key : networkIds.keySet()) {
				sb.append(key).append(System.lineSeparator()).append(networkIds.get(key)).append(System.lineSeparator());
			}
			String message = sb.toString();
			for (TCPWriter writer : tcpOut) {
				writer.sendMessage(message);
			}
		}

//		for (int i = 0; i < tcp.length; i++) {
//			try (BufferedWriter out = new BufferedWriter(
//					new OutputStreamWriter(tcp[i].getOutputStream()))) {
//				out.write("instantiate");
//				out.newLine();
//				if (PrefabManager.prefabExists(prefabName + "_other")) {
//					out.write(prefabName + "_other");
//				} else {
//					out.write(prefabName);
//				}
//				out.newLine();
//				out.write(String.valueOf(position.x));
//				out.newLine();
//				out.write(String.valueOf(position.y));
//				out.newLine();
//				out.write(String.valueOf(playerId));
//				out.newLine();
//				for (String key : networkIds.keySet()) {
//					out.write(key);
//					out.newLine();
//					out.write(networkIds.get(key));
//					out.newLine();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		return GameObject.instantiate(gameObject, position);
	}

	private static void processInstantiation(BufferedReader in) throws IOException {
		String name = in.readLine();
		GameObject gameObject = PrefabManager.loadPrefab(name);
		gameObject.networkId = Integer.parseInt(in.readLine());
		Vector2 position = new Vector2(Float.parseFloat(in.readLine()),
				Float.parseFloat(in.readLine()));
		int playerId = Integer.parseInt(in.readLine());
		List<NetworkBehaviour> networkBehaviours = gameObject
				.getComponentsOfType(NetworkBehaviour.class);
		HashMap<String, Integer> networkIds = new HashMap<>();
		for (int i = 0; i < networkBehaviours.size(); i++) {
			networkIds.put(in.readLine(), Integer.parseInt(in.readLine()));
		}
		for (NetworkBehaviour nb : networkBehaviours) {
			nb.setPlayerId(playerId);
			nb.setNetworkId(networkIds.get(nb.getClass().getCanonicalName()));
		}
		GameObject.instantiate(gameObject, position);
	}

	public static void destroy(GameObject gameObject) {
		GameObject.destroy(gameObject);
		StringBuilder sb = new StringBuilder();
		sb.append("destroy").append(System.lineSeparator());
		sb.append(gameObject.networkId).append(System.lineSeparator());
		String message = sb.toString();
		for (TCPWriter writer : tcpOut) {
			writer.sendMessage(message);
		}
	}

	public static void destroy(GameObject gameObject, long delay) {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				destroy(gameObject);
			}
		}, delay);
	}

	private static void processDestruction(BufferedReader in) throws IOException {
		GameObject.destroy(Integer.parseInt(in.readLine()));
	}

	public interface NetworkStateChangeListener {
		enum State {
			MATCHMAKING_SEARCHING,
			MATCHMAKING_FOUND,
			GAME_CONNECTING,
			GAME_CONNECTED
		}

		void onNetworkStateChanged(State state);
	}
}
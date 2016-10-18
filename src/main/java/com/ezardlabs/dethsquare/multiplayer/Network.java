package com.ezardlabs.dethsquare.multiplayer;

import com.ezardlabs.dethsquare.GameObject;
import com.ezardlabs.dethsquare.Time;
import com.ezardlabs.dethsquare.Vector2;
import com.ezardlabs.dethsquare.util.GameListeners;
import com.ezardlabs.dethsquare.util.GameListeners.UpdateListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Network {
	static {
		// Add hook into game loop
		GameListeners.addUpdateListener(Network::update);
	}

	private static UpdateListener updateListener;

	private static InetAddress[] addresses;
	private static int[] ports;
	private static DatagramSocket udpOut;
	private static DatagramSocket udpIn;
	private static Socket[] tcp;
	private static ServerSocket tcpIn;

	private static int myPort = 8282;

	private static int networkIdCounter = 0;

	private static int playerID;
	private static boolean host;

	public static int getPlayerID() {
		return playerID;
	}

	public static boolean isHost() {
		return host;
	}

	public static void findGame() {
		new TCPServer().start();
		MatchmakingThread mt = new MatchmakingThread();
		updateListener = () -> checkIfGameFound(mt);
		GameListeners.addUpdateListener(updateListener);
		mt.start();
	}

	private static void checkIfGameFound(MatchmakingThread mt) {
		if (mt.data != null) {
			JSONObject data = new JSONObject(mt.data);
			playerID = data.getInt("id");
			host = data.getBoolean("host");

			JSONArray peers = data.getJSONArray("peers");

			addresses = new InetAddress[peers.length()];
			ports = new int[peers.length()];
			tcp = new Socket[peers.length()];

			for (int i = 0; i < peers.length(); i++) {
				JSONObject player = peers.getJSONObject(i);
				try {
					addresses[i] = InetAddress.getByName(player.getString("address"));
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ports[i] = player.getInt("port");

				try {
					tcp[i] = new Socket(addresses[i], ports[i]);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			GameListeners.removeUpdateListener(updateListener);
		}
	}

	private static void update() {
		if (Time.frameCount % 2 == 0) {

		}
	}

	private static class MatchmakingThread extends Thread {
		private String data;

		@Override
		public void run() {
			System.out.println(myPort);
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
		@Override
		public void run() {
			try (DatagramSocket socket = new DatagramSocket(myPort)) {
				DatagramPacket packet = new DatagramPacket(new byte[2014], 0);
				while (true) {
					socket.receive(packet);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static class TCPServer extends Thread {

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
			this.socket = socket;
		}

		@Override
		public void run() {
			try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
				while (socket.isConnected()) {
					String command = in.readUTF();
					if (command.equals("instantiate")) {
						Vector2 position = new Vector2(in.readFloat(), in.readFloat());
						GameObject gameObject = (GameObject) in.readObject();
						GameObject.instantiate(gameObject, position);
					}
				}
				in.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public static GameObject instantiate(GameObject gameObject,
			Vector2 position) throws IOException {
		NetworkBehaviour nb = gameObject.getComponentOfType(NetworkBehaviour.class);
		if (nb != null) {
			nb.networkId = networkIdCounter++;
			nb.playerId = playerID;
		}
		for (int i = 0; i < tcp.length; i++) {
			ObjectOutputStream out = new ObjectOutputStream(tcp[i].getOutputStream());
			out.writeUTF("instantiate");
			out.writeFloat(position.x);
			out.writeFloat(position.y);
			out.writeObject(gameObject);
			out.close();
		}

		return gameObject;
	}

	private static void processInstantiationRequest(byte[] data) {
	}
}
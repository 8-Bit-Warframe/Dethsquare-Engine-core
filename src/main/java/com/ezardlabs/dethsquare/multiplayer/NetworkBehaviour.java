package com.ezardlabs.dethsquare.multiplayer;

import com.ezardlabs.dethsquare.Component;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Random;

public abstract class NetworkBehaviour extends Component {
	static HashMap<Integer, NetworkBehaviour> myNetworkBehaviours = new HashMap<>();
	static HashMap<Integer, NetworkBehaviour> otherNetworkBehaviours = new HashMap<>();
	static int totalSize = 0;
	int networkId;
	protected int playerId;
	protected final ByteBuffer data = ByteBuffer.allocate(getSize());

	public NetworkBehaviour() {
		networkId = Network.getNewNetworkId();
		playerId = Network.getPlayerId();
		if (playerId == -1) throw new Error("Player ID has not been set yet");
	}

	@Override
	public void start() {
		if (playerId == Network.getPlayerId()) {
			myNetworkBehaviours.put(networkId, this);
			totalSize += getSize();
		} else {
			otherNetworkBehaviours.put(networkId, this);
		}
	}

	@Override
	protected final void destroy() {
		if (playerId == Network.getPlayerId()) {
			myNetworkBehaviours.remove(networkId);
			totalSize -= getSize();
		} else {
			otherNetworkBehaviours.remove(networkId);
		}
	}

	private String generateRandomId() {
		char[] chars = {'0',
				'1',
				'2',
				'3',
				'4',
				'5',
				'6',
				'7',
				'8',
				'9',
				'a',
				'b',
				'c',
				'd',
				'e',
				'f',
				'g',
				'h',
				'i',
				'j',
				'k',
				'l',
				'm',
				'n',
				'o',
				'p',
				'q',
				'r',
				's',
				't',
				'u',
				'v',
				'w',
				'x',
				'y',
				'z',
				'A',
				'B',
				'C',
				'D',
				'E',
				'F',
				'G',
				'H',
				'I',
				'J',
				'K',
				'L',
				'M',
				'N',
				'O',
				'P',
				'Q',
				'R',
				'S',
				'T',
				'U',
				'V',
				'W',
				'X',
				'Y',
				'Z'};
		Random r = new Random();
		StringBuilder sb = new StringBuilder(10);
		for (int i = 0; i < sb.capacity(); i++) {
			sb.append(chars[r.nextInt(chars.length)]);
		}
		return sb.toString();
	}

	protected abstract ByteBuffer onSend();

	protected abstract void onReceive(ByteBuffer data, int index);

	public abstract int getSize();
}

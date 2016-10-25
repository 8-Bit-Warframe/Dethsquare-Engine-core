package com.ezardlabs.dethsquare.multiplayer;

import com.ezardlabs.dethsquare.Component;

import java.nio.ByteBuffer;
import java.util.Random;

public abstract class NetworkBehaviour extends Component {
	String networkId;
	protected int playerId;
	protected final ByteBuffer data = ByteBuffer.allocate(getSize());

	public NetworkBehaviour() {
		networkId = generateRandomId();
		playerId = Network.getPlayerID();
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

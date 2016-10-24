package com.ezardlabs.dethsquare.multiplayer;

import com.ezardlabs.dethsquare.Component;

import java.nio.ByteBuffer;

public abstract class NetworkBehaviour extends Component {
	private static int networkIdCounter = 0;
	int networkId;
	protected int playerId;
	protected final ByteBuffer data = ByteBuffer.allocate(getSize());

	public NetworkBehaviour() {
		this(networkIdCounter++, Network.getPlayerID());
	}

	NetworkBehaviour(int networkId, int playerId) {
		this.networkId = networkId;
		this.playerId = playerId;
	}

	protected abstract ByteBuffer onSend();

	protected abstract void onReceive(ByteBuffer data, int index);

	public abstract int getSize();
}

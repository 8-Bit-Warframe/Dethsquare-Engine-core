package com.ezardlabs.dethsquare.multiplayer;

import com.ezardlabs.dethsquare.Component;

import java.nio.ByteBuffer;
import java.util.HashMap;

public abstract class NetworkBehaviour extends Component {
	static HashMap<Integer, NetworkBehaviour> myNetworkBehaviours = new HashMap<>();
	static HashMap<Integer, NetworkBehaviour> otherNetworkBehaviours = new HashMap<>();
	static int totalSize = 0;
	private int networkId;
	private int playerId = -1;
	protected final ByteBuffer data = ByteBuffer.allocate(getSize());

	public NetworkBehaviour() {
		networkId = Network.getNewNetworkId();
		playerId = Network.getPlayerId();
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

	void setNetworkId(int networkId) {
		this.networkId = networkId;
	}

	public int getNetworkId() {
		return networkId;
	}

	void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getPlayerId() {
		return playerId;
	}

	protected abstract ByteBuffer onSend();

	protected abstract void onReceive(ByteBuffer data, int index);

	public abstract int getSize();
}

package com.ezardlabs.dethsquare.multiplayer;

import java.nio.ByteBuffer;

public class NetworkTransform extends NetworkBehaviour {

	@Override
	protected ByteBuffer onSend() {
		data.position(0);
		data.putFloat(0, transform.position.x); // 0 - 3
		data.putFloat(4, transform.position.y); // 4 - 7
		return data;
	}

	@Override
	protected void onReceive(ByteBuffer data, int index) {
		transform.position.x = data.getFloat(index);
		transform.position.y = data.getFloat(index + 4);
	}

	@Override
	public int getSize() {
		return 8;
	}
}

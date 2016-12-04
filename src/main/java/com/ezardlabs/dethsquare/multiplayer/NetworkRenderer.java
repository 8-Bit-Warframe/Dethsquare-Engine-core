package com.ezardlabs.dethsquare.multiplayer;

import java.nio.ByteBuffer;

public class NetworkRenderer extends NetworkBehaviour {

	@Override
	protected ByteBuffer onSend() {
		data.position(0);
		data.putFloat(0, gameObject.renderer.width); // 0 - 3
		data.putFloat(4, gameObject.renderer.height); // 4 - 7
		data.putFloat(8, gameObject.renderer.xOffset); // 8 - 11
		data.putFloat(12, gameObject.renderer.yOffset); // 12 - 15
		data.putInt(16, gameObject.renderer.hFlipped ? 1 : 0); // 16 - 19
		data.putInt(20, gameObject.renderer.vFlipped ? 1 : 0); // 20 - 23
		return data;
	}

	@Override
	protected void onReceive(ByteBuffer data, int index) {
		gameObject.renderer.setSize(data.getFloat(index), data.getFloat(index + 4));
		gameObject.renderer.setOffsets(data.getFloat(index + 8), data.getFloat(index + 12));
		gameObject.renderer.setFlipped(data.getInt(index + 16) == 1, data.getInt(index + 20) == 1);
	}

	@Override
	public short getSize() {
		return 24;
	}
}

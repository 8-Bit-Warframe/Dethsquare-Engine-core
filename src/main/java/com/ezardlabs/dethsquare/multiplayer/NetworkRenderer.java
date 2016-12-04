package com.ezardlabs.dethsquare.multiplayer;

import java.nio.ByteBuffer;

public class NetworkRenderer extends NetworkBehaviour {

	@Override
	protected ByteBuffer onSend() {
		data.position(0);
		data.putFloat(0, gameObject.renderer.width); // 8 - 11
		data.putFloat(4, gameObject.renderer.height); // 12 - 15
		data.putFloat(8, gameObject.renderer.xOffset); // 16 - 19
		data.putFloat(12, gameObject.renderer.yOffset); // 20 - 23
		data.putInt(16, gameObject.renderer.hFlipped ? 1 : 0); // 24 - 27
		data.putInt(20, gameObject.renderer.vFlipped ? 1 : 0); // 28 - 31
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

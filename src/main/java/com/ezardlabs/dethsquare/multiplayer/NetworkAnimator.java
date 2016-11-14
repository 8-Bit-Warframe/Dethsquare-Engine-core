package com.ezardlabs.dethsquare.multiplayer;

import java.nio.ByteBuffer;

public class NetworkAnimator extends NetworkBehaviour {

	@Override
	public void start() {
		super.start();
		assert gameObject.animator != null;
		gameObject.animator.shouldUpdate = getPlayerId() == Network.getPlayerId();
	}

	@Override
	protected ByteBuffer onSend() {
		data.position(0);
		data.putInt(0, gameObject.animator.getCurrentAnimationId()); // 0 - 3
		data.putInt(4, gameObject.animator.getCurrentAnimationFrame()); // 4 - 7
		data.putFloat(8, gameObject.renderer.width); // 8 - 11
		data.putFloat(12, gameObject.renderer.height); // 12 - 15
		data.putInt(16, gameObject.renderer.xOffset); // 16 - 19
		data.putInt(20, gameObject.renderer.yOffset); // 20 - 23
		data.putInt(24, gameObject.renderer.hFlipped ? 1 : 0); // 24 - 27
		data.putInt(28, gameObject.renderer.vFlipped ? 1 : 0); // 28 - 31
		return data;
	}

	@Override
	protected void onReceive(ByteBuffer data, int index) {
		gameObject.animator.setCurrentAnimationId(data.getInt(index));
		gameObject.animator.setCurrentAnimationFrame(data.getInt(index + 4));
		gameObject.renderer.setSize(data.getFloat(index + 8), data.getFloat(index + 12));
		gameObject.renderer.setOffsets(data.getInt(index + 16), data.getInt(index + 20));
		gameObject.renderer.setFlipped(data.getInt(index + 24) == 1, data.getInt(index + 28) == 1);
	}

	@Override
	public int getSize() {
		return 32;
	}
}

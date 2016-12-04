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
		data.putShort(0, (short) gameObject.animator.getCurrentAnimationId()); // 0 - 1
		data.putShort(2, (short) gameObject.animator.getCurrentAnimationFrame()); // 2 - 3
		return data;
	}

	@Override
	protected void onReceive(ByteBuffer data, int index) {
		gameObject.animator.setCurrentAnimationId(data.getShort(index));
		gameObject.animator.setCurrentAnimationFrame(data.getShort(index + 2));
	}

	@Override
	public short getSize() {
		return 4;
	}
}

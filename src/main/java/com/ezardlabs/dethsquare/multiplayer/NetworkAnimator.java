package com.ezardlabs.dethsquare.multiplayer;

import java.nio.ByteBuffer;

public class NetworkAnimator extends NetworkBehaviour {

	@Override
	protected ByteBuffer onSend() {
		data.putInt(0, gameObject.animator.getCurrentAnimationId()); // 0 - 3
		data.putInt(4, gameObject.animator.getCurrentAnimationFrame()); // 4 - 7
		return data;
	}

	@Override
	protected void onReceive(ByteBuffer data, int index) {
		gameObject.animator.setCurrentAnimationId(data.getInt(index));
		gameObject.animator.setCurrentAnimationFrame(data.getInt(index + 4));
	}

	@Override
	public int getSize() {
		return 8;
	}
}
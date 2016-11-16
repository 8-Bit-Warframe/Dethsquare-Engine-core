package com.ezardlabs.dethsquare;

import com.ezardlabs.dethsquare.util.AudioUtils;

public final class AudioSource extends Component {
	private final AudioClip initial;
	private int current = -1;
	private boolean loop;
	private int volume;

	public AudioSource() {
		this(null, false, 50);
	}

	public AudioSource(AudioClip audioClip) {
		this(audioClip, false, 50);
	}

	public AudioSource(AudioClip audioClip, boolean loop) {
		this(audioClip, loop, 50);
	}

	public AudioSource(AudioClip audioClip, int volume) {
		this(audioClip, false, volume);
	}

	public AudioSource(AudioClip audioClip, boolean loop, int volume) {
		initial = audioClip;
		this.loop = loop;
		this.volume = volume;
	}

	@Override
	public void start() {
		if (initial != null) {
			play(initial);
			setLoop(loop);
			setVolume(volume);
		}
	}

	public void play(AudioClip audioClip) {
		if (current != -1) AudioUtils.stopAudio(audioClip.id);
		AudioUtils.playAudio(current = audioClip.id, audioClip.path);
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
		AudioUtils.setAudioLoop(current, loop);
	}

	public void setVolume(int volume) {
		this.volume = volume;
		AudioUtils.setAudioVolume(current, volume);
	}

	public static final class AudioClip {
		private static int idCount = 0;
		private int id;
		private final String path;

		public AudioClip(String path) {
			this.path = path;
			id = idCount++;
		}

		@Override
		public boolean equals(Object obj) {
			return (obj instanceof AudioClip) && ((AudioClip) obj).id == id;
		}
	}
}

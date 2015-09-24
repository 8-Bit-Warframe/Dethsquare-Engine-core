package com.ezardlabs.dethsquare;

import com.ezardlabs.dethsquare.util.Utils;

public final class AudioSource extends Component {
	private AudioClip current;

	public void play(AudioClip audioClip) {
		if (current != null) Utils.stopAudio(current);
		Utils.playAudio(current = audioClip);
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

		public String getPath() {
			return path;
		}
	}
}

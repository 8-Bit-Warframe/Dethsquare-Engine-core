package com.ezardlabs.dethsquare;

import com.ezardlabs.dethsquare.TextureAtlas.Sprite;

public class GuiText extends Component {
	private String text;
	private TextureAtlas font;
	private float fontSize;
	private float spaceWidth;
	private GameObject[] characters = new GameObject[0];

	public GuiText(String text, TextureAtlas font, float fontSize) {
		this.text = text;
		this.font = font;
		this.fontSize = fontSize;
	}

	@Override
	public void start() {
		calculateSpaceWidth();
		generateRenderers();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		generateRenderers();
	}

	public TextureAtlas getFont() {
		return font;
	}

	public void setFont(TextureAtlas font) {
		this.font = font;
		calculateSpaceWidth();
		generateRenderers();
	}

	public float getFontSize() {
		return fontSize;
	}

	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
		calculateSpaceWidth();
		generateRenderers();
	}

	private void generateRenderers() {
		if (text == null) return;

		for (GameObject go : characters) {
			GameObject.destroy(go);
		}

		characters = new GameObject[text.length()];

		Sprite s;
		float xOffset = 0;
		for (int i = 0; i < text.length(); i++) {
			switch(text.charAt(i)) {
				case ' ':
					xOffset += spaceWidth;
					continue;
				case '!':
					s = font.getSprite("exclamation-mark");
					break;
				case '?':
					s = font.getSprite("question-mark");
					break;
				case '.':
					s = font.getSprite("period");
					break;
				default:
					s = font.getSprite(String.valueOf(text.charAt(i)).toLowerCase());
					break;
			}
			if (s == null) continue;

			float width = (s.w / s.h) * fontSize;

			characters[i] = new GameObject(null, new GuiRenderer(font, s, width, fontSize));
			GameObject.instantiate(characters[i], new Vector2(transform.position.x + xOffset, transform.position.y));

			xOffset += width + Screen.scale * 6.25f;
		}
	}

	private void calculateSpaceWidth() {
		Sprite[] chars = font.getSprites();
		float total = 0;
		for (Sprite s : chars) {
			total += (s.w / s.h) * fontSize;
		}
		spaceWidth = total / chars.length;
	}
}

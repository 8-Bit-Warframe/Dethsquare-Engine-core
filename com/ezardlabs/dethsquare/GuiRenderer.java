package com.ezardlabs.dethsquare;

public class GuiRenderer extends Renderer {

	public GuiRenderer(String imagePath, float width, float height) {
		super(imagePath, width, height);
	}

	public GuiRenderer(TextureAtlas textureAtlas, TextureAtlas.Sprite sprite, float width, float height) {
		super(textureAtlas, sprite, width, height);
	}

	@Override
	protected float getXPos() {
		return super.getXPos() + Camera.main.transform.position.x;
	}

	@Override
	protected float getYPos() {
		return super.getYPos() + Camera.main.transform.position.y;
	}

	@Override
	protected int getZIndex() {
		return super.getZIndex() + 1000000;
	}

	public boolean hitTest(float x, float y) {
		return x > super.getXPos() * Screen.scale && x < (super.getXPos() + width) * Screen.scale && y > super.getYPos() * Screen.scale && y < (super.getYPos() + height) * Screen.scale;
	}

	public boolean hitTest(Vector2 position) {
		return hitTest(position.x, position.y);
	}
}

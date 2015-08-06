package com.ezardlabs.dethsquare;

public class GuiRenderer extends Renderer {

	@Override
	public void start() {
		isGUI = true;
		guiRenderers.add(this);
	}
}

package gui;

import schmince.SchminceRenderer;

public class HowToPlayButton extends Button {
	public HowToPlayButton() {
		super("How To Play");
		NormalColor.set(0f, 1f, 0f, 0.25f);
	}

	@Override
	public void doAction(SchminceRenderer render) {
		render.howToPlay();
	}
}

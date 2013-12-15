package gui;

import schmince.SchminceRenderer;

public class StartScreenButton extends Button {
	public StartScreenButton(CharSequence text) {
		super(text);
	}

	@Override
	public void doAction(SchminceRenderer render) {
		render.startScreen();
	}
}

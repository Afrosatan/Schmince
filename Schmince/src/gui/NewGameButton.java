package gui;

import schmince.SchminceRenderer;

public class NewGameButton extends Button {
	public NewGameButton() {
		super("New Game");
		NormalColor.set(0f, 0f, 1f, 0.25f);
	}

	@Override
	public void doAction(SchminceRenderer render) {
		render.newGame();
	}
}

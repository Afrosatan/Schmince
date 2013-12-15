package gui;

import schmince.SchminceRenderer;

public class UseItemButton extends Button {
	public UseItemButton() {
		super("");
	}

	@Override
	public void doAction(SchminceRenderer render) {
		render.useCurrentItem();
	}
}

package gui;

import schmince.SchminceRenderer;
import texample.GLTextType;
import util.NumCharSequence;

public class SelectPlayerButton extends Button {
	private int i;

	private NumCharSequence text = new NumCharSequence("");

	public SelectPlayerButton(int i) {
		super("");
		super.Text = text;
		super.TextType = GLTextType.SansBold;
		super.TextColor.set(0f, 0f, 0f, 1f);
		this.i = i;
	}

	public void setName(CharSequence name) {
		text.setStr(0, name);
	}

	@Override
	public void doAction(SchminceRenderer render) {
		render.setSelectedPlayer(i);
	}

	public int getI() {
		return i;
	}
}

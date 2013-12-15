package gui;

import opengl.GLLibrary;
import schmince.SchminceRenderer;
import texample.GLText;
import texample.GLTextType;
import util.SColor;

/**
 * GUI label
 */
public class Label extends GUIItem {
	public CharSequence Text;

	public final SColor Color = new SColor();

	public GLTextType TextType = GLTextType.Sans;
	public float TextScale = 1f;

	public Label(CharSequence text) {
		this.Text = text;
	}

	public GLText getGLText(GLLibrary cache) {
		return cache.getText(TextType, TextScale);
	}

	@Override
	public void draw(SchminceRenderer render) {
		if (Text != null && Text.length() > 0) {
			GLText text = render.getGlib().getText(TextType, TextScale);
			float height = text.getHeight();
			float width = text.getLength(Text);
			text.begin(Color.Red, Color.Green, Color.Blue, Color.Alpha, render.getVPOrthoMatrix());
			text.draw(Text, Bounds.centerX() - width / 2f, Bounds.centerY() - height / 2f, 0,
					Bounds);
			text.end();
		}
	}
}

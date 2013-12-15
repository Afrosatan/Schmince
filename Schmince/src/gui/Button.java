package gui;

import opengl.GLLibrary;
import schmince.SchminceRenderer;
import texample.GLText;
import texample.GLTextType;
import util.SColor;

/**
 * GUI button.
 */
public abstract class Button extends GUIItem {
	public CharSequence Text;

	public final SColor FocusColor = new SColor(0f, 0.5f, 0f);
	public final SColor NormalColor = new SColor(0f, 0f, 0.5f);

	public GLTextType TextType = GLTextType.Sans;
	public float TextScale = 1f;
	public final SColor TextColor = new SColor();

	public Button(CharSequence text) {
		this.Text = text;
	}

	public abstract void doAction(SchminceRenderer render);

	public GLText getGLText(GLLibrary glib) {
		return glib.getText(TextType, TextScale);
	}

	public void draw(SchminceRenderer render) {
		render.getGlib().getRectangle().setBounds(Bounds.x, Bounds.y, Bounds.w, Bounds.h);
		SColor color = HasFocus ? FocusColor : NormalColor;
		render.getGlib().getRectangle()
				.draw(render.getVPOrthoMatrix(), color.Red, color.Green, color.Blue, color.Alpha);

		if (Text != null && Text.length() > 0) {
			GLText text = render.getGlib().getText(TextType, TextScale);
			float length = text.getLength(Text);
			float height = text.getHeight();
			text.begin(TextColor.Red, TextColor.Green, TextColor.Blue, TextColor.Alpha,
					render.getVPOrthoMatrix());
			text.draw(Text, Bounds.centerX() - length / 2f, Bounds.centerY() - height / 2f, 0,
					Bounds);
			text.end();
		}
	}
}

package opengl;

import java.util.HashMap;
import java.util.Map;

import texample.GLText;
import texample.GLTextType;
import android.content.Context;

/**
 * Handle instantiation of GL drawing utilities
 */
public class GLLibrary {
	private Map<GLTextType, GLText> fontText = new HashMap<GLTextType, GLText>();

	private Map<GLIconType, GLIcon> icons = new HashMap<GLIconType, GLIcon>();

	private GLLine line = null;
	private GLRectangle rectangle = null;
	private GLCircle circle = null;
	private GLTriangleUniform triangleUniform = null;

	private Context context;

	public GLLibrary(Context context) {
		this.context = context;
	}

	public GLLine getLine() {
		if (line == null) {
			line = new GLLine();
		}
		return line;
	}

	public GLRectangle getRectangle() {
		if (rectangle == null) {
			rectangle = new GLRectangle();
		}
		return rectangle;
	}

	public GLCircle getCirle() {
		if (circle == null) {
			circle = new GLCircle();
		}
		return circle;
	}

	public GLTriangleUniform getTriangleUniform() {
		if (triangleUniform == null) {
			triangleUniform = new GLTriangleUniform();
		}
		return triangleUniform;
	}

	public GLIcon getIcon(GLIconType type) {
		if (!icons.containsKey(type)) {
			try {
				icons.put(type, type.getClazz().newInstance());
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return icons.get(type);
	}

	public GLText getText(GLTextType type, float scale) {
		if (fontText.containsKey(type)) {
			GLText text = fontText.get(type);
			text.setScale(scale, scale);
			return text;
		}
		GLText glText = new GLText(context.getAssets());
		glText.load(type.FileName, 50, 2, 1);
		glText.setScale(scale, scale);
		fontText.put(type, glText);
		return glText;
	}
}

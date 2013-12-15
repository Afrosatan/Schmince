package schmince;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Extension of GLSurfaceView to handle input.
 */
public class SchminceGLView extends GLSurfaceView {
	private SchminceRenderer render;

	public SchminceGLView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setEGLContextClientVersion(2);
		//need a depth buffer
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
	}

	public void setRenderer(SchminceRenderer renderer) {
		this.render = renderer;
		super.setRenderer(render);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getActionMasked();
		int index = ev.getActionIndex();

		switch (action) {
		case MotionEvent.ACTION_DOWN: //first pointer goes down
		case MotionEvent.ACTION_POINTER_DOWN: //any additional pointers go down
		{
			queueEvent(new TouchDown(ev.getPointerId(index), ev.getX(index), ev.getY(index)));
			break;
		}
		case MotionEvent.ACTION_CANCEL: //i don't know when cancel gets triggered...
		{
			queueEvent(new TouchCancel(ev.getPointerId(index)));
			break;
		}
		case MotionEvent.ACTION_UP: //all pointers go up
		case MotionEvent.ACTION_POINTER_UP: //secondary pointers go up
		{
			queueEvent(new TouchUp(ev.getPointerId(index), ev.getX(index), ev.getY(index)));
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			for (int i = 0; i < ev.getPointerCount(); i++) {
				queueEvent(new TouchMove(ev.getPointerId(i), ev.getX(i), ev.getY(i)));
			}
			break;
		}
		default:
			Log.d(getClass().getName(), "Unrecognized MotionEvent action: " + action);
			break;
		}
		return true;
	}

	class TouchDown implements Runnable {
		int pid;
		float px;
		float py;

		TouchDown(int pid, float px, float py) {
			this.pid = pid;
			this.px = px;
			this.py = py;
		}

		@Override
		public void run() {
			py = render.getMatrix().deviceToOrthoY(py);
			float[] world = render.getMatrix().orthoToWorld(px, py);
			render.getGUI().touchDown(pid, px, py, world[0], world[1]);
		}
	}

	class TouchUp implements Runnable {
		int pid;
		float px;
		float py;

		TouchUp(int pid, float px, float py) {
			this.pid = pid;
			this.px = px;
			this.py = py;
		}

		@Override
		public void run() {
			py = render.getMatrix().deviceToOrthoY(py);
			float[] world = render.getMatrix().orthoToWorld(px, py);
			render.getGUI().touchUp(pid, px, py, world[0], world[1]);
		}
	}

	class TouchMove implements Runnable {
		int pid;
		float px;
		float py;

		TouchMove(int pid, float px, float py) {
			this.pid = pid;
			this.px = px;
			this.py = py;
		}

		@Override
		public void run() {
			py = render.getMatrix().deviceToOrthoY(py);
			float[] world = render.getMatrix().orthoToWorld(px, py);
			render.getGUI().touchMove(pid, px, py, world[0], world[1]);
		}
	}

	class TouchCancel implements Runnable {
		int pid;

		TouchCancel(int pid) {
			this.pid = pid;
		}

		@Override
		public void run() {
			render.getGUI().touchCancel(pid);
		}
	}
}
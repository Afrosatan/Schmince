package schmince;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Main activity for game
 */
public class SchminceActivity extends Activity {
	private SchminceGLView viewGL;
	private SchminceRenderer render;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); //Hide the status bar

		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

		render = new SchminceRenderer(this);
		viewGL = new SchminceGLView(this, null);
		viewGL.setRenderer(render);
		addContentView(viewGL, lp);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupSystemUI(View view) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE); //makes buttons on the navigation bar less visible (can use View.SYSTEM_UI_FLAG_HIDE_NAVIGATION to make it hide temporarily (until the user interacts))
		}
	}
}

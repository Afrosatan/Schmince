package util;

/**
 * ThreadLocal timer for things that need to update in steps.
 */
public class MyTimer {
	/**
	 * Nanos per millisecond.
	 */
	public static final long NPM = 1000000;
	private static ThreadLocal<MyTimer> timer = new ThreadLocal<MyTimer>() {
		protected MyTimer initialValue() {
			return new MyTimer();
		}
	};

	public static MyTimer get() {
		return timer.get();
	}

	private long current;
	private long last;

	private MyTimer() {
		update();
	}

	public void update() {
		last = current;
		current = currentMilliValue();
	}

	public long millis() {
		return current;
	}

	public long change() {
		return current - last;
	}

	/**
	 * Returns System.nanoTime() converted to milliseconds.
	 */
	public static long currentMilliValue() {
		return System.nanoTime() / MyTimer.NPM;
	}
}

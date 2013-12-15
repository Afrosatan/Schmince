package util;

import java.util.Random;

/**
 * ThreadLocal for java.util.Random instances.
 */
public class SRandom {
	private static ThreadLocal<Random> random = new ThreadLocal<Random>() {
		protected Random initialValue() {
			return new Random();
		}
	};

	public static Random get() {
		return random.get();
	}
}

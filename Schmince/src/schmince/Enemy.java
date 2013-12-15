package schmince;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import opengl.GLIconType;
import util.MyTimer;
import util.SRandom;
import android.graphics.Point;
import android.opengl.Matrix;

public class Enemy extends SObject {
	private long lastMoveMilli = 0;
	private static final int MOVE_MILLI = 2000;

	public boolean dead = false;

	public Player chasePlayer = null;

	@Override
	void update(SchminceRenderer render) {
		if (this.dead || lastMoveMilli > MyTimer.get().millis() - MOVE_MILLI) {
			return;
		}

		chasePlayer = null;
		float mind = 100000000;
		for (int i = 0; i < SchminceRenderer.PLAYER_COUNT; i++) {
			Player player = render.getPlayer(i);
			if (!player.dead && render.hasLOS(x, y, player.x, player.y)) {
				int dx = player.x - x;
				int dy = player.y - y;
				float d = (float) Math.sqrt(dx * dx + dy * dy);
				if (d < mind) {
					mind = d;
					chasePlayer = player;
				}
			}
		}

		if (chasePlayer != null) {
			if (Math.abs(x - chasePlayer.x) <= 1 && Math.abs(y - chasePlayer.y) <= 1) {
				chasePlayer.damage();
				this.dead = true;
				render.getBlock(x, y).setObject(null);
				return;
			}
			Queue<Point> path = render.findPath(x, y, chasePlayer.x, chasePlayer.y, false,
					chasePlayer);
			path.poll();
			Point next = path.poll();
			if (next != null) {
				lastMoveMilli = MyTimer.get().millis();
				Block block = render.getBlock(next.x, next.y);
				Block oldblock = render.getBlock(x, y);
				oldblock.setObject(null);
				x = next.x;
				y = next.y;
				block.setObject(this);
				return;
			} else {
				chasePlayer = null;
			}
		}

		lastMoveMilli = MyTimer.get().millis();
		openPath.clear();
		int dx = x - 1;
		int dy = y - 1;
		int cx = 1; //move right
		int cy = 0;
		for (int r = 0; r < 4; r++) {
			for (int i = 0; i < 2; i++) {
				if (render.inBounds(dx, dy) && !render.getBlock(dx, dy).isOccupied()
						&& !render.getBlock(dx, dy).isColored()) {
					openPath.add(new Point(dx, dy));
				}
				dx += cx;
				dy += cy;
			}
			if (cx == 1) {
				cy = 1; //move up
				cx = 0;
			} else if (cy == 1) {
				cx = -1; //move left
				cy = 0;
			} else if (cx == -1) {
				cy = -1; //move down
				cx = 0;
			}
		}
		if (openPath.size() > 0) {
			Point next = openPath.get(SRandom.get().nextInt(openPath.size()));
			Block block = render.getBlock(next.x, next.y);
			Block oldblock = render.getBlock(x, y);
			oldblock.setObject(null);
			x = next.x;
			y = next.y;
			block.setObject(this);
			return;
		}
	}

	private List<Point> openPath = new ArrayList<Point>();

	@Override
	void draw(SchminceRenderer render) {
		float[] vpMatrix = render.getVPMatrix();
		Matrix.translateM(vpMatrix, 0, x, y, 0);
		Matrix.scaleM(vpMatrix, 0, 0.5f, 0.5f, 1f);
		render.getGlib().getIcon(GLIconType.Enemy).draw(vpMatrix);
	}
}

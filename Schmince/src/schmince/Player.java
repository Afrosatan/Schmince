package schmince;

import java.util.Queue;

import opengl.GLIconType;
import opengl.GLLine;
import opengl.GLRectangle;
import util.MyTimer;
import util.SColor;
import util.SRandom;
import android.graphics.Point;
import android.opengl.Matrix;

/**
 * Player controlled character.
 */
public class Player extends SObject {
	private static final String[] names = { "Chauncey", "Ahmed", "Blair", "Leo", "Andy", "Sammy",
			"Monte", "Sam", "Isaias", "Rayford", "Nathanael", "Ariel", "Ned", "Earnest", "Diego",
			"Clint", "Mickey", "Edmund", "Augustus", "Len", "Emil", "Sanford", "Demarcus", "Gavin",
			"Fredrick", "Chung", "Mckinley", "Philip", "Reid", "Murray", "Nicky", "Terence",
			"Jarod", "Randolph", "Elmer", "Fabian", "Eduardo", "Jarrod", "Solomon", "Jamey",
			"Reyes", "Shayne", "Stewart", "Ignacio", "Zachery", "Trey", "Chad", "Graig", "Jessie",
			"Isiah", "Ellan", "Aura", "Vanda", "Kylee", "Raelene", "September", "Georgina",
			"Lenita", "Irma", "Elene", "Elenor", "Marth", "Hilma", "Juliet", "Rebbeca", "Toccara",
			"Emeline", "Rosanna", "Anisa", "Matilde", "Monet", "Marnie", "Rolanda", "Rhea", "Kai",
			"Rosalia", "Alisa", "Sofia", "Ria", "Wei", "Melynda", "Tillie", "Cassidy", "Kassie",
			"Desire", "Maurita", "Darcy", "Adelaida", "Dori", "Kirstie", "Francesca", "My",
			"Rosio", "Louetta", "Fiona", "Zella", "Bibi", "Christinia", "Deanna", "Oralee" };

	public CharSequence name = names[SRandom.get().nextInt(names.length)];
	public SColor color;
	public boolean dead = false;

	private int targetX = -1;
	private int targetY = -1;
	private Queue<Point> path = null;

	private long lastActionMilli = 0;
	private static final int ACTION_MILLI = 1000;

	private Item item;

	@Override
	void update(SchminceRenderer render) {
		if (dead || lastActionMilli > MyTimer.get().millis() - ACTION_MILLI) {
			return;
		}

		if (targetX == -1 || targetY == -1) {
			return;
		}

		Block block = render.getBlock(targetX, targetY);
		if (Math.abs(targetX - x) <= 1 && Math.abs(targetY - y) <= 1 && block.isOccupied()
				&& block.getObject() instanceof Item) {
			Item item = (Item) block.getObject();
			if (this.item != null) {
				this.item.x = targetX;
				this.item.y = targetY;
			}
			block.setObject(this.item);
			this.item = item;
			lastActionMilli = MyTimer.get().millis();
			setTarget(-1, -1, null);
			return;
		}

		if (path == null) {
			setTarget(targetX, targetY, render);
		}

		Point next = path.peek();

		if (next == null || (next.x == x && next.y == y)) {
			setTarget(-1, -1, null);
			return;
		}

		block = render.getBlock(next.x, next.y);
		if (block.isColored()) {
			lastActionMilli = MyTimer.get().millis();
			int mineValue = 1;
			if (item != null && item.getType() == ItemType.Pick) {
				mineValue = 2;
			}
			render.getBlock(next.x, next.y).mine(mineValue);
		} else if (block.isOccupied()) {
			setTarget(targetX, targetY, render);
		} else {
			lastActionMilli = MyTimer.get().millis();
			if (item != null && item.getType() == ItemType.Boots) {
				lastActionMilli -= 250;
			}
			Block oldblock = render.getBlock(x, y);
			oldblock.setObject(null);
			x = next.x;
			y = next.y;
			block.setObject(this);
			path.poll();
		}
	}

	public void setTarget(int targetX, int targetY, SchminceRenderer render) {
		this.targetX = targetX;
		this.targetY = targetY;
		this.path = null;
		if (targetX != -1 && targetY != -1 && render != null) {
			path = render.findPath(x, y, targetX, targetY, false, null);
			path.poll();
		}
	}

	@Override
	void draw(SchminceRenderer render) {
		GLRectangle rect = render.getGlib().getRectangle();
		rect.setBounds(x - 0.4f, y - 0.4f, 0.8f, 0.8f);
		rect.draw(render.getVPMatrix(), color.Red, color.Green, color.Blue, color.Alpha);

		if (path != null) {
			GLLine line = render.getGlib().getLine();
			int lx = x;
			int ly = y;
			for (Point point : path) {
				line.draw(render.getVPMatrix(), lx, ly, point.x, point.y, 1f, 0f, 0f, 1f, 1);
				lx = point.x;
				ly = point.y;
			}
		}
		if (lastActionMilli > MyTimer.get().millis() - ACTION_MILLI) {
			rect.setBounds(x - 0.5f, y - 0.5f, (MyTimer.get().millis() - lastActionMilli)
					/ (float) ACTION_MILLI, 0.1f);
			rect.draw(render.getVPMatrix(), 1f, 1f, 1f, 1f);
		}
		if (dead) {
			float[] vpMatrix = render.getVPMatrix();
			Matrix.translateM(vpMatrix, 0, x, y, 0);
			Matrix.scaleM(vpMatrix, 0, 0.5f, 0.5f, 1f);
			render.getGlib().getIcon(GLIconType.Enemy).draw(vpMatrix);
		}
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	private long flareUseMilli = 0;
	private static final long FLARE_MILLIS = 10000;

	public void useFlare() {
		flareUseMilli = MyTimer.get().millis();
	}

	public boolean isFlared() {
		return flareUseMilli > MyTimer.get().millis() - FLARE_MILLIS;
	}

	private long locUseMilli = 0;
	private static final long LOCATOR_MILLIS = 10000;

	public void useLocator() {
		locUseMilli = MyTimer.get().millis();
	}

	public boolean isLocating() {
		return locUseMilli > MyTimer.get().millis() - LOCATOR_MILLIS;
	}

	public void damage() {
		if (item != null && item.getType() == ItemType.Armor) {
			setItem(null);
		} else {
			dead = true;
		}
	}

	public boolean isAlert(SchminceRenderer render) {
		if (dead) {
			return true;
		} else {
			return MyTimer.get().millis() % 500 < 250 && render.hasEnemyChasing(this);
		}
	}
}

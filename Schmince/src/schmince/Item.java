package schmince;

import android.opengl.Matrix;

public class Item extends SObject {
	private ItemType type;

	public Item(ItemType type) {
		this.type = type;
	}

	public boolean useItem(Player player, SchminceRenderer render) {
		switch (type) {
		case Flare:
			player.useFlare();
			return true;
		case Locator:
			player.useLocator();
			return true;
		case Gun:
			Enemy closest = null;
			float closedist = 10000000;
			for (Enemy enemy : render.getEnemies()) {
				if (enemy.dead
						|| !render.hasLOS(render.getCurrentPlayer().x, render.getCurrentPlayer().y,
								enemy.x, enemy.y)) {
					continue;
				}
				float dx = render.getCurrentPlayer().x - enemy.x;
				float dy = render.getCurrentPlayer().y - enemy.y;
				float dist = (float) Math.sqrt(dx * dx + dy * dy);
				if (dist < closedist) {
					closedist = dist;
					closest = enemy;
				}
			}
			if (closest != null) {
				closest.dead = true;
			}
			return closest != null;
		default:
			return false;
		}
	}

	@Override
	void update(SchminceRenderer render) {

	}

	@Override
	void draw(SchminceRenderer render) {
		float[] vpMatrix = render.getVPMatrix();
		Matrix.translateM(vpMatrix, 0, x, y, 0);
		Matrix.scaleM(vpMatrix, 0, 0.5f, 0.5f, 1f);
		render.getGlib().getIcon(type.getIconType()).draw(vpMatrix);
	}

	public ItemType getType() {
		return type;
	}
}

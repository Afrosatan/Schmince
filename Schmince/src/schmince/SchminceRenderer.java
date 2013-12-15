package schmince;

import gui.SchminceGUI;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import opengl.GLLibrary;
import opengl.GLRectangle;
import opengl.GLTriangleUniform;
import perlin.SimplexNoise;
import util.MyTimer;
import util.SColor;
import util.SRandom;
import android.content.Context;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;

public class SchminceRenderer implements Renderer {
	public static final int PLAYER_COUNT = 6;
	public static final int MAP_SIZE = 100;
	public static final SColor[] PLAYER_COLORS = { new SColor(1, 0, 0), new SColor(0, 1, 0),
			new SColor(0, 0, 1), new SColor(1, 1, 0), new SColor(1, 0, 1), new SColor(0, 1, 1) };

	private final float[] tempMatrix = new float[16];
	private final MatrixController matrix = new MatrixController();
	private int screenWidth;
	private int screenHeight;

	private GLLibrary glib;
	private SchminceGUI gui = new SchminceGUI();

	private Block[][] blocks;
	private Player[] players = new Player[PLAYER_COUNT];
	private List<Enemy> enemies = new ArrayList<Enemy>();
	private Player currentPlayer;

	private Point safeZone = new Point();

	private int gameState = START_SCREEN;
	public static final int START_SCREEN = 0;
	public static final int IN_GAME = 1;
	public static final int GAME_OVER = 2;
	public static final int HOW_TO_PLAY = 3;

	public SchminceRenderer(Context context) {
		matrix.setMinZ(7.5f);
		matrix.setMaxZ(7.5f);

		gui.setRender(this);

		this.glib = new GLLibrary(context);

		blocks = new Block[MAP_SIZE][];
		for (int i = 0; i < blocks.length; i++) {
			Block[] blocker = blocks[i] = new Block[MAP_SIZE];
			for (int j = 0; j < blocker.length; j++) {
				blocker[j] = new Block();
			}
		}
		for (int i = 0; i < players.length; i++) {
			Player player = players[i] = new Player();
			player.color = PLAYER_COLORS[i];
		}
		newGame();
		gameState = START_SCREEN;
	}

	public void newGame() {
		double xoff = SRandom.get().nextDouble() * 1000000;
		double yoff = SRandom.get().nextDouble() * 1000000;
		safeZone.set(Math.min(MAP_SIZE - 1, Math.max(1, SRandom.get().nextInt(MAP_SIZE))),
				Math.min(MAP_SIZE - 1, Math.max(1, SRandom.get().nextInt(MAP_SIZE))));
		for (int i = 0; i < MAP_SIZE; i++) {
			for (int j = 0; j < MAP_SIZE; j++) {
				Block block = blocks[i][j];
				block.setHealth(-1);
				block.setObject(null);
				if (isSafe(i, j)) {
					continue;
				}
				float sample = (float) SimplexNoise.noise(xoff + i * 0.2, yoff + j * 0.2);
				if (sample > 0f) {
					block.setHealth(sample);
				}
			}
		}

		for (int i = 0; i < players.length; i++) {
			Player player = players[i] = new Player();
			player.color = PLAYER_COLORS[i];
			int x;
			int y;
			do {
				x = SRandom.get().nextInt(MAP_SIZE);
				y = SRandom.get().nextInt(MAP_SIZE);
			} while (blocks[x][y].isColored() || blocks[x][y].isOccupied() || isSafe(x, y));

			player.x = x;
			player.y = y;
			blocks[x][y].setObject(player);
		}
		currentPlayer = players[0];

		ItemType[] itemTypes = ItemType.values();
		for (int i = 0; i < MAP_SIZE; i++) {
			Item item = new Item(itemTypes[SRandom.get().nextInt(itemTypes.length)]);
			int x;
			int y;
			do {
				x = SRandom.get().nextInt(MAP_SIZE);
				y = SRandom.get().nextInt(MAP_SIZE);
			} while (blocks[x][y].isColored() || blocks[x][y].isOccupied());

			item.x = x;
			item.y = y;
			blocks[x][y].setObject(item);
		}

		enemies.clear();
		for (int i = 0; i < MAP_SIZE / 4; i++) {
			Enemy enemy = new Enemy();
			int x;
			int y;
			do {
				x = SRandom.get().nextInt(MAP_SIZE);
				y = SRandom.get().nextInt(MAP_SIZE);
			} while (blocks[x][y].isColored() || blocks[x][y].isOccupied());

			enemy.x = x;
			enemy.y = y;
			blocks[x][y].setObject(enemy);
			enemies.add(enemy);
		}
		gameState = IN_GAME;
	}

	public int getGameState() {
		return gameState;
	}

	public boolean isSafe(int x, int y) {
		int dx = safeZone.x - x;
		int dy = safeZone.y - y;
		return Math.sqrt(dx * dx + dy * dy) <= 4;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		//enable alpha blending
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		//enable depth testing
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL); //default is GL_LESS

		//Draw background (sets background color for future calls to GLES20.glClear)
		GLES20.glClearColor(0f, 0f, 0f, 1.0f);
		GLES20.glClearDepthf(1f);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		this.screenWidth = width;
		this.screenHeight = height;

		GLES20.glViewport(0, 0, width, height);

		matrix.setScreenSize(width, height);
	}

	private static final float BASE_TILE_COLOR = 0.2f;

	@Override
	public void onDrawFrame(GL10 gl) {
		MyTimer.get().update();

		if (gameState == IN_GAME) {
			int dead = 0;
			int safe = 0;
			for (Player player : players) {
				if (player.dead) {
					dead++;
				} else if (isSafe(player.x, player.y)) {
					safe++;
				}
			}
			if (dead + safe == PLAYER_COUNT) {
				gameState = GAME_OVER;
			}
		}

		if (gameState == IN_GAME) {
			for (Player player : players) {
				player.update(this);
			}
			for (Iterator<Enemy> iter = enemies.iterator(); iter.hasNext();) {
				Enemy enemy = iter.next();
				if (enemy.dead) {
					blocks[enemy.x][enemy.y].setObject(null);
					iter.remove();
				} else {
					enemy.update(this);
				}
			}
		}

		if (currentPlayer != null) {
			matrix.updateVPMatrix(currentPlayer.x, currentPlayer.y);
		}

		// Redraw background color (the last call to GLES20.glClearColor sets the color that will be used (Eg. in onSurfaceCreated))
		// also clear the depth buffer
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		float[] bottomleft = matrix.orthoToWorld(0, 0);
		float[] topright = matrix.orthoToWorld(screenWidth, screenHeight);
		int xs = Math.max(0, (int) bottomleft[0]);
		int ys = Math.max(0, (int) bottomleft[1]);
		int xe = Math.min(MAP_SIZE, (int) topright[0] + 1);
		int ye = Math.min(MAP_SIZE, (int) topright[1] + 1);

		GLRectangle rect = glib.getRectangle();
		for (int x = xs; x < xe; x++) {
			for (int y = ys; y < ye; y++) {
				if (gameState == IN_GAME && !currentPlayer.isFlared()
						&& !hasLOS(currentPlayer.x, currentPlayer.y, x, y)) {
					rect.setBounds(x - 0.5f, y - 0.5f, 1f, 1f);
					rect.draw(matrix.getVPMatrix(tempMatrix), 0f, 0f, 0f, 1f);
				} else {
					Block block = blocks[x][y];
					if (isSafe(x, y)) {
						rect.setBounds(x - 0.5f, y - 0.5f, 1f, 1f);
						rect.draw(matrix.getVPMatrix(tempMatrix), 1f, 1f, 1f, 1f);
					} else if (block.isColored()) {
						rect.setBounds(x - 0.5f, y - 0.5f, 1f, 1f);
						rect.draw(matrix.getVPMatrix(tempMatrix), 0.5f, 0.25f, 0f,
								0.5f + block.getHealth() * 0.5f);
					} else {
						rect.setBounds(x - 0.5f, y - 0.5f, 1f, 1f);
						rect.draw(matrix.getVPMatrix(tempMatrix), BASE_TILE_COLOR, BASE_TILE_COLOR,
								BASE_TILE_COLOR, 1f);
						rect.setLineBounds(x - 0.5f, y - 0.5f, 1f, 1f);
						rect.drawLine(matrix.getVPMatrix(tempMatrix), BASE_TILE_COLOR + 0.05f,
								BASE_TILE_COLOR + 0.05f, BASE_TILE_COLOR + 0.05f, 1f);
					}
					if (block.isOccupied()) {
						block.getObject().draw(this);
					}
				}
			}
		}

		if (currentPlayer.isLocating()) {
			drawLocators();
		}

		gui.draw(screenWidth, screenHeight);
	}

	public void howToPlay() {
		gameState = HOW_TO_PLAY;
	}

	private float[] vertices = new float[9];

	private void drawLocators() {
		float px = currentPlayer.x;
		float py = currentPlayer.y;

		for (Player player : players) {
			if (player == currentPlayer) {
				continue;
			}
			float dx = player.x - px;
			float dy = player.y - py;
			float h = (float) Math.sqrt(dx * dx + dy * dy);

			float outer = 5f;
			float xx = dx / h * outer;
			float yy = dy / h * outer;

			vertices[0] = px + xx;
			vertices[1] = py + yy;

			xx = dx / h * 2;
			yy = dy / h * 2;
			float xxx = dx / h * 0.25f;
			float yyy = dy / h * 0.25f;
			vertices[3] = px + xx - yyy;
			vertices[4] = py + yy + xxx;
			vertices[6] = px + xx + yyy;
			vertices[7] = py + yy - xxx;

			GLTriangleUniform tri = getGlib().getTriangleUniform();
			tri.setVertices(vertices);

			tri.draw(getVPMatrix(), player.color.Red, player.color.Green, player.color.Blue, 0.25f);
		}
	}

	/**
	 * Algorithm from http://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
	 */
	public boolean hasLOS(final int xf, final int yf, final int xp, final int yp) {
		int x0 = xp;
		int y0 = yp;
		int x1 = xf;
		int y1 = yf;
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);
		int sx;
		int sy;
		if (x0 < x1) {
			sx = 1;
		} else {
			sx = -1;
		}
		if (y0 < y1) {
			sy = 1;
		} else {
			sy = -1;
		}
		int err = dx - dy;

		while (true) {
			if (blocks[x0][y0].isColored() && !(x0 == xp && y0 == yp)) {
				return false;
			}

			if (x0 == x1 && y0 == y1) {
				break;
			}
			int e2 = 2 * err;
			if (e2 > -dy) {
				err = err - dy;
				x0 = x0 + sx;
			}
			if (x0 == x1 && y0 == y1) {
				break;
			}
			if (e2 < dx) {
				err = err + dx;
				y0 = y0 + sy;
			}
		}
		return true;
	}

	public GLLibrary getGlib() {
		return glib;
	}

	public float[] getVPOrthoMatrix() {
		return matrix.getVPOrthoMatrix(tempMatrix);
	}

	public float[] getVPMatrix() {
		return matrix.getVPMatrix(tempMatrix);
	}

	public SchminceGUI getGUI() {
		return gui;
	}

	public MatrixController getMatrix() {
		return matrix;
	}

	public boolean singleAction(float worldX, float worldY) {
		int x = Math.round(worldX);
		int y = Math.round(worldY);
		if (!inBounds(x, y)) {
			return true;
		}
		Block block = blocks[x][y];
		if (block.isOccupied() && x != currentPlayer.x && y != currentPlayer.y) {
			if (block.getObject() instanceof Item && Math.abs(x - currentPlayer.x) <= 1
					&& Math.abs(y - currentPlayer.y) <= 1) {
			} else {
				Point pt = findNearestUnoccupied(currentPlayer.x, currentPlayer.y, x, y);
				if (pt != null) {
					x = pt.x;
					y = pt.y;
				}
			}
		}
		currentPlayer.setTarget(x, y, this);
		return true;
	}

	private Point findNearestUnoccupied(final int px, final int py, final int x, final int y) {
		Point minPt = null;
		int minScore = 1000000000;
		for (int level = 1; level <= 3; level++) {
			int dx = x - level;
			int dy = y - level;
			int cx = 1; //move right
			int cy = 0;
			for (int r = 0; r < 4; r++) {
				for (int i = 0; i < level * 2; i++) {
					if (inBounds(dx, dy) && !blocks[dx][dy].isOccupied()) {
						int score = simpleScore(px, py, dx, dy);
						if (score < minScore) {
							if (minPt == null) {
								minPt = new Point(dx, dy);
							} else {
								minPt.set(dx, dy);
							}
							minScore = score;
						}
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
			if (minPt != null) {
				break;
			}
		}
		return minPt;
	}

	private int simpleScore(final int x1, int y1, int x2, int y2) {
		int dx = Math.abs(x2 - x1);
		int dy = Math.abs(y2 - y1);
		return Math.max(dx, dy);
	}

	public void setSelectedPlayer(int i) {
		currentPlayer = players[i];
	}

	public Player getPlayer(int i) {
		return players[i];
	}

	public void uniqueAction(float worldX, float worldY) {

	}

	public void endUniqueAction(float worldX, float worldY) {
	}

	/**
	 * A* algorithm, followed from wikipedia
	 */
	public Queue<Point> findPath(final int x, final int y, final int targetX, final int targetY,
			boolean skipColored, SObject ignoreObject) {
		Point start = new Point(x, y);
		Set<Point> closedSet = new HashSet<Point>();
		Set<Point> openSet = new HashSet<Point>();
		openSet.add(start);
		Map<Point, Point> cameFrom = new HashMap<Point, Point>();

		Map<Point, Float> gScore = new HashMap<Point, Float>();
		gScore.put(start, 0f);
		Map<Point, Float> fScore = new HashMap<Point, Float>();
		fScore.put(start, 0 + score(start, targetX, targetY));
		int iteration = 0;

		while (openSet.size() > 0) {
			iteration++;
			if (iteration > 1000) {
				break;
			}
			Point current = null;
			Float curval = null;
			for (Point pt : openSet) {
				Float ptValue = fScore.get(pt);
				if (curval == null || ptValue < curval) {
					current = pt;
					curval = ptValue;
				}
			}

			if (current.x == targetX && current.y == targetY) {
				Queue<Point> path = new ArrayDeque<Point>();
				reconstructPath(cameFrom, current, path);
				return path;
			}

			openSet.remove(current);
			closedSet.add(current);

			for (Point neighbor : neighbors(current, skipColored, ignoreObject)) {
				if (neighbor == null) {
					continue;
				}
				float tgScore = gScore.get(current) + 1;
				float tfScore = tgScore + score(neighbor, targetX, targetY);
				if (closedSet.contains(neighbor) && tfScore >= fScore.get(neighbor)) {
					continue;
				}

				if (!openSet.contains(neighbor) || tfScore < fScore.get(neighbor)) {
					cameFrom.put(neighbor, current);
					gScore.put(neighbor, tgScore);
					fScore.put(neighbor, tfScore);
					openSet.add(neighbor);
				}
			}
		}

		return new ArrayDeque<Point>();
	}

	private Point[] neighbors(Point current, boolean skipColored, SObject ignoreObject) {
		Point[] points = new Point[8];
		for (int i = 0; i < 8; i++) {
			int x;
			int y;
			switch (i) {
			case 0:
				x = current.x + 1;
				y = current.y + 1;//top right
				break;
			case 1:
				x = current.x;
				y = current.y + 1; // top middle
				break;
			case 2:
				x = current.x - 1;
				y = current.y + 1;//top left
				break;
			case 3:
				x = current.x - 1;
				y = current.y; //middle left;
				break;
			case 4:
				x = current.x - 1;
				y = current.y - 1; //bottom left
				break;
			case 5:
				x = current.x;
				y = current.y - 1; //bottom middle
				break;
			case 6:
				x = current.x + 1;
				y = current.y - 1; //bottom right
				break;
			case 7:
			default:
				x = current.x + 1;
				y = current.y; //middle right
				break;
			}
			if (inBounds(x, y)) {
				Block block = blocks[x][y];
				if ((!block.isOccupied() || (ignoreObject != null && block.getObject() == ignoreObject))
						&& (!skipColored || !blocks[x][y].isColored())) {
					points[i] = new Point(x, y);
				}
			}
		}

		return points;
	}

	public boolean inBounds(int x, int y) {
		return x < MAP_SIZE && y < MAP_SIZE && x >= 0 && y >= 0;
	}

	private void reconstructPath(Map<Point, Point> cameFrom, Point current, Queue<Point> path) {
		if (cameFrom.containsKey(current)) {
			reconstructPath(cameFrom, cameFrom.get(current), path);
			path.add(current);
		} else {
			path.add(current);
		}
	}

	/**
	 * Heuristic modified from http://theory.stanford.edu/~amitp/GameProgramming/Heuristics.html.
	 */
	private float score(Point from, int targetX, int targetY) {
		int dx = Math.abs(targetX - from.x);
		int dy = Math.abs(targetY - from.y);
		return Math.max(dx, dy) + Math.min(dx, dy) * 0.0001f
				+ Math.max(0, blocks[from.x][from.y].getHealth() * 10);
	}

	public Block getBlock(int x, int y) {
		return blocks[x][y];
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public void useCurrentItem() {
		if (currentPlayer.getItem() != null && !currentPlayer.dead) {
			if (currentPlayer.getItem().useItem(currentPlayer, this)) {
				currentPlayer.setItem(null);
			}
		}
	}

	public boolean hasEnemyChasing(Player player) {
		for (Enemy enemy : enemies) {
			if (enemy.chasePlayer == player) {
				return true;
			}
		}
		return false;
	}

	public List<Enemy> getEnemies() {
		return enemies;
	}

	public void startScreen() {
		gameState = START_SCREEN;
	}
}

package gui;

import java.util.ArrayList;
import java.util.List;

import schmince.Item;
import schmince.Player;
import schmince.SchminceRenderer;
import texample.GLText;
import texample.GLTextType;
import util.NumCharSequence;
import android.util.SparseArray;

/**
 * GUI component/touch input handler and stuff
 */
public class SchminceGUI {
	private SparseArray<PointerState> pointers = new SparseArray<PointerState>();
	private PointerState uniquePointer = null;

	private SchminceRenderer render;

	private List<GUIItem> inGameItems = new ArrayList<GUIItem>();
	private SelectPlayerButton[] playerButtons = new SelectPlayerButton[SchminceRenderer.PLAYER_COUNT];
	private UseItemButton useItemButton = new UseItemButton();

	private List<GUIItem> startScreenItems = new ArrayList<GUIItem>();
	private Label titleLabel = new Label("Schmince!");
	private NewGameButton newGameButton = new NewGameButton();
	private HowToPlayButton howToPlayButton = new HowToPlayButton();

	private List<GUIItem> gameOverItems = new ArrayList<GUIItem>();
	private StartScreenButton gameOverButton = new StartScreenButton("Game Over");
	private NumCharSequence gameOverText = new NumCharSequence("Living: ", null, " Dead: ", null);
	private Label gameOverLabel = new Label(gameOverText);

	private List<GUIItem> howToPlayItems = new ArrayList<GUIItem>();
	private Label[] howToPlayLabels = { //
	new Label("Buttons on bottom to switch characters"), //
			new Label("Tap in game to move the character"), //
			new Label("Move next to items and tap to switch items (1 at a time)"), //
			new Label("Button on top right to use items (some are passive)"), //
			new Label("Run away from purple/black star things (1 hit kill)"), //
			new Label("Each character has 1 life"), //
			new Label("Find the white area to win (NOTE: white area isn't safe from enemies)"), //
			new Label("Game ends when all characters are in the white area or dead"), //
			new Label(""), // 
			new Label("Items: "), //
			new Label(" - Flare - (one use) see surrounding area for 10 seconds"), //
			new Label(" - Locator - (one use) see locations of other characters for 10 seconds"), //
			new Label(" - Boots - (passive) move 25% faster on open ground"), //
			new Label(" - Pick - (passive) digs through walls twice as fast"), //
			new Label(" - Gun - (one use) kills the nearest visible enemy"), //
			new Label(" - Armor - (one use/passive) absorbs one enemy attack and enemy dies"), //
			new Label(""), // 
			new Label("Game created for Ludum Dare 28"), // 
	};
	private StartScreenButton startScreenButton = new StartScreenButton("Start Screen");

	public SchminceGUI() {
		for (int i = 0; i < playerButtons.length; i++) {
			SelectPlayerButton button = new SelectPlayerButton(i);
			playerButtons[i] = button;
			inGameItems.add(button);
		}
		inGameItems.add(useItemButton);

		gameOverButton.NormalColor.set(1f, 1f, 1f, 0.5f);
		gameOverButton.TextScale = 4f;
		gameOverItems.add(gameOverButton);
		gameOverLabel.TextScale = 2f;
		gameOverLabel.Color.set(0f, 0f, 0f, 1f);
		gameOverItems.add(gameOverLabel);

		titleLabel.TextScale = 4f;
		titleLabel.TextType = GLTextType.SerifBold;
		startScreenItems.add(titleLabel);
		newGameButton.TextScale = 2f;
		startScreenItems.add(newGameButton);
		howToPlayButton.TextScale = 2f;
		startScreenItems.add(howToPlayButton);

		for (Label label : howToPlayLabels) {
			label.TextScale = 0.5f;
			howToPlayItems.add(label);
		}
		howToPlayItems.add(startScreenButton);
	}

	public void setRender(SchminceRenderer render) {
		this.render = render;
	}

	public void touchDown(int pid, float orthoX, float orthoY, float worldX, float worldY) {
		PointerState ps = pointers.get(pid);
		if (ps != null && ps.onGUI != null) {
			ps.onGUI.HasFocus = false;
		}

		ps = new PointerState();
		ps.ox = orthoX;
		ps.oy = orthoY;
		ps.wx = worldX;
		ps.wy = worldY;
		pointers.put(pid, ps);

		List<GUIItem> items = getGUIItems();
		if (items != null) {
			for (int i = items.size() - 1; i >= 0; i--) {
				GUIItem gu = items.get(i);
				if (gu.Visible && gu.Bounds.contains(orthoX, orthoY)) {
					gu.HasFocus = true;
					ps.onGUI = gu;
					break;
				}
			}
		}
		if (ps.onGUI == null) {
			if (!render.singleAction(worldX, worldY) && uniquePointer == null) {
				render.uniqueAction(worldX, worldY);
				uniquePointer = ps;
			}
		}
	}

	public void touchUp(int pid, float orthoX, float orthoY, float worldX, float worldY) {
		PointerState ps = pointers.get(pid);
		if (ps == null) {
			return;
		}
		pointers.remove(pid);
		if (ps.onGUI != null) {
			if (ps.onGUI.Bounds.contains(orthoX, orthoY)) {
				if (ps.onGUI instanceof Button) {
					Button selectedButton = (Button) ps.onGUI;
					selectedButton.doAction(render);
				}
			}
			ps.onGUI.HasFocus = false;
		} else {
			if (ps == uniquePointer) {
				render.uniqueAction(worldX, worldY);
				render.endUniqueAction(worldX, worldY);
			}
		}
		if (ps == uniquePointer) {
			uniquePointer = null;
		}
	}

	public void touchMove(int pid, float orthoX, float orthoY, float worldX, float worldY) {
		PointerState ps = pointers.get(pid);
		if (ps == null) {
			return;
		}
		if (ps.ox == orthoX && ps.oy == orthoY) {
			return;
		}

		if (ps.onGUI != null) {
			if (!ps.onGUI.Bounds.contains(orthoX, orthoY)) {
				ps.onGUI.HasFocus = false;
			}
		} else {
			if (ps == uniquePointer) {
				render.uniqueAction(worldX, worldY);
			}

			ps.ox = orthoX;
			ps.oy = orthoY;
			ps.wx = worldX;
			ps.wy = worldY;
		}
	}

	public void touchCancel(int pid) {
		PointerState ps = pointers.get(pid);
		if (ps == null) {
			return;
		}
		pointers.remove(pid);
		if (ps.onGUI != null) {
			ps.onGUI.HasFocus = false;
		}
		if (uniquePointer == ps) {
			render.endUniqueAction(ps.wx, ps.wy);
			uniquePointer = null;
		}
	}

	public List<GUIItem> getGUIItems() {
		switch (render.getGameState()) {
		case SchminceRenderer.IN_GAME:
			return inGameItems;
		case SchminceRenderer.GAME_OVER:
			return gameOverItems;
		case SchminceRenderer.START_SCREEN:
			return startScreenItems;
		case SchminceRenderer.HOW_TO_PLAY:
			return howToPlayItems;
		}
		return null;
	}

	public void update(int screenWidth, int screenHeight) {
		switch (render.getGameState()) {
		case SchminceRenderer.IN_GAME:
			updateInGame(screenWidth, screenHeight);
			break;
		case SchminceRenderer.GAME_OVER:
			updateGameOver(screenWidth, screenHeight);
			break;
		case SchminceRenderer.START_SCREEN:
			updateStartScreen(screenWidth, screenHeight);
			break;
		case SchminceRenderer.HOW_TO_PLAY:
			updateHowToPlay(screenWidth, screenHeight);
			break;
		}
	}

	private void updateHowToPlay(int screenWidth, int screenHeight) {
		GLText text = howToPlayLabels[0].getGLText(render.getGlib());
		float h = text.getHeight();
		float y = screenHeight - h;
		float w;
		for (Label label : howToPlayLabels) {
			w = text.getLength(label.Text);
			label.Bounds.set(0, y, w, h);
			y -= h;
		}

		text = startScreenButton.getGLText(render.getGlib());
		w = text.getLength(startScreenButton.Text);
		h = text.getHeight();
		startScreenButton.Bounds.set(screenWidth - w, 0, w, h);
	}

	private void updateGameOver(int screenWidth, int screenHeight) {
		int living = 0;
		int dead = 0;
		for (int i = 0; i < SchminceRenderer.PLAYER_COUNT; i++) {
			if (render.getPlayer(i).dead) {
				dead++;
			} else {
				living++;
			}
		}
		gameOverText.setNum(0, living);
		gameOverText.setNum(1, dead);
		if (living > dead) {
			gameOverButton.NormalColor.set(0f, 1f, 0f, 0.5f);
		} else {
			gameOverButton.NormalColor.set(1f, 0f, 0f, 0.5f);
		}
		gameOverButton.Bounds.set(0, screenHeight / 2, screenWidth, screenHeight / 2);
		gameOverLabel.Bounds.set(0, 0, screenWidth, screenHeight / 2);
	}

	private void updateStartScreen(int screenWidth, int screenHeight) {
		titleLabel.Bounds.set(0, screenHeight / 2, screenWidth, screenHeight / 2);
		newGameButton.Bounds.set(0, 0, screenWidth / 2, screenHeight / 2);
		howToPlayButton.Bounds.set(screenWidth / 2, 0, screenWidth / 2, screenHeight / 2);
	}

	private void updateInGame(int screenWidth, int screenHeight) {
		float x = 0;
		float y = 0;
		float h = playerButtons[0].getGLText(render.getGlib()).getHeight() * 1.1f;
		for (SelectPlayerButton button : playerButtons) {
			Player player = render.getPlayer(button.getI());
			button.setName(player.name);
			if (player.isAlert(render)) {
				button.NormalColor.set(0f, 0f, 0f, 1f);
				button.TextColor.set(render.getPlayer(button.getI()).color);
			} else {
				button.NormalColor.set(render.getPlayer(button.getI()).color);
				button.TextColor.set(0f, 0f, 0f, 1f);
			}
			float w = button.getGLText(render.getGlib()).getLength(button.Text) * 1.1f;
			if (x + w > screenWidth) {
				y += h + 5;
				x = 0;
			}
			button.Bounds.set(x, y, w, h);
			x += w + 5;
		}

		Item item = render.getCurrentPlayer().getItem();
		useItemButton.Visible = item != null;
		if (item != null) {
			useItemButton.Text = item.getType().name();
			float w = useItemButton.getGLText(render.getGlib()).getLength(useItemButton.Text);
			h = useItemButton.getGLText(render.getGlib()).getHeight();
			useItemButton.Bounds.set(screenWidth - w, screenHeight - h, w, h);
		}
	}

	public void draw(int screenWidth, int screenHeight) {
		update(screenWidth, screenHeight);

		List<GUIItem> gui = getGUIItems();
		if (gui == null) {
			return;
		}

		int size = gui.size();
		for (int i = 0; i < size; i++) {
			GUIItem gu = gui.get(i);
			if (!gu.Visible || gu.Bounds.w == 0 || gu.Bounds.h == 0) {
				continue;
			}
			gu.draw(render);
		}
	}

	private static class PointerState {
		GUIItem onGUI;
		float ox;
		float oy;
		float wx;
		float wy;
	}
}

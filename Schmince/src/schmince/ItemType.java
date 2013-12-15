package schmince;

import opengl.GLIconType;

public enum ItemType {
	Flare(GLIconType.Flare),
	Locator(GLIconType.Locator),
	Boots(GLIconType.Boots),
	Pick(GLIconType.Pick),
	Gun(GLIconType.Gun),
	Armor(GLIconType.Armor),
	//
	;

	private GLIconType iconType;

	private ItemType(GLIconType iconType) {
		this.iconType = iconType;
	}

	public GLIconType getIconType() {
		return iconType;
	}
}

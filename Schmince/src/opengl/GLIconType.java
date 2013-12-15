package opengl;

/**
 * Enum of the various icon shapes available to draw.
 */
public enum GLIconType {
	Flare(GLFlare.class),
	Gun(GLGun.class),
	Locator(GLLocator.class),
	Boots(GLBoots.class),
	Armor(GLArmor.class),
	Pick(GLPick.class),
	//
	Enemy(GLEnemy.class),
	//
	;

	private Class<? extends GLIcon> clazz;

	GLIconType(Class<? extends GLIcon> clazz) {
		this.clazz = clazz;
	}

	public Class<? extends GLIcon> getClazz() {
		return clazz;
	}
}

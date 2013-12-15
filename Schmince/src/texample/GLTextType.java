package texample;

/**
 * Enumeration of the fonts in the assets folder for the project.
 */
public enum GLTextType {
	Mono("FreeMono.otf"),
	MonoBold("FreeMonoBold.otf"),
	MonoBoldItalic("FreeMonoBoldOblique.otf"),
	MonoItalic("FreeMonoOblique.otf"),
	Sans("FreeSans.otf"),
	SansBold("FreeSansBold.otf"),
	SansBoldOblique("FreeSansBoldOblique.otf"),
	SansOblique("FreeSansOblique.otf"),
	Serif("FreeSerif.otf"),
	SerifBold("FreeSerifBold.otf"),
	SerifBoldItalic("FreeSerifBoldItalic.otf"),
	SerifItalic("FreeSerifItalic.otf");

	public final String FileName;

	GLTextType(String fileName) {
		this.FileName = fileName;
	}
}

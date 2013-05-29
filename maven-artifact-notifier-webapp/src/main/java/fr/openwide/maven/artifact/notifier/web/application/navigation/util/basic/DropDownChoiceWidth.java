package fr.openwide.maven.artifact.notifier.web.application.navigation.util.basic;

public enum DropDownChoiceWidth {
	
	SMALL(104),
	NORMAL(220),
	ONE_AND_HALF(350),
	DOUBLE(443),
	XLARGE(284),
	XXLARGE(534);
	
	private int width;
	
	private DropDownChoiceWidth(int width) {
		this.width = width;
	}
	
	public int getWidth() {
		return width;
	}

}

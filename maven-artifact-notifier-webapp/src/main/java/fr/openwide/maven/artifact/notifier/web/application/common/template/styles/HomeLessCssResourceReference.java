package fr.openwide.maven.artifact.notifier.web.application.common.template.styles;

import fr.openwide.core.wicket.more.lesscss.LessCssResourceReference;

public final class HomeLessCssResourceReference extends LessCssResourceReference {
	
	private static final long serialVersionUID = -8166848230449963029L;
	
	private static final HomeLessCssResourceReference INSTANCE = new HomeLessCssResourceReference();
	
	private HomeLessCssResourceReference() {
		super(HomeLessCssResourceReference.class, "home.less");
	}
	
	public static HomeLessCssResourceReference get() {
		return INSTANCE;
	}
	
}

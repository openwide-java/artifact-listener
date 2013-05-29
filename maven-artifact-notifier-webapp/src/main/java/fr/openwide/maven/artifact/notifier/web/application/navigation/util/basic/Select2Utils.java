package fr.openwide.maven.artifact.notifier.web.application.navigation.util.basic;

import org.retzlaff.select2.Select2Settings;

public final class Select2Utils {
	
	public static final String CSS_DROP_MINI = "select2-drop-mini";
	public static final String CSS_DROP_SMALL = "select2-drop-small";
	public static final String CSS_DROP_MEDIUM = "select2-drop-medium";
	public static final String CSS_DROP_LARGE = "select2-drop-large";
	public static final String CSS_DROP_XLARGE = "select2-drop-xlarge";
	public static final String CSS_DROP_XXLARGE = "select2-drop-xxlarge";
	public static final String CSS_DROP_XXXLARGE = "select2-drop-xxxlarge";
	
	private static final String DEFAULT_MATCHER = "function(term, text) { return OWSI.StringUtils.removeAccents('' + text).indexOf(OWSI.StringUtils.removeAccents('' + term))>=0; }";
	
	private static final String DEFAULT_FORMAT_RESULT = "function(result, container, query) {" +
			"var markup=[];" +
			"var text = result.text;" +
			"var term = query.term;" +
			"var match=OWSI.StringUtils.removeAccents('' + text).indexOf(OWSI.StringUtils.removeAccents('' + term));" +
			"var tl=term.length;" +
			"if (match<0) {" +
				"return text;" +
			"}" +
			"markup.push(text.substring(0, match));" +
			"markup.push(\"<span class='select2-match'>\");" +
			"markup.push(text.substring(match, match + tl));" +
			"markup.push(\"</span>\");" +
			"markup.push(text.substring(match + tl, text.length));" +
			"return markup.join('');" +
		"}";
	
	public static void setDefaultSettings(Select2Settings settings) {
		settings.setTextEncoding("UTF-8");
		settings.setWidth("resolve");
		settings.setNoMatchesKey("common.select2.zero");
		settings.setMatcher(DEFAULT_MATCHER);
		settings.setFormatResult(DEFAULT_FORMAT_RESULT);
	}
	
	public static void disableSearch(Select2Settings settings) {
		settings.setMinimumResultsForSearch(Integer.MAX_VALUE);
	}
	
	private Select2Utils() {
	}
}

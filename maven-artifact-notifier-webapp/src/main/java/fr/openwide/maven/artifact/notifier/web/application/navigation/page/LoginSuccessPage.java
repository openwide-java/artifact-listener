package fr.openwide.maven.artifact.notifier.web.application.navigation.page;


public class LoginSuccessPage extends AbstractLoginSuccessPage {
	
	private static final long serialVersionUID = -7329107409428194512L;

	public LoginSuccessPage() {
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		redirectToSavedPage();
	}
}
package fr.openwide.maven.artifact.notifier.web.application;

import org.apache.wicket.Session;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.authentication.AuthenticationManager;

import fr.openwide.core.wicket.more.AbstractCoreSession;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;

public class MavenArtifactNotifierSession extends AbstractCoreSession<User> {
	
	private static final long serialVersionUID = 1870827020904365541L;
	
	public static final String SPRING_SECURITY_SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";
	
	@SpringBean(name = "authenticationManager")
	private AuthenticationManager authenticationManager;
	
	public MavenArtifactNotifierSession(Request request) {
		super(request);
	}
	
	public static MavenArtifactNotifierSession get() {
		return (MavenArtifactNotifierSession) Session.get();
	}
	
	public User getUser() {
		return super.getPerson();
	}
	
	public IModel<User> getUserModel() {
		return super.getPersonModel();
	}
	
	public void authenticatePac4j() {
		if (!isSignedIn()) {
			doInitializeSession();
			signIn(true);
			
			bind();
		}
	}
}
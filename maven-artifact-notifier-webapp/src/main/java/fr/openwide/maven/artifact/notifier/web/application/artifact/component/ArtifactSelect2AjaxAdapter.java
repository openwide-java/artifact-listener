package fr.openwide.maven.artifact.notifier.web.application.artifact.component;

import java.util.List;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.wicket.more.markup.html.select2.AbstractLongIdGenericEntitySelect2AjaxAdapter;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;

public class ArtifactSelect2AjaxAdapter extends AbstractLongIdGenericEntitySelect2AjaxAdapter<Artifact> {

	private static final long serialVersionUID = -5673297354048913676L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactSelect2AjaxAdapter.class);

	@SpringBean
	private IArtifactService artifactService;

	public ArtifactSelect2AjaxAdapter(IChoiceRenderer<Artifact> choiceRenderer) {
		super(Artifact.class, choiceRenderer);
	}

	@Override
	public List<Artifact> getChoices(int start, int count, String term) {
		try {
			return artifactService.searchAutocomplete(term, count, start);
		} catch (ServiceException e) {
			LOGGER.error("Error while searching for artifacts");
			return Lists.newArrayListWithExpectedSize(0);
		}
	}
}

package fr.openwide.maven.artifact.notifier.web.application.administration.component;

import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.internal.Lists;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.wicket.more.markup.html.image.BooleanIcon;
import fr.openwide.core.wicket.more.markup.html.link.InvisibleLink;
import fr.openwide.core.wicket.more.markup.html.list.GenericPortfolioPanel;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.core.wicket.more.model.ReadOnlyModel;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactDeprecationStatus;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.FollowedArtifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IFollowedArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.sync.service.IMavenSynchronizationService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.MavenArtifactNotifierSession;
import fr.openwide.maven.artifact.notifier.web.application.administration.page.AdministrationArtifactDescriptionPage;

public class ArtifactPortfolioPanel extends GenericPortfolioPanel<Artifact> {

	private static final long serialVersionUID = 6030960404037116497L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactPortfolioPanel.class);
	
	@SpringBean
	private IArtifactService artifactService;

	@SpringBean
	private IFollowedArtifactService followedArtifactService;
	
	@SpringBean
	private IMavenSynchronizationService mavenSynchronizationService;

	public ArtifactPortfolioPanel(String id, IDataProvider<Artifact> dataProvider, int itemsPerPage) {
		super(id, dataProvider, itemsPerPage);
	}

	@Override
	protected void addItemColumns(Item<Artifact> item, IModel<? extends Artifact> artifactModel) {
		item.add(new Label("groupId", BindingModel.of(artifactModel, Binding.artifact().group().groupId())));
		Link<Void> artifactLink = AdministrationArtifactDescriptionPage.linkDescriptor(ReadOnlyModel.of(artifactModel))
				.link("artifactLink");
		artifactLink.add(new Label("artifactId", BindingModel.of(artifactModel, Binding.artifact().artifactId())));
		item.add(artifactLink);
		item.add(new Label("nbVersions", BindingModel.of(artifactModel, Binding.artifact().versions().size())));
		
		final IModel<ArtifactDeprecationStatus> deprecatedModel = BindingModel.of(artifactModel, Binding.artifact().deprecationStatus());
		item.add(new BooleanIcon("deprecated", new LoadableDetachableModel<Boolean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected Boolean load() {
				return ArtifactDeprecationStatus.DEPRECATED.equals(deprecatedModel.getObject());
			}
		}));
	}

	@Override
	protected boolean isActionAvailable() {
		return true;
	}

	@Override
	protected boolean isDeleteAvailable() {
		return true;
	}

	@Override
	protected boolean isEditAvailable() {
		return true;
	}

	@Override
	protected boolean hasWritePermissionOn(IModel<? extends Artifact> artifactModel) {
		return MavenArtifactNotifierSession.get().hasRoleAdmin();
	}
	
	@Override
	protected MarkupContainer getActionLink(String id, IModel<? extends Artifact> artifactModel) {
		return AdministrationArtifactDescriptionPage.linkDescriptor(ReadOnlyModel.of(artifactModel))
				.link(id);
	}
	
	@Override
	protected MarkupContainer getEditLink(String id, IModel<? extends Artifact> itemModel) {
		IModel<Artifact> artifactModel = Model.of(itemModel);
		Link<Artifact> syncLink = new Link<Artifact>(id, artifactModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				try {
					mavenSynchronizationService.synchronizeArtifactsAndNotifyUsers(Lists.newArrayList(getModelObject()));
					getSession().success(getString("administration.artifact.sync.success"));
				} catch (Exception e) {
					LOGGER.warn("An error occurred while synchronizing artifact", e);
					getSession().error(getString("administration.artifact.sync.error"));
				}
			}
		};
		
		return syncLink;
	}
	
	@Override
	protected IModel<String> getEditText(IModel<? extends Artifact> itemModel) {
		return new ResourceModel("administration.artifact.sync");
	}
	
	@Override
	protected IModel<String> getEditBootstrapIconClass(IModel<? extends Artifact> itemModel) {
		return Model.of("fa fa-fw fa-refresh");
	}

	@Override
	protected MarkupContainer getDeleteLink(String id, IModel<? extends Artifact> artifactModel) {
		List<FollowedArtifact> followedArtifacts = followedArtifactService.listByArtifact(artifactModel.getObject());
		if (followedArtifacts.isEmpty()) {
			return super.getDeleteLink(id, artifactModel);
		}
		return new InvisibleLink<Void>(id);
	}
	
	@Override
	protected void doDeleteItem(IModel<? extends Artifact> artifactModel) throws ServiceException, SecurityServiceException {
		artifactService.delete(artifactModel.getObject());
	}

	@Override
	protected IModel<String> getDeleteConfirmationTitleModel(IModel<? extends Artifact> artifactModel) {
		return new StringResourceModel("administration.artifact.delete.confirmation.title", artifactModel);
	}

	@Override
	protected IModel<String> getDeleteConfirmationTextModel(IModel<? extends Artifact> artifactModel) {
		return new StringResourceModel("administration.artifact.delete.confirmation.text", artifactModel);
	}
}

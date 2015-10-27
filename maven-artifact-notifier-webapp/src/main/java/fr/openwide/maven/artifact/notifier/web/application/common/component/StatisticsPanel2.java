package fr.openwide.maven.artifact.notifier.web.application.common.component;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.core.wicket.more.markup.html.basic.DateLabel;
import fr.openwide.core.wicket.more.markup.html.collection.GenericEntitySortedSetView;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.core.wicket.more.model.CollectionToListWrapperModel;
import fr.openwide.core.wicket.more.model.GenericEntityTreeSetModel;
import fr.openwide.core.wicket.more.util.DatePattern;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactVersion;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactVersionService;
import fr.openwide.maven.artifact.notifier.core.business.parameter.service.IParameterService;
import fr.openwide.maven.artifact.notifier.core.business.statistics.service.IStatisticService;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.ArtifactVersionTagPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.page.ArtifactDescriptionPage;

public class StatisticsPanel2 extends GenericPanel<Map<Date, Set<ArtifactVersion>>> {
	
	private static final long serialVersionUID = 8349879446477301375L;
	
	@SpringBean
	private IParameterService parameterService;
	
	@SpringBean
	private IStatisticService statisticService;
	
	@SpringBean
	private IArtifactService artifactService;
	
	@SpringBean
	private IArtifactVersionService artifactVersionService;
	
	@SpringBean
	private MavenArtifactNotifierConfigurer configurer;
	
	public StatisticsPanel2(String id) {
		super(id);
		setOutputMarkupId(true);
		
		// Most followed artifacts
		IModel<List<Artifact>> mostFollowedArtifactsModel = new LoadableDetachableModel<List<Artifact>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<Artifact> load() {
				return artifactService.listMostFollowedArtifacts(configurer.getMostFollowedArtifactsLimit());
			}
		};
		add(new ListView<Artifact>("mostFollowedArtifacts", mostFollowedArtifactsModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Artifact> item) {
				item.add(ArtifactDescriptionPage.linkDescriptor(item.getModel()).link("artifactId")
						.setBody(BindingModel.of(item.getModel(), Binding.artifact().artifactKey().key())));
				
				item.add(new Label("followersCount", BindingModel.of(item.getModel(), Binding.artifact().followersCount())));
			}
		});
		
		// Recent updates
		final IModel<Map<Date, SortedSet<ArtifactVersion>>> artifactVersionLastUpdateDateModel = new LoadableDetachableModel<Map<Date, SortedSet<ArtifactVersion>>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected Map<Date, SortedSet<ArtifactVersion>> load() {
				Map<Date, SortedSet<ArtifactVersion>> result = Maps.newTreeMap(Collections.reverseOrder());
				List<ArtifactVersion> versionList = artifactVersionService.listRecentReleases(configurer.getLastUpdatesArtifactsLimit());

				Date previousDate = null;
				int daysCount = 0;
				for (ArtifactVersion version : versionList) {
					if (previousDate == null || !DateUtils.isSameDay(previousDate, version.getLastUpdateDate())) {
						if (daysCount >= configurer.getLastUpdatesDaysLimit()) {
							break;
						}
						previousDate = version.getLastUpdateDate();
						result.put(previousDate, Sets.<ArtifactVersion>newTreeSet());
						daysCount++;
					}
					result.get(previousDate).add(version);
				}
				return result;
			}
		};
		
		
		IModel<List<Date>> wrapperModel = CollectionToListWrapperModel.of(artifactVersionLastUpdateDateModel.getObject().keySet());
		
		add(new ListView<Date>("dayList", wrapperModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Date> item) {
				Date day = item.getModelObject();
				SortedSet<ArtifactVersion> versionSet = artifactVersionLastUpdateDateModel.getObject().get(day);
				
				Label dayLabel = new DateLabel("day", Model.of(day), DatePattern.SHORT_DATE);
				if (versionSet == null) {
					dayLabel = new Label("day");
					versionSet = Sets.newTreeSet();
				}
				
				IModel<SortedSet<ArtifactVersion>> versionSetModel = GenericEntityTreeSetModel.of(ArtifactVersion.class);
				versionSetModel.setObject(versionSet);
				
				// Day label
				item.add(dayLabel);
				item.add(new GenericEntitySortedSetView<ArtifactVersion>("notificationList", versionSetModel) {
					private static final long serialVersionUID = 1L;
					
					@Override
					protected void populateItem(Item<ArtifactVersion> item) {
						final IModel<ArtifactVersion> notificationModel = item.getModel();
						final IModel<Artifact> artifactModel = BindingModel.of(notificationModel,
								Binding.artifactVersion().artifact());
						
						// Artifact link
						Link<Void> artifactLink = ArtifactDescriptionPage
								.linkDescriptor(artifactModel)
								.link("artifactLink");
						artifactLink.add(new Label("id", BindingModel.of(artifactModel, Binding.artifact().artifactKey().key())));
						item.add(artifactLink);
						
						// Version tag
						item.add(new ArtifactVersionTagPanel("version", BindingModel.of(notificationModel,  Binding.artifactVersion().version())));
					}
				});
			}
		});
	}
}

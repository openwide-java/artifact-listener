package fr.openwide.maven.artifact.notifier.core.util.init.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import de.schlichtherle.truezip.file.TFileInputStream;
import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.core.jpa.more.business.link.model.ExternalLinkWrapper;
import fr.openwide.core.jpa.more.util.init.dao.IImportDataDao;
import fr.openwide.core.jpa.more.util.init.service.AbstractImportDataServiceImpl;
import fr.openwide.core.jpa.more.util.init.util.GenericEntityConverter;
import fr.openwide.core.jpa.more.util.init.util.WorkbookUtils;
import fr.openwide.core.spring.util.SpringBeanUtils;
import fr.openwide.core.spring.util.StringUtils;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.Artifact;
import fr.openwide.maven.artifact.notifier.core.business.artifact.model.ArtifactKey;
import fr.openwide.maven.artifact.notifier.core.business.artifact.service.IArtifactService;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.business.project.model.ProjectVersion;
import fr.openwide.maven.artifact.notifier.core.business.project.service.IProjectService;
import fr.openwide.maven.artifact.notifier.core.business.project.service.IProjectVersionService;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserService;

@Service("projectImportDataService")
public class ProjectImportDataServiceImpl extends AbstractImportDataServiceImpl implements IProjectImportDataService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectImportDataServiceImpl.class);

	private static final String ID_FIELD_NAME = "id";

	private static final String AUDIT_SUMMARY_CREATION_DATE_FIELD_NAME = "auditSummary.creationDate";

	private static final String AUDIT_SUMMARY_LAST_EDIT_DATE_FIELD_NAME = "auditSummary.lastEditDate";

	private static final String AUDIT_SUMMARY_CREATION_AUTHOR_FIELD_NAME = "auditSummary.creationAuthor";

	private static final String AUDIT_SUMMARY_LAST_EDIT_AUTHOR_FIELD_NAME = "auditSummary.lastEditAuthor";

	@Autowired
	private IImportDataDao importDataDao;

	@Autowired
	private IUserService userService;

	@Autowired
	private IProjectService projectService;

	@Autowired
	private IArtifactService artifactService;

	@Autowired
	private IProjectVersionService projectVersionService;

	@Override
	protected List<String> getGenericListItemPackagesToScan() {
		return Lists.newArrayList();
	}

	@Override
	protected void importMainBusinessItems(Map<String, Map<String, GenericEntity<Long, ?>>> idsMapping, Workbook workbook) {
	}

	@Override
	public void importProjects(File file) throws FileNotFoundException, IOException {
		Map<String, Map<String, GenericEntity<Long, ?>>> idsMapping = new HashMap<String, Map<String, GenericEntity<Long, ?>>>();

		Workbook workbook = new HSSFWorkbook(new TFileInputStream(file));
		doImportItem(idsMapping, workbook, Project.class);
		doImportItem(idsMapping, workbook, ProjectVersion.class);
	}

	@Override
	protected <E extends GenericEntity<Long, ?>> void doImportItem(
			Map<String, Map<String, GenericEntity<Long, ?>>> idsMapping, Workbook workbook, Class<E> clazz) {
		Sheet sheet = workbook.getSheet(getSheetName(clazz));
		if (sheet != null) {
			GenericConversionService conversionService = getConversionService(workbook, idsMapping);

			Map<String, GenericEntity<Long, ?>> idsMappingForClass = new HashMap<String, GenericEntity<Long, ?>>();
			idsMapping.put(clazz.getName(), idsMappingForClass);

			for (Class<?> referencedClass : getOtherReferencedClasses(clazz)) {
				if (!idsMapping.containsKey(referencedClass.getName())) {
					idsMapping.put(referencedClass.getName(), new HashMap<String, GenericEntity<Long, ?>>());
				}
			}

			for (Map<String, Object> line : WorkbookUtils.getSheetContent(sheet)) {
				E item = BeanUtils.instantiateClass(clazz);

				Long importId = Long.parseLong(line.get(ID_FIELD_NAME).toString());
				line.remove(ID_FIELD_NAME);

				doFilterLine(clazz, line);

				BeanWrapper wrapper = SpringBeanUtils.getBeanWrapper(item);
				wrapper.setConversionService(conversionService);
				wrapper.setPropertyValues(new MutablePropertyValues(line), true);

				importDataDao.create(item);

				onAfterCreate(item);
				idsMappingForClass.put(importId.toString(), item);

				for (Class<?> referencedClass : getOtherReferencedClasses(clazz)) {
					idsMapping.get(referencedClass.getName()).put(importId.toString(), item);
				}
			}

			LOGGER.info("Imported " + idsMappingForClass.size() + " objects for class: " + clazz.getSimpleName());
		} else {
			LOGGER.info("Nothing to do for class: " + clazz.getSimpleName());
		}
	}

	@Override
	protected <E extends GenericEntity<Long, ?>> void doFilterLine(Class<E> clazz, Map<String, Object> line) {
		super.doFilterLine(clazz, line);
		Date creationDate = new Date();
		if (!line.containsKey(AUDIT_SUMMARY_CREATION_DATE_FIELD_NAME)) {
			line.put(AUDIT_SUMMARY_CREATION_DATE_FIELD_NAME, creationDate);
		}
		if (!line.containsKey(AUDIT_SUMMARY_LAST_EDIT_DATE_FIELD_NAME)) {
			line.put(AUDIT_SUMMARY_LAST_EDIT_DATE_FIELD_NAME, creationDate);
		}

		User creationAuthor = userService.getAuthenticatedUser();
		if (!line.containsKey(AUDIT_SUMMARY_CREATION_AUTHOR_FIELD_NAME)) {
			line.put(AUDIT_SUMMARY_CREATION_AUTHOR_FIELD_NAME, creationAuthor);
		}
		if (!line.containsKey(AUDIT_SUMMARY_LAST_EDIT_AUTHOR_FIELD_NAME)) {
			line.put(AUDIT_SUMMARY_LAST_EDIT_AUTHOR_FIELD_NAME, creationAuthor);
		}
	}

	protected GenericConversionService getConversionService(Workbook workbook,
			Map<String, Map<String, GenericEntity<Long, ?>>> idsMapping) {
		GenericConversionService service = new GenericConversionService();

		GenericEntityConverter genericEntityConverter = new GenericEntityConverter(importDataDao, workbook,
				new HashMap<Class<?>, Class<?>>(0), idsMapping);
		genericEntityConverter.setConversionService(service);
		service.addConverter(genericEntityConverter);

		service.addConverter(new ProjectConverter());
		service.addConverter(new ArtifactConverter());
		service.addConverter(new ExternalLinkWrapperConverter());

		DefaultConversionService.addDefaultConverters(service);

		return service;
	}

	protected <E extends GenericEntity<Long, ?>> void onAfterCreate(E item) {
		try {
			if (item instanceof Project) {
				Project project = (Project) item;
				for (Artifact artifact : project.getArtifacts()) {
					if (artifact != null) {
						artifact.setProject(project);
						artifactService.update(artifact);
					}
				}
			} else if (item instanceof ProjectVersion) {
				projectVersionService.linkWithArtifactVersions((ProjectVersion) item);
			}
		} catch (Exception e) {
			LOGGER.info("Unable to read/write the database after entity creation");
		}
	}

	// Custom converters

	private class ProjectConverter implements GenericConverter {

		@Override
		public Set<ConvertiblePair> getConvertibleTypes() {
			Set<ConvertiblePair> convertibleTypes = new LinkedHashSet<ConvertiblePair>();

			convertibleTypes.add(new ConvertiblePair(String.class, Project.class));

			return Collections.unmodifiableSet(convertibleTypes);
		}

		@Override
		public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
			if (StringUtils.hasText((String) source)) {
				return projectService.getByUri((String) source);
			}
			return null;
		}
	}

	private class ArtifactConverter implements GenericConverter {

		@Override
		public Set<ConvertiblePair> getConvertibleTypes() {
			Set<ConvertiblePair> convertibleTypes = new LinkedHashSet<ConvertiblePair>();

			convertibleTypes.add(new ConvertiblePair(String.class, Artifact.class));

			return Collections.unmodifiableSet(convertibleTypes);
		}

		@Override
		public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
			if (StringUtils.hasText((String) source)) {
				ArtifactKey artifactKey = new ArtifactKey((String) source);
				try {
					return artifactService.getOrCreate(artifactKey);
				} catch (Exception e) {
					LOGGER.error("An error occurred while creating artifact " + artifactKey.getKey(), e);
				}
			}
			return null;
		}
	}

	private class ExternalLinkWrapperConverter implements GenericConverter {

		@Override
		public Set<ConvertiblePair> getConvertibleTypes() {
			Set<ConvertiblePair> convertibleTypes = new LinkedHashSet<ConvertiblePair>();

			convertibleTypes.add(new ConvertiblePair(String.class, ExternalLinkWrapper.class));

			return Collections.unmodifiableSet(convertibleTypes);
		}

		@Override
		public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
			if (StringUtils.hasText((String) source)) {
				return new ExternalLinkWrapper((String) source);
			}
			return null;
		}
	}
}

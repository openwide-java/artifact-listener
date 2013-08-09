package fr.openwide.maven.artifact.notifier.core.business.project.dao;

import java.util.List;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.ImmutableList;

import fr.openwide.core.jpa.business.generic.dao.GenericEntityDaoImpl;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.jpa.search.service.IHibernateSearchService;
import fr.openwide.core.spring.util.StringUtils;
import fr.openwide.maven.artifact.notifier.core.business.project.model.Project;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;

@Repository("projectDao")
public class ProjectDaoImpl extends GenericEntityDaoImpl<Long, Project> implements IProjectDao {
	
	@Autowired
	private IHibernateSearchService hibernateSearchService;
	
	@Override
	public List<Project> searchAutocomplete(String searchPattern, Integer limit, Integer offset) throws ServiceException {
		String[] searchFields = new String[] {
				Binding.project().name().getPath()
		};
		
		List<SortField> sortFields = ImmutableList.<SortField>builder()
				.add(new SortField(Project.NAME_SORT_FIELD_NAME, SortField.STRING))
				.build();
		Sort sort = new Sort(sortFields.toArray(new SortField[sortFields.size()]));
		return hibernateSearchService.searchAutocomplete(getObjectClass(), searchFields, searchPattern, limit, offset, sort);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Project> searchByName(String searchTerm, Integer limit, Integer offset) {
		FullTextQuery query = getSearchByNameQuery(searchTerm);
		
		// Sort
		List<SortField> sortFields = ImmutableList.<SortField>builder()
				.add(new SortField(Binding.project().getPath() + '.' + Project.NAME_SORT_FIELD_NAME, SortField.STRING))
				.build();
		query.setSort(new Sort(sortFields.toArray(new SortField[sortFields.size()])));
		
		if (offset != null) {
			query.setFirstResult(offset);
		}
		if (limit != null) {
			query.setMaxResults(limit);
		}
		
		return (List<Project>) query.getResultList();
	}
	
	@Override
	public int countSearchByName(String searchTerm) {
		return getSearchByNameQuery(searchTerm).getResultSize();
	}
	
	private FullTextQuery getSearchByNameQuery(String searchTerm) {
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(getEntityManager());
		
		QueryBuilder projectQueryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
				.forEntity(Project.class).get();
		
		BooleanJunction<?> booleanJunction = projectQueryBuilder.bool();
		if (StringUtils.hasText(searchTerm)) {
			booleanJunction.must(projectQueryBuilder
					.keyword()
					.fuzzy().withPrefixLength(1).withThreshold(0.8F)
					.onField(Binding.project().name().getPath())
					.matching(searchTerm)
					.createQuery());
		} else {
			booleanJunction.must(projectQueryBuilder.all().createQuery());
		}
		
		return fullTextEntityManager.createFullTextQuery(booleanJunction.createQuery(), Project.class);
	}
}
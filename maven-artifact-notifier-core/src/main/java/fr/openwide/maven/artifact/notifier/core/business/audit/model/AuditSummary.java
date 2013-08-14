package fr.openwide.maven.artifact.notifier.core.business.audit.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.bindgen.Bindable;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Resolution;

import fr.openwide.core.commons.util.CloneUtils;
import fr.openwide.core.jpa.search.util.HibernateSearchAnalyzer;
import fr.openwide.maven.artifact.notifier.core.business.user.model.User;

@Embeddable
@Bindable
public class AuditSummary implements Serializable {

	private static final long serialVersionUID = 2489494492556206554L;
	
	public static final String CREATION_DATE_SORT = "creationDateSort";
	public static final String LAST_EDIT_DATE_SORT = "lastEditDateSort";
	
	@Column(nullable = false)
	@DateBridge(resolution = Resolution.MILLISECOND)
	@Fields({ @Field(analyzer = @Analyzer(definition = HibernateSearchAnalyzer.KEYWORD)),
			@Field(name = CREATION_DATE_SORT, analyzer = @Analyzer(definition = HibernateSearchAnalyzer.TEXT_SORT)) })
	private Date creationDate;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private User creationAuthor;
	
	@Column(nullable = false)
	@DateBridge(resolution = Resolution.MILLISECOND)
	@Fields({ @Field(analyzer = @Analyzer(definition = HibernateSearchAnalyzer.KEYWORD)),
			@Field(name = LAST_EDIT_DATE_SORT, analyzer = @Analyzer(definition = HibernateSearchAnalyzer.TEXT_SORT)) })
	private Date lastEditDate;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private User lastEditAuthor;

	public AuditSummary() {
		super();
	}

	public Date getCreationDate() {
		return CloneUtils.clone(creationDate);
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = CloneUtils.clone(creationDate);
	}

	public User getCreationAuthor() {
		return creationAuthor;
	}

	public void setCreationAuthor(User creationAuthor) {
		this.creationAuthor = creationAuthor;
	}

	public Date getLastEditDate() {
		return CloneUtils.clone(lastEditDate);
	}

	public void setLastEditDate(Date lastEditDate) {
		this.lastEditDate = CloneUtils.clone(lastEditDate);
	}

	public User getLastEditAuthor() {
		return lastEditAuthor;
	}

	public void setLastEditAuthor(User lastEditAuthor) {
		this.lastEditAuthor = lastEditAuthor;
	}
}
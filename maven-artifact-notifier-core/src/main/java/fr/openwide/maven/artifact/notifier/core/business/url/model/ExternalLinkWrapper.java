package fr.openwide.maven.artifact.notifier.core.business.url.model;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.bindgen.Bindable;
import org.hibernate.search.annotations.DocumentId;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.openwide.core.jpa.business.generic.model.GenericEntity;

@Bindable
@Cacheable
@Entity
public class ExternalLinkWrapper extends GenericEntity<Long, ExternalLinkWrapper> {

	private static final long serialVersionUID = -4558332826839419557L;

	@Id
	@GeneratedValue
	@DocumentId
	private Long id;
	
	@Column(nullable = false)
	private String url;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ExternalLinkStatus status = ExternalLinkStatus.ONLINE;
	
	@Column(nullable = false)
	private int consecutiveFailures;
	
	@Column
	private Integer lastStatusCode;
	
	@Column
	private Date lastCheckDate;
	
	protected ExternalLinkWrapper() {
	}
	
	public ExternalLinkWrapper(String url) {
		this.url = url;
	}
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public ExternalLinkStatus getStatus() {
		return status;
	}
	
	public void setStatus(ExternalLinkStatus status) {
		this.status = status;
	}
	
	public int getConsecutiveFailures() {
		return consecutiveFailures;
	}
	
	public void setConsecutiveFailures(int consecutiveFailures) {
		this.consecutiveFailures = consecutiveFailures;
	}
	
	public Integer getLastStatusCode() {
		return lastStatusCode;
	}
	
	public void setLastStatusCode(Integer lastStatusCode) {
		this.lastStatusCode = lastStatusCode;
	}
	
	public Date getLastCheckDate() {
		return lastCheckDate;
	}
	
	public void setLastCheckDate(Date lastCheckDate) {
		this.lastCheckDate = lastCheckDate;
	}

	@Override
	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	public String getNameForToString() {
		return url;
	}

	@Override
	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	public String getDisplayName() {
		return url;
	}
	
	@Override
	public int compareTo(ExternalLinkWrapper other) {
		if (this.equals(other)) {
			return 0;
		}
		return this.url.compareTo(other.getUrl());
	}
}

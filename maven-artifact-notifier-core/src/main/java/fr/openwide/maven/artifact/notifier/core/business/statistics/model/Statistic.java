package fr.openwide.maven.artifact.notifier.core.business.statistics.model;

import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.bindgen.Bindable;
import org.hibernate.search.annotations.DocumentId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

import fr.openwide.core.jpa.business.generic.model.GenericEntity;

@Bindable
@Entity
public class Statistic extends GenericEntity<Long, Statistic> {

	private static final long serialVersionUID = -4172333476805159348L;

	@Id
	@GeneratedValue
	@DocumentId
	private Long id;
	
	@Column(unique = true, nullable = false)
	@Enumerated(EnumType.STRING)
	private StatisticEnumKey enumKey;
	
	@ElementCollection
	private List<Integer> data = Lists.newArrayList();
	
	protected Statistic() {
	}

	public Statistic(StatisticEnumKey enumKey) {
		this.enumKey = enumKey;
	}
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	public StatisticEnumKey getEnumKey() {
		return enumKey;
	}
	
	public void setEnumKey(StatisticEnumKey enumKey) {
		this.enumKey = enumKey;
	}

	public List<Integer> getData() {
		return Collections.unmodifiableList(data);
	}
	
	public void pushData(Integer value) {
		if (value != null) {
			data.add(value);
		}
	}
	
	public void popData() {
		data.remove(0);
	}

	public void setData(List<Integer> data) {
		this.data.clear();
		this.data.addAll(data);
	}
	
	// NOTE: It is assumed that the sum of the data elements will never overflow an integer
	public Integer getValue() {
		if (data.isEmpty()) {
			return 0; 
		}
		int movingAverage = 0;
		for (Integer value : data) {
			movingAverage += value;
		}
		return movingAverage / data.size();
	}

	@Override
	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	public String getNameForToString() {
		return getEnumKey().name();
	}

	@Override
	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	public String getDisplayName() {
		return getNameForToString();
	}
	
	public static enum StatisticEnumKey {
		NOTIFICATIONS_SENT_PER_DAY,
		VERSIONS_RELEASED_PER_DAY
	}
}

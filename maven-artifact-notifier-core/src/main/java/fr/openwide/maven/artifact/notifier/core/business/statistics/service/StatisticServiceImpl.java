package fr.openwide.maven.artifact.notifier.core.business.statistics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.business.generic.service.GenericEntityServiceImpl;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.statistics.dao.IStatisticDao;
import fr.openwide.maven.artifact.notifier.core.business.statistics.model.Statistic;
import fr.openwide.maven.artifact.notifier.core.business.statistics.model.Statistic.StatisticEnumKey;
import fr.openwide.maven.artifact.notifier.core.business.statistics.model.Statistic_;
import fr.openwide.maven.artifact.notifier.core.config.application.MavenArtifactNotifierConfigurer;

@Service("statisticService")
public class StatisticServiceImpl extends GenericEntityServiceImpl<Long, Statistic> implements IStatisticService {

	private IStatisticDao statisticDao;
	
	@Autowired
	private MavenArtifactNotifierConfigurer configurer;
	
	@Autowired
	public StatisticServiceImpl(IStatisticDao statisticDao) {
		super(statisticDao);
		this.statisticDao = statisticDao;
	}
	
	@Override
	public Statistic getByEnumKey(StatisticEnumKey enumKey) {
		if (enumKey == null) {
			return null;
		}
		Statistic result = statisticDao.getByField(Statistic_.enumKey, enumKey);
		return result;
	}
	
	@Override
	public void feed(StatisticEnumKey enumKey, Integer value) throws ServiceException, SecurityServiceException {
		Statistic statistic = getByEnumKey(enumKey);
		if (statistic == null) {
			statistic = new Statistic(enumKey);
			create(statistic);
		}
		statistic.pushData(value);
		while (statistic.getData().size() >= configurer.getAverageDataRange()) {
			statistic.popData();
		}
		update(statistic);
	}
}
package fr.openwide.maven.artifact.notifier.core.business.statistics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.core.jpa.business.generic.service.GenericEntityServiceImpl;
import fr.openwide.maven.artifact.notifier.core.business.statistics.dao.IStatisticDao;
import fr.openwide.maven.artifact.notifier.core.business.statistics.model.Statistic;
import fr.openwide.maven.artifact.notifier.core.business.statistics.model.Statistic.StatisticEnumKey;
import fr.openwide.maven.artifact.notifier.core.business.statistics.model.Statistic_;

@Service("statisticService")
public class StatisticServiceImpl extends GenericEntityServiceImpl<Long, Statistic> implements IStatisticService {

	private IStatisticDao statisticDao;
	
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
		if (result == null) {
			throw new IllegalStateException(String.format("%s manquant pour l'enumKey %s", Statistic.class.getSimpleName(), enumKey));
		}
		return result;
	}
}

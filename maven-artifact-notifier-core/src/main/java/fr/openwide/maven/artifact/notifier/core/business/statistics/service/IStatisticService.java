package fr.openwide.maven.artifact.notifier.core.business.statistics.service;

import fr.openwide.core.jpa.business.generic.service.IGenericEntityService;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.maven.artifact.notifier.core.business.statistics.model.Statistic;
import fr.openwide.maven.artifact.notifier.core.business.statistics.model.Statistic.StatisticEnumKey;

public interface IStatisticService extends IGenericEntityService<Long, Statistic> {

	Statistic getByEnumKey(StatisticEnumKey enumKey);

	void feed(StatisticEnumKey enumKey, Integer value) throws ServiceException, SecurityServiceException;
}

package fr.openwide.maven.artifact.notifier.core.business.statistics.dao;

import org.springframework.stereotype.Repository;

import fr.openwide.core.jpa.business.generic.dao.GenericEntityDaoImpl;
import fr.openwide.maven.artifact.notifier.core.business.statistics.model.Statistic;

@Repository("statisticDao")
public class StatisticDaoImpl extends GenericEntityDaoImpl<Long, Statistic> implements IStatisticDao {

}

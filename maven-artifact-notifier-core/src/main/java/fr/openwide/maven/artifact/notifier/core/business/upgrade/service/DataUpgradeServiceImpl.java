package fr.openwide.maven.artifact.notifier.core.business.upgrade.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import fr.openwide.core.jpa.more.business.upgrade.model.IDataUpgrade;
import fr.openwide.core.jpa.more.business.upgrade.service.AbstractDataUpgradeServiceImpl;

@Service("dataUpgradeServiceImpl")
public class DataUpgradeServiceImpl extends AbstractDataUpgradeServiceImpl {

	@Override
	public List<IDataUpgrade> listDataUpgrades() {
		return Lists.newArrayListWithCapacity(0);
	}
}

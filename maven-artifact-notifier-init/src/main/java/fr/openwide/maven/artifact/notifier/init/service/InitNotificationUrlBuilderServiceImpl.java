package fr.openwide.maven.artifact.notifier.init.service;

import org.springframework.stereotype.Service;

import fr.openwide.maven.artifact.notifier.core.business.notification.service.EmptyNotificationUrlBuilderServiceImpl;

/**
 * Implémentation bouche-trou, uniquement pour combler la dépendance.
 */
@Service("initNotificationUrlBuilderService")
public class InitNotificationUrlBuilderServiceImpl extends EmptyNotificationUrlBuilderServiceImpl {

}

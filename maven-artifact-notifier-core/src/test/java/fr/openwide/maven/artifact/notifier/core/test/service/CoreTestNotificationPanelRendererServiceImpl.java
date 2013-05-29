package fr.openwide.maven.artifact.notifier.core.test.service;

import org.springframework.stereotype.Service;

import fr.openwide.maven.artifact.notifier.core.business.notification.service.EmptyNotificationPanelRendererServiceImpl;

/**
 * Implémentation bouche-trou, uniquement pour combler la dépendance.
 */
@Service("coreTestNotificationPanelRendererService")
public class CoreTestNotificationPanelRendererServiceImpl extends EmptyNotificationPanelRendererServiceImpl {

}

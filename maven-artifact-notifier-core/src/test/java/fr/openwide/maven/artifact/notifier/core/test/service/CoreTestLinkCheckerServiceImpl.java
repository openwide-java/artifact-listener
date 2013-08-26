package fr.openwide.maven.artifact.notifier.core.test.service;

import org.springframework.stereotype.Service;

import fr.openwide.maven.artifact.notifier.core.business.url.service.EmptyLinkCheckerServiceImpl;

/**
 * Implémentation bouche-trou, uniquement pour combler la dépendance.
 */
@Service("coreTestLinkCheckerService")
public class CoreTestLinkCheckerServiceImpl extends EmptyLinkCheckerServiceImpl {

}

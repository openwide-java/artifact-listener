package fr.openwide.maven.artifact.notifier.init.service;

import org.springframework.stereotype.Service;

import fr.openwide.maven.artifact.notifier.core.business.url.service.EmptyLinkCheckerServiceImpl;

/**
 * Implémentation bouche-trou, uniquement pour combler la dépendance.
 */
@Service("initLinkCheckerService")
public class InitLinkCheckerServiceImpl extends EmptyLinkCheckerServiceImpl {

}

package fr.openwide.maven.artifact.notifier.core.business.search.service;

import java.io.File;

import fr.openwide.maven.artifact.notifier.core.business.search.model.PomBean;

public interface IPomParserService {

	PomBean parse(String xml);

	PomBean parse(File in);
}

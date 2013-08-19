package fr.openwide.maven.artifact.notifier.core.util.init.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface IProjectImportDataService {

	void importProjects(File file) throws FileNotFoundException, IOException;
}
package fr.openwide.maven.artifact.notifier.core.business.search.service;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import fr.openwide.core.spring.util.StringUtils;
import fr.openwide.maven.artifact.notifier.core.business.search.model.ArtifactBean;
import fr.openwide.maven.artifact.notifier.core.business.search.model.PomBean;

@Service("pomParserService")
public class PomParserServiceImpl implements Serializable, IPomParserService {

	private static final long serialVersionUID = 6383714945788645661L;

	private static final Logger LOGGER = LoggerFactory.getLogger(PomParserServiceImpl.class);
	
	private static final String POM_ENCODING = "UTF-8";
	
	@Override
	public PomBean parse(String xml) {
		if (StringUtils.hasText(xml)) {
			Document doc = Jsoup.parse(xml);
			return parse(doc);
		}
		return null;
	}
	
	@Override
	public PomBean parse(File in) {
		try {
			Document doc = Jsoup.parse(in, POM_ENCODING);
			return parse(doc);
		} catch (IOException e) {
			LOGGER.error("Unable to parse " + in.getName() + " file", e);
		}
		return null;
	}
	
	private String getGroupId(Document doc) {
		Elements groupIdElements = doc.select("project > groupId");
		if (groupIdElements != null && groupIdElements.size() == 1) {
			return groupIdElements.get(0).text();
		}
		groupIdElements = doc.select("project > parent > groupId");
		if (groupIdElements != null && groupIdElements.size() == 1) {
			return groupIdElements.get(0).text();
		}
		return null;
	}
	
	private String getArtifactId(Document doc) {
		Elements artificatIdElements = doc.select("project > artifactId");
		if (artificatIdElements != null && artificatIdElements.size() == 1) {
			return artificatIdElements.get(0).text();
		}
		return null;
	}
	
	private List<ArtifactBean> populate(Elements elements) {
		List<ArtifactBean> artifactList = Lists.newArrayList();
		for (Element dependency : elements) {
			
			ArtifactBean artifactBean = new ArtifactBean();
			for (Element child : dependency.children()) {
				if (child.nodeName().compareTo("groupid") == 0) {
					artifactBean.setGroupId(child.text());
				} else if (child.nodeName().compareTo("artifactid") == 0) {
					artifactBean.setArtifactId(child.text());
				} else if (child.nodeName().compareTo("type") == 0) {
					artifactBean.setType(child.text());
				}
			}
			
			if (StringUtils.hasText(artifactBean.getGroupId()) && StringUtils.hasText(artifactBean.getArtifactId())) {
				artifactBean.setId(artifactBean.getGroupId() + ":" + artifactBean.getArtifactId());
				artifactList.add(artifactBean);
			}
		}
		
		Collections.sort(artifactList);
		
		return artifactList;
	}
	
	private PomBean parse(Document doc) {
		PomBean pomBean = new PomBean();
		
		pomBean.setGroupId(getGroupId(doc));
		pomBean.setArtifactId(getArtifactId(doc));
		pomBean.setDependencies(populate(doc.select("dependency:not(dependencyManagement dependencies dependency)")));
		pomBean.setDependencyManagement(populate(doc.select("dependencyManagement > dependencies > dependency")));
		pomBean.setPlugins(populate(doc.select("plugin:not(pluginManagement plugins plugin)")));
		pomBean.setPluginManagement(populate(doc.select("pluginManagement > plugins > plugin")));
		
		return pomBean;
	}
	
}

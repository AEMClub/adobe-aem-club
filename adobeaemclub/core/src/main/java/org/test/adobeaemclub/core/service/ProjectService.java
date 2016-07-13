package org.test.adobeaemclub.core.service;

import org.apache.sling.api.resource.ResourceResolver;

import com.adobe.cq.projects.api.Project;

public interface ProjectService {
	
	public void createProject();
	
	public void deleteProject();
	
	public Project getProject(ResourceResolver rr);

}

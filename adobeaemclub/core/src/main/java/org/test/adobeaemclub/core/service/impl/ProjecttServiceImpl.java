/**
 * 
 */
package org.test.adobeaemclub.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.test.adobeaemclub.core.service.ProjectService;

import com.adobe.cq.projects.api.Project;
import com.adobe.cq.projects.api.ProjectFilter;
import com.adobe.cq.projects.api.ProjectManager;

/**
 * @author Loki
 *
 */
@Component(immediate=true)
@Service
public class ProjecttServiceImpl implements ProjectService {

	/* (non-Javadoc)
	 * @see org.test.adobeaemclub.core.service.ProjectService#createProject()
	 *  
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Reference
	ResourceResolverFactory rrfactory;
	
	HashMap<String, Object> map = new HashMap<String, Object>();
	ResourceResolver rr = null;
	
	@Activate
    protected void activate(final Map<String, Object> config) {
		map.put(ResourceResolverFactory.SUBSERVICE, "writeService");	
		deleteProject();
		createProject();		
    }
	
	
	@Override
	public void createProject() {		
		try {
			rr = rrfactory.getServiceResourceResolver(map);
			ProjectManager pm = rr.adaptTo(ProjectManager.class);
			Project project = pm.createProject("simpleProject1", "Simple Project1", "//libs//cq//core//content//projects//templates//default");
			project.setDescription("This Project is created programmatically");			
			rr.commit();			
			logger.info("Project Created Successfully.....");
			logger.info("Project Title : " + project.getTitle());
			logger.info("Project Description : " + project.getDescription());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rr.isLive())
				rr.close();
		}


	}

	/* (non-Javadoc)
	 * @see org.test.adobeaemclub.core.service.ProjectService#deleteProject()
	 */
	@Override
	public void deleteProject() {
		try {
			rr = rrfactory.getServiceResourceResolver(map);
			ProjectManager pm = rr.adaptTo(ProjectManager.class);
			Project delProj = getProject(rr);
			pm.deleteProject(delProj);				
			rr.commit();			
			logger.info("Project Deleted Successfully ... ");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rr.isLive())
				rr.close();
		}

	}

	/* (non-Javadoc)
	 * @see org.test.adobeaemclub.core.service.ProjectService#getProjects()
	 */
	@Override
	public Project getProject(ResourceResolver rr) {
		
			Project delProj = null;
			ProjectManager pm = rr.adaptTo(ProjectManager.class);
			ProjectFilter filters = new ProjectFilter();
			List<String> templates = new ArrayList<String>();
			templates.add("/libs/cq/core/content/projects/templates/default");
			filters.setProjectTemplates(templates);
			//filters.setActive(true); To get all the active projects
			Iterator<Project> projects = pm.getProjects(filters, 0, 10);
			while (projects.hasNext()) {
				Project project = projects.next();
				if(project.getTitle().equals("Simple Project1")){
					delProj = project;
					logger.info("Found a Project... ");
				}
			}	
		
		return delProj;
	}
	
	
}

package org.test.adobeaemclub.core.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.test.adobeaemclub.core.service.MsmExample;

import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;


@Component(immediate=true)
@Service
public class MsmExampleImpl implements MsmExample{
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Reference
	LiveRelationshipManager liveRelManager;
	
	@Reference
	ResourceResolverFactory resolverFactory;
	
	@Activate
    protected void activate(final Map<String, Object> config) {
		logger.info("Inside MSM Example Impl class");
		//getLiveCopyStatus();
    }
	
	public void getLiveCopyStatus(){
		logger.info("Inside before try LiveCopy Status MSM Example Impl class");
		try {
			Map<String, Object> param = new HashMap<String, Object>();
	        param.put(ResourceResolverFactory.SUBSERVICE, "readService");
			logger.info("Inside LiveCopy Status MSM Example Impl class");
			//please dont use getAdministrativeResourceResolver. This is just for example
			//ResourceResolver resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);
			ResourceResolver resourceResolver = resolverFactory.getServiceResourceResolver(param);
			/*logger.info("UserId : " + resourceResolver.getUserID());
			Session session = resourceResolver.adaptTo(Session.class);
			
			Node node = session.getNode("/content/geometrixx-outdoors-mobile/en/user");
			logger.info(node.getName());*/
			
			Resource res = resourceResolver.getResource("/content/geometrixx-outdoors-mobile/en/user"); 
		    LiveRelationship liveRelationship = liveRelManager.getLiveRelationship(res, true);		
			 Map< String, Boolean > map = liveRelationship.getStatus().getAdvancedStatus();
			 for (Entry< String, Boolean > element: map.entrySet()) {
			  logger.info("Key is: " + element.getKey());
			  logger.info("Value is: " + element.getValue());
		 }
	}catch (Exception e){
		 logger.error("Error:", e.getStackTrace());
		 
	 }

}
}

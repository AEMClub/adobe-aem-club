package org.adoeaemclub.contexthubExample.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.adoeaemclub.contexthubExample.core.service.ContextHubService;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.contexthub.api.ContextHub;
import com.adobe.granite.contexthub.api.Store;

@Component(immediate=true)
@Service
public class ContextHubServiceImpl implements ContextHubService {
	
private final Logger logger = LoggerFactory.getLogger(ContextHubServiceImpl.class);

@Reference
ContextHub contexthub;

@Reference
ResourceResolverFactory resolverFactory;
	
	@Activate
    protected void activate(final Map<String, Object> config) {
		logger.info("Inside activate method");
		List<Store> stores = new ArrayList<Store>();
		ResourceResolver rr = null;
		try {
			rr = resolverFactory.getAdministrativeResourceResolver(null);
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
			e.printStackTrace();
		}
		Resource res = rr.getResource("/content/geometrixx-outdoors/en.html");
		stores = contexthub.getEnabledStores(res);
		logger.info("Size of stores list :" + stores.size());
		
	}

}

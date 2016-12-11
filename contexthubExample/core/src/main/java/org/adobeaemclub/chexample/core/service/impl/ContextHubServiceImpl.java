package org.adobeaemclub.chexample.core.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adobeaemclub.chexample.core.service.ContextHubService;
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
import com.adobe.granite.contexthub.api.Mode;
import com.adobe.granite.contexthub.api.Module;
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
		List<Module> modules = new ArrayList<Module>();
		List<Mode> modes = new ArrayList<Mode>();
		ResourceResolver rr = null;
		try {
			rr = resolverFactory.getAdministrativeResourceResolver(null);
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
			e.printStackTrace();
		}
		logger.info("RR : " + rr.getUserID());
		Resource res = rr.getResource("/etc/cloudsettings/default/contexthub");
		logger.info("Resource : " + res.getName());
		stores = contexthub.findStores(res);
		logger.info("Size of stores list :" + stores.size());
		logger.info("---------Store----------");
		for (Iterator<Store> iterator = stores.iterator(); iterator.hasNext();) {
			Store store = (Store) iterator.next();
			logger.info(store.getName());			
		}
		modules = contexthub.findModules(res);
		logger.info("Size of modules list :" + modules.size());
		logger.info("---------Modules----------");
		for (Iterator<Module> iterator = modules.iterator(); iterator.hasNext();) {
			Module module = (Module) iterator.next();
			logger.info(module.getName());			
		}
		modes = contexthub.findModes(res);
		logger.info("Size of modes list :" + modes.size());
		logger.info("---------Mode----------");
		for (Iterator<Mode> iterator = modes.iterator(); iterator.hasNext();) {
			Mode mode = (Mode) iterator.next();
			logger.info(mode.getName());			
		}
		
	}

}

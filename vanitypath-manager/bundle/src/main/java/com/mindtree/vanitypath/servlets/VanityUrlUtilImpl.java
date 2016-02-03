package com.mindtree.vanitypath.servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.DynamicOperand;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelConstants;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;
import javax.jcr.query.qom.StaticOperand;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.TidyJSONWriter;
import com.day.cq.wcm.api.Page;

@SlingServlet(paths={"/services/vanityurlcheck"}, methods={"GET","POST"})
	public class VanityUrlUtilImpl extends SlingAllMethodsServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Logger log = LoggerFactory.getLogger(VanityUrlUtilImpl.class);
	
	String rootPath = "/content/";
	
	@Override
	public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException{ 

		log.info("####### Inside VanityUrlUtilImpl ########");		
		String check = request.getParameter("validate");
		if(check != null && check.equals("true")){			
				getVanityPath(request, response);				
		}else{
				getAllVanityPaths(request, response); 	
		}
		
	}


	private void getAllVanityPaths(SlingHttpServletRequest request, SlingHttpServletResponse response){

		Session session = request.getResourceResolver().adaptTo(Session.class);
		ResourceResolver rr = request.getResourceResolver();
		
		try{
			NodeIterator nodes = getVanityPaths(session, rootPath, null);
			HashMap<String,String> duplicateVanityList = getDuplicateVanity(session, rr);
	
			TidyJSONWriter tidyJSONWriter = new TidyJSONWriter(response.getWriter());			 
			tidyJSONWriter.object();
	
			for (int i = 0; i < nodes.getSize(); i++) {
				Node node = nodes.nextNode();
				Page page = getPageObject(rr, node);
				String site = getSite(page);
				
			    tidyJSONWriter.key(page.getName());			    
			    tidyJSONWriter.object();
			    
			    if(!duplicateVanityList.isEmpty()){
			    	log.info("####### duplicate Key ######## : "+ duplicateVanityList.get(site+page.getVanityUrl()));
			        if(duplicateVanityList.containsKey(site+page.getVanityUrl())){
			        	tidyJSONWriter.key("isVanityDup").value("true");
					}else{
						tidyJSONWriter.key("isVanityDup").value("false");
					}
			    }else{
			    	tidyJSONWriter.key("isVanityDup").value("false");
			    }
			    tidyJSONWriter.key("Site").value(site);
				tidyJSONWriter.key("Title").value(page.getTitle());
				tidyJSONWriter.key("Path").value(page.getPath());
				tidyJSONWriter.key("VanityUrl");
				tidyJSONWriter.array();
				String vanityUrls = "";				
				Property vanityPath = node.getProperty("sling:vanityPath");				
				if(vanityPath.isMultiple()){
					Value[] values = vanityPath.getValues();
					for (int j = 0; j < values.length; j++) {
						log.info("####### vanityUrls ######## : "+ values.length + j);
						tidyJSONWriter.value(values[j]);
						vanityUrls = vanityUrls+values[j]+",";
					}					
				}else{
					vanityUrls = page.getVanityUrl();
					tidyJSONWriter.value(page.getVanityUrl());
				}
				
				tidyJSONWriter.endArray();
				
				String redirectType = null;
				if(node.hasProperty("sling:redirect")){
					String redirect = node.getProperty("sling:redirect").getString();
					log.info("####### redirect ######## : "+ redirect);
					if(node.hasProperty("sling:redirectStatus")){
						String redirectStatus = node.getProperty("sling:redirectStatus").getString();
						log.info("####### redirectStatus ######## : "+ redirectStatus);
						redirectType = redirectStatus;
					}else{
						redirectType = "301";
					}
				}else{
					redirectType = "vanity";
				}					
				
				tidyJSONWriter.key("RedirectType").value(redirectType);
				
			    tidyJSONWriter.endObject();				
			}
	
			tidyJSONWriter.endObject();
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
		}catch(Exception e){
			log.error(e.getMessage());
		}finally{
			if(session.isLive()){
				session.logout();
			}
			if(rr.isLive()){
				rr.close();
			}
		}	
	}
	

	private void getVanityPath(SlingHttpServletRequest request, SlingHttpServletResponse response){
		Session session = request.getResourceResolver().adaptTo(Session.class);  
		try{                      
            log.info("Session UserId : "+  session.getUserID());
            final String vanityPath = request.getParameter("vanityPath");
            final String pagePath = request.getParameter("pagePath");
            log.info("vanity path parameter passed is {}", vanityPath);
            log.info("page path parameter passed is {}", pagePath);
            try {

                NodeIterator nodes = getVanityPaths(session, rootPath, vanityPath);
 
                TidyJSONWriter tidyJSONWriter = new TidyJSONWriter(response.getWriter()); 
                tidyJSONWriter.object(); 
                tidyJSONWriter.key("vanitypaths").array(); 
                String pageSite = getSite(pagePath);;
                String nodeSite = null;
                while (nodes.hasNext()) {
                    Node node = nodes.nextNode();
                    log.info("Node path is {}", node.getPath());
                    log.info("Page path is {}", pagePath);
                    if(node != null && node.getPath().contains("/content"))
                    {
                        // check whether the path of the page where the vanity path is defined matches the dialog's path
                        // which means that the vanity path is legal.
                    	nodeSite = getSite(node.getPath());
                        if(node.getPath().equals(pagePath) || !(nodeSite.equals(pageSite)))
                        {
                            //do not add that to the list
                        	log.info("Node path is {}", node.getPath());
                        	log.info("Page path is {}", pagePath);
                        } else {
                            tidyJSONWriter.value(node.getPath());
                        }
                    }
                }
 
                tidyJSONWriter.endArray();
                tidyJSONWriter.endObject();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
            }
            catch(RepositoryException re){
            	log.error( "Error in doGet", re );
            }
        }catch (JSONException e) {
        	log.error( "Error in doGet", e );
        }catch(Exception e){
			log.error(e.getMessage());
		}finally{
			if(session.isLive()){
				session.logout();
			}
			
		}	
	}

	/**
	 * @param page
	 * @return
	 */
	private String getSite(Page page) {
		String[] sites = page.getPath().split("/");
		String site = sites[2];
		log.info("### site  : " + site);
		return site;
	}
	
	/**
	 * @param page
	 * @return
	 */
	private String getSite(String pagePath) {
		String[] sites = pagePath.split("/");
		String site = sites[2];
		log.info("### site  : " + site);
		return site;
	}
	
	/**
	 * @param rr
	 * @param node
	 * @return
	 * @throws RepositoryException
	 */
	private Page getPageObject(ResourceResolver rr, Node node) throws RepositoryException {
		log.info("##### NOde Path ####### : " + node.getPath() + " : " + node.getPath().replace("/jcr:content", ""));
		Resource resource = rr.getResource(node.getPath().replace("/jcr:content", ""));
		Page page = resource.adaptTo(Page.class);
		return page;
	}

	private HashMap<String, String> getDuplicateVanity(Session session, ResourceResolver rr) throws RepositoryException {
		HashMap<String, String> duplicateVanity = new HashMap<String, String>();
		HashMap<String, String> map = new HashMap<String, String>();
		NodeIterator itr = getVanityPaths(session, rootPath, null);
		for (int i = 0; i < itr.getSize(); i++) {
			Node node = itr.nextNode();
			Page page = getPageObject(rr, node);
			String site = getSite(page);
			if(map.containsKey(site+page.getVanityUrl())){
				duplicateVanity.put(site+page.getVanityUrl(), page.getVanityUrl());
			}else{
				map.put(site+page.getVanityUrl(), page.getVanityUrl());
			}					
		}
		return duplicateVanity;
	}

	/**
	 * @param session
	 * @return
	 * @throws RepositoryException
	 * @throws PathNotFoundException
	 * @throws UnsupportedRepositoryOperationException
	 * @throws InvalidQueryException
	 */
	private NodeIterator getVanityPaths(Session session, String path, String vanityPath) throws RepositoryException, PathNotFoundException,
			UnsupportedRepositoryOperationException, InvalidQueryException {
		log.info("####### session ######### : " + session.getUserID());
		Node root = session.getRootNode();
		Node currentNode = root.getNode("content");
		QueryObjectModelFactory qf = currentNode.getSession().getWorkspace().getQueryManager().getQOMFactory();
		Selector selector = qf.selector("nt:base", "s");		
		Constraint constriant = qf.descendantNode("s", path);
		constriant = qf.and(constriant, qf.propertyExistence("s", "sling:vanityPath"));
		if(vanityPath != null){
			ValueFactory valueFactory = session.getValueFactory();
			String operator  = QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO; 
			DynamicOperand dynOperand  = qf.propertyValue("s", "sling:vanityPath"); 
		    StaticOperand statOperand  = qf.literal(valueFactory.createValue(vanityPath)); 
			constriant = qf.and(constriant, qf.comparison(dynOperand , operator, statOperand));	
		}
		QueryObjectModel qm = qf.createQuery(selector, constriant, null, null);
		log.info("######### Query ######### : " + qm.getStatement());
		NodeIterator nodes = qm.execute().getNodes();
		log.info("### query result : " + nodes.getSize());
		return nodes;
	}
	
	@Override
	public void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException{ 
		String operation = request.getParameter("op");
		String path = request.getParameter("path");
		String pageSite = getSite(path);
		log.error("######## path ######## : " + path);	
		Session session = null;
		try{
			TidyJSONWriter tidyJSONWriter = new TidyJSONWriter(response.getWriter());			 
            tidyJSONWriter.object();
            session=request.getResourceResolver().adaptTo(Session.class);
            ValueFactory valueFactory = session.getValueFactory();
			//session = repo.login(new SimpleCredentials("admin", "admin".toCharArray()));
			if(operation.equals("update")){
				String vanityurl = request.getParameter("vanityurl");
				String redirectType = request.getParameter("redirectType");
				log.error("######## vanityurl ########" + vanityurl + " : " + request.getParameter("vanityurl"));	
				String site = rootPath+request.getParameter("site");
				String[] vanitypaths = vanityurl.split(",");
				Value[] values = new Value[vanitypaths.length];
				boolean isDup = false;
				String nodeSite = "";
				for (int i = 0; i < vanitypaths.length; i++) {
					values[i] = valueFactory.createValue(vanitypaths[i]);
					NodeIterator nodes = getVanityPaths(session, site, values[i].getString());
					if(nodes.getSize() > 0){
						 while (nodes.hasNext()) {
			                    Node node = nodes.nextNode();
			                    nodeSite = getSite(node.getPath());
			                    log.error("######## node path ######## : " + node.getPath());	
			                    if(!(node.getPath().replace("/jcr:content", "").equals(path))  && (nodeSite.equals(pageSite)))
			                    {
			                    	isDup = true;
			                    	log.error("######## Vanity Path Duplication ########");					
			                    	tidyJSONWriter.key("Error").value("This Vanity Path already exists in this Site");
			                    }
						 }
					}
				}				
				if(!isDup){
					updateVanityUrl(session, path, values, redirectType);
					tidyJSONWriter.key("Success").value("Added/Updated Successfully");
				}
				
			}else{
				deleteVanityUrl(session, path);
				tidyJSONWriter.key("Error").value("Deleted Successfully"); 
			}
			session.save();
			tidyJSONWriter.endObject();
			response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
			
		}catch(Exception e){
			log.error(e.getMessage());
		}finally{
			if(session.isLive()){
				session.logout();
			}
		}	
		
	}

	private void updateVanityUrl(Session session, String path, Value[] vanityurl, String redirectType) throws PathNotFoundException, RepositoryException {
		log.info("####### Inside updateVanityUrl ########");

			Node currentNode = session.getNode(path);
			currentNode.getNode("jcr:content").setProperty("sling:vanityPath", vanityurl);
			if(!redirectType.equals("1")){
				currentNode.getNode("jcr:content").setProperty("sling:redirect", true);
				currentNode.getNode("jcr:content").setProperty("sling:redirectStatus", redirectType);
			}
	
	}

	private void deleteVanityUrl(Session session, String path) throws  PathNotFoundException, RepositoryException {
		log.info("####### Inside deleteVanityUrl ########");
		String[] vanityUrls = null;
		Node currentNode = session.getNode(path);
		currentNode.getNode("jcr:content").setProperty("sling:vanityPath", vanityUrls);
		String v = null;
		if(currentNode.getNode("jcr:content").hasProperty("sling:redirect")){
			currentNode.getNode("jcr:content").setProperty("sling:redirect", v);
		}
		if(currentNode.getNode("jcr:content").hasProperty("sling:redirectStatus")){
			currentNode.getNode("jcr:content").setProperty("sling:redirectStatus", v);
		}	
		
			
		
	}
	

}

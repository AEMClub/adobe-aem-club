package com.mindtree.vanitypath.servlets;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.nodetype.NodeType;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.TidyJSONWriter;


//@Component(immediate=true)
//@Service
//public class VanityUrlUtilImpl implements VanityUrlUtil{
@SlingServlet(paths={"/services/siteslist"}, methods={"GET"})
	public class SitesListImpl extends SlingAllMethodsServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Reference
	private ResourceResolverFactory resolverFactory;
	
	@Reference
	private SlingRepository repo;
	
	private final Logger log = LoggerFactory.getLogger(SitesListImpl.class);
	
	@Override
	public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException{ 

		log.info("####### Inside SitesListImpl ########");
		Session session = null;
		TidyJSONWriter tidyJSONWriter = new TidyJSONWriter(response.getWriter());	

		try{
			tidyJSONWriter.array();			
			session = repo.login(new SimpleCredentials("admin", "admin".toCharArray()));
			Node root = session.getRootNode();
			Node currentNode = root.getNode("content");
			getNodes(currentNode, tidyJSONWriter);
			           
            tidyJSONWriter.endArray();
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
	
	public void getNodes(Node path, TidyJSONWriter tidyJSONWriter) {
		try{
			NodeIterator itr = path.getNodes();
			
			while (itr.hasNext()) {
				Node node = itr.nextNode();
				NodeType nt = node.getPrimaryNodeType();
				if(nt.isNodeType("cq:Page")){
					log.info(" Node Type : " + nt);
					log.info(" Node Path : " + node.getPath());
					
					String[] sites = node.getPath().split("/");
					String site = sites[2];
					
					tidyJSONWriter.object();
					
					tidyJSONWriter.key("title").value(node.getName());
					tidyJSONWriter.key("path").value(node.getPath());
					tidyJSONWriter.key("site").value(site);
					tidyJSONWriter.key("collapsed").value(true);
					tidyJSONWriter.key("child");
					tidyJSONWriter.array();
					if(node.hasNodes()){					
						getNodes(node, tidyJSONWriter);										
					}
					tidyJSONWriter.endArray();
					tidyJSONWriter.endObject();		
					
				}	
	        }
			
		}catch(Exception e){
				log.error(e.getMessage());
		}
	    
	}
		
	

}

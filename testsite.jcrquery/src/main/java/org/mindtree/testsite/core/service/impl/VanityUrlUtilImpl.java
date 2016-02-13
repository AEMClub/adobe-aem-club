package org.mindtree.testsite.core.service.impl;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.ValueFactory;
import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.DynamicOperand;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelConstants;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;
import javax.jcr.query.qom.StaticOperand;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.TidyJSONWriter;


//@Component(immediate=true)
//@Service
//public class VanityUrlUtilImpl implements VanityUrlUtil{
@SlingServlet(paths={"/services/vanityurlcheck"}, methods={"GET"})
	public class VanityUrlUtilImpl extends SlingAllMethodsServlet {
	
	@Reference
	private ResourceResolverFactory resolverFactory;
	
	@Reference
	private SlingRepository repo;
	
	private final Logger log = LoggerFactory.getLogger(VanityUrlUtilImpl.class);
	
	@Override
	public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException{ 
	//protected void activate(ComponentContext componentContext){
		log.info("####### Inside VanityUrlUtilImpl ########");
		Session session = null;
		ResourceResolver rr = null;
		
		final String vanityPath = request.getParameter("vanityPath");
        final String pagePath = request.getParameter("pagePath");
        log.info("###### request info : " + vanityPath + " : " + pagePath);
        
		try{
			
			/*Map<String, Object> map = new HashMap<String,Object>();
			map.put("CREDENTIALS", new SimpleCredentials("author", "author".toCharArray()));			
			rr = resolverFactory.getResourceResolver(map);
			log.info("####### rr ######## : "+ rr.getUserID());*/
			session = repo.login(new SimpleCredentials("admin", "admin".toCharArray()));
			log.info("####### session ######### : " + session.getUserID());
			Node root = session.getRootNode();
			Node currentNode = root.getNode("content");
			QueryObjectModelFactory qf = currentNode.getSession().getWorkspace().getQueryManager().getQOMFactory();
			ValueFactory vf = currentNode.getSession().getValueFactory();
			Selector selector = qf.selector("nt:base", "s");		
			Constraint constriant = qf.descendantNode("s", pagePath);
			constriant = qf.and(constriant, qf.propertyExistence("s", "sling:vanityPath"));
				ValueFactory valueFactory = session.getValueFactory();
				String operator  = QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO; 
				DynamicOperand dynOperand  = qf.propertyValue("s", "sling:vanityPath"); 
		        StaticOperand statOperand  = qf.literal(valueFactory.createValue(vanityPath)); 
            constriant = qf.and(constriant, qf.comparison(dynOperand , operator, statOperand));				
			QueryObjectModel qm = qf.createQuery(selector, constriant, null, null);
			log.info("######### Query ######### : " + qm.getStatement());
			NodeIterator nodes = qm.execute().getNodes();
			log.info("### query result : " + nodes.getSize());
			for (int i = 0; i < nodes.getSize(); i++) {
				Node node = nodes.nextNode();
				log.info("##### NOde Path ####### : " + node.getPath());
			}
			
			
			TidyJSONWriter tidyJSONWriter = new TidyJSONWriter(response.getWriter());			 
            tidyJSONWriter.object();
            tidyJSONWriter.key("isVanity").array();
            response.setContentType("text/html");

            if(nodes.getSize() > 0){
            	 tidyJSONWriter.value("true");
            }else{
            	tidyJSONWriter.value("false");
            }
            tidyJSONWriter.endArray();
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
		
	

}

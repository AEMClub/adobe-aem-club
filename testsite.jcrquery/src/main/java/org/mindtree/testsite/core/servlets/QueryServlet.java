package org.mindtree.testsite.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.query.RowIterator;
import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;
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


@SlingServlet(paths={"/services/query"}, methods={"GET"})
	public class QueryServlet extends SlingAllMethodsServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Reference
	private ResourceResolverFactory resolverFactory;
	
	@Reference
	private SlingRepository repo;
	
	private final Logger log = LoggerFactory.getLogger(QueryServlet.class);
	
	@Override
	public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException{ 

		log.info("####### Inside Servlet ########");
		Session session = null;
     
		try{

			session = repo.login(new SimpleCredentials("admin", "admin".toCharArray()));
			log.info("####### session ######### : " + session.getUserID());
			Node root = session.getRootNode();
			Node currentNode = root.getNode("content");
			QueryObjectModelFactory qf = currentNode.getSession().getWorkspace().getQueryManager().getQOMFactory();
			Selector selector = qf.selector("cq:PageContent", "s");		
			Constraint constriant = qf.descendantNode("s", "/content/geometrixx");
			constriant = qf.and(constriant, qf.propertyExistence("s", "pageTitle"));
			
			// If you want to get specific title, then use this 
				/*ValueFactory valueFactory = session.getValueFactory();
				String operator  = QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO; 
				DynamicOperand dynOperand  = qf.propertyValue("s", "jcr:title"); 
		        StaticOperand statOperand  = qf.literal(valueFactory.createValue("title value")); 
		        constriant = qf.and(constriant, qf.comparison(dynOperand , operator, statOperand));*/
			
            				
			QueryObjectModel qm = qf.createQuery(selector, constriant, null, null);
			log.info("######### Query ######### : " + qm.getStatement());
			NodeIterator nodes = qm.execute().getNodes();
			log.info("### query result : " + nodes.getSize());
			PrintWriter pw = response.getWriter();
			for (int i = 0; i < nodes.getSize(); i++) {
				Node node = nodes.nextNode();
				log.info("##### NOde Path ####### : " + node.getPath() + node.getProperty("pageTitle").getString());
				pw.println(node.getPath() + " : " + node.getProperty("pageTitle").getString());				
			}           

		}catch(Exception e){
			log.error(e.getMessage());
		}finally{
			if(session.isLive()){
				session.logout();
			}
		}	
		
	}	
	

}

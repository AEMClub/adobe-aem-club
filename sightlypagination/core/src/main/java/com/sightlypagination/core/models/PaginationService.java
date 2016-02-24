package com.sightlypagination.core.models;

import java.util.List;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

//Sling Imports
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.Resource;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

//QUeryBuilder APIs
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.Query;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.result.SearchResult;
import com.day.cq.search.result.Hit;

@Component
@Service
public class PaginationService implements PaginationInterface {

	Logger logger = LoggerFactory.getLogger(PaginationService.class);

	private Session session;
	private String matches;

	// Inject a Sling ResourceResolverFactory
	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	private QueryBuilder builder;

	@Override
	public List getPaginationData(String pathtopage, String number,
			String selector) {

		List<String> hyperLinks = new ArrayList();

		try {

			logger.info("Getting Ready to create SESSION!!");

			// Invoke the adaptTo method to create a Session
			ResourceResolver resourceResolver = resolverFactory
					.getAdministrativeResourceResolver(null);
			session = resourceResolver.adaptTo(Session.class);

			// create query description as hash map
			Map<String, String> map = new HashMap<String, String>();
			map.put("path", pathtopage);
			map.put("type", "cq:Page");
			map.put("p.limit", number);
			Query query = builder.createQuery(PredicateGroup.create(map),
					session);
			query.setStart(Integer.valueOf(number) * Integer.valueOf(selector));
			logger.info("Multiply:" + Integer.valueOf(number)
					* Integer.valueOf(selector));
			// query.setHitsPerPage(Integer.valueOf(number));

			SearchResult result = query.getResult();

			// paging metadata
			int hitsPerPage = result.getHits().size(); // 20 (set above) or

			// lower
			long totalMatches = result.getTotalMatches();
			this.matches = String.valueOf(totalMatches);
			logger.info("Matches" + totalMatches);
			// logger.info("Matches"+ totalMatches);
			long offset = result.getStartIndex();
			long numberOfPages = totalMatches / 20;

			// iterating over the results
			for (Hit hit : result.getHits()) {
				String path = hit.getPath();
				// Create a result element
				hyperLinks.add(path);

			}

			// close the session

		} catch (Exception e) {
			this.logger.info("Something went wrong with session .. {}", e);
		} finally {
			session.logout();
			return hyperLinks;
		}

	}

	@Override
	public String getMatches() {
		logger.info("Returning matches:" + this.matches);
		return this.matches;
	}

}
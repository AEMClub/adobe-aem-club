/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.sightlypagination.core.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Session;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.durbo.DurboInput.Node;

@Model(adaptables = SlingHttpServletRequest.class)
public class HelloWorldModel {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Inject
	SlingHttpServletRequest request;

	private String message;

	private String sel = null;

	private List<SimplePage> pageList = null;

	SimplePage page = null;

	private List<String> file;

	@Inject
	@Source("osgi-services")
	PaginationInterface service;

	@PostConstruct
	protected void init() {
		try {
			pageList = new ArrayList<SimplePage>();
			Session session = request.getResource().getResourceResolver()
					.adaptTo(Session.class);
			ValueMap node = request.getResource().getValueMap();
			String path = node.get("text", String.class);

			if (request != null) {
				List<String> selectors = Arrays.asList(request
						.getRequestPathInfo().getSelectors());
				if (selectors.size() > 0)
					this.sel = selectors.get(0);
				else
					this.sel = "0";

				this.file = service.getPaginationData(path, String.valueOf(5),
						this.sel);
				this.message = service.getMatches();

				for (String s : this.file) {
					page = new SimplePage();
					String title = session.getNode(s + "/jcr:content")
							.getProperty("jcr:title").getString();
					page.setPath(s);
					page.setTitle(title);

					pageList.add(page);

				}

			}
		} catch (Exception e) {
			logger.info("Exception:" + e.getMessage());
		}
	}

	public List<String> getFiles() {

		return this.file;
	}

	public String getMessage() {
		return this.message;
	}

	public List<SimplePage> getPage() {
		return this.pageList;
	}

}

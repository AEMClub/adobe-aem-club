package com.sightlypagination.core.models;

import java.util.List;

public interface PaginationInterface {
	
	List<String> getPaginationData(String path, String numberOfResults, String offset);
	
	String getMatches();
}

package org.test.testmulti.core.models;

import java.util.List;

public class TouchMultiBean {
	
	private String dashboard;
	private List<ItemsBean> items;

	public List<ItemsBean> getItems() {
		return items;
	}

	public void setItems(List<ItemsBean> items) {
		this.items = items;
	}

	public String getDashboard() {
		return dashboard;
	}

	public void setDashboard(String dashboard) {
		this.dashboard = dashboard;
	}
	
}

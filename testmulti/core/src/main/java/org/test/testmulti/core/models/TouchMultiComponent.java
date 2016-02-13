package org.test.testmulti.core.models;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import com.adobe.cq.sightly.WCMUse;

public class TouchMultiComponent extends WCMUse{

	private TouchMultiBean mBean = null;
	private ItemsBean iBean = null;
	private List<ItemsBean> lBean = null;
	private List<TouchMultiBean> multiList = null;
	
	@Override
	public void activate() throws Exception {

		multiList = new ArrayList<TouchMultiBean>();
		Node currentNode = getResource().adaptTo(Node.class);
		String[] tabs = {"i","u","uk"};
		
		for (int i = 0; i < tabs.length; i++) {
			String currentItem = tabs[i]+"Items";
			if(currentNode.hasProperty(currentItem)){
				setItems(currentNode, currentItem);
				if(currentNode.hasProperty(tabs[i]+"Dashboard")){
					mBean.setDashboard(currentNode.getProperty(tabs[i]+"Dashboard").getString());
				}
				multiList.add(mBean);				
			}
		}		
	}

	private void setItems(Node currentNode, String tab)
			throws PathNotFoundException, RepositoryException, ValueFormatException, JSONException {
		Value[] value;
		JSONObject jObj;
		Property currentProperty;
		mBean = new TouchMultiBean();
		lBean = new ArrayList<ItemsBean>();
		currentProperty = currentNode.getProperty(tab);
		if(currentProperty.isMultiple()){				
			value = currentProperty.getValues();
			
		}else{
			value = new Value[1];
			value[0] = currentProperty.getValue();
			
		}
		for (int i = 0; i < value.length; i++) {
			jObj = new JSONObject(value[i].getString());
			iBean = new ItemsBean();
			iBean.setPage(jObj.getString("page"));
			iBean.setPath(jObj.getString("path"));
			lBean.add(iBean);					
		}
		
		mBean.setItems(lBean);
	}
	
	public List<TouchMultiBean> getMBean(){
		return this.multiList;
	}

}

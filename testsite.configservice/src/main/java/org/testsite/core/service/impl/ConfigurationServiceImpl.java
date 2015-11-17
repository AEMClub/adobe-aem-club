package org.testsite.core.service.impl;

import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyUnbounded;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testsite.core.service.ConfigurationService;

@Service(value = ConfigurationServiceImpl.class)
@Component(immediate = true, metatype = true, label = "Example Configuration Service")
public class ConfigurationServiceImpl implements ConfigurationService {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationServiceImpl.class);
    private static final String CLASS_NAME = "[ConfigurationService]: ";
 
    @Property(unbounded = PropertyUnbounded.ARRAY, label = "Multi String", cardinality = 50, description = "Example for Multi field config")
    private static final String MULTI_FIELD = "multifield";
 

    @Property(unbounded = PropertyUnbounded.DEFAULT, label = "Simple String", description = "Example for Simple text field config")
    private static final String SIMPLE_FIELD = "simplefield";
 
 
    private String[] multiString;
    private String simpleString;

 
    /* (non-Javadoc)
	 * @see org.testsite.core.service.impl.ConfigurationService#getMultiString()
	 */

	@Override
    public String[] getMultiString() {
        return multiString;
    }
 

    /* (non-Javadoc)
	 * @see org.testsite.core.service.impl.ConfigurationService#getSimpleString()
	 */
	@Override
    public String getSimpleString() {
        return simpleString;
    }
 
    
    @Activate
    protected void activate(final Map<String, Object> properties) {
        LOG.info(CLASS_NAME + "activating configuration service");
        readProperties(properties);
    }
 
    protected void readProperties(final Map<String, Object> properties) {
        LOG.info(properties.toString());
        this.multiString = PropertiesUtil.toStringArray(properties.get(MULTI_FIELD));
        LOG.info("Mutli String Size: " + multiString.length);        
        this.simpleString = PropertiesUtil.toString(properties.get(SIMPLE_FIELD),"default");
        LOG.info("Simple String: " + simpleString); 
        
    }
 
   
}


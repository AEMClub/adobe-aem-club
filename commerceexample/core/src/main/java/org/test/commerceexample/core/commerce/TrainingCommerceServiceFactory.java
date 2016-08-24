package org.test.commerceexample.core.commerce;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.commerce.api.CommerceService;
import com.adobe.cq.commerce.api.CommerceServiceFactory;
import com.adobe.cq.commerce.common.AbstractJcrCommerceServiceFactory;
 
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
 
/**
 * A simple new (for training) implementation for the {@link CommerceServiceFactory} interface.
 */
@Component(metatype = true, label = "Adobe CQ Commerce Factory for Training")
@Service
@Properties(value = {
        @Property(name = "service.description", value = "Factory for training commerce service"),
        @Property(name = "commerceProvider", value = "training")
})
 
public class TrainingCommerceServiceFactory extends AbstractJcrCommerceServiceFactory implements CommerceServiceFactory {
 
    /**
     * Create a new <code>TrainingCommerceServiceImpl</code>.
     */
    public CommerceService getCommerceService(Resource res) {
        return new TrainingCommerceServiceImpl(getServiceContext(), res);
    }
 
}

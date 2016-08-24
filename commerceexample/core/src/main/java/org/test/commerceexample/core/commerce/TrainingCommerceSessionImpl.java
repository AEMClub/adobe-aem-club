package org.test.commerceexample.core.commerce;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.common.AbstractJcrCommerceService;
import com.adobe.cq.commerce.common.AbstractJcrCommerceSession;
import com.adobe.cq.commerce.common.promotion.AbstractJcrVoucher;
 
public class TrainingCommerceSessionImpl extends AbstractJcrCommerceSession {
     
    protected SlingHttpServletRequest request;
    protected SlingHttpServletResponse response;
    protected Resource resource;
    protected ResourceResolver resolver;
    protected AbstractJcrCommerceService commerceService;
     
    protected List<CartEntry> cart = new ArrayList<CartEntry>();
   // protected List<AbstractJcrVoucher> vouchers = new ArrayList<AbstractJcrVoucher>();
    protected List<String> activePromotions = new ArrayList<String>();
 
    public TrainingCommerceSessionImpl(
            AbstractJcrCommerceService commerceService,
            SlingHttpServletRequest request, SlingHttpServletResponse response,
            Resource resource) throws CommerceException {
        super(commerceService, request, response, resource);
        
        this.PN_UNIT_PRICE="price";
     /*
         this.request = request;
         this.response = response;
         this.resource = resource;
         this.resolver = resource.getResourceResolver();
         this.commerceService = commerceService;*/
         
    }
    
    protected BigDecimal getShipping(String method)
    {
      String[][] shippingCosts = { { "/etc/commerce/shipping-methods/geometrixx-outdoors/ground", "10.00" }, { "/etc/commerce/shipping-methods/geometrixx-outdoors/three-day", "20.00" }, { "/etc/commerce/shipping-methods/geometrixx-outdoors/two-day", "25.00" }, { "/etc/commerce/shipping-methods/geometrixx-outdoors/overnight", "40.00" } };
      for (String[] entry : shippingCosts) {
        if (entry[0].equals(method)) {
          return new BigDecimal(entry[1]);
        }
      }
      return BigDecimal.ZERO;
    }
}

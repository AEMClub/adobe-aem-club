/**
 * 
 */
package org.mindtree.customworkflow.core.service.impl;

import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.HistoryItem;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.metadata.MetaDataMap;

/**
 * @author Lokesh BS
 *
 */
@Component(immediate=true)
@Service

@Properties({
    @Property(name = Constants.SERVICE_DESCRIPTION, value = "Sample Implementation of dynamic participant chooser."),
    @Property(name = ParticipantStepChooser.SERVICE_PROPERTY_LABEL, value = "Sample Workflow Participant Chooser") 
})

public class SampleParticipantStepChooserImpl implements ParticipantStepChooser {

	private static final Logger logger = LoggerFactory.getLogger(SampleParticipantStepChooserImpl.class);
	
	@Override
	public String getParticipant(WorkItem workItem, WorkflowSession wfSession, MetaDataMap metaDataMap) throws WorkflowException {
		// TODO Auto-generated method stub
		logger.info("################ Inside the SampleProcessStepChooserImpl GetParticipant ##########################");
		String participant = "admin";
		Workflow wf = workItem.getWorkflow();
		List<HistoryItem> wfHistory = wfSession.getHistory(wf);
		if(!wfHistory.isEmpty()){
			participant = "administrators";
		}else{
			participant = "admin";
		}		
		logger.info("####### Participant : " + participant + " ##############");
		return participant;
	}

}

package edu.uams.clara.webapp.protocol.businesslogic;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.uams.clara.core.util.xml.DomUtils;
import edu.uams.clara.core.util.xml.XmlHandler;
import edu.uams.clara.core.util.xml.XmlHandlerFactory;
import edu.uams.clara.integration.outgoing.epic.StudyDefinitionWSClient;
import edu.uams.clara.webapp.common.businesslogic.BusinessObjectStatusHelper;
import edu.uams.clara.webapp.common.domain.email.EmailTemplate;
import edu.uams.clara.webapp.common.domain.form.Form;
import edu.uams.clara.webapp.common.domain.history.Track;
import edu.uams.clara.webapp.common.domain.usercontext.User;
import edu.uams.clara.webapp.common.domain.usercontext.enums.Committee;
import edu.uams.clara.webapp.common.exception.ClaraRunTimeException;
import edu.uams.clara.webapp.common.service.audit.AuditService;
import edu.uams.clara.webapp.common.service.relation.RelationService;
import edu.uams.clara.webapp.common.util.DateFormatUtil;
import edu.uams.clara.webapp.protocol.dao.ProtocolDao;
import edu.uams.clara.webapp.protocol.dao.budget.code.EpicCdmByCptCodeDao;
import edu.uams.clara.webapp.protocol.dao.businesslogicobject.ProtocolFormCommitteeStatusDao;
import edu.uams.clara.webapp.protocol.dao.businesslogicobject.ProtocolFormStatusDao;
import edu.uams.clara.webapp.protocol.dao.businesslogicobject.ProtocolStatusDao;
import edu.uams.clara.webapp.protocol.dao.irb.AgendaDao;
import edu.uams.clara.webapp.protocol.dao.irb.AgendaItemDao;
import edu.uams.clara.webapp.protocol.dao.protocolform.ProtocolFormDao;
import edu.uams.clara.webapp.protocol.dao.protocolform.ProtocolFormXmlDataDao;
import edu.uams.clara.webapp.protocol.dao.protocolform.ProtocolFormXmlDataDocumentDao;
import edu.uams.clara.webapp.protocol.domain.Protocol;
import edu.uams.clara.webapp.protocol.domain.businesslogicobject.ProtocolFormCommitteeStatus;
import edu.uams.clara.webapp.protocol.domain.businesslogicobject.ProtocolFormStatus;
import edu.uams.clara.webapp.protocol.domain.businesslogicobject.ProtocolStatus;
import edu.uams.clara.webapp.protocol.domain.businesslogicobject.enums.AgendaStatusEnum;
import edu.uams.clara.webapp.protocol.domain.businesslogicobject.enums.ProtocolFormCommitteeStatusEnum;
import edu.uams.clara.webapp.protocol.domain.businesslogicobject.enums.ProtocolFormStatusEnum;
import edu.uams.clara.webapp.protocol.domain.businesslogicobject.enums.ProtocolStatusEnum;
import edu.uams.clara.webapp.protocol.domain.irb.Agenda;
import edu.uams.clara.webapp.protocol.domain.irb.AgendaItem;
import edu.uams.clara.webapp.protocol.domain.irb.AgendaItem.AgendaItemCategory;
import edu.uams.clara.webapp.protocol.domain.irb.AgendaItem.AgendaItemStatus;
import edu.uams.clara.webapp.protocol.domain.protocolform.ProtocolForm;
import edu.uams.clara.webapp.protocol.domain.protocolform.ProtocolFormXmlData;
import edu.uams.clara.webapp.protocol.domain.protocolform.ProtocolFormXmlDataDocument;
import edu.uams.clara.webapp.protocol.domain.protocolform.ProtocolFormXmlDataDocument.Status;
import edu.uams.clara.webapp.protocol.domain.protocolform.enums.ProtocolFormType;
import edu.uams.clara.webapp.protocol.domain.protocolform.enums.ProtocolFormXmlDataType;
import edu.uams.clara.webapp.protocol.scheduler.SendBudgetApprovedNotificationForPBService;
import edu.uams.clara.webapp.protocol.service.ProtocolFormService;
import edu.uams.clara.webapp.protocol.service.ProtocolMetaDataXmlService;
import edu.uams.clara.webapp.protocol.service.ProtocolService;
import edu.uams.clara.webapp.protocol.service.email.ProtocolEmailService;
import edu.uams.clara.webapp.protocol.service.history.ProtocolTrackService;
import edu.uams.clara.webapp.xml.processor.XmlProcessor;

public class ProtocolBusinessObjectStatusHelper extends
		BusinessObjectStatusHelper {
	private final static Logger logger = LoggerFactory
			.getLogger(ProtocolBusinessObjectStatusHelper.class);

	public ProtocolBusinessObjectStatusHelper()
			throws ParserConfigurationException {
		super();
		setObjectType("PROTOCOL");
	}

	private ProtocolDao protocolDao;

	private ProtocolStatusDao protocolStatusDao;

	private ProtocolFormStatusDao protocolFormStatusDao;

	private ProtocolFormDao protocolFormDao;

	private AgendaDao agendaDao;

	private AgendaItemDao agendaItemDao;

	private ProtocolFormCommitteeStatusDao protocolFormCommitteeStatusDao;
	
	private ProtocolFormXmlDataDocumentDao protocolFormXmlDataDocumentDao;
	
	private ProtocolFormXmlDataDao protocolFormXmlDataDao;

	private ProtocolTrackService protocolTrackService;

	private ProtocolMetaDataXmlService protocolMetaDataXmlService;

	private ProtocolEmailService protocolEmailService;
	
	private RelationService relationService;
	
	private ProtocolFormService protocolFormService;
	
	private ProtocolService protocolService;
	
	private StudyDefinitionWSClient studyDefinitionWSClient;
	
	private EpicCdmByCptCodeDao epicCdmByCptCodeDao;
	
	private AuditService auditService;
	
	private SendBudgetApprovedNotificationForPBService sendBudgetApprovedNotificationForPBService;
	
	

	@Override
	public void logStatusChange(Form form, User user,
			Map<String, Object> attributeRawValues, String logsTemplate)
			throws IOException, SAXException {
		ProtocolForm protocolForm = (ProtocolForm) form;

		protocolTrackService.logStatusChange(protocolForm, user,
				attributeRawValues, logsTemplate);

	}


	private void sendBudgetApprovePBNotification(ProtocolForm protocolForm) {
		try{
			Protocol protocol = protocolForm.getProtocol();
			sendBudgetApprovedNotificationForPBService.sendBudgetApprovedNotification(protocol);
		}catch(Exception e){
			
		}
	}
	
	protected void generateEpicCDM(ProtocolForm protocolForm) {
			try{
			Protocol protocol = protocolForm.getProtocol();
			XmlProcessor xmlProcessor = getXmlProcessor();
			XmlHandler xmlHandler = XmlHandlerFactory.newXmlHandler();
			String protocolMetaXml = protocol.getMetaDataXml();
			String budgetApproveDate = "";
			budgetApproveDate=xmlHandler.getSingleStringValueByXPath(protocolMetaXml, "/protocol/summary/budget-determination/approval-date");
			if(!budgetApproveDate.isEmpty()){
				ProtocolFormXmlData pfxd = protocolFormXmlDataDao.getLastProtocolFormXmlDataByProtocolFormIdAndType(protocolForm.getId(), ProtocolFormXmlDataType.BUDGET);
				String budgetXmlData = pfxd.getXmlData();
				
				long protocolId = pfxd.getProtocolForm().getProtocol().getId();
				logger.debug("protooclId: " + protocolId);
				
				CSVWriter writer = new CSVWriter(new FileWriter("C:\\Data\\epic\\"+ protocolId +" HB "+ budgetApproveDate.replace("/", "-") +".csv"));
				
				try {
					List<String> cpdCodeLst = xmlProcessor.getAttributeValuesByPathAndAttributeName("/budget/epochs/epoch/procedures/procedure[@type=\"normal\"]", budgetXmlData, "cptcode");
					
					if (cpdCodeLst.size() > 0){
						Set<String> cpdCodeSet = new HashSet<String>(cpdCodeLst);
						
						for (String cptCode : cpdCodeSet){
							
							try {
								
								String epicCdmCode = epicCdmByCptCodeDao.getEpicCdmByCptCode(cptCode);
								logger.debug("cptCode: " + cptCode + " cdm code: " + epicCdmCode);
								String cost = xmlProcessor.getAttributeValueByPathAndAttributeName("/budget/epochs/epoch/procedures/procedure[@type=\"normal\" and @cptcode=\""+ cptCode +"\"]/hosp", budgetXmlData, "cost");
								logger.debug("cost: " + cost);
								//logger.debug("protooclId: " + protocolId + "cpt code: " + cptCode + " epic cdm code: " + epicCdmCode + " cost: " + cost);

								String[] entry = {String.valueOf(protocolId), epicCdmCode, cost};
								
								writer.writeNext(entry);
								
							} catch (Exception e) {
								
							}
						}
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				writer.close();
				
				//process log info
				Track track = protocolTrackService.getOrCreateTrack("PROTOCOL",
	                    protocol.getId());

				Document logsDoc = protocolTrackService.getLogsDocument(track);

				Element logEl = logsDoc.createElement("log");
	      
				String logId = UUID.randomUUID().toString();
	      
				Date now = new Date();
	            logEl.setAttribute("id", logId);
	            logEl.setAttribute("parent-id", logId);
	            logEl.setAttribute("action-user-id", "0");
	            logEl.setAttribute("actor", "System");
	            logEl.setAttribute("date-time", DateFormatUtil.formateDate(now));
	            logEl.setAttribute("event-type", "GENERATE_EPIC_CDM");
	            logEl.setAttribute("form-id", "0");
	            logEl.setAttribute("parent-form-id", "0");
	            logEl.setAttribute("form-type", "PROTOCOL");
	            logEl.setAttribute("log-type", "ACTION");
	            logEl.setAttribute("timestamp", String.valueOf(now.getTime()));
	            
	            String message = "Epic fee schedule has been generated.";
	            logEl.setTextContent(message);

	            logsDoc.getDocumentElement().appendChild(logEl);

	            track = protocolTrackService.updateTrack(track, logsDoc);
	            
	            //add audit operation
	            auditService.auditEvent("GENERATE_EPIC_CDM",
						"Epic fee schedule has been generated for Protocol: "+protocol.getId()+".");
			}}catch(Exception e){
				e.printStackTrace();
			}
			
	}
	
	/**
	 * 
	 */
	@Override
	public void sendNotifications(Form form, User user,
			Map<String, Object> attributeRawValues,
			String notificationsTemplate, String extraDataXml)
			throws IOException, SAXException {
		ProtocolForm protocolForm = (ProtocolForm) form;

		XmlProcessor xmlProcessor = getXmlProcessor();

		Document notificationsTemplateDoc = xmlProcessor
				.loadXmlStringToDOM(notificationsTemplate);

		NodeList notifications = notificationsTemplateDoc.getDocumentElement()
				.getElementsByTagName("notification");

		Committee committee = null;
		
		try{
			committee = Committee.valueOf(attributeRawValues.get(
					"COMMITTEE").toString());
		}catch(Exception e){
			//don't care... down stream handles null
		}
		
		
		// String userMessage = attributeRawValues.get("USER_MESSAGE") !=
		// null?attributeRawValues.get("USER_MESSAGE").toString():"";

		List<EmailTemplate> emailTemplates = new ArrayList<EmailTemplate>();

		logger.debug("formId" + protocolForm.getId());

		String emailComment = "";
		String mailToLst = "";
		String ccLst = "";
		String committeeNote = attributeRawValues.get("COMMITTEE_NOTE") != null ? attributeRawValues
				.get("COMMITTEE_NOTE").toString() : "";

		try {
			emailComment = (xmlProcessor.listElementStringValuesByPath(
					"//message/body", extraDataXml) != null && !xmlProcessor
					.listElementStringValuesByPath("//message/body",
							extraDataXml).isEmpty()) ? xmlProcessor
					.listElementStringValuesByPath("//message/body",
							extraDataXml).get(0) : "";
			mailToLst = (xmlProcessor.listElementStringValuesByPath(
					"//message/to", extraDataXml) != null && !xmlProcessor
					.listElementStringValuesByPath("//message/to",
							extraDataXml).isEmpty()) ? xmlProcessor
					.listElementStringValuesByPath("//message/to",
							extraDataXml).get(0) : "";
			ccLst = (xmlProcessor.listElementStringValuesByPath(
					"//message/cc", extraDataXml) != null && !xmlProcessor
					.listElementStringValuesByPath("//message/cc",
							extraDataXml).isEmpty()) ? xmlProcessor
					.listElementStringValuesByPath("//message/cc",
							extraDataXml).get(0) : "";
		} catch (Exception e) {
			logger.debug("no letter body");
			// e.printStackTrace();
		}

		if (emailComment.isEmpty()){
			emailComment = committeeNote;
		}

		for (int i = 0; i < notifications.getLength(); i++) {

			Element notificationEl = (Element) notifications.item(i);

			String notificationType = notificationEl
					.getAttribute("notification-type");

			String emailTemplateIdentifier = notificationEl
					.getAttribute("email-template-identifier");
			
			String xpathCondition = notificationEl
					.getAttribute("xpath-condition");
			
			String formXpathCondition = notificationEl
					.getAttribute("form-xpath-condition");

			logger.debug("emailTemplateIdentifier: " + emailTemplateIdentifier);

			if (notificationType == null || notificationType.isEmpty()
					|| emailTemplateIdentifier == null
					|| emailTemplateIdentifier.isEmpty())
				continue;
			
			if (xpathCondition != null && !xpathCondition.isEmpty()){
				if (!xpathConditionCheck(protocolForm.getObjectMetaData(), xpathCondition)){
					continue;
				}
			}
			
			if (formXpathCondition != null && !formXpathCondition.isEmpty()){
				if (!xpathConditionCheck(protocolForm.getMetaDataXml(), formXpathCondition)){
					continue;
				}
			}

			// notification-type="NOTIFICATION"
			// email-template-identifier="NEW_SUBMISSION_SUBMITTED_TO_GATEKEEPER"
			EmailTemplate emailTemplate = null;

			if (notificationType.equals("NOTIFICATION")) {
				logger.debug("send notification ...");
				emailTemplate = protocolEmailService.sendNotification(
						protocolForm, committee, attributeRawValues, user, emailTemplateIdentifier,
						emailComment, mailToLst, ccLst);

			} else if (notificationType.equals("LETTER")) {
				String letterName = notificationEl.getAttribute("letter-name");
				String docType = notificationEl.getAttribute("doc-type");

				if (letterName == null || letterName.isEmpty()) {
					letterName = emailTemplateIdentifier;
				}

				if (docType == null || docType.isEmpty()) {
					docType = "Letter";
				}
				logger.debug("send letter ...");
				emailTemplate = protocolEmailService.sendLetter(protocolForm,
						committee, attributeRawValues, user, emailTemplateIdentifier, emailComment,
						letterName, docType, mailToLst, ccLst);

			}

			if (emailTemplate != null) { // impossible to be null

				emailTemplates.add(emailTemplate);

				logger.debug("emailContent: "
						+ emailTemplate.getTemplateContent());
			}

		}

		attributeRawValues.put("EMAIL_TEMPLATES", emailTemplates);
	}

	@Override
	public void changeObjectStatus(Form form, Date now, Committee committee,
			User user, String status) {

		logger.debug("changing ObjectStatus to " + status);
		ProtocolForm protocolForm = (ProtocolForm) form;

		ProtocolStatus protocolStatus = new ProtocolStatus();
		protocolStatus.setProtocol(protocolForm.getProtocol());
		protocolStatus.setModified(now);
		protocolStatus.setCausedByCommittee(committee);
		protocolStatus.setCauseByUser(user);
		protocolStatus.setProtocolStatus(ProtocolStatusEnum.valueOf(status));

		protocolStatusDao.saveOrUpdate(protocolStatus);

	}

	@Override
	public void changeObjectFormStatus(Form form, Date now,
			Committee committee, User user, String status) {
		logger.debug("changing ObjectFormStatus to " + status);

		ProtocolForm protocolForm = (ProtocolForm) form;

		ProtocolFormStatus protocolFormStatus = new ProtocolFormStatus();
		protocolFormStatus.setProtocolForm(protocolForm);
		protocolFormStatus.setModified(now);
		protocolFormStatus.setCausedByCommittee(committee);
		protocolFormStatus.setCauseByUser(user);
		protocolFormStatus.setProtocolFormStatus(ProtocolFormStatusEnum
				.valueOf(status));

		protocolFormStatusDao.saveOrUpdate(protocolFormStatus);
		
		if (status.equals(ProtocolFormStatusEnum.PENDING_PI_SIGN_OFF.toString())){
			logger.debug("change document stastus");
			this.changeObjectFormDocumentStatus(protocolForm, now, committee, user, Status.RSC_APPROVED.toString(), true, true, true);
			
			String protocolFormMetaData = protocolForm.getMetaDataXml();
			
			try {
				protocolFormMetaData = getXmlProcessor().replaceOrAddNodeValueByPath("/protocol/summary/budget-determination/approval-date", protocolFormMetaData, DateFormatUtil.formateDateToMDY(new Date()));
				
				protocolForm.setMetaDataXml(protocolFormMetaData);
				protocolFormDao.saveOrUpdate(protocolForm);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * @TODO need to save xmldata here
	 */
	@Override
	public void changeObjectFormCommitteeStatus(Form form, Date now,
			Committee committee, User user, Committee involvedCommittee,
			String status, String commiteeNote, String xmlData, String action) {

		logger.debug("changing ObjectFormCommitteeStatus to " + status);

		ProtocolForm protocolForm = (ProtocolForm) form;
		ProtocolFormCommitteeStatus protocolFormCommitteeStatus = new ProtocolFormCommitteeStatus();
		protocolFormCommitteeStatus.setProtocolForm(protocolForm);
		protocolFormCommitteeStatus.setModified(now);
		protocolFormCommitteeStatus.setCauseByUser(user);
		protocolFormCommitteeStatus.setCausedByCommittee(committee);

		protocolFormCommitteeStatus.setNote(commiteeNote);
		protocolFormCommitteeStatus.setCommittee(involvedCommittee);
		protocolFormCommitteeStatus.setXmlData(xmlData);
		protocolFormCommitteeStatus.setAction(action);

		protocolFormCommitteeStatus
				.setProtocolFormCommitteeStatus(ProtocolFormCommitteeStatusEnum
						.valueOf(status));

		protocolFormCommitteeStatusDao
				.saveOrUpdate(protocolFormCommitteeStatus);
		
		if (action.equals("REQUEST_REVIEW") || action.equals("REQUEST_WAIVER") || (committee.equals(Committee.GATEKEEPER) && action.equals("ASSIGN_TO_COMMITTEES") && involvedCommittee.equals(Committee.PHARMACY_REVIEW)) || (committee.equals(Committee.BUDGET_REVIEW) && action.equals("ROUTE_TO_PHARMACY"))){
			ProtocolFormXmlData protocolFormXmlData = protocolForm.getTypedProtocolFormXmlDatas().get(protocolForm.getProtocolFormType().getDefaultProtocolFormXmlDataType());
			
			String protocolFormXmlDataString = protocolFormXmlData.getXmlData();
			
			try{
				protocolFormXmlDataString = getXmlProcessor().replaceOrAddNodeValueByPath("/protocol/pharmacy-created", protocolFormXmlDataString, "y");
			} catch (Exception e){
				//don't care
			}
			
			protocolFormXmlData.setXmlData(protocolFormXmlDataString);
			protocolFormXmlDataDao.saveOrUpdate(protocolFormXmlData);
		}
	}
	
	@Override
	public void changeObjectFormDocumentStatus(Form form, Date now,
			Committee committee, User user, String status, boolean changeBudgetDocStatus, boolean changeProtocolDocStatus, boolean changeConsentDocStatus) {
		logger.debug("changing ObjectFormDocumentStatus to " + status);
		
		ProtocolForm protocolForm = (ProtocolForm) form;
		
		List<ProtocolFormXmlDataDocument> protocolFormXmlDataDocuments = protocolFormXmlDataDocumentDao.getLatestDocumentByProtocolFormId(protocolForm.getId());
		
		if (protocolFormXmlDataDocuments != null && !protocolFormXmlDataDocuments.isEmpty()){
			for (ProtocolFormXmlDataDocument pfxd : protocolFormXmlDataDocuments){	
				if (pfxd.getCategory().equals("Budget Document")) {
					if (changeBudgetDocStatus) {
						pfxd.setStatus(ProtocolFormXmlDataDocument.Status.valueOf(status));
					}
				} else if (pfxd.getCategory().equals("Protocol")) {
					if (changeProtocolDocStatus) {
						pfxd.setStatus(ProtocolFormXmlDataDocument.Status.valueOf(status));
					}
				} else if (pfxd.getCategory().equals("Consent, Assent and HIPAA")) {
					if (changeConsentDocStatus) {
						pfxd.setStatus(ProtocolFormXmlDataDocument.Status.valueOf(status));
					}
				} else {
					pfxd.setStatus(ProtocolFormXmlDataDocument.Status.valueOf(status));
				}
				
				protocolFormXmlDataDocumentDao.saveOrUpdate(pfxd);
			}
		}
		
	}

	@Override
	public String getObjectStatus(Form form) {
		ProtocolForm protocolForm = (ProtocolForm) form;

		ProtocolStatus protocolStatus = null;

		try {
			protocolStatus = protocolDao
					.getLatestProtocolStatusByProtocolId(protocolForm
							.getProtocol().getId());
		} catch (Exception ex) {
			return ""; // no status
		}

		return protocolStatus.getProtocolStatus().toString();
	}

	@Override
	public String getFormStatus(Form form) {
		ProtocolForm protocolForm = (ProtocolForm) form;

		ProtocolFormStatus protocolFormStatus = null;

		try {
			protocolFormStatus = protocolFormStatusDao
					.getLatestProtocolFormStatusByFormId(protocolForm.getId());
			/*
			 * Why do I added this?? if
			 * (protocolFormStatus.getProtocolForm().getId() !=
			 * protocolForm.getId()){ return ""; }
			 */
		} catch (Exception ex) {
			return ""; // no status
		}

		return protocolFormStatus.getProtocolFormStatus().toString();
	}

	@Override
	public String getFormCommitteeStatus(Form form, Committee committee) {
		ProtocolForm protocolForm = (ProtocolForm) form;

		ProtocolFormCommitteeStatus protocolFormCommitteeStatus = null;

		try {
			protocolFormCommitteeStatus = protocolFormCommitteeStatusDao
					.getLatestByCommitteeAndProtocolFormId(committee,
							protocolForm.getId());
		} catch (Exception ex) {
			return ""; // no status
		}

		return (protocolFormCommitteeStatus != null) ? protocolFormCommitteeStatus
				.getProtocolFormCommitteeStatus().toString() : "";
	}

	@Override
	public void updateMetaDataXml(Form form, String extraDataXml, boolean updateMetaData) {

		ProtocolForm protocolForm = (ProtocolForm) form;

		logger.debug("extraDataXml for update: " + extraDataXml);

		protocolForm = protocolMetaDataXmlService
				.updateProtocolFormMetaDataXml(
						protocolForm.getTypedProtocolFormXmlDatas().get(
								protocolForm.getProtocolFormType()
										.getDefaultProtocolFormXmlDataType()),
						extraDataXml);
		
		if (protocolForm.getProtocolFormType().equals(ProtocolFormType.NEW_SUBMISSION) || protocolForm.getProtocolFormType().equals(ProtocolFormType.EMERGENCY_USE) || protocolForm.getProtocolFormType().equals(ProtocolFormType.HUMAN_SUBJECT_RESEARCH_DETERMINATION)){
			protocolMetaDataXmlService.updateProtocolMetaDataXml(protocolForm);
		} else {
			if (updateMetaData){
				protocolMetaDataXmlService.updateProtocolMetaDataXml(protocolForm);
			}
		}
		
	}

	private List<AgendaStatusEnum> availableAgendaStatuses = new ArrayList<AgendaStatusEnum>(
			0);
	{
		availableAgendaStatuses.add(AgendaStatusEnum.AGENDA_INCOMPLETE);
		availableAgendaStatuses
				.add(AgendaStatusEnum.AGENDA_PENDING_CHAIR_APPROVAL);
		availableAgendaStatuses.add(AgendaStatusEnum.AGENDA_APPROVED);
	}

	protected void assignToAgenda(ProtocolForm protocolForm) {
		AgendaItem agendaItem = new AgendaItem();
		try {
			Agenda nextAvailabeAgenda = agendaDao
					.getNextAvailableAgenda(availableAgendaStatuses);
			logger.debug("agenda date: " + nextAvailabeAgenda.getDate()
					+ " is available!");

			agendaItem.setAgenda(nextAvailabeAgenda);
			agendaItem.setAgendaItemStatus(AgendaItemStatus.NEW);
			agendaItem.setProtocolForm(protocolForm);
			agendaItem.setAgendaItemCategory(AgendaItemCategory.REPORTED);
			agendaItem.setOrder(0);

			agendaItemDao.saveOrUpdate(agendaItem);
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			throw new ClaraRunTimeException(ClaraRunTimeException.ErrorType.NO_AGENDA_ASSIGNED.getMessage(), ClaraRunTimeException.ErrorType.NO_AGENDA_ASSIGNED);
		}
	}
	
	/*
	protected void cleanRecentMotion(ProtocolForm protocolForm) {
		//clean recent-motion element
		try {
			String protocolFormMetaData = protocolForm.getMetaDataXml();
			
			protocolFormMetaData = getXmlProcessor().replaceOrAddNodeValueByPath("/"+ protocolForm.getProtocolFormType().getBaseTag() +"/summary/irb-determination/recent-motion", protocolFormMetaData, "");
			
			protocolForm.setMetaDataXml(protocolFormMetaData);
			
			protocolForm = protocolFormDao.saveOrUpdate(protocolForm);
			
		} catch (Exception e) {
			
		}
	}
	*/
	
	private void setSubmitDate(ProtocolForm protocolForm) {
		Protocol protocol = protocolForm.getProtocol();
		
		String currentDate = DateFormatUtil.formateDateToMDY(new Date());
		
		try {
			String protocolMetaDataXml = protocol.getMetaDataXml();
			
			protocolMetaDataXml = getXmlProcessor()
					.replaceOrAddNodeValueByPath(
							"/protocol/original-study/submit-date",
							protocolMetaDataXml, currentDate);
			
			protocol.setMetaDataXml(protocolMetaDataXml);
			protocolDao.saveOrUpdate(protocol);
		} catch (Exception e){
			logger.debug("Failed to set submit date!");
		}
		
		
	}
	
	private void setFormSubmitDate(ProtocolForm protocolForm) {
		
		String currentDate = DateFormatUtil.formateDateToMDY(new Date());
		
		try {
			String protocolFormMetaDataXml = protocolForm.getMetaDataXml();
			
			protocolFormMetaDataXml = getXmlProcessor()
					.replaceOrAddNodeValueByPath(
							"/"+ protocolForm.getProtocolFormType().getBaseTag() +"/form-submit-date",
							protocolFormMetaDataXml, currentDate);
			
			protocolForm.setMetaDataXml(protocolFormMetaDataXml);
			protocolForm = protocolFormDao.saveOrUpdate(protocolForm);
		} catch (Exception e){
			logger.debug("Failed to set form submit date!");
		}

		if ((protocolForm.getProtocolFormType().equals(ProtocolFormType.NEW_SUBMISSION) || protocolForm.getProtocolFormType().equals(ProtocolFormType.MODIFICATION)) && !getProtocolFormService().requireBudget(protocolForm)) {
			List<String> xpathList = Lists.newArrayList();
			xpathList.add("/protocol/budget-created");
			
			protocolForm = getProtocolFormService().consolidateProtocolForm(protocolForm, xpathList);
			
			Protocol protocol = protocolForm.getProtocol();
			
			protocol = getProtocolService().consolidateProtocol(protocol, xpathList);
		}
		
	}

	private void updateApprovalStatusAndDate(ProtocolForm protocolForm, String action,
			String clockStart, String condition) {
		
		Protocol protocol = protocolForm.getProtocol();
		
		String protocolFormMetaData = protocolForm.getMetaDataXml();
		
		String protocolMetaDataXml = protocol.getMetaDataXml();
		
		Date date = new Date();
		String currentDate = DateFormatUtil.formateDateToMDY(date);

		try {
			XmlHandler xmlHandler = XmlHandlerFactory.newXmlHandler();
			
			List<String> reviewPeriodValues = getXmlProcessor()
					.listElementStringValuesByPath(
							"//irb-determination/review-period",
							protocolFormMetaData);

			int reviewPeriod = 0;
			if (reviewPeriodValues != null && reviewPeriodValues.size() > 0) {
				reviewPeriod = Integer.valueOf(reviewPeriodValues.get(0));
			}

			String reviewEndDate = "";
			
			String agendaDateStr = "";
			
			String approvalDate = "";
			
			/*
			 * If Full Board approval, set approval date as agenda date and review end date as agenda date + review period - 1 day
			 * */
			try {
				Date agendaDate = agendaItemDao.getLatestByProtocolFormId(protocolForm.getId()).getAgenda().getDate();
				
				agendaDateStr = DateFormatUtil.formateDateToMDY(agendaDate);
				
				LocalDate agendaLocalDate = new LocalDate(agendaDate);
				
				if (clockStart.equals("Full Board") && action.equals("APPROVE")) {
					//Calendar fullBoardReviewCal = Calendar.getInstance();
					//fullBoardReviewCal.add(Calendar.MONTH, +reviewPeriod);
					//fullBoardReviewCal.add(Calendar.DATE, -1);
					
					if (reviewPeriod > 0) {
						reviewEndDate = DateFormatUtil
								.formateDateToMDY(agendaLocalDate.plusMonths(reviewPeriod).minusDays(1).toDate());
					}
					
					approvalDate = agendaDateStr;
				}
			} catch (Exception e){

			}
			
			String originalDeferWithMinorDate = xmlHandler
					.getSingleStringValueByXPath(protocolFormMetaData,
							"/"+ protocolForm.getProtocolFormType().getBaseTag() +"/original-version/defer-with-minor-approval-date");
			
			String recentMotion = xmlHandler.getSingleStringValueByXPath(protocolFormMetaData, "/"+ protocolForm.getProtocolFormType().getBaseTag() +"/summary/irb-determination/recent-motion");
			
			LocalDate localDate = new LocalDate(date);
			
			/*
			 * If Expedited approval, check if normal approval or minor met approval.
			 * If normal approval, set approval date as current date and review end date as current date + 1 year - 1 day
			 * If minor met approval, check original form defer approval date, if exists, set that date as approval date, otherwise set agenda date as approval date, and set review end date as agenda date + review period - 1 day.
			 * */
			if (clockStart.equals("Expedited") && action.equals("APPROVE")) {
				if (recentMotion.equals("Defer with minor contingencies")) {
					if (originalDeferWithMinorDate.isEmpty()) {
						String agendaDate = xmlHandler.getSingleStringValueByXPath(protocolFormMetaData, "/"+ protocolForm.getProtocolFormType().getBaseTag() +"/summary/irb-determination/agenda-date");
						
						approvalDate = agendaDate;
						
						LocalDate agendaLocalDate = new LocalDate(DateFormatUtil.toDate(agendaDate));
						
						reviewEndDate = DateFormatUtil
								.formateDateToMDY(agendaLocalDate.plusMonths(reviewPeriod).minusDays(1).toDate());
					} else {
						approvalDate = originalDeferWithMinorDate;
						
						LocalDate originalAgendaLocalDate = new LocalDate(DateFormatUtil.toDate(originalDeferWithMinorDate));
						
						reviewEndDate = DateFormatUtil
								.formateDateToMDY(originalAgendaLocalDate.plusMonths(reviewPeriod).minusDays(1).toDate());
					}
					
					clockStart = "Full Board";
					
				} else {
					reviewEndDate = DateFormatUtil
							.formateDateToMDY(localDate.plusYears(1).minusDays(1).toDate());
					
					approvalDate = currentDate;
				}
			}
			
			/*
			 * If Exempt Approval, set approval date as current date and review end date as current date + 3 years.
			 * */
			if (clockStart.equals("Exempt") && action.equals("APPROVE")) {
				//Calendar exemptCal = Calendar.getInstance();
				//exemptCal.add(Calendar.YEAR, +3);
				//exemptCal.add(Calendar.DATE, -1);
				reviewEndDate = DateFormatUtil
						.formateDateToMDY(localDate.plusYears(3).toDate());
				
				approvalDate = currentDate;
			}

			//String actionPath = "/"+ protocolForm.getProtocolFormType().getBaseTag() +"/approval-action";
			
			/*
			if (!elementExistsOrNot(actionPath, protocolMetaDataXml)) {
				protocolMetaDataXml = getXmlProcessor()
						.replaceOrAddNodeValueByPath(actionPath,
								protocolMetaDataXml, action);
			}*/
			
			//protocolFormMetaData = getXmlProcessor()
					//.replaceOrAddNodeValueByPath(actionPath,
							//protocolFormMetaData, action);

			if ((protocolForm.getProtocolFormType().equals(ProtocolFormType.STUDY_CLOSURE) && action.equals("ACKNOWLEDGED")) || (protocolForm.getProtocolFormType().equals(ProtocolFormType.NEW_SUBMISSION) && action.equals("IS_NOT_HSR"))){
				protocolMetaDataXml = getXmlProcessor()
						.replaceOrAddNodeValueByPath(
								"/protocol/original-study/close-date",
								protocolMetaDataXml, currentDate);
			}
			
			if (protocolForm.getProtocolFormType().equals(ProtocolFormType.CONTINUING_REVIEW) && action.equals("IS_NOT_HSR")){
				protocolMetaDataXml = getXmlProcessor()
						.replaceOrAddNodeValueByPath(
								"/protocol/most-recent-study/close-date",
								protocolMetaDataXml, currentDate);
			}
			
			if ((protocolForm.getProtocolFormType().equals(ProtocolFormType.CONTINUING_REVIEW) || protocolForm.getProtocolFormType().equals(ProtocolFormType.REPORTABLE_NEW_INFORMATION)) && action.equals("SUSPENDED_FOR_CAUSE")){
				protocolMetaDataXml = getXmlProcessor()
						.replaceOrAddNodeValueByPath(
								"/protocol/original-study/suspend-date",
								protocolMetaDataXml, agendaDateStr);
			}
			
			if (action.equals("DEFER_WITH_MINOR") && originalDeferWithMinorDate.isEmpty()) {
				String deferWithMinorApprovalDatePath = "/"+ protocolForm.getProtocolFormType().getBaseTag() +"/original-version/defer-with-minor-approval-date";
				protocolFormMetaData = getXmlProcessor()
						.replaceOrAddNodeValueByPath(
								deferWithMinorApprovalDatePath,
								protocolFormMetaData, agendaDateStr);
			}
			
			String statusPath = "";

			if (protocolForm.getProtocolFormType().equals(
					ProtocolFormType.NEW_SUBMISSION)) {
				statusPath = "/protocol/original-study/approval-status";
				
				/*
				if (!elementExistsOrNot(statusPath, protocolMetaDataXml)) {
					// the whole protocol approval status
					protocolMetaDataXml = getXmlProcessor()
							.replaceOrAddNodeValueByPath(statusPath,
									protocolMetaDataXml, clockStart);
				}
				*/
				protocolMetaDataXml = getXmlProcessor()
						.replaceOrAddNodeValueByPath(statusPath,
								protocolMetaDataXml, clockStart);

				String approvalDatePath = "/protocol/original-study/approval-date";

				protocolMetaDataXml = getXmlProcessor()
						.replaceOrAddNodeValueByPath(approvalDatePath,
								protocolMetaDataXml, approvalDate);
				
				protocolMetaDataXml = getXmlProcessor()
						.replaceOrAddNodeValueByPath("/protocol/most-recent-study/approval-end-date",
								protocolMetaDataXml, reviewEndDate);
				protocolMetaDataXml = getXmlProcessor()
						.replaceOrAddNodeValueByPath("/protocol/most-recent-study/approval-status",
								protocolMetaDataXml, clockStart);
				protocolMetaDataXml = getXmlProcessor()
						.replaceOrAddNodeValueByPath("/protocol/most-recent-study/approval-date",
								protocolMetaDataXml, approvalDate);
			} else if (protocolForm.getProtocolFormType().equals(ProtocolFormType.CONTINUING_REVIEW) || protocolForm.getProtocolFormType().equals(ProtocolFormType.HUMANITARIAN_USE_DEVICE_RENEWAL)){
				if (action.equals("DECLINED")) {
					clockStart = "Expedited";
				}
				
				statusPath = "/protocol/most-recent-study/approval-status";
				String approvalEndDatePath = "/protocol/most-recent-study/approval-end-date";
				String approvalDatePath = "/protocol/most-recent-study/approval-date";

				protocolMetaDataXml = getXmlProcessor()
						.replaceOrAddNodeValueByPath(approvalEndDatePath,
								protocolMetaDataXml, reviewEndDate);
				protocolMetaDataXml = getXmlProcessor()
						.replaceOrAddNodeValueByPath(statusPath,
								protocolMetaDataXml, clockStart);
				protocolMetaDataXml = getXmlProcessor()
						.replaceOrAddNodeValueByPath(approvalDatePath,
								protocolMetaDataXml, approvalDate);
			}

			protocolForm.setMetaDataXml(protocolFormMetaData);
			protocolForm = protocolFormDao.saveOrUpdate(protocolForm);
			
			protocol.setMetaDataXml(protocolMetaDataXml);
			logger.debug("save ...");
			protocol = protocolDao.saveOrUpdate(protocol);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	protected void setMinorContingencyFlag(ProtocolForm protocolForm) {
		String protocolFormMetaData = protocolForm.getMetaDataXml();
		
		try {
			protocolFormMetaData = getXmlProcessor().replaceOrAddNodeValueByPath("/"+ protocolForm.getProtocolFormType().getBaseTag() +"/summary/irb-determination/recent-motion", protocolFormMetaData, "Defer with minor contingencies by Expedited Review");
			
			protocolForm.setMetaDataXml(protocolFormMetaData);
			
			protocolFormDao.saveOrUpdate(protocolForm);
		} catch (Exception e) {
			
		}
		
	}

	private Map<Committee, ProtocolFormStatusEnum> committeeFormStatusPair = new HashMap<Committee, ProtocolFormStatusEnum>();
	{
		committeeFormStatusPair.put(Committee.PHARMACY_REVIEW,
				ProtocolFormStatusEnum.UNDER_PHARMACY_REVIEW);
		committeeFormStatusPair.put(Committee.BUDGET_REVIEW,
				ProtocolFormStatusEnum.UNDER_BUDGET_REVIEW);
		committeeFormStatusPair.put(Committee.COVERAGE_REVIEW,
				ProtocolFormStatusEnum.UNDER_COVERAGE_REVIEW);
		committeeFormStatusPair.put(Committee.HOSPITAL_SERVICES,
				ProtocolFormStatusEnum.UNDER_HOSPITAL_SERVICES_REVIEW);
		committeeFormStatusPair.put(Committee.DEPARTMENT_CHAIR,
				ProtocolFormStatusEnum.UNDER_DEPARTMENT_REVIEW);
		committeeFormStatusPair.put(Committee.COLLEGE_DEAN,
				ProtocolFormStatusEnum.UNDER_COLLEGE_REVIEW);
		committeeFormStatusPair.put(Committee.PI,
				ProtocolFormStatusEnum.PENDING_PI_SIGN_OFF);
		committeeFormStatusPair.put(Committee.IRB_ASSIGNER,
				ProtocolFormStatusEnum.UNDER_IRB_PREREVIEW);
	}

	private Map<Committee, ProtocolStatusEnum> committeeObjectStatusPair = new HashMap<Committee, ProtocolStatusEnum>();
	{
		committeeObjectStatusPair.put(Committee.PHARMACY_REVIEW,
				ProtocolStatusEnum.UNDER_PHARMACY_REVIEW);
		committeeObjectStatusPair.put(Committee.BUDGET_REVIEW,
				ProtocolStatusEnum.UNDER_BUDGET_REVIEW);
		committeeObjectStatusPair.put(Committee.COVERAGE_REVIEW,
				ProtocolStatusEnum.UNDER_COVERAGE_REVIEW);
		committeeObjectStatusPair.put(Committee.HOSPITAL_SERVICES,
				ProtocolStatusEnum.UNDER_HOSPITAL_SERVICES_REVIEW);
		committeeObjectStatusPair.put(Committee.DEPARTMENT_CHAIR,
				ProtocolStatusEnum.UNDER_DEPARTMENT_REVIEW);
		committeeObjectStatusPair.put(Committee.COLLEGE_DEAN,
				ProtocolStatusEnum.UNDER_COLLEGE_REVIEW);
		committeeObjectStatusPair.put(Committee.PI,
				ProtocolStatusEnum.PENDING_PI_SIGN_OFF);
		committeeObjectStatusPair.put(Committee.IRB_ASSIGNER,
				ProtocolStatusEnum.UNDER_IRB_PREREVIEW);
	}

	// get the form status if this committee is in review
	@Override
	protected String getCommitteeReviewFormStatus(Committee nextCommittee) {
		if (nextCommittee != null
				&& committeeFormStatusPair.containsKey(nextCommittee)) {
			return committeeFormStatusPair.get(nextCommittee).toString();
		}

		return null;
	}

	@Override
	protected String getCommitteeReviewObjectStatus(Committee nextCommittee) {
		if (nextCommittee != null
				&& committeeObjectStatusPair.containsKey(nextCommittee)) {
			return committeeObjectStatusPair.get(nextCommittee).toString();
		}

		return null;
	}
	
	private void updateBudget(Committee committee, Form form) {		
		try{
			ProtocolFormXmlData pharmacyXmlData = protocolFormXmlDataDao.getLastProtocolFormXmlDataByProtocolFormIdAndType(form.getFormId(), ProtocolFormXmlDataType.PHARMACY);
			
			ProtocolFormXmlData budgetXmlData = protocolFormXmlDataDao.getLastProtocolFormXmlDataByProtocolFormIdAndType(form.getFormId(), ProtocolFormXmlDataType.BUDGET);
			
			if (pharmacyXmlData != null && budgetXmlData != null && budgetXmlData.getXmlData() != null && !budgetXmlData.getXmlData().isEmpty()){
				XPath xPath = getXmlProcessor().getXPathInstance();
				
				Document currentPharmacyXmlDataDom = getXmlProcessor().loadXmlStringToDOM(pharmacyXmlData.getXmlData());
				
				Document currentBudgetXmlDataDom = getXmlProcessor().loadXmlStringToDOM(budgetXmlData.getXmlData());
				
				Element pharmacyEl = (Element) (xPath.evaluate("/pharmacy",
						currentPharmacyXmlDataDom, XPathConstants.NODE));
				
				String waivedOrNot = (pharmacyEl.getAttribute("waived").equals("true"))?"(WAIVED)":"";
				
				Element expensesEl = (Element) (xPath.evaluate("/budget/expenses", currentBudgetXmlDataDom, XPathConstants.NODE));
				
				Element initialExpenseEl = (Element) (xPath.evaluate("/budget/expenses/expense[@type=\"Initial Cost\" and @subtype=\"Pharmacy Fee\"]",
						currentBudgetXmlDataDom, XPathConstants.NODE));
				
				if (initialExpenseEl != null){
					initialExpenseEl.setAttribute("cost", pharmacyEl.getAttribute("total"));
					initialExpenseEl.setAttribute("description", "Pharmacy Fee " + waivedOrNot);
				} else {
					Element newInitialExpenseNode = currentBudgetXmlDataDom.createElement("expense");
					newInitialExpenseNode.setAttribute("type", "Initial Cost");
					newInitialExpenseNode.setAttribute("subtype", "Pharmacy Fee");
					newInitialExpenseNode.setAttribute("cost", pharmacyEl.getAttribute("total"));
					newInitialExpenseNode.setAttribute("fa", "0");
					newInitialExpenseNode.setAttribute("faenabled", "false");
					newInitialExpenseNode.setAttribute("external", "true");
					newInitialExpenseNode.setAttribute("count", "1");
					newInitialExpenseNode.setAttribute("description", "Pharmacy Fee " + waivedOrNot);
					newInitialExpenseNode.setAttribute("notes", "");
					
					expensesEl.appendChild(newInitialExpenseNode);
				}
				
				String otherPharmacyExpensesXPath = "/pharmacy/expenses/expense[@type!='simc' and @type!='drug']";
				
				xPath.reset();
				
				NodeList pharmacyExpensesLst = (NodeList) (xPath.evaluate(otherPharmacyExpensesXPath,
						currentPharmacyXmlDataDom, XPathConstants.NODESET));
								
				if (pharmacyExpensesLst.getLength() > 0){
					for (int i=0; i<pharmacyExpensesLst.getLength(); i++){
						Element otherPharmacyFeeEl = (Element) pharmacyExpensesLst.item(i);
						
						//String otherFeeWaivedOrNot = (otherPharmacyFeeEl.getAttribute("waived").equals("true"))?"(WAIVED)":"";
						
						long cost = Long.valueOf(otherPharmacyFeeEl.getAttribute("cost")) * Long.valueOf(otherPharmacyFeeEl.getAttribute("count"));
						
						if (waivedOrNot.equals("(WAIVED)")){
							cost = 0;
						}
						
						Element invoicableExpenseEl = (Element) (xPath.evaluate("/budget/expenses/expense[@type=\"Invoicable\" and @subtype=\"Pharmacy Fee\" and @description[contains(.,\""+ otherPharmacyFeeEl.getAttribute("description") +"\")]]",
								currentBudgetXmlDataDom, XPathConstants.NODE));

						if (invoicableExpenseEl != null){
							invoicableExpenseEl.setAttribute("cost", String.valueOf(cost));
							invoicableExpenseEl.setAttribute("description", "" + otherPharmacyFeeEl.getAttribute("description") + waivedOrNot);
							invoicableExpenseEl.setAttribute("notes", "");
						} else {
							Element newInvoicableExpenseNode = currentBudgetXmlDataDom.createElement("expense");
							newInvoicableExpenseNode.setAttribute("type", "Invoicable");
							newInvoicableExpenseNode.setAttribute("subtype", "Pharmacy Fee");
							newInvoicableExpenseNode.setAttribute("cost", String.valueOf(cost));
							newInvoicableExpenseNode.setAttribute("fa", "0");
							newInvoicableExpenseNode.setAttribute("faenabled", "false");
							newInvoicableExpenseNode.setAttribute("external", "true");
							newInvoicableExpenseNode.setAttribute("count", "1");
							newInvoicableExpenseNode.setAttribute("description", "" + otherPharmacyFeeEl.getAttribute("description") + waivedOrNot);
							newInvoicableExpenseNode.setAttribute("notes", "");
							
							expensesEl.appendChild(newInvoicableExpenseNode);
						}
					}
				}
				
				budgetXmlData.setXmlData(DomUtils.elementToString(currentBudgetXmlDataDom));
				protocolFormXmlDataDao.saveOrUpdate(budgetXmlData);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void updateAssignedCommittees(Form form,
			List<Committee> selectedCommittees) {
		ProtocolForm protocolForm = (ProtocolForm) form;
		
		String protocolFormMetaData = protocolForm.getMetaDataXml();
		
		try {
			Document doc = getXmlProcessor().loadXmlStringToDOM(protocolFormMetaData);
			
			XPath xpath = getXmlProcessor().getXPathInstance();
			
			Map<String, Object> resultMap = Maps.newHashMap();
			
			Element assignedCommitteeEl = (Element) xpath
					.evaluate(
							"/"+ protocolForm.getProtocolFormType().getBaseTag() +"/assigned-committee",
							doc,
							XPathConstants.NODE);
			
			if (assignedCommitteeEl == null) {
				resultMap = getXmlProcessor().addElementByPath("/"+ protocolForm.getProtocolFormType().getBaseTag() +"/assigned-committee", protocolFormMetaData, "<assign-committee />", false);
				
				protocolFormMetaData = resultMap.get("finalXml")
						.toString();
			}
			
			for (Committee committee : selectedCommittees) {
				Element individualCommitteeEl = (Element) xpath
						.evaluate(
								"/"+ protocolForm.getProtocolFormType().getBaseTag() +"/assigned-committee/individual-committee[@name='"+ committee.toString() +"']",
								doc,
								XPathConstants.NODE);
				
				if (individualCommitteeEl == null){
					String subEl = "<individual-committee name=\""+ committee.toString() +"\" />";
					
					resultMap = getXmlProcessor()
							.addSubElementToElementIdentifiedByXPath(
									"/"
											+ protocolForm.getProtocolFormType()
													.getBaseTag()
											+ "/assigned-committee",
									protocolFormMetaData,
									subEl,
									false);
					protocolFormMetaData = resultMap.get("finalXml")
							.toString();
				}
			}
			
			protocolForm.setMetaDataXml(protocolFormMetaData);
			protocolFormDao.saveOrUpdate(protocolForm);
			
		} catch (Exception e) {
			
		}
		
	}
	
	private void setRequiredContractBeforeIRB(ProtocolForm protocolForm) {
		
		String protocolFormMetaData = protocolForm.getMetaDataXml();
		
		try {
			protocolFormMetaData = getXmlProcessor().replaceOrAddNodeValueByPath("/protocol/contract/require-contract-before-irb", protocolFormMetaData, "y");
			
			protocolForm.setMetaDataXml(protocolFormMetaData);
			
			protocolFormDao.saveOrUpdate(protocolForm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private boolean canPushToEpic(Protocol protocol) {
		boolean canPushToEpic = false;
		
		String protocolMetaData = protocol.getMetaDataXml();
		
		boolean isUAMSStudy = protocolService.checkStudyCharacteristic(protocolMetaData).get("isUAMSStudy");
		
		boolean isPushedToEpic = protocolService.isPushedToEpic(protocolMetaData);
		
		if (isUAMSStudy && !isPushedToEpic) {
			canPushToEpic = true;
		}
		
		return canPushToEpic;
	}
	
	protected void pushToEpic(ProtocolForm protocolForm) {
		Protocol protocol = protocolForm.getProtocol();
		
		String protocolMetaData = protocol.getMetaDataXml();
		
		if (canPushToEpic(protocol)) {
			try {
				XmlHandler xmlHandler = XmlHandlerFactory.newXmlHandler();
				String epicTitle = xmlHandler.getSingleStringValueByXPath(protocolMetaData, "/protocol/epic/epic-title");
				String epicSummary = xmlHandler.getSingleStringValueByXPath(protocolMetaData, "/protocol/epic/epic-desc");
				
				if (epicTitle.isEmpty()) {
					epicTitle = xmlHandler.getSingleStringValueByXPath(protocolMetaData, "/protocol/title");
				}
				
				if (epicSummary.isEmpty()) {
					epicSummary = protocolService.populateEpicDesc(protocolMetaData);
				}
				
				studyDefinitionWSClient.retrieveProtocolDefResponse("" + protocol.getId(), epicTitle, epicSummary, protocolMetaData);
				
				protocolService.addPushedToEpic(protocol);
				
				Track track = protocolTrackService.getOrCreateTrack("PROTOCOL",
						protocol.getId());

				Document logsDoc = protocolTrackService.getLogsDocument(track);

				Element logEl = logsDoc.createElement("log");
				
				String logId = UUID.randomUUID().toString();
				
				Date now = new Date();
				
				logEl.setAttribute("id", logId);
				logEl.setAttribute("parent-id", logId);
				logEl.setAttribute("action-user-id", "0");
				logEl.setAttribute("actor", "System");
				logEl.setAttribute("date-time", DateFormatUtil.formateDate(now));
				logEl.setAttribute("event-type", "PUSH_TO_EPIC");
				logEl.setAttribute("form-id", "0");
				logEl.setAttribute("parent-form-id", "0");
				logEl.setAttribute("form-type", "PROTOCOL");
				logEl.setAttribute("log-type", "ACTION");
				logEl.setAttribute("timestamp", String.valueOf(now.getTime()));

				String message = "Protocol has been pushed to EPIC by system.";

				logEl.setTextContent(message);

				logsDoc.getDocumentElement().appendChild(logEl);

				track = protocolTrackService.updateTrack(track, logsDoc);
				
			} catch (Exception e) {

				logger.error("failed: ", e);
			}
		}
		
	}
	
	@Override
	public void triggerEvents(Form form, User user, Committee committee, String eventsTemplate, String action, String condition) throws IOException, SAXException {
		ProtocolForm protocolForm = (ProtocolForm) form;

		XmlProcessor xmlProcessor = getXmlProcessor();

		Document eventsTemplateDoc = xmlProcessor
				.loadXmlStringToDOM(eventsTemplate);

		NodeList events = eventsTemplateDoc.getDocumentElement()
				.getElementsByTagName("event");
		
		for (int i = 0; i < events.getLength(); i++) {
			Element currentEventEl = (Element) events.item(i);
			
			logger.debug("event type: " + currentEventEl.getTextContent());
			switch (currentEventEl.getTextContent()) {
			case "SET_SUBMIT_DATE": 
				this.setSubmitDate(protocolForm);
				break;
			case "SET_FORM_SUBMIT_DATE":
				this.setFormSubmitDate(protocolForm);
				break;
			case "UPDATE_BUDGET":
				if (committee.equals(Committee.PHARMACY_REVIEW)) {
					this.updateBudget(committee, protocolForm);
				}
				
				break;
			case "SET_REQUIRED_CONTRACT_BEFORE_IRB":
				this.setRequiredContractBeforeIRB(protocolForm);
				break;
			case "SEND_PB_NOTIFICATION":
				this.sendBudgetApprovePBNotification(protocolForm);
				break;
			case "UPDATE_APPROVAL_DATE_STATUS":
				this.updateApprovalStatusAndDate(protocolForm, action, (currentEventEl.getAttribute("clock-start") != null && !currentEventEl.getAttribute("clock-start").isEmpty())?currentEventEl.getAttribute("clock-start"):"", condition);
				break;
			case "PUSH_TO_EPIC":
				this.pushToEpic(protocolForm);
				break;
			case "GENERATE_EPIC_CDM":
				this.generateEpicCDM(protocolForm);
				break;
			case "ASSIGN_TO_AGENDA":
				this.assignToAgenda(protocolForm);
				break;
			//case "CLEAN_RECENT_MOTION":
				//this.cleanRecentMotion(protocolForm);
				//break;
			case "SET_MINOR_CONTINGENCY_FLAG":
				this.setMinorContingencyFlag(protocolForm);
			}
		}
		
	}

	public ProtocolStatusDao getProtocolStatusDao() {
		return protocolStatusDao;
	}

	@Autowired(required = true)
	public void setProtocolStatusDao(ProtocolStatusDao protocolStatusDao) {
		this.protocolStatusDao = protocolStatusDao;
	}

	public ProtocolFormStatusDao getProtocolFormStatusDao() {
		return protocolFormStatusDao;
	}

	@Autowired(required = true)
	public void setProtocolFormStatusDao(
			ProtocolFormStatusDao protocolFormStatusDao) {
		this.protocolFormStatusDao = protocolFormStatusDao;
	}

	public ProtocolFormCommitteeStatusDao getProtocolFormCommitteeStatusDao() {
		return protocolFormCommitteeStatusDao;
	}

	@Autowired(required = true)
	public void setProtocolFormCommitteeStatusDao(
			ProtocolFormCommitteeStatusDao protocolFormCommitteeStatusDao) {
		this.protocolFormCommitteeStatusDao = protocolFormCommitteeStatusDao;
	}

	public ProtocolMetaDataXmlService getProtocolMetaDataXmlService() {
		return protocolMetaDataXmlService;
	}

	@Autowired(required = true)
	public void setProtocolMetaDataXmlService(
			ProtocolMetaDataXmlService protocolMetaDataXmlService) {
		this.protocolMetaDataXmlService = protocolMetaDataXmlService;
	}

	public ProtocolTrackService getProtocolTrackService() {
		return protocolTrackService;
	}

	@Autowired(required = true)
	public void setProtocolTrackService(
			ProtocolTrackService protocolTrackService) {
		this.protocolTrackService = protocolTrackService;
	}

	public ProtocolEmailService getProtocolEmailService() {
		return protocolEmailService;
	}

	@Autowired(required = true)
	public void setProtocolEmailService(
			ProtocolEmailService protocolEmailService) {
		this.protocolEmailService = protocolEmailService;
	}

	public AgendaDao getAgendaDao() {
		return agendaDao;
	}

	@Autowired(required = true)
	public void setAgendaDao(AgendaDao agendaDao) {
		this.agendaDao = agendaDao;
	}

	public AgendaItemDao getAgendaItemDao() {
		return agendaItemDao;
	}

	@Autowired(required = true)
	public void setAgendaItemDao(AgendaItemDao agendaItemDao) {
		this.agendaItemDao = agendaItemDao;
	}

	public ProtocolDao getProtocolDao() {
		return protocolDao;
	}

	@Autowired(required = true)
	public void setProtocolDao(ProtocolDao protocolDao) {
		this.protocolDao = protocolDao;
	}

	public ProtocolFormDao getProtocolFormDao() {
		return protocolFormDao;
	}

	@Autowired(required = true)
	public void setProtocolFormDao(ProtocolFormDao protocolFormDao) {
		this.protocolFormDao = protocolFormDao;
	}

	public ProtocolFormXmlDataDocumentDao getProtocolFormXmlDataDocumentDao() {
		return protocolFormXmlDataDocumentDao;
	}
	
	@Autowired(required = true)
	public void setProtocolFormXmlDataDocumentDao(
			ProtocolFormXmlDataDocumentDao protocolFormXmlDataDocumentDao) {
		this.protocolFormXmlDataDocumentDao = protocolFormXmlDataDocumentDao;
	}

	public RelationService getRelationService() {
		return relationService;
	}
	
	@Autowired(required = true)
	public void setRelationService(RelationService relationService) {
		this.relationService = relationService;
	}

	public ProtocolFormXmlDataDao getProtocolFormXmlDataDao() {
		return protocolFormXmlDataDao;
	}
	
	@Autowired(required = true)
	public void setProtocolFormXmlDataDao(ProtocolFormXmlDataDao protocolFormXmlDataDao) {
		this.protocolFormXmlDataDao = protocolFormXmlDataDao;
	}

	public ProtocolFormService getProtocolFormService() {
		return protocolFormService;
	}
	
	@Autowired(required = true)
	public void setProtocolFormService(ProtocolFormService protocolFormService) {
		this.protocolFormService = protocolFormService;
	}

	public ProtocolService getProtocolService() {
		return protocolService;
	}
	
	@Autowired(required = true)
	public void setProtocolService(ProtocolService protocolService) {
		this.protocolService = protocolService;
	}

	public StudyDefinitionWSClient getStudyDefinitionWSClient() {
		return studyDefinitionWSClient;
	}
	
	@Autowired(required = true)
	public void setStudyDefinitionWSClient(StudyDefinitionWSClient studyDefinitionWSClient) {
		this.studyDefinitionWSClient = studyDefinitionWSClient;
	}


	public AuditService getAuditService() {
		return auditService;
	}

	@Autowired(required = true)
	public void setAuditService(AuditService auditService) {
		this.auditService = auditService;
	}


	public EpicCdmByCptCodeDao getEpicCdmByCptCodeDao() {
		return epicCdmByCptCodeDao;
	}

	@Autowired(required = true)
	public void setEpicCdmByCptCodeDao(EpicCdmByCptCodeDao epicCdmByCptCodeDao) {
		this.epicCdmByCptCodeDao = epicCdmByCptCodeDao;
	}


	public SendBudgetApprovedNotificationForPBService getSendBudgetApprovedNotificationForPBService() {
		return sendBudgetApprovedNotificationForPBService;
	}

	@Autowired(required = true)
	public void setSendBudgetApprovedNotificationForPBService(
			SendBudgetApprovedNotificationForPBService sendBudgetApprovedNotificationForPBService) {
		this.sendBudgetApprovedNotificationForPBService = sendBudgetApprovedNotificationForPBService;
	}
}

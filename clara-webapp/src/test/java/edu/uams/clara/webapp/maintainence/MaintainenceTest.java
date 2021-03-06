package edu.uams.clara.webapp.maintainence;

import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Sets;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.uams.clara.core.util.xml.XmlHandler;
import edu.uams.clara.core.util.xml.XmlHandlerFactory;
import edu.uams.clara.webapp.common.dao.usercontext.UserDao;
import edu.uams.clara.webapp.common.domain.usercontext.User;
import edu.uams.clara.webapp.common.service.form.FormService;
import edu.uams.clara.webapp.common.service.form.impl.FormServiceImpl.UserSearchField;
import edu.uams.clara.webapp.common.util.DateFormatUtil;
import edu.uams.clara.webapp.maintainence.dao.MaintainenceDao;
import edu.uams.clara.webapp.protocol.dao.ProtocolDao;
import edu.uams.clara.webapp.protocol.dao.businesslogicobject.ProtocolStatusDao;
import edu.uams.clara.webapp.protocol.dao.protocolform.ProtocolFormDao;
import edu.uams.clara.webapp.protocol.dao.protocolform.ProtocolFormXmlDataDao;
import edu.uams.clara.webapp.protocol.domain.Protocol;
import edu.uams.clara.webapp.protocol.domain.businesslogicobject.ProtocolStatus;
import edu.uams.clara.webapp.protocol.domain.protocolform.ProtocolFormXmlData;
import edu.uams.clara.webapp.protocol.domain.protocolform.enums.ProtocolFormXmlDataType;
import edu.uams.clara.webapp.xml.processor.XmlProcessor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "file:src/test/java/edu/uams/clara/webapp/maintainence/MaintainenceTest-context.xml" })
public class MaintainenceTest {
	private final static Logger logger = LoggerFactory
			.getLogger(MaintainenceTest.class);

	private MaintainenceDao maintainenceDao;

	private ProtocolFormXmlDataDao protocolFormXmlDataDao;

	private ProtocolStatusDao protocolStatusDao;

	private ProtocolDao protocolDao;

	private ProtocolFormDao protocolFormDao;

	private UserDao userDao;

	private XmlProcessor xmlProcessor;

	private FormService formService;

	@Test
	public void generateEpicCDM() throws Exception {

		List<BigInteger> formIds =(List<BigInteger>) maintainenceDao.listProtocolFormXmlDatasWithLatestApprovedBudget();
		for(BigInteger formIDBingInt : formIds){
			long formID = Long.valueOf(formIDBingInt.toString());
			/*if(protocolFormDao.findById(formID).getProtocol().getId()<200000){
				continue;
			}*/
			List<ProtocolFormXmlData> protocolFormXmlDataLst = maintainenceDao.listProtocolFormXmlDatasHaveNormalProcedure(formID);

			Date now = new Date();

			for (ProtocolFormXmlData pfxd : protocolFormXmlDataLst){
				String budgetXmlData = pfxd.getXmlData();

				long protocolId = pfxd.getProtocolForm().getProtocol().getId();

				CSVWriter writer = new CSVWriter(new FileWriter("C:\\Data\\epic\\"+ protocolId +" HB "+ DateFormatUtil.formateDateToMDY(now).replace("/", "-") +".csv"));

				try {
					List<String> cpdCodeLst = xmlProcessor.getAttributeValuesByPathAndAttributeName("/budget/epochs/epoch/procedures/procedure[@type=\"normal\"]", budgetXmlData, "cptcode");

					if (cpdCodeLst.size() > 0){
						Set<String> cpdCodeSet = new HashSet<String>(cpdCodeLst);

						for (String cptCode : cpdCodeSet){

							try {

								//String epicCdmCode = maintainenceDao.getEpicCdmByCptCode(cptCode);
								//logger.debug("cptCode: " + cptCode);
								String cost = xmlProcessor.getAttributeValueByPathAndAttributeName("/budget/epochs/epoch/procedures/procedure[@type=\"normal\" and @cptcode=\""+ cptCode +"\"]/hosp", budgetXmlData, "cost");
								//logger.debug("cost: " + cost);
								//logger.debug("protooclId: " + protocolId + "cpt code: " + cptCode + " epic cdm code: " + epicCdmCode + " cost: " + cost);

								String[] entry = {String.valueOf(protocolId),cptCode, cost};

								writer.writeNext(entry);

							} catch (Exception e) {

							}
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				writer.close();
			}
		}

	}

	//@Test
	public void addMissingStatusToProtocol() throws Exception {
		List<Protocol> protocolLst = maintainenceDao.listProtocolWithoutStatusInMetaData();

		logger.debug("size: " + protocolLst.size());

		for (Protocol p : protocolLst) {
			String protocolMeta = p.getMetaDataXml();

			ProtocolStatus latestProtocolStatus = protocolStatusDao.findProtocolStatusByProtocolId(p.getId());

			protocolMeta = xmlProcessor.replaceOrAddNodeValueByPath("/protocol/status", protocolMeta, latestProtocolStatus.getProtocolStatus().getDescription());

			Map<String, String> attributes = new HashMap<String, String>();
			attributes.put("priority", latestProtocolStatus.getProtocolStatus().getPriorityLevel());

			protocolMeta = xmlProcessor.addAttributesByPath(
					"/protocol/status", protocolMeta, attributes);

			p.setMetaDataXml(protocolMeta);
			p = protocolDao.saveOrUpdate(p);
		}
	}

	//@Test
	public void addMissingCitiIDs() throws Exception {
		CSVReader reader = new CSVReader(new FileReader("C:\\Data\\MissingCITI.csv"));

		String [] nextLine;

		while ((nextLine = reader.readNext()) != null) {
	        // nextLine[] is an array of values from the line

	        String userName = nextLine[0];
	        String citiId = nextLine[1];

	        try {
			User user = userDao.getUserByUsername(userName);

		        String profileXml = user.getProfile();

		        if (profileXml == null || profileXml.trim().isEmpty() || profileXml.equals("null")){
				profileXml = "<metadata><citi-id>"+ citiId +"</citi-id></metadata>";
		        } else {
				profileXml = xmlProcessor.replaceOrAddNodeValueByPath("/metadata/citi-id", profileXml, citiId);
		        }

		        user.setProfile(profileXml);
		        userDao.saveOrUpdate(user);
	        } catch (Exception e) {
			e.printStackTrace();
			logger.debug("cannot find user by username: " + userName);
	        }

	    }
	}

	//@Test
	public void addMissingApprovalEndDate() throws Exception {
		List<Protocol> protocolLst = maintainenceDao.listProtocolWithoutCorrectApprovalEndDate("EXPEDITED_APPROVED");

		logger.debug("size: " + protocolLst.size());

		XmlHandler xmlHandler = XmlHandlerFactory.newXmlHandler();

		for (Protocol p : protocolLst){
			logger.debug("Protocol Id: " + p.getId());
			String protocolMetaData = p.getMetaDataXml();

			String approvalStatus = xmlHandler.getSingleStringValueByXPath(protocolMetaData, "/protocol/most-recent-study/approval-status");

			if (approvalStatus.equals("Expedited")){
				String approvalDate = xmlHandler.getSingleStringValueByXPath(protocolMetaData, "/protocol/most-recent-study/approval-date");
				String approvalEndDate = xmlHandler.getSingleStringValueByXPath(protocolMetaData, "/protocol/most-recent-study/approval-end-date");

				if (approvalEndDate.isEmpty()){
					try {
						Date date = new SimpleDateFormat("MM/dd/yyyy").parse(approvalDate);

						LocalDate localDate = new LocalDate(date);


						protocolMetaData = xmlProcessor.replaceOrAddNodeValueByPath("/protocol/most-recent-study/approval-end-date", protocolMetaData, DateFormatUtil
								.formateDateToMDY(localDate.plusYears(1).minusDays(1).toDate()));

						p.setMetaDataXml(protocolMetaData);
						protocolDao.saveOrUpdate(p);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		}
	}

	//@Test
	public void copyBudgetFieldToMetaData() throws Exception {
		List<ProtocolFormXmlData> pfxdLst = protocolFormXmlDataDao.listProtocolformXmlDatasByType(ProtocolFormXmlDataType.ARCHIVE);

		logger.debug("size: " + pfxdLst.size());

		XmlHandler xmlHandler = XmlHandlerFactory.newXmlHandler();

		for (ProtocolFormXmlData pfxd : pfxdLst){
			String xmlData = pfxd.getXmlData();
			String crimsonHasBudget  = xmlHandler.getSingleStringValueByXPath(xmlData, "/protocol/crimson/has-budget");

			if (!crimsonHasBudget.isEmpty()){
				Protocol p = pfxd.getProtocolForm().getProtocol();
				logger.debug("Protocol Id: " + p.getId());

				String metaData = p.getMetaDataXml();

				metaData = xmlProcessor.replaceOrAddNodeValueByPath("/protocol/crimson/has-budget", metaData, crimsonHasBudget);

				p.setMetaDataXml(metaData);
				protocolDao.saveOrUpdate(p);
			}
		}
	}

	//@Test
	public void updatePharmacyExpenseDesc() throws Exception {
		List<ProtocolFormXmlData> pfxdLst = maintainenceDao.listProtocolFormXmlDataWithPharmacyFee();

		logger.debug("size: " + pfxdLst.size());


		for (ProtocolFormXmlData pfxd : pfxdLst){
			String xmlData = pfxd.getXmlData();

			List<String> descList = xmlProcessor.getAttributeValuesByPathAndAttributeName("/budget/expenses/expense[@type=\"Invoicable\" and @subtype=\"Pharmacy Fee\"]", xmlData, "description");

			if (descList.size() > 0) {
				for (String desc : descList) {
					xmlData = xmlProcessor.replaceAttributeValueByPathAndAttributeName("/budget/expenses/expense[@type=\"Invoicable\" and @subtype=\"Pharmacy Fee\" and @description=\""+ desc +"\"]", "description", xmlData, "Pharmacy: "+ desc +"");

					pfxd.setXmlData(xmlData);
					protocolFormXmlDataDao.saveOrUpdate(pfxd);
				}
			}
		}
	}

	//@Test
	public void getPIAndSC() throws Exception {
		List<Protocol> tempProtocols = maintainenceDao.listProtocolFromTempList();

		logger.debug("size: " + tempProtocols.size());

		CSVWriter writer = new CSVWriter(new FileWriter("C:\\Data\\maint\\temp.csv"));

		for (Protocol p : tempProtocols) {
			String protocolMeta = p.getMetaDataXml();

			List<User> piUsers = getFormService().getUsersByKeywordAndSearchField("Principal Investigator", protocolMeta, UserSearchField.ROLE);

			List<User> scUsers = getFormService().getUsersByKeywordAndSearchField("Study Coordinator", protocolMeta, UserSearchField.ROLE);

			String pi = "";
			if (piUsers != null && !piUsers.isEmpty()) {
				pi = piUsers.get(0).getPerson().getFullname();
			}

			if (scUsers == null || scUsers.isEmpty()) {
				String [] entry = {String.valueOf(p.getId()), pi, ""};

				writer.writeNext(entry);
			} else {
				for (User u : scUsers) {
					String [] entry = {String.valueOf(p.getId()), pi, u.getPerson().getFullname()};

					writer.writeNext(entry);
				}
			}

		}

		writer.close();
	}

	public MaintainenceDao getMaintainenceDao() {
		return maintainenceDao;
	}

	@Autowired(required = true)
	public void setMaintainenceDao(MaintainenceDao maintainenceDao) {
		this.maintainenceDao = maintainenceDao;
	}

	public ProtocolFormXmlDataDao getProtocolFormXmlDataDao() {
		return protocolFormXmlDataDao;
	}

	@Autowired(required = true)
	public void setProtocolFormXmlDataDao(ProtocolFormXmlDataDao protocolFormXmlDataDao) {
		this.protocolFormXmlDataDao = protocolFormXmlDataDao;
	}

	public XmlProcessor getXmlProcessor() {
		return xmlProcessor;
	}

	@Autowired(required = true)
	public void setXmlProcessor(XmlProcessor xmlProcessor) {
		this.xmlProcessor = xmlProcessor;
	}

	public ProtocolStatusDao getProtocolStatusDao() {
		return protocolStatusDao;
	}

	@Autowired(required = true)
	public void setProtocolStatusDao(ProtocolStatusDao protocolStatusDao) {
		this.protocolStatusDao = protocolStatusDao;
	}

	public ProtocolDao getProtocolDao() {
		return protocolDao;
	}

	@Autowired(required = true)
	public void setProtocolDao(ProtocolDao protocolDao) {
		this.protocolDao = protocolDao;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	@Autowired(required = true)
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public FormService getFormService() {
		return formService;
	}

	@Autowired(required = true)
	public void setFormService(FormService formService) {
		this.formService = formService;
	}

	public ProtocolFormDao getProtocolFormDao() {
		return protocolFormDao;
	}

	@Autowired(required = true)
	public void setProtocolFormDao(ProtocolFormDao protocolFormDao) {
		this.protocolFormDao = protocolFormDao;
	}
}

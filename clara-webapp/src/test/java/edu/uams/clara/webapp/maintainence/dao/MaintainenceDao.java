package edu.uams.clara.webapp.maintainence.dao;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.transaction.annotation.Transactional;

import edu.uams.clara.webapp.protocol.domain.Protocol;
import edu.uams.clara.webapp.protocol.domain.protocolform.ProtocolFormXmlData;

public class MaintainenceDao {

	private EntityManager em;

	@Transactional(readOnly = true)
	public List<ProtocolFormXmlData> listProtocolFormXmlDatasHaveNormalProcedure(long protocolFormId){
		String qry = "SELECT pfxd.* FROM protocol_form_xml_data as pfxd"
				+ " WHERE  pfxd.retired = :retired"
				+ " AND pfxd.protocol_form_id = :protocolFormId"
				+ " AND pfxd.protocol_form_xml_data_type = 'BUDGET'"
				+ " AND pfxd.xml_data.exist('/budget/epochs/epoch/procedures/procedure[@type=\"normal\"]')=1";
		TypedQuery<ProtocolFormXmlData> q = (TypedQuery<ProtocolFormXmlData>) em
				.createNativeQuery(qry, ProtocolFormXmlData.class);
		q.setHint("org.hibernate.cacheable", false);
		q.setParameter("retired", Boolean.FALSE);
		q.setParameter("protocolFormId", protocolFormId);
		//Query query = em.createNativeQuery(qry);
		//query.setParameter("retired", Boolean.FALSE);
		//query.setParameter("protocolId", protocolId);

		return q.getResultList();
	}

	@Transactional(readOnly = true)
	public List<BigInteger> listProtocolFormXmlDatasWithLatestApprovedBudget(){
		String qry = "SELECT max(pf.id) from protocol_form pf, protocol_form_status  pfs, protocol_form_xml_data  pfxd"
				+ " WHERE  pf.retired = :retired"
				+ " AND pf.id = pfs.protocol_form_id AND pfs.protocol_form_status IN ('IRB_ACKNOWLEDGED','IRB_APPROVED','EXPEDITED_APPROVED')"
				+ " AND pfs.retired = :retired"
				+ " AND pfxd.protocol_form_xml_data_type ='BUDGET'"
				+ " AND pfxd.retired =:retired AND pfxd.protocol_form_id = pf.id"
				+ " group by pf.protocol_id";
		/*TypedQuery<ProtocolFormXmlData> q = (TypedQuery<ProtocolFormXmlData>) em
				.createNativeQuery(qry, ProtocolFormXmlData.class);*/
		Query q = em.createNativeQuery(qry);

		q.setHint("org.hibernate.cacheable", false);
		q.setParameter("retired", Boolean.FALSE);
		//Query query = em.createNativeQuery(qry);
		//query.setParameter("retired", Boolean.FALSE);
		//query.setParameter("protocolId", protocolId);

		return q.getResultList();
	}


	@Transactional(readOnly = true)
	public List<Protocol> listProtocolWithoutStatusInMetaData(){
		String qry = "SELECT p.* FROM protocol p"
				+ " WHERE  p.retired = :retired"
				+ " AND p.meta_data_xml.exist('/protocol/status')=0";
		TypedQuery<Protocol> q = (TypedQuery<Protocol>) em
				.createNativeQuery(qry, Protocol.class);
		q.setHint("org.hibernate.cacheable", false);
		q.setParameter("retired", Boolean.FALSE);
		//Query query = em.createNativeQuery(qry);
		//query.setParameter("retired", Boolean.FALSE);
		//query.setParameter("protocolId", protocolId);

		return q.getResultList();
	}

	@Transactional(readOnly = true)
	public List<Protocol> listProtocolWithoutCorrectApprovalEndDate(String formStatus){
		String qry = "SELECT p.* from protocol p WHERE id IN ("
				+ " SELECT DISTINCT protocol_id FROM protocol_form WHERE id IN ("
				+ " SELECT DISTINCT protocol_form_id FROM protocol_form_status WHERE protocol_form_status = :formStatus AND retired = :retired)"
				+ " AND retired = :retired) AND retired = :retired";
		TypedQuery<Protocol> q = (TypedQuery<Protocol>) em
				.createNativeQuery(qry, Protocol.class);
		q.setHint("org.hibernate.cacheable", false);
		q.setParameter("retired", Boolean.FALSE);
		q.setParameter("formStatus", formStatus);
		//Query query = em.createNativeQuery(qry);
		//query.setParameter("retired", Boolean.FALSE);
		//query.setParameter("protocolId", protocolId);

		return q.getResultList();
	}

	@Transactional(readOnly = true)
	public List<ProtocolFormXmlData> listProtocolFormXmlDataWithPharmacyFee(){
		String qry = "SELECT pfxd.* from protocol_form_xml_data pfxd WHERE retired = :retired"
				+ " AND pfxd.protocol_form_xml_data_type = 'BUDGET'"
				+ " AND pfxd.xml_data.exist('/budget/expenses/expense[@type=\"Invoicable\" and @subtype=\"Pharmacy Fee\"]')=1";
		TypedQuery<ProtocolFormXmlData> q = (TypedQuery<ProtocolFormXmlData>) em
				.createNativeQuery(qry, ProtocolFormXmlData.class);
		q.setHint("org.hibernate.cacheable", false);
		q.setParameter("retired", Boolean.FALSE);
		//Query query = em.createNativeQuery(qry);
		//query.setParameter("retired", Boolean.FALSE);
		//query.setParameter("protocolId", protocolId);

		return q.getResultList();
	}

	@Transactional(readOnly = true)
	public String getEpicCdmByCptCode(String cptCode) {
		String qry = "SELECT epic_cdm_code FROM epic_cdm WHERE default_cpt_code = :cptCode";

		Query q = em.createNativeQuery(qry);

		q.setHint("org.hibernate.cacheable", false);
		q.setParameter("cptCode", cptCode);

		q.setFirstResult(0);
		q.setMaxResults(1);


		return q.getSingleResult().toString();
	}

	@Transactional(readOnly = true)
	public List<Protocol> listProtocolFromTempList(){
		String qry = "SELECT p.* from protocol p, temp_ccto_protocol tp WHERE p.id = tp.protocol_id AND p.retired = :retired";
		TypedQuery<Protocol> q = (TypedQuery<Protocol>) em
				.createNativeQuery(qry, Protocol.class);
		q.setHint("org.hibernate.cacheable", false);
		q.setParameter("retired", Boolean.FALSE);

		return q.getResultList();
	}

	public EntityManager getEm() {
		return em;
	}

	@PersistenceContext(unitName = "defaultPersistenceUnit")
	public void setEm(EntityManager em) {
		this.em = em;
	}

}

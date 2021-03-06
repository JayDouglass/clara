package edu.uams.clara.webapp.report.domain;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.uams.clara.webapp.common.domain.usercontext.enums.Committee;
import edu.uams.clara.webapp.protocol.domain.businesslogicobject.enums.ProtocolFormCommitteeStatusEnum;
import edu.uams.clara.webapp.protocol.domain.businesslogicobject.enums.ProtocolFormStatusEnum;

public class CommitteeActions {
	
	private  Map<Committee, List<ProtocolFormCommitteeStatusEnum>> startCommitteeStatusMapForHSR = Maps
			.newHashMap();
	{
		// Expedited
		List<ProtocolFormCommitteeStatusEnum> expeditedList = Lists
				.newArrayList();
		expeditedList
				.add(ProtocolFormCommitteeStatusEnum.PENDING_EXPEDITED_IRB_REVIEW);
		startCommitteeStatusMapForHSR.put(Committee.IRB_EXPEDITED_REVIEWER,
				expeditedList);

		// Exempt
		List<ProtocolFormCommitteeStatusEnum> exemptList = Lists.newArrayList();
		exemptList
				.add(ProtocolFormCommitteeStatusEnum.PENDING_EXEMPT_IRB_REVIEW);
		startCommitteeStatusMapForHSR.put(Committee.IRB_EXEMPT_REVIEWER, exemptList);

		// irbReview
		List<ProtocolFormCommitteeStatusEnum> irbReviewList = Lists
				.newArrayList();
		irbReviewList.add(ProtocolFormCommitteeStatusEnum.IN_REVIEW);
		startCommitteeStatusMapForHSR.put(Committee.IRB_REVIEWER, irbReviewList);

		// irbOffice
		List<ProtocolFormCommitteeStatusEnum> irbOfficeList = Lists
				.newArrayList();
		irbOfficeList
				.add(ProtocolFormCommitteeStatusEnum.PENDING_IRB_REVIEW_ASSIGNMENT);
		irbOfficeList
				.add(ProtocolFormCommitteeStatusEnum.PENDING_IRB_REVIEW_RE_ASSIGNMENT);
		//irbOfficeList.add(ProtocolFormCommitteeStatusEnum.IRB_AGENDA_ASSIGNED);
		irbOfficeList.add(ProtocolFormCommitteeStatusEnum.IN_REVIEW);
		startCommitteeStatusMapForHSR.put(Committee.IRB_OFFICE, irbOfficeList);

		// irbPrereview
		List<ProtocolFormCommitteeStatusEnum> irbPrereviewList = Lists
				.newArrayList();
		irbPrereviewList.add(ProtocolFormCommitteeStatusEnum.IN_REVIEW);
		startCommitteeStatusMapForHSR.put(Committee.IRB_PREREVIEW, irbPrereviewList);

		// irbConsentReview
		List<ProtocolFormCommitteeStatusEnum> irbConsentReviewList = Lists
				.newArrayList();
		irbConsentReviewList.add(ProtocolFormCommitteeStatusEnum.IN_REVIEW);
		startCommitteeStatusMapForHSR.put(Committee.IRB_CONSENT_REVIEWER,
				irbConsentReviewList);

		// irbAssigner
		List<ProtocolFormCommitteeStatusEnum> irbAssignerList = Lists
				.newArrayList();
		irbAssignerList
				.add(ProtocolFormCommitteeStatusEnum.PENDING_REVIEWER_ASSIGNMENT);
		startCommitteeStatusMapForHSR.put(Committee.IRB_ASSIGNER, irbAssignerList);

		// colleageDean
		List<ProtocolFormCommitteeStatusEnum> colleageDeanList = Lists
				.newArrayList();
		colleageDeanList.add(ProtocolFormCommitteeStatusEnum.IN_REVIEW);
		startCommitteeStatusMapForHSR.put(Committee.COLLEGE_DEAN, colleageDeanList);

		// departmentChair
		List<ProtocolFormCommitteeStatusEnum> departmentChairList = Lists
				.newArrayList();
		departmentChairList.add(ProtocolFormCommitteeStatusEnum.IN_REVIEW);
		startCommitteeStatusMapForHSR.put(Committee.DEPARTMENT_CHAIR,
				departmentChairList);
	}
	
	private  Map<Committee, List<ProtocolFormCommitteeStatusEnum>> endCommitteeStatusMapForHSR = Maps
			.newHashMap();
	{
		List<ProtocolFormCommitteeStatusEnum> expeditedList = Lists
				.newArrayList();
		expeditedList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		expeditedList
				.add(ProtocolFormCommitteeStatusEnum.NOT_APPLICABLE_TO_EXPEDITED_IRB_REVIEW);
		expeditedList.add(ProtocolFormCommitteeStatusEnum.REVISION_REQUESTED);
		endCommitteeStatusMapForHSR.put(Committee.IRB_EXPEDITED_REVIEWER,
				expeditedList);

		// exempt
		List<ProtocolFormCommitteeStatusEnum> exemptList = Lists.newArrayList();
		exemptList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		exemptList
				.add(ProtocolFormCommitteeStatusEnum.NOT_APPLICABLE_TO_EXEMPT_IRB_REVIEW);
		exemptList.add(ProtocolFormCommitteeStatusEnum.REVISION_REQUESTED);
		exemptList.add(ProtocolFormCommitteeStatusEnum.COMPLETED);
		endCommitteeStatusMapForHSR.put(Committee.IRB_EXEMPT_REVIEWER, exemptList);

		// irbReview
		List<ProtocolFormCommitteeStatusEnum> irbReviewList = Lists
				.newArrayList();
		irbReviewList.add(ProtocolFormCommitteeStatusEnum.ACKNOWLEDGED);
		irbReviewList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		irbReviewList
				.add(ProtocolFormCommitteeStatusEnum.DEFERRED_WITH_MAJOR_CONTINGENCIES);
		irbReviewList
				.add(ProtocolFormCommitteeStatusEnum.DEFERRED_WITH_MINOR_CONTINGENCIES);
		irbReviewList
				.add(ProtocolFormCommitteeStatusEnum.REMOVED_FROM_IRB_AGENDA);
		endCommitteeStatusMapForHSR.put(Committee.IRB_REVIEWER, irbReviewList);

		// irbOffice
		List<ProtocolFormCommitteeStatusEnum> irbOfficeList = Lists
				.newArrayList();
		irbOfficeList.add(ProtocolFormCommitteeStatusEnum.IRB_AGENDA_ASSIGNED);
		irbOfficeList.add(ProtocolFormCommitteeStatusEnum.EXEMPT_IRB_REVIEW);
		irbOfficeList.add(ProtocolFormCommitteeStatusEnum.EXPEDITED_IRB_REVIEW);
/*		
		irbOfficeList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		irbOfficeList.add(ProtocolFormCommitteeStatusEnum.REVISION_REQUESTED);
		irbOfficeList.add(ProtocolFormCommitteeStatusEnum.ACKNOWLEDGED);
		irbOfficeList
				.add(ProtocolFormCommitteeStatusEnum.DEFERRED_WITH_MINOR_CONTINGENCIES);
		irbOfficeList
				.add(ProtocolFormCommitteeStatusEnum.DEFERRED_WITH_MAJOR_CONTINGENCIES);
		irbOfficeList.add(ProtocolFormCommitteeStatusEnum.COMPLETED);*/
		endCommitteeStatusMapForHSR.put(Committee.IRB_OFFICE, irbOfficeList);

		// irbPrereview
		List<ProtocolFormCommitteeStatusEnum> irbPrereviewList = Lists
				.newArrayList();
		irbPrereviewList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		irbPrereviewList
				.add(ProtocolFormCommitteeStatusEnum.REVISION_REQUESTED);
		irbPrereviewList.add(ProtocolFormCommitteeStatusEnum.ACKNOWLEDGED);
		irbPrereviewList.add(ProtocolFormCommitteeStatusEnum.COMPLETED);
		endCommitteeStatusMapForHSR.put(Committee.IRB_PREREVIEW, irbPrereviewList);

		// irbConsentReview
		List<ProtocolFormCommitteeStatusEnum> irbConsentReviewList = Lists
				.newArrayList();
		irbConsentReviewList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		endCommitteeStatusMapForHSR.put(Committee.IRB_CONSENT_REVIEWER,
				irbConsentReviewList);

		// irbAssigner
		List<ProtocolFormCommitteeStatusEnum> irbAssignerList = Lists
				.newArrayList();
		irbAssignerList.add(ProtocolFormCommitteeStatusEnum.REVIEWER_ASSIGNED);
		endCommitteeStatusMapForHSR.put(Committee.IRB_ASSIGNER, irbAssignerList);

		

		// colleageDean
		List<ProtocolFormCommitteeStatusEnum> colleageDeanList = Lists
				.newArrayList();
		colleageDeanList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		endCommitteeStatusMapForHSR.put(Committee.COLLEGE_DEAN, colleageDeanList);

		// departmentChair
		List<ProtocolFormCommitteeStatusEnum> departmentChairList = Lists
				.newArrayList();
		departmentChairList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		endCommitteeStatusMapForHSR.put(Committee.DEPARTMENT_CHAIR,
				departmentChairList);
	}
	
	private  Map<Committee, List<ProtocolFormCommitteeStatusEnum>> startCommitteeStatusMap = Maps
			.newHashMap();
	{
		// Expedited
		List<ProtocolFormCommitteeStatusEnum> expeditedList = Lists
				.newArrayList();
		expeditedList
				.add(ProtocolFormCommitteeStatusEnum.PENDING_EXPEDITED_IRB_REVIEW);
		startCommitteeStatusMap.put(Committee.IRB_EXPEDITED_REVIEWER,
				expeditedList);

		// Exempt
		List<ProtocolFormCommitteeStatusEnum> exemptList = Lists.newArrayList();
		exemptList
				.add(ProtocolFormCommitteeStatusEnum.PENDING_EXEMPT_IRB_REVIEW);
		startCommitteeStatusMap.put(Committee.IRB_EXEMPT_REVIEWER, exemptList);

		// irbReview
		List<ProtocolFormCommitteeStatusEnum> irbReviewList = Lists
				.newArrayList();
		irbReviewList.add(ProtocolFormCommitteeStatusEnum.IN_REVIEW);
		startCommitteeStatusMap.put(Committee.IRB_REVIEWER, irbReviewList);

		// irbOffice
		List<ProtocolFormCommitteeStatusEnum> irbOfficeList = Lists
				.newArrayList();
		irbOfficeList
				.add(ProtocolFormCommitteeStatusEnum.PENDING_IRB_REVIEW_ASSIGNMENT);
		irbOfficeList
				.add(ProtocolFormCommitteeStatusEnum.PENDING_IRB_REVIEW_RE_ASSIGNMENT);
		//irbOfficeList.add(ProtocolFormCommitteeStatusEnum.IRB_AGENDA_ASSIGNED);
		irbOfficeList.add(ProtocolFormCommitteeStatusEnum.IN_REVIEW);
		startCommitteeStatusMap.put(Committee.IRB_OFFICE, irbOfficeList);

		// irbPrereview
		List<ProtocolFormCommitteeStatusEnum> irbPrereviewList = Lists
				.newArrayList();
		irbPrereviewList.add(ProtocolFormCommitteeStatusEnum.IN_REVIEW);
		startCommitteeStatusMap.put(Committee.IRB_PREREVIEW, irbPrereviewList);

		// irbConsentReview
		List<ProtocolFormCommitteeStatusEnum> irbConsentReviewList = Lists
				.newArrayList();
		irbConsentReviewList.add(ProtocolFormCommitteeStatusEnum.IN_REVIEW);
		startCommitteeStatusMap.put(Committee.IRB_CONSENT_REVIEWER,
				irbConsentReviewList);

		// irbAssigner
		List<ProtocolFormCommitteeStatusEnum> irbAssignerList = Lists
				.newArrayList();
		irbAssignerList
				.add(ProtocolFormCommitteeStatusEnum.PENDING_REVIEWER_ASSIGNMENT);
		startCommitteeStatusMap.put(Committee.IRB_ASSIGNER, irbAssignerList);

		// budgetReview
		List<ProtocolFormCommitteeStatusEnum> budgetReviewList = Lists
				.newArrayList();
		budgetReviewList.add(ProtocolFormCommitteeStatusEnum.IN_REVIEW);
		startCommitteeStatusMap.put(Committee.BUDGET_REVIEW, budgetReviewList);

		// budgetManager
		List<ProtocolFormCommitteeStatusEnum> budgetManagerList = Lists
				.newArrayList();
		budgetManagerList.add(ProtocolFormCommitteeStatusEnum.IN_REVIEW);
		/*budgetManagerList
				.add(ProtocolFormCommitteeStatusEnum.PENDING_REVIEWER_ASSIGNMENT);*/
		startCommitteeStatusMap
				.put(Committee.BUDGET_MANAGER, budgetManagerList);

		// coverageReview
		List<ProtocolFormCommitteeStatusEnum> coverageReviewList = Lists
				.newArrayList();
		coverageReviewList.add(ProtocolFormCommitteeStatusEnum.IN_REVIEW);
		startCommitteeStatusMap.put(Committee.COVERAGE_REVIEW,
				coverageReviewList);

		// pharmacyReview
		List<ProtocolFormCommitteeStatusEnum> pharmacyReviewList = Lists
				.newArrayList();
		pharmacyReviewList
				.add(ProtocolFormCommitteeStatusEnum.IN_REVIEW_REQUESTED);
		pharmacyReviewList
				.add(ProtocolFormCommitteeStatusEnum.IN_WAIVER_REQUESTED);
		startCommitteeStatusMap.put(Committee.PHARMACY_REVIEW,
				pharmacyReviewList);

		// hospitalReview
		List<ProtocolFormCommitteeStatusEnum> hospitalReviewList = Lists
				.newArrayList();
		hospitalReviewList.add(ProtocolFormCommitteeStatusEnum.IN_REVIEW);
		startCommitteeStatusMap.put(Committee.HOSPITAL_SERVICES,
				hospitalReviewList);

		// colleageDean
		List<ProtocolFormCommitteeStatusEnum> colleageDeanList = Lists
				.newArrayList();
		colleageDeanList.add(ProtocolFormCommitteeStatusEnum.IN_REVIEW);
		startCommitteeStatusMap.put(Committee.COLLEGE_DEAN, colleageDeanList);

		// departmentChair
		List<ProtocolFormCommitteeStatusEnum> departmentChairList = Lists
				.newArrayList();
		departmentChairList.add(ProtocolFormCommitteeStatusEnum.IN_REVIEW);
		startCommitteeStatusMap.put(Committee.DEPARTMENT_CHAIR,
				departmentChairList);

		// gatekeeper
		List<ProtocolFormCommitteeStatusEnum> gatekeeperList = Lists
				.newArrayList();
		gatekeeperList.add(ProtocolFormCommitteeStatusEnum.IN_REVIEW);
		startCommitteeStatusMap.put(Committee.GATEKEEPER, gatekeeperList);

		// COMPLIANCE_REVIEW
		List<ProtocolFormCommitteeStatusEnum> complianceList = Lists
				.newArrayList();
		complianceList
				.add(ProtocolFormCommitteeStatusEnum.IN_REVIEW_FOR_BUDGET);
		complianceList.add(ProtocolFormCommitteeStatusEnum.IN_REVIEW);
		startCommitteeStatusMap
				.put(Committee.COMPLIANCE_REVIEW, complianceList);

		// achri
		List<ProtocolFormCommitteeStatusEnum> achriList = Lists.newArrayList();
		achriList.add(ProtocolFormCommitteeStatusEnum.IN_REVIEW);
		startCommitteeStatusMap.put(Committee.ACHRI, achriList);

	}
	private  Map<Committee, List<ProtocolFormCommitteeStatusEnum>> endCommitteeStatusMap = Maps
			.newHashMap();
	{
		List<ProtocolFormCommitteeStatusEnum> expeditedList = Lists
				.newArrayList();
		expeditedList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		expeditedList
				.add(ProtocolFormCommitteeStatusEnum.NOT_APPLICABLE_TO_EXPEDITED_IRB_REVIEW);
		expeditedList.add(ProtocolFormCommitteeStatusEnum.REVISION_REQUESTED);
		endCommitteeStatusMap.put(Committee.IRB_EXPEDITED_REVIEWER,
				expeditedList);

		// exempt
		List<ProtocolFormCommitteeStatusEnum> exemptList = Lists.newArrayList();
		exemptList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		exemptList
				.add(ProtocolFormCommitteeStatusEnum.NOT_APPLICABLE_TO_EXEMPT_IRB_REVIEW);
		exemptList.add(ProtocolFormCommitteeStatusEnum.REVISION_REQUESTED);
		exemptList.add(ProtocolFormCommitteeStatusEnum.COMPLETED);
		endCommitteeStatusMap.put(Committee.IRB_EXEMPT_REVIEWER, exemptList);

		// irbReview
		List<ProtocolFormCommitteeStatusEnum> irbReviewList = Lists
				.newArrayList();
		irbReviewList.add(ProtocolFormCommitteeStatusEnum.ACKNOWLEDGED);
		irbReviewList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		irbReviewList
				.add(ProtocolFormCommitteeStatusEnum.DEFERRED_WITH_MAJOR_CONTINGENCIES);
		irbReviewList
				.add(ProtocolFormCommitteeStatusEnum.DEFERRED_WITH_MINOR_CONTINGENCIES);
		irbReviewList
				.add(ProtocolFormCommitteeStatusEnum.REMOVED_FROM_IRB_AGENDA);
		endCommitteeStatusMap.put(Committee.IRB_REVIEWER, irbReviewList);

		// irbOffice
		List<ProtocolFormCommitteeStatusEnum> irbOfficeList = Lists
				.newArrayList();
		irbOfficeList.add(ProtocolFormCommitteeStatusEnum.IRB_AGENDA_ASSIGNED);
		irbOfficeList.add(ProtocolFormCommitteeStatusEnum.EXEMPT_IRB_REVIEW);
		irbOfficeList.add(ProtocolFormCommitteeStatusEnum.EXPEDITED_IRB_REVIEW);
/*		
		irbOfficeList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		irbOfficeList.add(ProtocolFormCommitteeStatusEnum.REVISION_REQUESTED);
		irbOfficeList.add(ProtocolFormCommitteeStatusEnum.ACKNOWLEDGED);
		irbOfficeList
				.add(ProtocolFormCommitteeStatusEnum.DEFERRED_WITH_MINOR_CONTINGENCIES);
		irbOfficeList
				.add(ProtocolFormCommitteeStatusEnum.DEFERRED_WITH_MAJOR_CONTINGENCIES);
		irbOfficeList.add(ProtocolFormCommitteeStatusEnum.COMPLETED);*/
		endCommitteeStatusMap.put(Committee.IRB_OFFICE, irbOfficeList);

		// irbPrereview
		List<ProtocolFormCommitteeStatusEnum> irbPrereviewList = Lists
				.newArrayList();
		irbPrereviewList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		irbPrereviewList
				.add(ProtocolFormCommitteeStatusEnum.REVISION_REQUESTED);
		irbPrereviewList.add(ProtocolFormCommitteeStatusEnum.ACKNOWLEDGED);
		irbPrereviewList.add(ProtocolFormCommitteeStatusEnum.COMPLETED);
		endCommitteeStatusMap.put(Committee.IRB_PREREVIEW, irbPrereviewList);

		// irbConsentReview
		List<ProtocolFormCommitteeStatusEnum> irbConsentReviewList = Lists
				.newArrayList();
		irbConsentReviewList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		endCommitteeStatusMap.put(Committee.IRB_CONSENT_REVIEWER,
				irbConsentReviewList);

		// irbAssigner
		List<ProtocolFormCommitteeStatusEnum> irbAssignerList = Lists
				.newArrayList();
		irbAssignerList.add(ProtocolFormCommitteeStatusEnum.REVIEWER_ASSIGNED);
		endCommitteeStatusMap.put(Committee.IRB_ASSIGNER, irbAssignerList);

		// budgetReview
		List<ProtocolFormCommitteeStatusEnum> budgetReviewList = Lists
				.newArrayList();
		budgetReviewList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		budgetReviewList.add(ProtocolFormCommitteeStatusEnum.COMPLETED);
		budgetReviewList
				.add(ProtocolFormCommitteeStatusEnum.REVISION_REQUESTED);
		endCommitteeStatusMap.put(Committee.BUDGET_REVIEW, budgetReviewList);

		// budgetManager
		List<ProtocolFormCommitteeStatusEnum> budgetManagerList = Lists
				.newArrayList();
		budgetManagerList
				.add(ProtocolFormCommitteeStatusEnum.REVIEWER_ASSIGNED);
		budgetManagerList
				.add(ProtocolFormCommitteeStatusEnum.REVISION_REQUESTED);
		endCommitteeStatusMap.put(Committee.BUDGET_MANAGER, budgetManagerList);

		// coverageReview
		List<ProtocolFormCommitteeStatusEnum> coverageReviewList = Lists
				.newArrayList();
		coverageReviewList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		coverageReviewList.add(ProtocolFormCommitteeStatusEnum.COMPLETED);
		coverageReviewList
				.add(ProtocolFormCommitteeStatusEnum.REVISION_REQUESTED);
		coverageReviewList
				.add(ProtocolFormCommitteeStatusEnum.PENDING_CONSENT_LEGAL_REVIEW);
		endCommitteeStatusMap
				.put(Committee.COVERAGE_REVIEW, coverageReviewList);

		// pharmacyReview
		List<ProtocolFormCommitteeStatusEnum> pharmacyReviewList = Lists
				.newArrayList();
		pharmacyReviewList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		pharmacyReviewList
				.add(ProtocolFormCommitteeStatusEnum.WAIVER_REQUEST_APPROVED);
		endCommitteeStatusMap
				.put(Committee.PHARMACY_REVIEW, pharmacyReviewList);

		// hospitalReview
		List<ProtocolFormCommitteeStatusEnum> hospitalReviewList = Lists
				.newArrayList();
		hospitalReviewList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		endCommitteeStatusMap.put(Committee.HOSPITAL_SERVICES,
				hospitalReviewList);

		// colleageDean
		List<ProtocolFormCommitteeStatusEnum> colleageDeanList = Lists
				.newArrayList();
		colleageDeanList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		endCommitteeStatusMap.put(Committee.COLLEGE_DEAN, colleageDeanList);

		// departmentChair
		List<ProtocolFormCommitteeStatusEnum> departmentChairList = Lists
				.newArrayList();
		departmentChairList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		endCommitteeStatusMap.put(Committee.DEPARTMENT_CHAIR,
				departmentChairList);

		// gatekeeper
		List<ProtocolFormCommitteeStatusEnum> gatekeeperList = Lists
				.newArrayList();
		gatekeeperList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		gatekeeperList.add(ProtocolFormCommitteeStatusEnum.REVISION_REQUESTED);
		gatekeeperList.add(ProtocolFormCommitteeStatusEnum.PENDING_PTL_DEVELOP);
		gatekeeperList.add(ProtocolFormCommitteeStatusEnum.REJECTED);
		endCommitteeStatusMap.put(Committee.GATEKEEPER, gatekeeperList);

		// COMPLIANCE_REVIEW
		List<ProtocolFormCommitteeStatusEnum> complianceList = Lists
				.newArrayList();
		complianceList
				.add(ProtocolFormCommitteeStatusEnum.COMMITTEE_REVIEW_NOT_APPLICABLE);
		complianceList.add(ProtocolFormCommitteeStatusEnum.REVISION_REQUESTED);
		complianceList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		endCommitteeStatusMap.put(Committee.COMPLIANCE_REVIEW, complianceList);

		// achri
		List<ProtocolFormCommitteeStatusEnum> achriList = Lists.newArrayList();
		achriList.add(ProtocolFormCommitteeStatusEnum.REVISION_REQUESTED);
		achriList.add(ProtocolFormCommitteeStatusEnum.APPROVED);
		endCommitteeStatusMap.put(Committee.ACHRI, achriList);
	}
	
	private  List<ProtocolFormStatusEnum> draftFormStatus = Lists.newArrayList();
	{
		draftFormStatus.add(ProtocolFormStatusEnum.DRAFT);
		draftFormStatus.add(ProtocolFormStatusEnum.PENDING_PI_ENDORSEMENT);
		draftFormStatus.add(ProtocolFormStatusEnum.PENDING_PL_ENDORSEMENT);
		draftFormStatus.add(ProtocolFormStatusEnum.PENDING_TP_ENDORSEMENT);
	}
	
	private  List<ProtocolFormStatusEnum> piStartActions = Lists.newArrayList();{
		piStartActions.add(ProtocolFormStatusEnum.DRAFT);
		piStartActions.add(ProtocolFormStatusEnum.UNDER_REVISION);
		piStartActions.add(ProtocolFormStatusEnum.REVISION_REQUESTED);
		piStartActions.add(ProtocolFormStatusEnum.REVISION_PENDING_PI_ENDORSEMENT);
		piStartActions.add(ProtocolFormStatusEnum.PENDING_PI_ENDORSEMENT);
		piStartActions.add(ProtocolFormStatusEnum.PENDING_PI_SIGN_OFF);
		piStartActions.add(ProtocolFormStatusEnum.PENDING_PL_ENDORSEMENT);
		piStartActions.add(ProtocolFormStatusEnum.PENDING_TP_ENDORSEMENT);
		piStartActions.add(ProtocolFormStatusEnum.IRB_DEFERRED_WITH_MAJOR_CONTINGENCIES);
		piStartActions.add(ProtocolFormStatusEnum.IRB_DEFERRED_WITH_MINOR_CONTINGENCIES);
		piStartActions.add(ProtocolFormStatusEnum.UNDER_REVISION_MAJOR_CONTINGENCIES);
		piStartActions.add(ProtocolFormStatusEnum.UNDER_REVISION_MINOR_CONTINGENCIES);
		piStartActions.add(ProtocolFormStatusEnum.REVISION_WITH_MAJOR_PENDING_PI_ENDORSEMENT);
		piStartActions.add(ProtocolFormStatusEnum.REVISION_WITH_MINOR_PENDING_PI_ENDORSEMENT);
	}
	
	private  List<ProtocolFormStatusEnum> completeFormStatus = Lists.newArrayList();
	{
		completeFormStatus.add(ProtocolFormStatusEnum.ACKNOWLEDGED);
		completeFormStatus.add(ProtocolFormStatusEnum.APPROVED);
		completeFormStatus.add(ProtocolFormStatusEnum.COMPLETED);
		completeFormStatus.add(ProtocolFormStatusEnum.EXEMPT_APPROVED);
		completeFormStatus.add(ProtocolFormStatusEnum.EXPEDITED_APPROVED);
		completeFormStatus.add(ProtocolFormStatusEnum.IRB_ACKNOWLEDGED);
		completeFormStatus.add(ProtocolFormStatusEnum.IRB_APPROVED);
		completeFormStatus.add(ProtocolFormStatusEnum.IRB_DECLINED);
		completeFormStatus.add(ProtocolFormStatusEnum.IRB_REVIEW_NOT_NEEDED);
		completeFormStatus.add(ProtocolFormStatusEnum.DETERMINED_HUMAN_SUBJECT_RESEARCH);
		completeFormStatus.add(ProtocolFormStatusEnum.DETERMINED_NOT_HUMAN_SUBJECT_RESEARCH);
		completeFormStatus.add(ProtocolFormStatusEnum.DETERMINED_NOT_REPORTABLE_NEW_INFORMATION);
	}
	
	
	private  List<ProtocolFormStatusEnum> irbApprovalNSFFormStatus = Lists.newArrayList();
	{
		irbApprovalNSFFormStatus.add(ProtocolFormStatusEnum.EXEMPT_APPROVED);
		irbApprovalNSFFormStatus.add(ProtocolFormStatusEnum.EXPEDITED_APPROVED);
		irbApprovalNSFFormStatus.add(ProtocolFormStatusEnum.IRB_APPROVED);
	}
	
	private  List<Long> testStudyOnProduction = Lists.newArrayList();
	{
		testStudyOnProduction.add(201775l);
		testStudyOnProduction.add(201821l);
		testStudyOnProduction.add(201855l);
		testStudyOnProduction.add(201890l);
		testStudyOnProduction.add(201913l);
		testStudyOnProduction.add(201957l);
		testStudyOnProduction.add(202038l);
		testStudyOnProduction.add(202041l);
		testStudyOnProduction.add(202055l);
		testStudyOnProduction.add(202071l);
		testStudyOnProduction.add(202096l);
		testStudyOnProduction.add(202104l);
		testStudyOnProduction.add(202126l);
		testStudyOnProduction.add(202138l);
		testStudyOnProduction.add(202279l);
		testStudyOnProduction.add(202292l);
		testStudyOnProduction.add(202302l);
		testStudyOnProduction.add(202310l);
		testStudyOnProduction.add(202330l);
		testStudyOnProduction.add(202337l);
		testStudyOnProduction.add(202354l);
		testStudyOnProduction.add(202385l);
		testStudyOnProduction.add(202452l);
		testStudyOnProduction.add(202456l);
		testStudyOnProduction.add(202487l);
		testStudyOnProduction.add(202554l);
		testStudyOnProduction.add(202580l);
	}
	
	
	public Map<Committee, List<ProtocolFormCommitteeStatusEnum>> getStartCommitteeStatusMap() {
		return startCommitteeStatusMap;
	}
	public Map<Committee, List<ProtocolFormCommitteeStatusEnum>> getEndCommitteeStatusMap() {
		return endCommitteeStatusMap;
	}
	public List<ProtocolFormStatusEnum> getDraftFormStatus() {
		return draftFormStatus;
	}
	public List<ProtocolFormStatusEnum> getCompleteFormStatus() {
		return completeFormStatus;
	}
	public List<Long> getTestStudyOnProduction() {
		return testStudyOnProduction;
	}
	public  List<ProtocolFormStatusEnum> getPiStartActions() {
		return piStartActions;
	}
	public List<ProtocolFormStatusEnum> getIrbApprovalNSFFormStatus() {
		return irbApprovalNSFFormStatus;
	}
	public Map<Committee, List<ProtocolFormCommitteeStatusEnum>> getStartCommitteeStatusMapForHSR() {
		return startCommitteeStatusMapForHSR;
	}
	public Map<Committee, List<ProtocolFormCommitteeStatusEnum>> getEndCommitteeStatusMapForHSR() {
		return endCommitteeStatusMapForHSR;
	}
}

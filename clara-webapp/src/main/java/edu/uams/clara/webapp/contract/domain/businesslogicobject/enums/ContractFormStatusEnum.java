package edu.uams.clara.webapp.contract.domain.businesslogicobject.enums;


public enum ContractFormStatusEnum {
	ANY("Any Status (placeholder)", "NORMAL"),
	LIMBO("Limbo Status (Program error occurred)", "ERROR"),
	TO_BE_DETERMINED("TO BE DETERMINED (placeholder)", "NORMAL"),
	DRAFT("Draft", "NORMAL"),
	ARCHIVED("Archived", "INFO"),
	PENDING_PI_ENDORSEMENT("Pending PI Endorsement", "WARN"),
	PENDING_REVIEWER_ASSIGNMENT("Pending Reviewer Assignment", "WARN"),
	APPROVED("Approved", "INFO"),
	COMPLETED("Completed", "INFO"),
	ACKNOWLEDGED("Acknowledged", "INFO"),
	REVISION_REQUESTED("Revision Requested", "ERROR"),
	REVISED("Revised", "INFO"),
	RECEIVED("Received", "INFO"),
	UNDER_BUDGET_REVIEW("Pending Budget Review", "INFO"),
	UNDER_COVERAGE_REVIEW("Pending Coverage Review", "INFO"),
	UNDER_REVISION("Under Revision", "INFO"),
	UNDER_REVIEW("Under Review", "INFO"),
	REVISION_PENDING_PI_ENDORSEMENT("Revision Pending PI Endorsement", "WARN"),
	PENDING_LEGAL_REVIEW("Pending Legal Review", "WARN"),
	PENDING_SPONSOR_RESPONSE("Pending Sponsor Response", "WARN"),
	UNDER_CONTRACT_REVIEW("Under Contract Review", "INFO"),
	UNDER_LEGAL_REVIEW("Under Legal Review", "INFO"),
	NOT_APPLICABLE("Not Applicable", "INFO"),
	FINAL_LEGAL_APPROVAL("Final Legal Approval", "INFO"),
	UNDER_CONTRACT_MANAGER_REVIEW("Under Contract Manager Review", "INFO"),
	CONTRACT_EXECUTED("Contract Executed", "INFO"),
	PENDING_PI("Pending PI", "WARN"),
	PENDING_BUDGET("Pending Budget", "WARN"),
	PENDING_COVERAGE("Pending Coverage", "WARN"),
	PENDING_IRB("Pending IRB", "WARN"),
	OUTSIDE_IRB("Outside IRB", "WARN"),
	PENDING_SIGNATURE("Pending Signature", "WARN"),
	CANCELLED("Cancelled", "WARN"),
	CONTRACT_EXECUTED_PENDING_DOCUMENTS("Contract Excuted Pending Documents", "INFO");
	
	private String description;
	private String priorityLevel;
	
	private ContractFormStatusEnum(String description, String priorityLevel){
		this.description = description;
		this.priorityLevel = priorityLevel;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public String getPriorityLevel() {
		return priorityLevel;
	}

	public void setPriorityLevel(String priorityLevel) {
		this.priorityLevel = priorityLevel;
	}
}

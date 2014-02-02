Ext.define('Clara.Dashboard.store.AvailableSearchFields', {
    extend: 'Ext.data.ArrayStore',
    requires: 'Clara.Dashboard.model.AvailableSearchField',    
    model: 'Clara.Dashboard.model.AvailableSearchField',
    data:[['protocol','TITLE','Title','string','textfield'],
          ['protocol','STAFF_USERID','Staff Name',null,'clarafield.combo.user'],
          ['protocol','PI_USERID','PI Name',null,'clarafield.combo.user'],
          ['protocol','PRIMARY_SITE','Primary Institution','clarafield.combo.primaryresponsibleinstitution','clarafield.combo.criteria'],
          ['protocol','LOCATION','Location',null,'textfield'], 
          ['protocol','DRUG_NAME','Drug Name',null,'textfield'],
          ['protocol','STUDY_TYPE','Study: Type','clarafield.combo.protocol.studytype','clarafield.combo.criteria'],
          ['protocol','PROTOCOL_STATUS','Study: Overall Status','clarafield.combo.protocol.studystatus','clarafield.combo.criteria'],
          ['protocol','PROTOCOL_FORM_STATUS','Study: Form Status','clarafield.combo.protocol.formstatus','clarafield.combo.criteria'],
          ['protocol','COLLEGE', 'College / Department',null,'clarafield.college'], 
          ['protocol','FORM_TYPE', 'Form Type','clarafield.combo.protocol.formtype','clarafield.combo.criteria'],
          ['protocol','ASSIGNED_REVIEWER_USERID', 'Assigned Reviewer',null,'clarafield.combo.user'],
          ['contract','TITLE','Title',null,'textfield'],
          ['contract','STAFF_USERID','Staff Name',null,'clarafield.combo.user'],
          ['contract','PI_USERID','PI Name',null,'clarafield.combo.user'],
          ['contract','ENTITY_NAME','Entity Name',null,'textfield'],
          ['protocol','FUNDING_SOURCE', 'Funding Source',null,'clarafield.combo.fundingsource'],
          ['contract','CONTRACT_TYPE','Contract Type','clarafield.combo.contract.type','clarafield.combo.criteria'],
          ['contract','CONTRACT_STATUS','Status','clarafield.combo.contract.status','clarafield.combo.criteria'],
          ['contract','ASSIGNED_REVIEWER_USERID', 'Assigned Reviewer',null,'clarafield.combo.user']]
});
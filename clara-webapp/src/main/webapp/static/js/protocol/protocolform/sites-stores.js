var protocolSiteStore;
var stateStore;
var approvedSiteStore;
function initializeSitesStores(){
	var url = appContext + "/ajax/"+claraInstance.type+"s/" + claraInstance.id + "/"+claraInstance.type+"-forms/" + claraInstance.form.id + "/"+claraInstance.type+"-form-xml-datas/" + claraInstance.form.xmlDataId + "/";

	approvedSiteStore = new Ext.data.Store({
		header :{
	           'Accept': 'application/json'
	       },
		proxy: new Ext.data.HttpProxy({
			url: appContext+"/ajax/"+claraInstance.type+"s/"+claraInstance.type+"-forms/sites/search",
			method:'GET'
		}),
		autoLoad:false,
		reader: new Ext.data.JsonReader({
			root: 'sites',
			idProperty: 'id'
		}, [
		    {name:'id', mapping: 'id'},
		    {name:'siteName', mapping:'siteName'},
		    {name:'siteFullname', mapping:'siteFullname'},
		    {name:'address', mapping:'address'},
		    {name:'city', mapping:'city'},
		    {name:'state', mapping:'state'},
		    {name:'zip', mapping:'zip'},
		    {name:'common', mapping:'common'}
		])
	});
	
	protocolSiteStore = new Ext.data.XmlStore({
		proxy: new Ext.data.HttpProxy({
			url: url + "xml-elements/list",
			method:"GET",
			headers:{'Accept':'application/xml;charset=UTF-8'}
		}),
		record: 'site', 
		autoLoad:true,
		root:'list',
		baseParams:{listPath:'/'+claraInstance.form.xmlBaseTag+'/study-sites/site'},
		fields: [
		    {name:'id', mapping: '@id'},
		    {name:'site-id', mapping: '@site-id'},
		    {name:'site-name', mapping:'site-name'},
		    {name:'address', mapping:'address'},
		    {name:'city', mapping:'city'},
		    {name:'state', mapping:'state'},
		    {name:'zip', mapping:'zip'},
			{name:'contact',mapping:'site-contact'},
			{name:'phone', mapping:'phone'},
			{name:'email',mapping:'email'},
			{name:'approved',mapping:'@approved'}
		]
	});
	
	
	var states = [
	  		      ['--', 'Not Applicable', 'International'],
	              ['AL', 'Alabama', 'The Heart of Dixie'],
	              ['AK', 'Alaska', 'The Land of the Midnight Sun'],
	              ['AZ', 'Arizona', 'The Grand Canyon State'],
	              ['AR', 'Arkansas', 'The Natural State'],
	              ['CA', 'California', 'The Golden State'],
	              ['CO', 'Colorado', 'The Mountain State'],
	              ['CT', 'Connecticut', 'The Constitution State'],
	              ['DE', 'Delaware', 'The First State'],
	              ['DC', 'District of Columbia', "The Nation's Capital"],
	              ['FL', 'Florida', 'The Sunshine State'],
	              ['GA', 'Georgia', 'The Peach State'],
	              ['HI', 'Hawaii', 'The Aloha State'],
	              ['ID', 'Idaho', 'Famous Potatoes'],
	              ['IL', 'Illinois', 'The Prairie State'],
	              ['IN', 'Indiana', 'The Hospitality State'],
	              ['IA', 'Iowa', 'The Corn State'],
	              ['KS', 'Kansas', 'The Sunflower State'],
	              ['KY', 'Kentucky', 'The Bluegrass State'],
	              ['LA', 'Louisiana', 'The Bayou State'],
	              ['ME', 'Maine', 'The Pine Tree State'],
	              ['MD', 'Maryland', 'Chesapeake State'],
	              ['MA', 'Massachusetts', 'The Spirit of America'],
	              ['MI', 'Michigan', 'Great Lakes State'],
	              ['MN', 'Minnesota', 'North Star State'],
	              ['MS', 'Mississippi', 'Magnolia State'],
	              ['MO', 'Missouri', 'Show Me State'],
	              ['MT', 'Montana', 'Big Sky Country'],
	              ['NE', 'Nebraska', 'Beef State'],
	              ['NV', 'Nevada', 'Silver State'],
	              ['NH', 'New Hampshire', 'Granite State'],
	              ['NJ', 'New Jersey', 'Garden State'],
	              ['NM', 'New Mexico', 'Land of Enchantment'],
	              ['NY', 'New York', 'Empire State'],
	              ['NC', 'North Carolina', 'First in Freedom'],
	              ['ND', 'North Dakota', 'Peace Garden State'],
	              ['OH', 'Ohio', 'The Heart of it All'],
	              ['OK', 'Oklahoma', 'Oklahoma is OK'],
	              ['OR', 'Oregon', 'Pacific Wonderland'],
	              ['PA', 'Pennsylvania', 'Keystone State'],
	              ['RI', 'Rhode Island', 'Ocean State'],
	              ['SC', 'South Carolina', 'Nothing Could be Finer'],
	              ['SD', 'South Dakota', 'Great Faces, Great Places'],
	              ['TN', 'Tennessee', 'Volunteer State'],
	              ['TX', 'Texas', 'Lone Star State'],
	              ['UT', 'Utah', 'Salt Lake State'],
	              ['VT', 'Vermont', 'Green Mountain State'],
	              ['VA', 'Virginia', 'Mother of States'],
	              ['WA', 'Washington', 'Green Tree State'],
	              ['WV', 'West Virginia', 'Mountain State'],
	              ['WI', 'Wisconsin', "America's Dairyland"],
	              ['WY', 'Wyoming', 'Like No Place on Earth']
	          ];


	stateStore = new Ext.data.ArrayStore({
	storeId: 'stateStore',
	fields: ['abbr','state','nick'],
	data: states
	});
	   

}
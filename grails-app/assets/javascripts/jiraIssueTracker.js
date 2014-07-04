// Requires jQuery!
jQuery.ajax({
	url: "https://softeng.atlassian.net/s/d41d8cd98f00b204e9800998ecf8427e/en_US2ixtbg/6322/26/1.4.11/_/download/batch/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector-embededjs/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector-embededjs.js?collectorId=014d4491",
	type: "get",
	cache: true,
	dataType: "script"
});

window.ATL_JQ_PAGE_PROPS =  {
	"triggerFunction": function(showCollectorDialog) {
		//Requries that jQuery is available!
		jQuery("p#feedback").click(function(e) {
			e.preventDefault();
			showCollectorDialog();
		});
	}};

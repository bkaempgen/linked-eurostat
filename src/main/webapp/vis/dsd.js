// Main runner method
$(document).ready(
    function() {
	var dsdquery = $("#dsdquery").clone();

	loadDsd(htmldecode(dsdquery));
    }
);

function loadDsd(querystring) {
    var request = { 
	query : querystring
    };
    
    $.ajax( 
	{
	    url: "http://ontologycentral.com/sparql", 
	    data : request, 
	    success:  function(data) {
		displayDsd(data);		
	    },
	    dataType: "json",
	    cache: true,
	    headers: {
		Accept: "application/sparql-results+json"
	    },
	    beforeSend: function() {
		$('#dsdprogress').show();
	    },
	    complete: function() {
		$('#dsdprogress').hide();
	    },
	    error: function() {   
		$("#dsderror").html("Error looking up results. Might be because of a timeout. Try again in a bit.");
	    }
	} 
    );
}

function displayDsd(data) { 
    var bindings = data.results.bindings;

    for (var b in bindings) {
	var res = bindings[b];
	var tmp = res.m;
	$("#dsd").append("<li><a href='" + tmp.value + "'>" + tmp.value + "</a></li>");	
    }
}

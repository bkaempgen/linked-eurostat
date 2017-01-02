
$(document).ready(function() {
    //var request = { accept : "application/sparql-results+json"};

    //$.ajaxSetup({ scriptCharset: "utf-8" , contentType: "application/sparql-results+json; charset=utf-8"});

	//var param = { query: query , accept: "application/sparql-results+json" };
    /*
    $.getJSON("http://localhost:8180/lodq-0.1/sparql", param,
	      function(data) {
		  alert(data);
		  $("map").html(data);
	      }
	);

    $.getJSON("http://localhost:8180/lodq-0.1/index.html",
	      function(data) {
		  alert(data);
		  $("map").html(data);
	      }
	);
    */

    $.getJSON("demo.json",
	      function(data) {
		  alert(data);
		  $("map").html(data);
	      });

});
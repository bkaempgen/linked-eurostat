// Main runner method
$(document).ready(
    function() {
	var query = $("#query").clone();
	load(htmldecode(query));
    }
);

// Content loader
function load(querystring) {
    var request = { 
	query : querystring
    };
    
    $.ajax(
	{
	    url: "http://ontologycentral.com/sparql",
	    data: request, 
	    success : function(data) {				
		  renderResults(data);		
	      },
	    dataType: "json",
	    headers: {
		Accept: 'application/sparql-results+json'
	    },
	    beforeSend: function() {
		$('#progress').show();
	    },
	    complete: function() {
		$('#progress').hide();
	    },
	    error: function() {   
		$("#error").html("Error looking up results. Might be because of a timeout. Try again in a bit.");
	    }
	}
    );
}

// Dimensions of the visualisation graph
var xFreiraum = 350, yFreiraum = 100;
var schaubildHoehe = 300, schaubildBreite = 600;	
var height = yFreiraum + schaubildHoehe;
var width = xFreiraum + schaubildBreite;


// Main visualisation method using Protovis
function renderResults(data) { 
    // Assignment of the xAxis, yAxis and Label variables according to the specifications.
    // Other variables mustn't appear in the query. 
    try {
	var xlabel = data.head.vars[0];
	var ylabel = data.head.vars[1];
	if (data.head.vars[2] != null) {
	    var label = data.head.vars[2];
	} else {
	    label = "undefined";	
	}
	//alert(xlabel);
	// Bindings of the data
	var bindings = data.results.bindings; 
    } catch (e) {
	$('#progress').hide();
	$("#error").html("Loading data failed." + " " + e);
    }	

    /*
    try {
	var time = bindings[0][xlabel].value;
    } catch(e) {
	$('#progress').hide();
	$("#error").html("Your query does not contain any bindings. Please verify your query." + " " + e);
    }*/

    // Date parsing
    bindings.forEach(function(d) {return d[xlabel].value = Date.parse(d[xlabel].value); });

    // Nested Array according to the different labels. If the label variable does not exist the key has the value "undefined".
    if (label != "undefined") {
	var nestedArray = pv.nest(data.results.bindings).key(function(d) { return d[label].value ; }).sortValues(dateSort).entries();
    } else {
	var nestedArray = pv.nest(data.results.bindings).key(function() { return label ; }).sortValues(dateSort).entries();
    }
					
    // Protovis 3.2 linear scales
    var x = pv.Scale.linear(bindings, function(d) { return d[xlabel].value; } ).range(0, width-xFreiraum);
    var y = pv.Scale.linear(pv.min(bindings, function(d) { return d[ylabel].value; } ), pv.max(bindings, function(d) { return d[ylabel].value; } )).range(yFreiraum, height).nice();	
	
    // 20 default colors 
    var c = pv.Colors.category20(); 
    
    // Ticks
    var xTicks = x.ticks();
    var yTicks = y.ticks();
	
    // Root Panel
    var vis = new pv.Panel()
	.canvas("map")
	.width(width)
	.height(height)
	.left(60)	// Abstaende Root-Panel zum Rand
	.bottom(20)
	.top(45)
	.right(40);
		
    // X-Ticks
    vis.add(pv.Rule)
	.data(xTicks)
	.visible(function() { return !(this.index % 2); } )
	.left(x)
	.bottom(yFreiraum)
	.strokeStyle("#eee")
	.anchor("bottom").add(pv.Label)
	.text(x.tickFormat); //neu
	  
		
    // Y-Ticks
    vis.add(pv.Rule)
	.data(yTicks)
	//.visible(function() !(this.index ٪ 2))  // visible ticks
	.bottom(y)
	.left(0)
	.right(xFreiraum) 
	//.strokeStyle(function() this.index ? "#eee" : "#000") // color
	.strokeStyle("#eee")
	.anchor("left").add(pv.Label)
	.text(y.tickFormat);
    
    // A panel for each label entry
    var panel = vis.add(pv.Panel).data(nestedArray)
	
    // A line for each label entry	  
    panel.add(pv.Line)
	.data(function(d) { return d.values; } )
	.strokeStyle(function(d) {
			 if( label != "undefined"){
			     return c(d[label].value);
			 } else {
			     return c(label);	
			 }
		     })
	.left(function(d) { return x(d[xlabel].value); } )	
	.bottom(function(d) { return y(d[ylabel].value); } ) 
			
    // Cutline if multible label exist	
    if (label != "undefined"){		
	panel.add(pv.Dot)
	    .left(schaubildBreite+70) // x-Position of the cutline
	    .top(function() { return (this.parent.index-3.3) * 12; } ) // y-Position of the cutline
	    .fillStyle(function(d) { return c(d.key); } )
	    .strokeStyle(null)
	    .anchor("right").add(pv.Label)
	    .text(function(d) { return d.key; } );
    }
	
    // XAxis labeling	
    vis.add(pv.Label)
	.left(schaubildBreite-20)
	.top(schaubildHoehe+35)
	.text(xlabel)
	.font(14);
		
    // YAxis labeling
    vis.add(pv.Label)
	.left(-35)
	.top(10)
	.text(ylabel)
	.font(20)
	.textAngle(-Math.PI / 2);
	
    // Rendering. Central protovis method.	
    vis.render();
}

// urldecode string
function htmldecode(value) { 
    return $('<div/>').html(value).text(); 
}

//  Sort function for time values
function dateSort(a, b) {
    return a.time.value.getTime() - b.time.value.getTime();
}

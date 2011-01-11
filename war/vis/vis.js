/**
 * Main runner method.
 */
$(document).ready(function() {
	/*
	var url = document.URL;
	var query = url.substring(url.indexOf("=")+1);
	query = unescape(query);
	query = query.replace(/\+/g," ");
	*/
	var query = $("#query").clone();
	load(htmldecode(query));
	/*
	query = query.replace(/\</g, "&lt;");
	query = query.replace(/\>/g, "&gt;");
	$("#query").html(query);	  	 	
	*/
});

function htmldecode(value){ 
  return $('<div/>').html(value).text(); 
}

jQuery.ajaxSetup({
  beforeSend: function() {
	    $('#progress').show();
  },
  complete: function() {
	    $('#progress').hide();
  },
  error: function() {   
	    $("#error").html("Error looking up results. Might be because of GAE timeout. Try again in a bit.");
  },
  success: function() {}
});

/**
 * Pool for Time Parser Namespaces
 */
var TimeConfigPool = { 
	"http://estatwrap.ontologycentral.com/dic/time#" : {
			parser : "http://estatwrap.ontologycentral.com/dic/time#%Y" 
			}, 
	"http://ontologycentral.com/2009/01/eurostat/time#" : {
			parser : "http://ontologycentral.com/2009/01/eurostat/time#%Ym%m" 
			}
	 };
  
/**
 * Content loader
 */
function load( /*String*/ querystring){
    var request = { accept : "application/sparql-results+json"};
	
    request.query = querystring;

    /*
    $.getJSON("http://q.ontologycentral.com/sparql", request, 
	      function(data){				
		  visualiseResults(data);		
	      }
	);
    */
    $.getJSON("../proxy", request, 
	      function(data){				
		  visualiseResults(data);		
	      }
	);
}

/**
 * Sort function for the time values
 */
function yearSort(a, b) {
	return a.time.value.getTime() - b.time.value.getTime();
}

/**
 * Dimensions of the visualization graph
 */
var xFreiraum = 350, yFreiraum = 100;
var schaubildHoehe = 300, schaubildBreite = 600;	
var height = yFreiraum + schaubildHoehe;
var width = xFreiraum + schaubildBreite;

/**
 * Main visualization method using Protovis
 */
function visualiseResults(/* JSON */ data){ 
    // Assignment of the xAxis, yAxis and Label variables according to the specefications.
    // Other variables mustn't appear in the query. 
    try {
	const xlabel = data.head.vars[0];
	const ylabel = data.head.vars[1];
	if (data.head.vars[2] != null) {
	    var label = data.head.vars[2];
	} else {
	    label = "undefined";	
	}		
		
	// Bindings of the data
	var bindings = data.results.bindings; 
    } catch (e) {
	$('#progress').hide();
	$("#error").html("Loading data failed." + " " + e);
    }		

    try {
	var time = bindings[0][xlabel].value;
    } catch(e) {
	$('#progress').hide();
	$("#error").html("Your query does not contain any bindings. Plese verify your query." + " " + e);
    }

    // Choosing the right parser
    var index = time.indexOf("#");
    var timeNS = time.substring(0, index + 1);
    var TimeConfig = TimeConfigPool[timeNS];
	
    // Date parsing
    var dateFormat = pv.Format.date(TimeConfig.parser);	  
    bindings.forEach(function(d) {return d[xlabel].value = dateFormat.parse(d[xlabel].value); });

    // Nested Array according to the different labels. If the label variable does not exist the key has the value "undefined".
    if (label != "undefined") {
	var nestedArray = pv.nest(data.results.bindings).key(function(d) { return d[label].value ; }).sortValues(yearSort).entries();
    } else {
	var nestedArray = pv.nest(data.results.bindings).key(function() { return label ; }).sortValues(yearSort).entries();
    }
    // Protovis 3.2 Number Formatting
    //var numberFormat = pv.Format.number(1);  // verändert irgendwas, muss noch untersucht werden. 
    //bindings.forEach(function(d) d[yAxis].value = numberFormat.parse(d[yAxis].value));

    // Delete non numerical values in the yAxis values. Scheint nich immer zu funktionieren (teicp000, nochmal überprüfen)
    for(var i in nestedArray){
	for(var j in nestedArray[i].values){	
	    nestedArray[i].values[j][ylabel].value = deleteNonNumerical(nestedArray[i].values[j][ylabel].value); 		
	}
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
    var panel = vis.add(pv.Panel)
	.data(nestedArray)
	
	// A line for each label entry	  
    panel.add(pv.Line)
	.data(function(d) { return d.values; } )
	.strokeStyle(function(d){
		if( label != "undefined"){
		    return c(d[label].value);
		}
		else{
		    return c(label);	
			}
		})
		.left(function(d) { return x(d[xlabel].value); } )	
		.bottom(function(d) { return y(d[ylabel].value); } ) 
			
	// Cutline if multible label exist	
	if(label != "undefined"){		
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

/**
 *  Deletes non numerical characters in a String // nochmal überprüfen
 */
function deleteNonNumerical(pstrSource) { 
var m_strOut = new String(pstrSource); 
    m_strOut = m_strOut.replace(/[^0-9\.]/g, ''); 

    return m_strOut; 
}


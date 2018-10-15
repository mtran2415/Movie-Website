var resultDict = {};

function handleLookup(query, doneCallback) {	
	if(query.length < 3){
		console.log("Query '" + query + "' is not long enough to justify result search");
		return;
	}
	
	for(var key in resultDict){
		if (key == query.toLowerCase()){
			console.log("Using past query results instead of requesting data from servlet");
			handleLookupSuccess(resultDict[key], key, doneCallback);
			return;
		}
	}

	
	console.log("autocomplete initiated")
	console.log("sending AJAX request to backend Java Servlet")
	
	jQuery.ajax({
		"method": "GET",
		"url": "autocomplete-search?query=" + escape(query),
		"success": function(data) {
			resultDict[query.toLowerCase()] = data;
			handleLookupSuccess(data, query, doneCallback) 
		},
		"error": function(errorData) {
			console.log("lookup ajax error")
			console.log(errorData)
		}
	})
}



function handleLookupSuccess(data, query, doneCallback) {
	console.log("lookup successful")
	
	var jsonData = JSON.parse(data);
	console.log(jsonData)

	console.log(jsonData === undefined);
	if(jsonData != undefined)
		doneCallback( { suggestions: jsonData } );
}


function handleSelectSuggestion(suggestion) {
	
	console.log("you select " + suggestion["value"])
    var url = "single-movie.html?id=" + suggestion["data"]["movieID"];
	window.location = url;	
}



$('#autocomplete').autocomplete({
	// documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
    		handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
    		handleSelectSuggestion(suggestion)
    },
    groupBy: "category",
    // set delay time
    deferRequestBy: 300,
});



function handleNormalSearch(query) {
	console.log("doing normal search with query: " + query);
	var url = "results.html?title="+ escape(query) +"&year=&director=&star=&limit=10&offset=0&header=title&sort=asc"
	window.location = url;
}

$('#autocomplete').keypress(function(event) {
	// keyCode 13 is the enter key
	if (event.keyCode == 13) {
		// pass the value of the input box to the handler function
		handleNormalSearch($('#autocomplete').val());
	}
});

$('#submit').click(
		function(){
			handleNormalSearch(escape($('#autocomplete').val()));
		});

//https://localhost:8443/Fablix/results.html?title=Chief&year=&director=&star=&limit=10&offset=0&header=title&sort=asc
//https://localhost:8443/Fablix/results.html?title=Chiefyear=&director=&star=&limit=10&offset=0&header=title&sort=asc
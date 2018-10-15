function handleLoadResult(resultDataJson) {
    console.log("handle load response");
    console.log(resultDataJson);
    
    //resultDataJson = JSON.parse(resultData);

    if (resultDataJson.length) {        
        let dbTablesDivElement = jQuery("#db_tables");
        for (let i = 0; i < resultDataJson.length; i++) {
        	let tableHTML = "<h4>"+resultDataJson[i]["table_name"]+"</h4>"
        	tableHTML += "<table class=\"table table-striped\">"+
        			"<thead><tr><th>Column Name</th><th>Type</th></tr></thead>";
        	for(var key in resultDataJson[i]){
        		if(key != "table_name"){
	        		tableHTML += "<tr>";
	        		tableHTML += "<th>" +resultDataJson[i][key].split(",")[0] +"</th>";
	        		tableHTML += "<th>" + resultDataJson[i][key].split(",")[1] + "</th>";
	        		tableHTML += "</tr>";
        		}
        	}
        	tableHTML += "</tbody></table><br>";
        	dbTablesDivElement.append(tableHTML);
        }
    }
    else {
        console.log("show error message");
    }
}

console.log("before ajax");
jQuery.ajax({
    dataType: "json",  
    method: "POST",
    url: "api/_dashboard_load", 
    success: (resultData) => handleLoadResult(resultData)
});
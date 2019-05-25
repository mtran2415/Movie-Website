function submitForm(formSubmitEvent){
    console.log("submit search form");
    formSubmitEvent.preventDefault();
        
    for(var i = 0; i < document.forms["checkout_form"].elements.length; i++){
    	if(document.forms["checkout_form"].elements[i].value == ''){
    		alert("Please input a value for each quantity.");
    		return;
    	}
    }
    
    jQuery.post(
    	    "api/update-cart",
    	    jQuery("#checkout_form").serialize()
    );
	window.location = "checkout.html"
}

function removeFromCartForm(event, formToSubmit){
    jQuery.post(
    "api/remove-cart",
    jQuery("#"+formToSubmit).serialize());
}

function handleMovieResult(resultData) {
	console.log("handle movie result: populate movie table");
	console.log(resultData);
	
    let starTableBodyElement = jQuery("#movie_table_body");

    for (let i = 0; i < resultData.length; i++) {

    	var removeForm = "removeForm"+i.toString();
    	var hiddenId = "hidden_id"+i.toString();
    	var quantityId = "quantity_id"+i.toString();
    	
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +     
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";
        rowHTML += '<td>'+
        	'<form id="'+removeForm+'" onsubmit="removeFromCartForm(event, \''+removeForm+'\')">' + 
        	  '<input id="'+hiddenId+'" type="text" value="'+resultData[i]['movie_id']+'" name="movie_id">' + 
        	  '<button type="submit">Remove</button>'+
        	'</form>'+
        	'</td>';
        rowHTML += '<td>'+
    	  '<input form="checkout_form" id="'+quantityId+'" type="number" min="1" max="2000000000" step="1" value="'+resultData[i]['quantity']+'" name="'+resultData[i]['movie_id']+'">' + 
    	'</td>';
        rowHTML += "</tr>";

        starTableBodyElement.append(rowHTML);
    }
}

jQuery("#checkout_form").submit((event) => submitForm(event));

jQuery.ajax({
    dataType: "json", 
    method: "GET", 
    url: "api/shopping-cart",
    success: (resultData) => handleMovieResult(resultData)  
});
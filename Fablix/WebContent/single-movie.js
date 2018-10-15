function getParameterByName(target) {
    let url = window.location.href;
    target = target.replace(/[\[\]]/g, "\\$&");

    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function linkListOfAttributes(nameList, idList, link, nameSplit, idSplit){
	var newString = "";
	var nameList = nameList.split(nameSplit);
	var idList = idList.split(idSplit);
	
	for(var i = 0; i < nameList.length-1; i++){
		newString += '<a href="'+link+idList[i]+ '">' + nameList[i] + '</a>, ';
	}
	
	newString += '<a href="'+link+idList[nameList.length-1]+ '">' + nameList[nameList.length-1] + '</a>'
	
	return newString;
}

function linkGenre(nameList, idList, link, nameSplit, idSplit){
	var newString = "";
	var nameList = nameList.split(nameSplit);
	var idList = idList.split(idSplit);
	
	for(var i = 0; i < nameList.length-1; i++){
		newString += '<a href="'+link+idList[i].trim()+ '&letter=&limit=10&offset=0&header=title&sort=asc">' + nameList[i] + '</a>, ';
	}
	
	newString += '<a href="'+link+idList[nameList.length-1].trim() + '&letter=&limit=10&offset=0&header=title&sort=asc">' + nameList[nameList.length-1] + '</a>'
	
	return newString;
}

function handleResult(resultData) {
    console.log("handleResult: populating star info from resultData");
    console.log(resultData);

    
    
    let starInfoElement = jQuery("#movie_info");
    
    if(resultData.length == 0){
    	starInfoElement.append("<p>Movie could not be loaded. Missing Stars or Genres</p>");
    	return;
    }

    starInfoElement.append("<p>Movie Title: " + resultData[0]["movie_title"] + "</p>" +
        "<p>Director: " + resultData[0]["movie_director"] + "</p>" + 
        "<p>Year: " + resultData[0]["movie_year"] + "</p>" +
        "<p>Stars: " + linkListOfAttributes(resultData[0]["movie_stars"], resultData[0]["star_ids"], "single-star.html?id=", ",", " ") + "</p>" +
        "<p>Genres: " + linkGenre(resultData[0]["movie_genres"], resultData[0]["movie_genres"], "browse-results.html?genre=", ",", ",") + "</p>");
}

function handleAddCartResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);

    console.log("handle add cart response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    if (resultDataJson["status"] === "success") {
        console.log("movie added to cart successfully");
        jQuery("#add_cart_message").text(resultDataJson["message"]);
    }    
    else {

        console.log("show error message");
        console.log(resultDataJson["message"]);
        jQuery("#add_cart_message").text(resultDataJson["message"]);
    }
}

function submitAddCartForm(formSubmitEvent) {
    console.log("submit add cart form");
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/add-cart",
        jQuery("#add_cart_form").serialize(),
        (resultDataString) => handleAddCartResult(resultDataString));
}

let movieId = getParameterByName('id');

jQuery.ajax({
    dataType: "json",  
    method: "GET",
    url: "api/single-movie?id=" + movieId, 
    success: (resultData) => handleResult(resultData)
});

jQuery("#add_cart_form").submit((event) => submitAddCartForm(event));
jQuery("#hidden_id").val(movieId)
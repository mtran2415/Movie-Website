function getParameterByName(target) {
    let url = window.location.href;
    
    target = target.replace(/[\[\]]/g, "\\$&");
    target = target.replace(/%/g, "");
    
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function submitForm(formSubmitEvent) {
    console.log("submit search form");

    formSubmitEvent.preventDefault();

    var title = getParameterByName('title');
    var year = getParameterByName('year');
    var dir = getParameterByName('director');
    var star = getParameterByName('star');
    var limit = $("#limit").val();
    var offset = 0;
    var header = getParameterByName('header');
    var sort = getParameterByName('sort');
    
    var url = "results.html?title=" + title + "&year=" + year + "&director=" + dir + "&star=" + star + "&limit=" + limit + "&offset=" + offset +
    "&header=" + header + "&sort=" + sort;
    window.location = url;
}
jQuery("#page_form").submit((event) => submitForm(event));

function linkGenre(nameList, idList, link){
	var newString = "";
	var nameList = nameList.split(',');
	var idList = idList.split(',');
	
	for(var i = 0; i < nameList.length-1; i++){
		newString += '<a href="'+link+idList[i].trim() + '&letter=&limit=10&offset=0&header=title&sort=asc">' + nameList[i] + '</a>, ';
	}
	
	newString += '<a href="'+link+idList[nameList.length-1].trim() + '&letter=&limit=10&offset=0&header=title&sort=asc">' + nameList[nameList.length-1] + '</a>'
	
	return newString;
}

function handleResult(resultData) {
    let starInfoElement = jQuery("#movie_table_info");

    console.log(JSON.stringify(resultData));
    
    let movieTableBodyElement = jQuery("#movie_table_body");

    if(resultData.length < getParameterByName("limit"))
    	document.getElementById("next").style.visibility = "hidden";
    if(getParameterByName("offset") <= 0)
    	document.getElementById("prev").style.visibility = "hidden";
    
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<td>" + resultData[i]["id"] + "</td>";
        rowHTML += "<td><a href=\"single-movie.html?id=" + resultData[i]["id"] + "\">" + resultData[i]["title"] + "</td>";       
        rowHTML += "<td>" + resultData[i]["year"] + "</td>";
        rowHTML += "<td>" + resultData[i]["director"] + "</td>";
        rowHTML += "<td>" + resultData[i]["rating"] + "</td>";
        rowHTML += "<td>" + linkGenre(resultData[i]["genres"], resultData[i]["genres"], "browse-results.html?genre=") + "</td>";
        rowHTML += "<td>" + linkListOfAttributes(resultData[i]["stars"], resultData[i]["starIds"], "single-star.html?id=") + "</td>";
        rowHTML += "<td><button type='button' id='" + resultData[i]["id"] + "' onClick='addToCart(this.id)'>Add to Cart</button></td>"
        rowHTML += "</tr>";

        movieTableBodyElement.append(rowHTML);
    }
    	
    if(resultData.length == 0){
    	movieTableBodyElement.append("No Results");
    }
}

function addToCart(mid){
	console.log("submit add cart form");

	jQuery.ajax({
	    type: "POST",
	    data: {
	        movie_id: mid
	    },
	    url: "api/add-cart",
	    success: (resultData) => handleAddCartResult(resultData)
	})
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

function titleClick() {
	console.log("title click");
	
	var title = getParameterByName('title');
    var year = getParameterByName('year');
    var dir = getParameterByName('director');
    var star = getParameterByName('star');
    var limit = getParameterByName('limit');
    var offset = 0;
    var header = getParameterByName('header');
    var sort = getParameterByName('sort');
    if(sort == "asc")
    	sort = "desc";
    else
    	sort = "asc";
    
    var url = "results.html?title=" + title + "&year=" + year + "&director=" + dir + "&star=" + star + "&limit=" + limit + "&offset=" + offset +
    "&header=" + header + "&sort=" + sort;
    window.location = url;
}

function ratingClick() {
	console.log("rating click");
	
	var title = getParameterByName('title');
    var year = getParameterByName('year');
    var dir = getParameterByName('director');
    var star = getParameterByName('star');
    var limit = getParameterByName('limit');
    var offset = 0;
    var header = "rating";
    var sort = getParameterByName('sort');
    if(sort == "asc")
    	sort = "desc";
    else
    	sort = "asc";
    
    var url = "results.html?title=" + title + "&year=" + year + "&director=" + dir + "&star=" + star + "&limit=" + limit + "&offset=" + offset +
    "&header=" + header + "&sort=" + sort;
    window.location = url;
}

function linkListOfAttributes(nameList, idList, link){
	var newString = "";
	var nameList = nameList.split(',');
	var idList = idList.split(' ');
	
	for(var i = 0; i < nameList.length-1; i++){
		newString += '<a href="'+link+idList[i]+ '">' + nameList[i] + '</a>, ';
	}
	
	newString += '<a href="'+link+idList[nameList.length-1]+ '">' + nameList[nameList.length-1] + '</a>'
	
	return newString;
}

function clickNext(formSubmitEvent) {
    console.log("click next");

    var title = getParameterByName('title');
    var year = getParameterByName('year');
    var dir = getParameterByName('director');
    var star = getParameterByName('star');
    var limit = getParameterByName('limit');
    var offset = parseInt(getParameterByName('offset')) + parseInt(limit);
    var header = getParameterByName('header');
    var sort = getParameterByName('sort');
    
    var url = "results.html?title=" + title + "&year=" + year + "&director=" + dir + "&star=" + star + "&limit=" + limit + "&offset=" + offset.toString() +
    "&header=" + header + "&sort=" + sort;
    window.location = url;
}
jQuery("#next").click((event) => clickNext(event));

function clickPrev(formSubmitEvent) {
    console.log("click prev");

    var title = getParameterByName('title');
    var year = getParameterByName('year');
    var dir = getParameterByName('director');
    var star = getParameterByName('star');
    var limit = getParameterByName('limit');
    var offset = parseInt(getParameterByName('offset')) - parseInt(limit);
    if(offset < 0)
    	offset = 0;
    var header = getParameterByName('header');
    var sort = getParameterByName('sort');
    
    var url = "results.html?title=" + title + "&year=" + year + "&director=" + dir + "&star=" + star + "&limit=" + limit + "&offset=" + offset.toString() +
    "&header=" + header + "&sort=" + sort;
    window.location = url;
}
jQuery("#prev").click((event) => clickPrev(event));

let title = getParameterByName('title');
let year = getParameterByName('year');
let dir = getParameterByName('director');
let star = getParameterByName('star');
let limit = getParameterByName('limit');
let offset = getParameterByName('offset');
let header = getParameterByName('header');
let sort = getParameterByName('sort');

jQuery.ajax({
    dataType: "json", 
    method: "GET",
    url: "search?title=" + title + "&year=" + year + "&director=" + dir + "&star=" + star + "&limit=" + limit + "&offset=" + offset + "&header=" + header + "&sort=" + sort,
    success: (resultData) => handleResult(resultData)
})
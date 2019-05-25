function handleResult(resultData)
{
	let genreDiv = jQuery("#genres");
	
	for (let i = 0; i < resultData.length; i++) {
        let ref = "";
        ref += "<a href='browse-results.html?genre=" + resultData[i]["gname"] + "&letter=&limit=10&offset=0&header=title&sort=asc'>" + resultData[i]["gname"] + "</a><br>"
        genreDiv.append(ref);
	}
}

jQuery.ajax({
    dataType: "json", 
    method: "GET",
    url: "browse-genres",
    success: (resultData) => handleResult(resultData)
})



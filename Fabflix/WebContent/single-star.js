function getParameterByName(target) {
    let url = window.location.href;
    target = target.replace(/[\[\]]/g, "\\$&");

    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleResult(resultData) {
    console.log("handleResult: populating star info from resultData");
    console.log(resultData)

    let starInfoElement = jQuery("#star_info");

    starInfoElement.append("<p>Star Name: " + resultData[0]["star_name"] + "</p>" +
        "<p>Date Of Birth: " + resultData[0]["star_dob"] + "</p>");

    let movieTableBodyElement = jQuery("#movie_table_body");

    for (let i = 0; i < Math.min(10, resultData.length); i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" +"<a href=\"single-movie.html?id="+resultData[i]["movie_id"]+"\">"+ resultData[i]["movie_title"] +"</a>"+"</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "</tr>";

        movieTableBodyElement.append(rowHTML);
    }
}

let starId = getParameterByName('id');
console.log(starId);
jQuery.ajax({
    dataType: "json",  
    method: "GET",
    url: "api/single-star?id=" + starId, 
    success: (resultData) => handleResult(resultData)
});
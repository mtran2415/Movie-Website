function handleAddMovieResult(resultData){
	resultDataJson = JSON.parse(resultData);
    console.log("handle add movie response");
    if (resultDataJson["status"] == "success") {
        console.log(resultDataJson["message"]);
        jQuery("#add_movie_response").text(resultDataJson["message"]);
    }
    else {
        console.log(resultDataJson["message"]);
        jQuery("#add_movie_response").text(resultDataJson["message"]);
    }
}

function submitAddMovieForm(formSubmitEvent) {
    console.log("submit add movie form");
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/_dashboard_add_movie",
        jQuery("#add_movie_form").serialize(),
        (resultData) => handleAddMovieResult(resultData));
}

jQuery("#add_movie_form").submit((event) => submitAddMovieForm(event));

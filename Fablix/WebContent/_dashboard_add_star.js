function handleAddStarResult(resultData){
	resultDataJson = JSON.parse(resultData);
    console.log("handle add star response");
    if (resultDataJson["status"] == "success") {
        console.log(resultDataJson["message"]);
        jQuery("#add_star_response").text(resultDataJson["message"]);
    }
    else {
        console.log(resultDataJson["message"]);
        jQuery("#add_star_response").text(resultDataJson["message"]);
    }
}

function submitAddStarForm(formSubmitEvent) {
    console.log("submit add star form");
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/_dashboard_add_star",
        jQuery("#add_star_form").serialize(),
        (resultData) => handleAddStarResult(resultData));
}

jQuery("#add_star_form").submit((event) => submitAddStarForm(event));

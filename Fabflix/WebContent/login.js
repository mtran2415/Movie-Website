function handleLoginResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    if (resultDataJson["status"] === "success") {
        window.location.replace("index.html");
    }
    else {

        console.log("show error message");
        console.log(resultDataJson["message"]);
        jQuery("#login_error_message").text(resultDataJson["message"]);
    }
}

function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");

    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/login",
        jQuery("#login_form").serialize(),
        (resultDataString) => handleLoginResult(resultDataString));

}

jQuery("#login_form").submit((event) => submitLoginForm(event));


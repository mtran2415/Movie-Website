function handleCheckoutResult(resultData) {
    console.log("handle checkout response");
    console.log(resultData);
    
    resultDataJson = JSON.parse(resultData);

    if (resultDataJson.length) {
        jQuery("#checkout_message").text("Checkout successful! Thank you for choosing Fablix!");
        document.getElementById("checkout_form").style.display = "none";
        document.getElementById("sale_table").style.visibility = "visible";
        
        let saleTableBodyElement = jQuery("#sale_table_body");
        for (let i = 0; i < resultDataJson.length; i++) {
            let rowHTML = "";
            rowHTML += "<tr>";
            rowHTML += "<th>" +resultDataJson[i]["title"] +"</th>";
            rowHTML += "<th>" + resultDataJson[i]["saleId"] + "</th>";
            rowHTML += "<th>" + resultDataJson[i]["quantity"] + "</th>";
            rowHTML += "</tr>";

            saleTableBodyElement.append(rowHTML);
        }
    }
    else {
        console.log("show error message");
        jQuery("#checkout_message").text("Could not verify user info!");
    }
}


function submitCheckoutForm(formSubmitEvent) {
    console.log("submit checkout form");
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/checkout",
        jQuery("#checkout_form").serialize(),
        (resultDataString) => handleCheckoutResult(resultDataString));

}

document.getElementById("sale_table").style.visibility = "hidden";

jQuery("#checkout_form").submit((event) => submitCheckoutForm(event));


function submitForm(formSubmitEvent) {
    console.log("submit search form");

    formSubmitEvent.preventDefault();

    var title = $("#title").val();
    var year = $("#year").val();
    var dir = $("#dir").val();
    var star = $("#star").val();
    var limit = $("#limit").val();
    var offset = 0;
    var header = "title";
    var sort = "asc";
    
    var url = "results.html?title=" + title + "&year=" + year + "&director=" + dir + "&star=" + star + "&limit=" + limit + "&offset=" + offset +
    "&header=" + header + "&sort=" + sort;
    window.location = url;
}
jQuery("#search_form").submit((event) => submitForm(event));
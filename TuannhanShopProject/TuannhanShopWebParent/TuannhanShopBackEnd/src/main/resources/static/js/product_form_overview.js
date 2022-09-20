dropDownBrands = $("#brand")
dropDownCategories = $("#category")

$(document).ready(function() {

	$("#shortDescription").richText()
	$("#fullDescription").richText()

	dropDownBrands.change(function() {
		dropDownCategories.empty()
		getCategories()
	})
	
	getCategories()
	
})


function getCategories() {
	brandID = dropDownBrands.val()
	url = brandModuleURL + "/" + brandID + "/categories"

	$.get(url, function(responseJson) {
		$.each(responseJson, function(index, category) {
			$("<option>").val(category.id).text(category.name).appendTo(dropDownCategories)
		})
	})
}

function checkUnique(form) {
	productId = $("#id").val()
	productName = $("#name").val()

	csrfValue = $("input[name='_csrf']").val()

	params = { id: productId, name: productName, _csrf: csrfValue }

	$.post(checkUniqueURL, params, function(response) {
		if (response == "OK") {
			form.submit()
		} else if (response == "Duplicated") {
			showWarningModal("There is another product having same name " + productName)
		} else {
			showErrorModal("Error server")
		}
	}).fail(function() {
		showErrorModal("Could not connect to the server")
	})

	return false;
}
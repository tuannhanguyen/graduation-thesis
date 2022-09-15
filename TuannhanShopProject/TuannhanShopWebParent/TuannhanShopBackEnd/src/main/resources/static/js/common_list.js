function clearFilter() {
    window.location = moduleURL
}

function showDeleteConfirmation(link, entityName) {
    entityID = link.attr("entityID");

    $("#yesButton").attr("href", link.attr("href"))
    $("#confirmText").text("Are you sure want to delete this " + entityName + " ID " + entityID + "?")
    $("#confirmModal").modal()
}
$(document).ready(function(e) {
        $("#logoutLink").on('click', function(e) {
            e.preventDefault()
            document.logoutForm.submit()
        })
    })
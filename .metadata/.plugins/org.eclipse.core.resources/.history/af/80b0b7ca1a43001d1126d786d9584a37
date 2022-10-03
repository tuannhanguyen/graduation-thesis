$(document).ready(function(){
	
	
	$("#btnAddToCart").click(function(){
		addToCart();
	})
});

function addToCart(){
	quantity = $("#quantity" + productId).val();
	url = contextPath + "cart/add/" + productId + "/" + quantity;
	
	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(res){
		showModalDialog("Shopping Cart", res);
	}).fail(function(){
		showErrorModal("Error while adding product to shopping cart.");
	})
}

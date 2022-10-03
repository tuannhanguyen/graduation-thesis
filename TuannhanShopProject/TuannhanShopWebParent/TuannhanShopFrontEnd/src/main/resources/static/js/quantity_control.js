$(document).ready(function(){
    minusControl = $(".link-Minus");
    plusControl = $(".link-Plus");
    
    minusControl.click(function(e){
        e.preventDefault();
        productId = $(this).attr("pid");
        quantityInput = $("#quantity" + productId);
        newQuantity = parseInt(quantityInput.val()) - 1;
        
        if(newQuantity > 0){
            quantityInput.val(newQuantity);
        } else{
            showWarningModal("Minimum quantity is 1");
        }
    });
    
    plusControl.click(function(e){
        e.preventDefault();
        productId = $(this).attr("pid");
        quantityInput = $("#quantity" + productId);
        newQuantity = parseInt(quantityInput.val()) + 1;
        
        if(newQuantity <= 5){
            quantityInput.val(newQuantity);
        } else{
            showWarningModal("Maximum quantity is 5");          
        }
    });
});

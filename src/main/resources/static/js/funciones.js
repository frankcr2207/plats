function numbersonly(e){
				
			    var unicode=e.charCode? e.charCode : e.keyCode
			    
			    if (unicode!=8){ //if the key isn't the backspace key (which we should allow)
			        if (unicode<48||unicode>57) //if not a number
			            return false //disable key press
			    }
			    
			}

function lettersonly(event){
			    var regex = new RegExp("^[a-zA-Z ]+$");
				var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
				if (!regex.test(key)) {
				    event.preventDefault();
					return false;
				}
			    
			}

function tecladoDesactivado(e){
	

            return false;
   
    
}
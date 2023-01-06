/**
 * 
 */

function verInstanciasSecretario(cod){
	var json = {
			"id" : cod
		};

	  	$.ajax({
	   		type : "POST",
	   		contentType : "application/json",
	   		url : "obtInstanciasSecretario",
	   		data : JSON.stringify(json),
	        dataType : 'json',
	        cache : false,
	        timeout : 600000,
	        success : function(data) {
	        	var html = '<table style="font-size:11px"><tr><th></th><th>INSTANCIAS DEL SECRETARIO</th></tr>';
	         	var len = data.length;
	         	html += '';
	         	for (var i = 0; i < len; i++) {
	         		html += '<tr><td>' + data[i].c_instancia + '</td><td>' + data[i].x_nom_instancia + '</td><td><button class="btn btn-outline-danger btn-sm" type="button" onclick="quitarInstancia(\'' + data[i].c_instancia + '\')"><i class="fas fa-minus fa-lg"></i></button></td></tr>';
		        }
		        html += '</table>';
		        $('.divInstanciasSecretario').html(html);
	        },
	    	error : function(e) {
	    	 	Swal.fire({
				  	icon: 'error',
				  	title: 'ATENCIÓN',
				  	text: 'Ooops, algo salió mal, no se pudo cargar instancias.!!',
				});
	     	}
	  	});
};

function quitarInstancia(instancia){
	var usuario = $('.c_usuario').val();
	var json = {
		"usuario" : usuario,
		"instancia" : instancia
	};

  	$.ajax({
   		type : "POST",
   		contentType : "application/json",
   		url : "quitarInstancia",
   		data : JSON.stringify(json),
        dataType : 'json',
        cache : false,
        success : function(response) {
        	if(response.Status == 200){ 
        		toastr.clear();
				toastr.options = {
					"closeButton":true,
					"progressBar": true,
					"positionClass":"toast-topcenter-right"
				};
				toastr.success("Instancia eliminada correctamente!!");
        		verInstanciasSecretario(usuario);
			}
        	else if(response.Status == 400){
        		toastr.clear();
				toastr.options = {
					"closeButton":true,
					"progressBar": true,
					"positionClass":"toast-topcenter-right"
				};
				toastr.warning("La instancia ya fue eliminada al Secretario!!");
        	}
        	else{
        		Swal.fire({
					icon: 'error',
					title: 'ATENCIÓN',
					text: 'Ooops, algo salió mal, no se pudo eliminar la información.!!',
					  
				});
        	}
        }
  	});
};

function verPermisosSecretario(cod,nom){
	$('.c_usuario').val(cod);
	$('.nombresCompletos').val(nom);
	$('.divInstanciasCDM').empty();
	var json = {
		"id" : "1"
	};

  	$.ajax({
   		type : "POST",
   		contentType : "application/json",
   		url : "obtCDM",
   		data : JSON.stringify(json),
        dataType : 'json',
        cache : false,
        timeout : 600000,
        success : function(data) {
         	var html = '';
         	var len = data.length;
         	html += '';
         	for (var i = 0; i < len; i++) {
         		html += '<option value="' + data[i].id + '">' + data[i].nombres + '</option>';
	        }
	        $('.listaCDM').html(html);
        },
    	error : function(e) {
    	 	Swal.fire({
			  	icon: 'error',
			  	title: 'ATENCIÓN',
			  	text: 'Ooops, algo salió mal, no se pudo cargar instancias.!!',
			});
     	}
  	});
  	verInstanciasSecretario(cod);
	$('.modalSecretario').modal('show');
};

$('.btnFiltrarCDM').click(function(){
	var c_usuario = $('.c_usuario').val();
	var idCDM = null; 
	$('.divInstanciasCDM').empty();
	idCDM = $('.listaCDM').val();
	if(idCDM == '0'){
		return false;
	};
	var json = {
		"id" : idCDM
	};
	$.ajax({
   		type : "POST",
   		contentType : "application/json",
   		url : "obtInstanciasCDM",
   		data : JSON.stringify(json),
        dataType : 'json',
        cache : false,
        timeout : 600000,
        success : function(data) {
         	var html = '<table style="font-size:11px"><tr><th></th><th>INSTANCIAS CDM</th></tr>';
         	var len = data.length;
         	html += '';
         	for (var i = 0; i < len; i++) {
         		html += '<tr><td>' + data[i].c_instancia + '</td><td>' + data[i].x_nom_instancia + '</td><td><button class="btn btn-outline-success btn-sm" type="button" onclick="asignarInstancia(\'' + data[i].c_instancia + '\')"><i class="fas fa-chevron-circle-right fa-lg"></i></button></td></tr>';
	        }
	        html += '</table>';
	        $('.divInstanciasCDM').html(html);
        },
    	error : function(e) {
    	 	Swal.fire({
			  	icon: 'error',
			  	title: 'ATENCIÓN',
			  	text: 'Ooops, algo salió mal, no se pudo cargar instancias.!!',
			});
     	}
  	});
});

function asignarInstancia(instancia){
	var usuario = $('.c_usuario').val();
	usuario = usuario.toUpperCase();
	instancia = instancia.toUpperCase();
	var json = {
		"usuario" : usuario,
		"instancia" : instancia
	};

  	$.ajax({
   		type : "POST",
   		contentType : "application/json",
   		url : "asignarInstancia",
   		data : JSON.stringify(json),
        dataType : 'json',
        cache : false,
        success : function(response) {
        	if(response.Status == 200){ 
        		toastr.clear();
				toastr.options = {
					"closeButton":true,
					"progressBar": true,
					"positionClass":"toast-topcenter-right"
				};
				toastr.success("Instancia agregada correctamente!!");
        		verInstanciasSecretario(usuario);
			}
        	else if(response.Status == 400){
        		toastr.clear();
				toastr.options = {
					"closeButton":true,
					"progressBar": true,
					"positionClass":"toast-topcenter-right"
				};
				toastr.warning("La instancia ya se encuentra asignada al Secretario!!");
        	}
        	else{
        		Swal.fire({
					icon: 'error',
					title: 'ATENCIÓN',
					text: 'Ooops, algo salió mal, no se pudo guardar la información.!!',
					  
				});
        	}
        }
  	});
};



$(".restUsu").click(function(){	
	$.ajax({
		type:"POST",
		url:"restablecerClave",
		data:"usuario=" + $('.usuarioUsuario').val(),
		success:function(response) {
			if(response.Status == 200){ 
				$(".modalUsuario").modal('hide');//ocultamos el modal
				Swal.fire(
					'CLAVE RESTABLECIDA!!',
					'Click en OK para continuar!',
					'success'
				);
			}
			else {
				$(".modalUsuario").modal("hide");
				Swal.fire({
					icon: 'error',
					title: 'ATENCIÓN',
					text: 'Ooops, algo salió mal, no se pudo enviar la información.!!',
					  
				});
			}
		}
	
	});
});




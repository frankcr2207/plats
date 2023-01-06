/**
 * 
 */



$(".updateAud").on("submit", function(e) {
	e.preventDefault();
	var formData = new FormData(this);
	$.ajax({
		type:"POST",
		url:"actualizarAudiencia",
		data:formData,
		cache:false,
		dataType:"json",
		contentType:false,
		processData:false,
		success:function(response) {
			if(response.Status == 200){ 
				$(".detalleObjetos").empty();
				var url= 'AUDIENCIAS';
				$(".listaObjetos").load(url);
				Swal.fire(
						  'AUDIENCIA ACTUALIZADA!!',
						  'Click en OK para continuar!',
						  'success'
						);
			}
			else if(response.Status == 400){
				Swal.fire({
					  icon: 'info',
					  title: 'ATENCIÓN',
					  text: 'No se pudo actualizar, la audiencia ya está iniciando!!',
					  
					});
			}
			else {
				Swal.fire({
					  icon: 'error',
					  title: 'ATENCIÓN',
					  text: 'Ooops, algo salió mal, no se pudo enviar la información.!!',
					  
					});
			}
		}
	
	});
});

$(document).ready(function() {
	$(".actParam").click(function() {
		
		Swal.fire({
			  title: 'Está seguro de cambiar el parámetro?',
			  text: "",
			  icon: 'question',
			  showCancelButton: true,
			  confirmButtonColor: '#3085d6',
			  cancelButtonColor: '#d33',
			  confirmButtonText: 'SI, CAMBIAR!'
			}).then((result) => {
				if (result.value) {
					
					$(function () {
					    var token = $("input[name='_csrf']").val();
					    var header = "X-CSRF-TOKEN";
					    $(document).ajaxSend(function(e, xhr, options) {
					        xhr.setRequestHeader(header, token);
					    });
					});
					
	                // Creamos un objeto con los datos a enviar
					var cod = $(".cod").val();
		      		var valor = $(".valor").val();
		      		var json = {
		      			"codigo" : cod,
		       			"valor" : valor
		      		};
		 
			      	$.ajax({
			       		type : "POST",
			       		contentType : "application/json",
						
			       		url : "actParam",
			       		data : JSON.stringify(json),
				        dataType : 'json',
				        cache : false,
				        success : function(response) {
				        	if(response.Status == 200){ 
				        		$(".detalleObjetos").empty();
								$(".listaObjetos").load('parametros');
					        	Swal.fire(
									'ACTUALIZACIÓN EXITOSA!!',
									'Click en OK para continuar!',
									'success'
								);
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
					  

				  }
			})
		
  		
	});
});

$(document).ready(function() {
	$(".updateLaw").click(function() {
		Swal.fire({
			  title: 'Está seguro de actualizar datos del abogado?',
			  text: "",
			  icon: 'question',
			  showCancelButton: true,
			  confirmButtonColor: '#3085d6',
			  cancelButtonColor: '#d33',
			  confirmButtonText: 'SI, CAMBIAR!'
			}).then((result) => {
				if (result.value) {
					
					$(function () {
					    var token = $("input[name='_csrf']").val();
					    var header = "X-CSRF-TOKEN";
					    $(document).ajaxSend(function(e, xhr, options) {
					        xhr.setRequestHeader(header, token);
					    });
					});
					
	                // Creamos un objeto con los datos a enviar
					var dni = $(".dni").val();
		      		var estado = $(".estateAbogado").val();
		      		var json = {
		      			"dni" : dni,
		       			"estado" : estado
		      		};
		 
			      	$.ajax({
			       		type : "POST",
			       		contentType : "application/json",
						
			       		url : "actAbo",
			       		data : JSON.stringify(json),
				        dataType : 'json',
				        cache : false,
				        success : function(response) {
				        	if(response.Status == 200){ 
				        		$(".detalleObjetos").empty();
								$(".listaObjetos").load('abogados');
					        	Swal.fire(
									'ACTUALIZACIÓN EXITOSA!!',
									'Click en OK para continuar!',
									'success'
								);
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
					  

				  }
			})
		
  		
	});
});

$(".sendLaw").on("submit", function(e) {
	e.preventDefault();
	var formData = new FormData(this);
	$.ajax({
		type:"POST",
		url:"enviarMensajeAbogado",
		data:formData,
		cache:false,
		dataType:"json",
		contentType:false,
		processData:false,
		beforeSend:function(){
            $('.loader').show();
        },
		success:function(response) {
			if(response.Status == 200){ 
				$(".detalleObjetos").empty();
				var url= 'abogados';
				$(".listaObjetos").load(url);
				$('.loader').hide();
				$(".modalRespuestaAbogado").modal('hide');//ocultamos el modal
				$('body').removeClass('modal-open');//eliminamos la clase del body para poder hacer scroll
				$('.modal-backdrop').remove();//e
				Swal.fire(
						  'MENSAJE ENVIADO!!',
						  'Click en OK para continuar!',
						  'success'
						);
			}
			else if(response.Status == 400){
				$('.loader').hide();
				$(".modalRespuestaAbogado").modal("hide");
				Swal.fire({
					  icon: 'info',
					  title: 'ATENCIÓN',
					  text: 'La solicitud ya fue atendida actualice la lista!!',
					  
					});
			}
			else {
				$('.loader').hide();
				$(".modalRespuestaAbogado").modal("hide");
				Swal.fire({
					  icon: 'error',
					  title: 'ATENCIÓN',
					  text: 'Ooops, algo salió mal, no se pudo enviar la información.!!',
					  
					});
			}
		}
	
	});
});

$(document).ready(function(){
	$(".textoRespuestaAbogado").val('Estimado(a) '+ $(".nombres").val() + ', Ud. acaba de ser admitido(a) para realizar consultas virtuales de expedientes.'); 
});
$( function() {
$("#confirma").change( function() {
    if ($(this).val() == "SI") {
    	$(".textoRespuestaAbogado").val('Estimado(a) '+ $(".nombres").val() + ', Ud. acaba de ser admitido(a) para realizar consultas virtuales de expedientes.'); 
    } else {
    	$(".textoRespuestaAbogado").val('');
    }
    
});
});

$(document).ready(function() {
	$(".nuevaClaveAbo").click(function() {
		
		Swal.fire({
			  title: 'Está seguro de generar nueva clave?',
			  text: "",
			  icon: 'question',
			  showCancelButton: true,
			  confirmButtonColor: '#3085d6',
			  cancelButtonColor: '#d33',
			  confirmButtonText: 'SI, GENERAR!'
			}).then((result) => {
				if (result.value) {
					
					$(function () {
					    var token = $("input[name='_csrf']").val();
					    var header = "X-CSRF-TOKEN";
					    $(document).ajaxSend(function(e, xhr, options) {
					        xhr.setRequestHeader(header, token);
					    });
					});
					
	                // Creamos un objeto con los datos a enviar
					var dni = $(".dni").val();
					var correo = $(".email").val();
					var nombres = $(".nombres").val();
		      		var json = {
		      			"dni" : dni,
		      			"email" : correo,
		      			"nombres" : nombres
		      		};
		 
			      	$.ajax({
			       		type : "POST",
			       		contentType : "application/json",
			       		url : "nuevaClaveAbo",
			       		data : JSON.stringify(json),
				        dataType : 'json',
				        cache : false,
				        beforeSend:function(){
							$(".modalLoader").modal("show");
			            },
				        success : function(response) {
				        	if(response.Status == 200){ 
				        		$(".detalleObjetos").empty();
				        		$(".modalLoader").modal("hide");
				        		$('body').removeClass('modal-open');//eliminamos la clase del body para poder hacer scroll
								$('.modal-backdrop').remove();//e
					        	Swal.fire(
									'NUEVA CLAVE ENVIADA!!',
									'Click en OK para continuar!',
									'success'
								);
							}
				        	else{
				        		$(".modalLoader").modal("hide");
				        		$('body').removeClass('modal-open');//eliminamos la clase del body para poder hacer scroll
								$('.modal-backdrop').remove();//e
				        		Swal.fire({
									icon: 'error',
									title: 'ATENCIÓN',
									text: 'Ooops, algo salió mal, no se pudo enviar el mensaje.!!',
									  
								});
				        	}
				        }
			      	});
					  

				  }
			})
		
  		
	});
});

$(".formFinalizar").on("submit", function(e) {
	e.preventDefault();
	var formData = new FormData(this);
	$.ajax({
		type:"POST",
		url:"enviarMensajeSolicitud",
		data:formData,
		cache:false,
		dataType:"json",
		contentType:false,
		processData:false,
		beforeSend:function(){
			$(".modalLoader").modal("show");
        },
		success:function(response) {
			if(response.Status == 200){ 
				$(".detalleObjetos").empty();
				var url= 'solicitudExpedientes';
				$(".listaObjetos").load(url);
				$(".modalLoader").modal("hide");
				Swal.fire(
						  'SOLICITUD PROCESADA CON ÉXITO!!',
						  'Click en OK para continuar!',
						  'success'
						);
			}
			else if(response.Status == 400){
				$(".modalLoader").modal("hide");
				Swal.fire({
					  icon: 'info',
					  title: 'ATENCIÓN',
					  text: 'Aún existen expedientes sin revisión!!',
					  
					});
			}
			else {
				$(".modalLoader").modal("hide");
				Swal.fire({
					  icon: 'error',
					  title: 'ATENCIÓN',
					  text: 'Ooops, algo salió mal, no se pudo enviar la información.!!',
					  
					});
			}
		}
	
	});
});

$(".btnEnviarCorreo").click(function() {
	if($('#cita').val() == 'SI' && ($('#agenda1').val() == '' || $('#agenda2').val() == '')){
		toastr.clear();
		toastr.options = {
			"closeButton":true,
			"progressBar": true,
			"positionClass":"toast-topcenter-right"
		};
		toastr.warning("Ingrese fechas de cita!!");
		return false;
	}
	
	var form = document.getElementById('sendAnswer');
	var formData = new FormData(form);
	$.ajax({
		type:"POST",
		url:"enviarMensaje",
		data:formData,
		cache:false,
		dataType:"json",
		contentType:false,
		processData:false,
		beforeSend:function(){
            $('.loader').show();
        },
		success:function(response) {
			if(response.Status == 200){ 
				$(".detalleObjetos").empty();
				var url=$('.formulario').val();
				$(".listaObjetos").load(url);
				$('.loader').hide();
				$(".modalRespuesta").modal('hide');//ocultamos el modal
				$('body').removeClass('modal-open');//eliminamos la clase del body para poder hacer scroll
				$('.modal-backdrop').remove();//e
				Swal.fire(
						  'MENSAJE ENVIADO!!',
						  'Click en OK para continuar!',
						  'success'
						);
				
			}
			else if(response.Status == 400){
				$('.loader').hide();
				$(".modalRespuesta").modal("hide");
				Swal.fire({
					  icon: 'info',
					  title: 'ATENCIÓN',
					  text: 'La solicitud ya fue atendida actualice la lista!!',
					  
					});
			}
			else if(response.Status == 300){
				$('.loader').hide();
				$(".modalRespuesta").modal("hide");
				Swal.fire({
					  icon: 'info',
					  title: 'ATENCIÓN',
					  text: 'Sin conexion al servidor FTP!!',
					  
					});
			}
			else if(response.Status == 350){
				$('.loader').hide();
				$(".modalRespuesta").modal("hide");
				Swal.fire({
					  icon: 'info',
					  title: 'ATENCIÓN',
					  text: 'Error en el servicio de envío de correo electrónico!!',
					  
					});
			}
			else if(response.Status == 600){
				$('.loader').hide();
				$(".modalRespuesta").modal("hide");
				$('.modalSesion').modal('show');
			}
			else {
				$('.loader').hide();
				$(".modalRespuesta").modal("hide");
				Swal.fire({
					  icon: 'error',
					  title: 'ATENCIÓN',
					  text: 'Algo salió mal, no se pudo enviar la información.!!',
					  
					});
			}
		}
	
	});
});
function archivoAbogado(){	
	var url='verAdjuntoAbogado';
		url = url + '/' + $('.dni').val();
	window.open(url,'nuevaVentana','width=600, height=800, toolbar=0, location=0');
};
function ventanaNueva(){	
	var url='verAdjunto';
		url = url + '/' + $('.solicitud').val();
	window.open(url,'nuevaVentana','width=600, height=630, toolbar=0, location=0');
};
function ventanaNueva2(){	
	var url='verArancel';
		url = url + '/' + $('.solicitud').val();
	window.open(url,'nuevaVentana','width=600, height=630');
};
function ventanaNueva3(){	
	var url='verRespuesta';
		url = url + '/' + $('.solicitud').val();
	window.open(url,'nuevaVentana','width=600, height=630');
};
function verAgenda(){	
	var url='verAgenda';
	window.open(url,'nuevaVentana','width=600, height=630');
};
$(document).on('change','input[type="file"]',function(){
	// this.files[0].size recupera el tamaño del archivo
	// alert(this.files[0].size);
	
		var fileName = this.files[0].name;
		var fileSize = this.files[0].size;

		if(fileSize > 5000000){
			toastr.clear();
				toastr.options = {
					"closeButton":true,
					"progressBar": true,
					"positionClass":"toast-topcenter-right"
				};
				toastr.error("El archivo no debe superar los 5 MB!");
			this.value = '';
			this.files[0].name = '';
		}else{
			// recuperamos la extensión del archivo
			var ext = fileName.split('.').pop();
			
			// Convertimos en minúscula porque 
			// la extensión del archivo puede estar en mayúscula
			ext = ext.toLowerCase();
	    
			// console.log(ext);
			switch (ext) {
				case 'pdf': break;
				default:
					toastr.clear();
					toastr.options = {
						"closeButton":true,
						"progressBar": true,
						"positionClass":"toast-topcenter-right"
					};
					toastr.error("Archivo con formato no aceptado!");
					this.value = ''; // reset del valor
					this.files[0].name = '';
			}
		}
	});
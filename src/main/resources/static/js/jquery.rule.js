/**
 * 
 */

$(document).ready(function() {
	getExpedientes();
});


function getExpedientes(){
	var id = $(".id").val();
	var json = {
		"id" : id
	};
	$.ajax({
   		type : "POST",
   		contentType : "application/json",
   		url : "obtExpedientesSentenciados",
   		data : JSON.stringify(json),
        dataType : 'json',
        cache : false,
        timeout : 600000,
        success : function(data) {
         	var html = '';
         	var len = data.length;
         	html += '';
         	if(len > 0){
         		html += '<option value="0">SELECCIONE ...</option>';
         		for (var i = 0; i < len; i++) {
	         		html += '<option value="' + data[i].id_correlativo + '">' + data[i].numero + ' -- ' + data[i].instancia + '</option>';
		        }
         	}
         	else{
         		html += '<option value="99999">SIN EXPEDIENTES</option>';
         	}
         		
	        html += '</option>&nbsp;&nbsp;&nbsp;';
	        $('.listaExpedientesSentenciados').html(html);
        },
    	error : function(e) {
    	 	Swal.fire({
			  	icon: 'error',
			  	title: 'ATENCIÓN',
			  	text: 'Ooops, algo salió mal, no se pudo cargar los expedientes.!!',
			});
     	}
  	});
};


$('.listaExpedientesSentenciados').change(function(){
	var id = $(this).val();
	obtDatosExpedienteSentenciado(id);
});

function obtDatosExpedienteSentenciado(id){
	var json = {
		"id" : id
	};
	$.ajax({
   		type : "POST",
   		contentType : "application/json",
   		url : "obtDatosExpedienteSentenciado",
   		data : JSON.stringify(json),
        dataType : 'json',
        cache : false,
        timeout : 600000,
        success : function(data) {
        	if(id == 0){
        		$(".divReglaExpediente").hide();
        		$(".txtEstado").html('');
        	}
        	else{
     			$('.txtIdExpSen').val(data.id_expediente);
     			$('.txtExpSen').val(data.expediente);
     			$('#txtIdExpSede').val(data.id_sede);
     			$('.txtSedeSen').val(data.sede);
     			$('#txtIdExpIns').val(data.id_instancia);
     			$('.txtInsSen').val(data.instancia);
     			$('#txtIdSec').val(data.id_secretario);
     			$('.txtDetReglas').val(data.medidas);
     			$('.txtRepCivil').val(data.reparacion);
     			$('.txtAcumulado').val(data.acumulado);
     			if(data.estado == 'T'){
     				$('.txtEstado').html('TRAMITE');
     				$('.btnNuevaCita').show();
     			}
     			else if(data.estado == 'V'){
     				$('.txtEstado').html('VENCIDO');
     				$('.btnNuevaCita').hide();
     			}
     			else if(data.estado == 'B'){
     				$('.txtEstado').html('BIOMETRICO');
     				$('.btnNuevaCita').hide();
     			}
     			else if(data.estado == 'X'){
     				$('.txtEstado').html('NO CORRESPONDE');
     				$('.btnNuevaCita').hide();
     			}
     			$('.posSecretario').val(data.secretario);
     			$('.posEstado').val(data.estado);
             	$('.divReglaExpediente').show();
             	verReglasAnteriores(id);
        	}
        },
    	error : function(e) {
    	 	Swal.fire({
			  	icon: 'error',
			  	title: 'ATENCIÓN',
			  	text: 'Ooops, algo salió mal, no se pudo obtener la informacion.!!',
			});
     	}
  	});
};


$('.btnGuardarRegla').click(function(){
	if($(".txtObsRegla").val() == ''){
		toastr.clear();
		toastr.options = {
			"closeButton":true,
			"progressBar": true,
			"positionClass":"toast-topcenter-right"
		};
		toastr.warning("Debe ingresar observación!!");
		return false;
	}
	
	Swal.fire({
		title: 'Está seguro de guardar los datos?',
		text: "Este paso no podrá ser revertido.",
		icon: 'question',
		showCancelButton: true,
		confirmButtonColor: '#3085d6',
		cancelButtonColor: '#d33',
		confirmButtonText: 'SI, Guardar!'
	}).then((result) => {
		if (result.value) {
			var reparacion = $('#txtReparacion').val();
			var ubicacion = $('#ubicacion').val();
			if(reparacion == '')
      			reparacion = 0;
			else
				reparacion = reparacion;
			if(ubicacion == '')
      			ubicacion = '0';
			
			var form = document.getElementById('formRegla');
			var formData = new FormData(form);
			formData.append('repara', reparacion );
			formData.append('ubicacion', ubicacion);

	      	$.ajax({
	       		type : "POST",
	       		url : "guardarReglaSentenciado",
	       		data:formData,
	    		cache:false,
	    		dataType:"json",
	    		contentType:false,
	    		processData:false,
		        success : function(response) {
		        	if(response.Status == 200){ 

		        		$('.modalGuardarRegla').modal('hide');
						obtDatosExpedienteSentenciado($('.txtIdExpSen').val());
			        	Swal.fire(
							'DATOS GUARDADOS CON ÉXITO!!',
							'Click en OK para continuar!',
							'success'
						);
					}
		        	else{
		        		Swal.fire({
							icon: 'error',
							title: 'ATENCIÓN',
							text: 'Algo salió mal, no se pudo guardar la información.!!',
							  
						});
		        	}
		        }
	      	});
		}
	})
});


$('.btnEditarExpSen').click(function(){
	$('.posSede').val($('.txtSedeSen').val());
	$('.posInstancia').val($('.txtInsSen').val());
	$('.posExpediente').val($('.txtExpSen').val());
	$('.posMedidas').val($('.txtDetReglas').val());
	$('.posReparacion').val($('.txtRepCivil').val());
	loadSedes(); loadSecretarios();
	$('.modalModificarExpSen').modal('show');
	
});

function loadSedesNuevo(){
	var json = {
	   	"id" : 1
	};
	$.ajax({
	    type : "POST",
   		contentType : "application/json",
   		url : "obtSedes",
   		data : JSON.stringify(json),
        dataType : 'json',
        cache : false,
        timeout : 600000,
        success : function(data) {
         	var html = '';
         	var len = data.length;
         	html += '<option value="0">SELECCIONE ... </option>';
         	for (var i = 0; i < len; i++) {
         		html += '<option value="' + data[i].c_sede + '">' + data[i].s_sede + '</option>';
	        }
	        html += '</option>';
	        $('#sede').html(html);
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

function loadSedes(){
	var json = {
	   	"id" : 1
	};
	$.ajax({
	    type : "POST",
   		contentType : "application/json",
   		url : "obtSedes",
   		data : JSON.stringify(json),
        dataType : 'json',
        cache : false,
        timeout : 600000,
        success : function(data) {
         	var html = '';
         	var len = data.length;
         	var idSede = $('#txtIdExpSede').val();
         	var select = "";
         	html += '<option value="0">SELECCIONE ... </option>';
         	for (var i = 0; i < len; i++) {
         		if(idSede == data[i].c_sede)
         			select = 'selected';
         		else
         			select = '';
         		html += '<option value="' + data[i].c_sede + '" ' + select + '>' + data[i].s_sede + '</option>';
	        }
	        html += '</option>';
	        $('.posSede').html(html);
	        loadInstancias(idSede);
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

function loadSecretarios(){
	var json = {
	   	"id" : 1
	};
	$.ajax({
	    type : "POST",
   		contentType : "application/json",
   		url : "obtSecretariosNCPP",
   		data : JSON.stringify(json),
        dataType : 'json',
        cache : false,
        timeout : 600000,
        success : function(data) {
         	var html = '';
         	var len = data.length;
         	var idSec = $('#txtIdSec').val();
         	var select = "";
         	for (var i = 0; i < len; i++) {
         		if(idSec == data[i].c_usuario)
         			select = 'selected';
         		else
         			select = '';
         		html += '<option value="' + data[i].c_usuario + '" ' + select + '>' + data[i].nombresCompletos + '</option>';
	        }
	        html += '</option>';
	        $('.posSecretario').html(html);
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

$(document).ready(function() {
	$(".sede").change(function() {
  		var idSede = $(this).find(":selected").val();
  		var json = {
   			"id" : idSede
      	};
 
  		$.ajax({
	       	type : "POST",
       		contentType : "application/json",
       		url : "obtInstanciasNCPP",
       		data : JSON.stringify(json),
	        dataType : 'json',
	        cache : false,
	        timeout : 600000,
	        success : function(data) {
	         	var html = '';
	         	var len = data.length;
	         	html += '';
	         	for (var i = 0; i < len; i++) {
	         	html += '<option value="' + data[i].c_instancia + '">'
	         	+ data[i].x_nom_instancia
	           	+ '</option>';
		        }
		        html += '</option>';
		        $('.instancia').html(html);
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
	
	$(".posSede").change(function() {
  		var idSede = $(this).find(":selected").val();
  		var json = {
   			"id" : idSede
      	};
 
  		$.ajax({
	       	type : "POST",
       		contentType : "application/json",
       		url : "obtInstanciasNCPP",
       		data : JSON.stringify(json),
	        dataType : 'json',
	        cache : false,
	        timeout : 600000,
	        success : function(data) {
	         	var html = '';
	         	var len = data.length;
	         	html += '';
	         	for (var i = 0; i < len; i++) {
	         	html += '<option value="' + data[i].c_instancia + '">'
	         	+ data[i].x_nom_instancia
	           	+ '</option>';
		        }
		        html += '</option>';
		        $('.posInstancia').html(html);
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
});

function loadInstancias(id) {
	var json = {
		"id" : id
  	};

	$.ajax({
       	type : "POST",
   		contentType : "application/json",
   		url : "obtInstanciasNCPP",
   		data : JSON.stringify(json),
        dataType : 'json',
        cache : false,
        timeout : 600000,
        success : function(data) {
         	var html = '';
         	var len = data.length;
         	var idIns = $('#txtIdExpIns').val();
         	var select = "";
         	html += '';
         	for (var i = 0; i < len; i++) {
         		if(idIns == data[i].c_instancia)
         			select = 'selected';
         		else
         			select = '';
         		html += '<option value="' + data[i].c_instancia + '" ' + select + '>' + data[i].x_nom_instancia + '</option>';
	        }
	        html += '</option>';
	        $('.posInstancia').html(html);
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

$(".btnGuardarNuevoExpSen").click(function() {
	if($('.expediente').val() == '' && ($('.sede').val() == '0' || $('.instancia').val() == '')){
		toastr.clear();
		toastr.options = {
			"closeButton":true,
			"progressBar": true,
			"positionClass":"toast-topcenter-right"
		};
		toastr.warning("Algun campo se encuentra vacio, complete!!");
		return false;
	}
	Swal.fire({
		  title: 'Está seguro de agregar el nuevo expediente?',
		  text: "Este paso no podra ser revertido.",
		  icon: 'question',
		  showCancelButton: true,
		  confirmButtonColor: '#3085d6',
		  cancelButtonColor: '#d33',
		  confirmButtonText: 'Si, Agregar!'
		}).then((result) => {
			if (result.value) {
			var form = document.getElementById('formAgregarExpSen');
			var formData = new FormData(form);
			$.ajax({
				type:"POST",
				url:"guardarNuevoExpSen",
				data:formData,
				cache:false,
				dataType:"json",
				contentType:false,
				processData:false,
				success:function(response) {
					if(response.Status == 200){ 
						$(".divReglaExpediente").hide();
						$('.txtEstado').html('');
						$(".modalAgregarExp").modal('hide');
						$('body').removeClass('modal-open');
						$('.modal-backdrop').remove();
						getExpedientes();
						Swal.fire(
							'EXPEDIENTE GUARDADO!!',
							'Click en OK para continuar!',
							'success'
						);
						
					}
					else {
						Swal.fire({
							icon: 'error',
							title: 'ATENCIÓN',
							text: 'Algo salió mal, no se pudo guardar la información.!!',
							  
						});
					}
				}
			
			});
		}
	});
});

$(".btnGuardarEdicionExp").click(function() {
	if($('.posExpediente').val() == '' || $('.posSede').val() == '0' || $('.instancia').val() == ''){
		toastr.clear();
		toastr.options = {
			"closeButton":true,
			"progressBar": true,
			"positionClass":"toast-topcenter-right"
		};
		toastr.warning("Algun campo se encuentra vacio, complete!!");
		return false;
	}
	Swal.fire({
		  title: 'Está seguro de modificar el registro?',
		  text: "",
		  icon: 'question',
		  showCancelButton: true,
		  confirmButtonColor: '#3085d6',
		  cancelButtonColor: '#d33',
		  confirmButtonText: 'Si, Agregar!'
		}).then((result) => {
			if (result.value) {
				var form = document.getElementById('formModificarExp');
				var formData = new FormData(form);
				$.ajax({
					type:"POST",
					url:"modificarExpSen",
					data:formData,
					cache:false,
					dataType:"json",
					contentType:false,
					processData:false,
					success:function(response) {
						if(response.Status == 200){ 
							$(".divReglaExpediente").hide();
							$('.txtEstado').html('');
							$(".modalModificarExpSen").modal('hide');//ocultamos el modal
							$('body').removeClass('modal-open');//eliminamos la clase del body para poder hacer scroll
							$('.modal-backdrop').remove();//e
							getExpedientes();
							Swal.fire(
								'EXPEDIENTE MODIFICADO CON EXITO!!',
								'Click en OK para continuar!',
								'success'
							);
							
						}
						else if(response.Status == 300){
							Swal.fire({
								icon: 'info',
								title: 'ATENCIÓN',
								text: 'Aun existen registros sin obervacion, vertifique.!!',
								  
							});
						}
						else {
							Swal.fire({
								icon: 'error',
								title: 'ATENCIÓN',
								text: 'Algo salió mal, no se pudo guardar la información.!!',
								  
							});
						}
					}
				
				});
			}
	});
});

function verReglasAnteriores(id){
	var json = {
		"id" : id
	};
	$.ajax({
   		type : "POST",
   		contentType : "application/json",
   		url : "obtReglasAnteriores",
   		data : JSON.stringify(json),
        dataType : 'json',
        cache : false,
        timeout : 600000,
        success : function(data) {
         	var html = '';
         	var len = data.length;
         	html += '';
         	if(len > 0){
         		html = '<tr style="font-weight: bold"><th>FECHAS</th><th>OBSERVACIONES</th><th>REP</th><th>IMG</th><th>OPCION</th></tr>'
         		for (var i = 0; i < len; i++) {
         			var estado = data[i].estado;
         			var archivo = data[i].archivo;
         			if(estado == 'P')
         				estado = '<button class="btn btn-light btn-sm" type="button" onclick="addObservacion(' + data[i].id+ ')"><i class="fas fa-phone"></i></button>';
         				//estado = '<a style="cursor: pointer" type="button" onclick="addObservacion(' + data[i].id+ ')"><img src="img/telInc.png" width="25"></a>';
         			else if(estado == 'E')
         				//estado = '<img src="img/telVisto.png" width="25">';
         				estado = '<button class="btn btn-success btn-sm" type="button"><i class="fas fa-phone"></i></button>';
         			
         			if(archivo != '')
         				archivo = '<button class="btn btn-info btn-sm" type="button" onclick="verArchivoReglas(' + data[i].id+ ')"><i class="fas fa-map-marker-alt"></i></button>';
         			else
         				archivo = '';
         			html += '<tr><td><strong>CITA </strong><i>(' + data[i].usuario+ ')</i>:<br><i>' + data[i].registro + '</i><br><strong>ENTREVISTA: </strong><br><i>' + data[i].regla + '</i></td><td>' + data[i].observaciones + '</td><td style="horizontal-align:baseline">' + data[i].reparacion + '</td><td style="horizontal-align:baseline">' + archivo + '</td><td>'+estado+'</td></tr>';
         		}
         	}
         	else{
         		html = '';
         		html = '<span style="font-weight: bold">NO EXISTEN REGLAS ANTERIORES</span>'
         	}
         	$('#tablaReglas').html(html);
        },
    	error : function(e) {
    	 	Swal.fire({
			  	icon: 'error',
			  	title: 'ATENCIÓN',
			  	text: 'Ooops, algo salió mal, no se pudo obtener la informacion.!!',
			});
     	}
  	});
};

$('.btnGuardarCita').click(function(){
	if($("#txtNuevaCita").val() == ''){
		toastr.clear();
		toastr.options = {
			"closeButton":true,
			"progressBar": true,
			"positionClass":"toast-topcenter-right"
		};
		toastr.warning("Debe ingresar fecha y hora correcta!!");
		return false;
	}
	
	Swal.fire({
		title: 'Está seguro de guardar los datos?',
		text: "Este paso no podrá ser revertido.",
		icon: 'question',
		showCancelButton: true,
		confirmButtonColor: '#3085d6',
		cancelButtonColor: '#d33',
		confirmButtonText: 'SI, Guardar!'
	}).then((result) => {
		if (result.value) {
			var idExpediente = $(".txtIdExpSen").val();
			var cita = $("#txtNuevaCita").val();
      		var json = {
       			"id" : idExpediente,
       			"cita" : cita
      		};
	      	$.ajax({
	       		type : "POST",
	       		url : "guardarNuevaCita",
	       		data : json,
		        dataType : 'json',
		        cache : false,
		        success : function(response) {
		        	if(response.Status == 200){ 
		        		$("#modalNuevaCita").modal('hide');
		        		verReglasAnteriores(idExpediente);
			        	Swal.fire(
							'CITA GUARDADA CON ÉXITO!!',
							'Click en OK para continuar!',
							'success'
						);
					}
		        	else{
		        		Swal.fire({
							icon: 'error',
							title: 'ATENCIÓN',
							text: 'Algo salió mal, no se pudo guardar la información.!!',
							  
						});
		        	}
		        }
	      	});
		}
	})
});


function addObservacion(id){
	$("#modalGuardarRegla input").val("");
    $("#modalGuardarRegla textarea").val("");
	$('#modalGuardarRegla').modal('show');
	$('#idRegla').val(id);
};

function verArchivoReglas(id){	
	var url='verArchivoReglas';
		url = url + '/' + id;
	window.open(url,'nuevaVentana','width=600, height=800, toolbar=0, location=0');
};
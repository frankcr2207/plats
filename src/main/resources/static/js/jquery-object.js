/**
 * 
 */
	$(document).ready(function() {
	    $('.listaSecretarios').select2();
	});
	$(document).ready(function() {
        $('.sumilla').richText();
	});
	
	$('.btnModalResolver').click(function(){
		var formulario = $(".tipoFormulario").val();
	    if(formulario == 'DOCUMENTAL' || formulario == 'EXPEDICION' || formulario == 'LECTURA'){
	    	$(".txtResolucionResolver").hide();	$(".lblResolucionResolver").hide();
	    	$(".txtFechaResolver").hide();		$(".lblFechaResolver").hide();
	    	$(".aprobar").hide();				$(".lblAprobar").hide();
	    	$(".observacionResolver").hide();	$(".lblObsResolver").hide();
	    }
	    else{
	    	$(".txtResolucionResolver").show();	$(".lblResolucionResolver").show();
	    	$(".txtFechaResolver").show();		$(".lblFechaResolver").show();
	    	$(".aprobar").show();				$(".lblAprobar").show();
	    	$(".observacionResolver").show();	$(".lblObsResolver").show();
	    }
		$('.modalResolver').modal('show');
	});
	

	$('.btnDerivar').click(function(){
  		var c_instancia = $('.c_instancia').val();
		var json = {
   			"id" : c_instancia
  		};
      	$.ajax({
       		type : "POST",
       		contentType : "application/json",
       		url : "obtSecretarios",
       		data : JSON.stringify(json),
	        dataType : 'json',
	        cache : false,
	        timeout : 600000,
	        success : function(data) {
	         	var html = '';
	         	var len = data.length;
	         	html += '';
	         	if(len > 0){
	         		for (var i = 0; i < len; i++) {
		         		html += '<option value="' + data[i].c_usuario + '">' + data[i].nombresCompletos + '</option>';
			        }
	         	}
	         	else{
	         		html += '<option value="99999">SIN SECRETARIOS ASIGNADOS</option>';
	         	}
	         		
		        html += '</option>';
		        $('.txtSolicitanteDerivar').val($('#nombres').val());
		        $('.txtInstanciaDerivar').val($('#instancia').val());
		        $('.txtExpedienteDerivar').val($('#expediente').val());
		        $('.listaSecretarios').html(html);
	        },
        	error : function(e) {
        	 	Swal.fire({
				  	icon: 'error',
				  	title: 'ATENCIÓN',
				  	text: 'Ooops, algo salió mal, no se pudo cargar secretarios.!!',
				});
	     	}
      	});
		
		$('.modalDerivar').modal('show');
	});

	function imprSelec(nombre) {
		  var ficha = document.getElementById(nombre);
		  var ventimp = window.open(' ', 'popimpr');
		  ventimp.document.write( ficha.innerHTML );
		  ventimp.document.close();
		  ventimp.print( );
		  ventimp.close();
	}

	$('.btnModalResolver').click(function(){
		$('.txtSolicitanteResolver').val($('#nombres').val());
		$('.txtInstanciaResolver').val($('#instancia').val());
		$('.txtExpedienteResolver').val($('#expediente').val());
		$('.modalResolver').modal('show');
	});
	
	$('.btnModalDevolver').click(function(){
		$('.txtSolicitanteDevolver').val($('#nombres').val());
		$('.txtInstanciaDevolver').val($('#instancia').val());
		$('.txtExpedienteDevolver').val($('#expediente').val());
		$('.txtMotivoDevolucion').val('');
		$('.modalDevolver').modal('show');
	});
	
	$( function() {
	    $("#cita").change( function() {
	        if ($(this).val() == "NO") {
				$("#lblDia1").hide();
				$("#lblDia2").hide();
				$("#lblEspacio").hide();
	        	$("#agenda1").hide();
	        	$("#agenda2").hide();
	        	$("#espacio").hide();
	        	$("#agenda1").val("");
	            $("#agenda2").val("");
	        } else {
	        	$("#lblDia1").show();
				$("#lblDia2").show();
				$("#lblEspacio").show();
	        	$("#agenda1").val("");
	            $("#agenda2").val("");
	        	$("#agenda1").show();
	        	$("#agenda2").show();
	        	$("#espacio").show();	
	        }
	        
	    });
	});

	$('.btnGuardarDerivacion').click(function(){
		if($(".listaSecretarios").val() == "99999"){
			Swal.fire({
				icon: 'info',
				title: 'Imposible Derivar',
				text: 'No se tiene secretarios para derivar, comuníquese con el Informático de Sede!!',
				  
			});
			return false;
		}
		
		Swal.fire({
			title: 'Está seguro de derivar la solicitud?',
			text: "",
			icon: 'question',
			showCancelButton: true,
			confirmButtonColor: '#3085d6',
			cancelButtonColor: '#d33',
			confirmButtonText: 'SI, Derivar!'
		}).then((result) => {
			if (result.value) {
				
				$(function () {
				    var token = $("input[name='_csrf']").val();
				    var header = "X-CSRF-TOKEN";
				    $(document).ajaxSend(function(e, xhr, options) {
				        xhr.setRequestHeader(header, token);
				    });
				});

				var id = $(".id").val();
	      		var secretario = $(".listaSecretarios").val();
	      		var json = {
	      			"id" : id,
	       			"secretario" : secretario
	      		};
		      	$.ajax({
		       		type : "POST",
		       		contentType : "application/json",
		       		url : "derivarSolicitud",
		       		data : JSON.stringify(json),
			        dataType : 'json',
			        cache : false,
			        success : function(response) {
			        	if(response.Status == 200){ 
			        		$(".detalleObjetos").empty();
			        		$(".modalDerivar").modal("hide");
			        		$('body').removeClass('modal-open');
							$('.modal-backdrop').remove();
							$(".listaObjetos").load($('.formulario').val());
				        	Swal.fire(
								'SOLICITUD DERIVADA CON ÉXITO!!',
								'Click en OK para continuar!',
								'success'
							);
						}
			        	else{
			        		Swal.fire({
								icon: 'error',
								title: 'ATENCIÓN',
								text: 'Ooops, algo salió mal, no se pudo derivar la solicitud.!!',
								  
							});
			        	}
			        }
		      	});
		      	
			}
		})
	});
	
	$('.btnGuardarResolucion').click(function(){
		var formulario = $(".tipoFormulario").val();
		var numero = $(".txtResolucionResolver").val();
  		var fecha = $(".txtFechaResolver").val();
  		var fallo = $(".aprobar").val();
  		
  		if(formulario == 'COPIAS' || formulario == 'ENDOSE'){
  			if(numero == ''){
  	  			toastr.clear();
  				toastr.options = {
  					"closeButton":true,
  					"progressBar": true,
  					"positionClass":"toast-topcenter-right"
  				};
  				toastr.warning("Ingrese número de resolución!");
  				$(".txtResolucionResolver").focus();
  	  			return false;	
  	  		}
  	  		
  			if(fecha== ''){
  				toastr.clear();
  				toastr.options = {
  					"closeButton":true,
  					"progressBar": true,
  					"positionClass":"toast-topcenter-right"
  				};
  				toastr.warning("Ingrese fecha!");
  				$(".txtFechaResolver").focus();
  	  			return false;
  	  		}
  			
  			var sum = $(".sumilla").val();
  			if(sum.length == 15){
  				toastr.clear();
  				toastr.options = {
  					"closeButton":true,
  					"progressBar": true,
  					"positionClass":"toast-topcenter-right"
  				};
  				toastr.warning("Ingrese breve sumilla!");
  				$(".sumilla").focus();
  	  			return false;
  	  		}
  		}
  		else{
  			numero = '0';
  	  		fecha = '';
  	  		fallo = '';
	  	  	var sum = $(".sumilla").val();
			if(sum.length == 15){
  				toastr.clear();
  				toastr.options = {
  					"closeButton":true,
  					"progressBar": true,
  					"positionClass":"toast-topcenter-right"
  				};
  				toastr.warning("Ingrese breve sumilla!");
  				$(".sumilla").focus();
  	  			return false;
  	  		}
  		}

		Swal.fire({
			title: 'Está seguro de resolver la solicitud?',
			text: "",
			icon: 'question',
			showCancelButton: true,
			confirmButtonColor: '#3085d6',
			cancelButtonColor: '#d33',
			confirmButtonText: 'SI, Resolver!'
		}).then((result) => {
			if (result.value) {
				
				$(function () {
				    var token = $("input[name='_csrf']").val();
				    var header = "X-CSRF-TOKEN";
				    $(document).ajaxSend(function(e, xhr, options) {
				        xhr.setRequestHeader(header, token);
				    });
				});

				var id = $(".id").val();
	      		var sumilla = $(".sumilla").val();
	      		var observacion = $(".observacionResolver").val();
	      			
	      		var json = {
	      			"id" : id,
	      			"numero" : numero,
	      			"fecha" : fecha,
	      			"fallo" : fallo,
	       			"sumilla" : sumilla,
	       			"observacion" : observacion
	      		};
		      	$.ajax({
		       		type : "POST",
		       		contentType : "application/json",
		       		url : "resolverSolicitud",
		       		data : JSON.stringify(json),
			        dataType : 'json',
			        cache : false,
			        success : function(response) {
			        	if(response.Status == 200){ 
			        		$(".detalleObjetos").empty();
			        		$(".modalResolver").modal("hide");
			        		$('body').removeClass('modal-open');
							$('.modal-backdrop').remove();
							$(".listaObjetos").load('DERIVADOS');
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
								text: 'Ooops, algo salió mal, no se pudo guardar la información.!!',
								  
							});
			        	}
			        }
		      	});
		      	
			}
		})
	});
	
	
	$('.btnGuardarDevolucion').click(function(){
		var motivo = $(".txtMotivoDevolucion").val();
  		
  		if(motivo == ''){
  			toastr.clear();
			toastr.options = {
				"closeButton":true,
				"progressBar": true,
				"positionClass":"toast-topcenter-right"
			};
			toastr.warning("Ingrese motivo de devolución!");
			$(".txtMotivoDevolucion").focus();
  			return false;	
  		}
		
		Swal.fire({
			title: 'Está seguro de devolver la solicitud?',
			text: "",
			icon: 'question',
			showCancelButton: true,
			confirmButtonColor: '#3085d6',
			cancelButtonColor: '#d33',
			confirmButtonText: 'SI, Devolver!'
		}).then((result) => {
			if (result.value) {
				
				$(function () {
				    var token = $("input[name='_csrf']").val();
				    var header = "X-CSRF-TOKEN";
				    $(document).ajaxSend(function(e, xhr, options) {
				        xhr.setRequestHeader(header, token);
				    });
				});

				var id = $(".id").val();
	      		var json = {
	      			"id" : id,
	      			"motivo" : motivo
	      		};
		      	$.ajax({
		       		type : "POST",
		       		contentType : "application/json",
		       		url : "devolverSolicitud",
		       		data : JSON.stringify(json),
			        dataType : 'json',
			        cache : false,
			        success : function(response) {
			        	if(response.Status == 200){ 
			        		$(".detalleObjetos").empty();
			        		$(".modalDevolver").modal("hide");
			        		$('body').removeClass('modal-open');
							$('.modal-backdrop').remove();//e
							$(".listaObjetos").load('DERIVADOS');
				        	Swal.fire(
								'SOLICITUD DEVUELTA CON ÉXITO!!',
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
	
	$('.btnVerMotivoDevolucion').click(function(){
  		var id = $('.id').val();
		var json = {
   			"id" : id
  		};
      	$.ajax({
       		type : "POST",
       		contentType : "application/json",
       		url : "verMotivoDevolucion",
       		data : JSON.stringify(json),
	        dataType : 'json',
	        cache : false,
	        timeout : 600000,
	        success : function(data) {
		        $('.txtSecretarioDevolucion').val(data[0].secretario);
		        $('.txtFechaDevolucion').val(data[0].fecha);
		        $('.taMotivoDevolucion').val(data[0].observacion);
	        },
        	error : function(e) {
        	 	Swal.fire({
				  	icon: 'error',
				  	title: 'ATENCIÓN',
				  	text: 'Ooops, algo salió mal, no se pudo cargar secretarios.!!',
				});
	     	}
      	});
		
		$('.modalVerDevolucion').modal('show');
	});

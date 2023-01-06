/**
 * 
 */
	/*$(document).ready(function() {
		//if($('.sesionRole').val() == '5')
			//$('#modalMensajeAsistente').modal('show');
	});
	
	$('.carousel').carousel({
		  interval: 10000
		});
*/

		
function getDev(){	
	$.ajax({
		url: "DEVOLUCION",
        type: 'GET',
        async : false,
        success: function (result) {
        	mostrarDivs(); $(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'Ooops, no se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};
function getExp(){
	$.ajax({
		url: "EXPEDICION",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'Ooops, no se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};
function getCop(){
	$.ajax({
		url: "COPIAS",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
	        $(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'Ooops, no se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};
function getLec(){
	$.ajax({
		url: "LECTURA",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'Ooops, no se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};
function getEnd(){
	$.ajax({
		url: "ENDOSE",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'Ooops, no se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getDoc(){
	$.ajax({
		url: "DOCUMENTAL",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'Ooops, no se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getMesaPartes(){
	$.ajax({
		url: "MESAPARTES",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'Ooops, no se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getMesaUrgentes(){
	$.ajax({
		url: "URGENTES",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'Ooops, no se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getCasillas(){
	$.ajax({
		url: "CASILLAS",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'Ooops, no se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getAccesoInformacion(){
	$.ajax({
		url: "ACCESOINFORMACION",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'No se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getRectificacion(){
	$.ajax({
		url: "RECTIFICACION",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'No se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getCasillaElectronica(){
	$.ajax({
		url: "getCasillasElectronicas",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'No se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getAuxilioJudicial(){
	$.ajax({
		url: "getAuxiliosJudiciales",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'No se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getSalvaguardia(){
	$.ajax({
		url: "getSalvaguardias",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'No se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getInpeSolicitud(){
	$.ajax({
		url: "getInpeSolicitudes",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'No se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getMee(){	
	$.ajax({
		url: "AUDIENCIAS",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'Ooops, no se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getPublicacionAudiencia(){
	$(".divReporte").empty();
	$(".divReporte").show();
	$("#divOperaciones").empty();
	$("#divOperaciones").hide();
	$(".listaObjetos").hide();
	$(".detalleObjetos").hide();
	var url = 'getPublicacionAudiencia';
	$(".divReporte").load(url);
};

function getServicio(){
	$.ajax({
		url: "SERVICIO",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'Ooops, no se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getAlimentos(){
	$.ajax({
		url: "ALIMENTOS",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'Ooops, no se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getViolenciaFamiliar(){
	$.ajax({
		url: "VIOLENCIAFAMILIAR",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'Ooops, no se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getSunarp(){
	$.ajax({
		url: "SUNARP",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'Ooops, no se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getBanco(){
	$.ajax({
		url: "BANCO",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'Ooops, no se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getMultasCoor(){
	$.ajax({
		url: "multasCoor",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'Ooops, no se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getNuevoProcesado(){
	$.ajax({
		url: "nuevoProcesado",
        type: 'GET',
        async : false,
        success: function (result) {
        	$("#divOperaciones").empty();
        	$("#divOperaciones").show();
        	$("#divDetalleReglas").empty();
            $("#divDetalleReglas").hide();
        	$("#divReglas").hide();
        	$(".divReporte").hide();
        	$(".listaObjetos").hide();
        	$(".detalleObjetos").hide();
        	$("#divOperaciones").html(result);
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'No se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getMultas(){
	$.ajax({
		url: "multas",
        type: 'GET',
        async : false,
        success: function (result) {
        	$("#divOperaciones").empty();
        	$("#divOperaciones").show();
        	$("#divDetalleReglas").empty();
            $("#divDetalleReglas").hide();
        	$("#divReglas").hide();
        	$(".divReporte").hide();
        	$(".listaObjetos").hide();
        	$(".detalleObjetos").hide();
        	$("#divOperaciones").html(result);
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'No se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getBan(){	
	$.ajax({
		url: "DERIVADOS",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'Ooops, no se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getMis(){	
	$.ajax({
		url: "PENDIENTES",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'Ooops, no se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getReporteServicio(){
	$(".divReporte").empty();
	$(".divReporte").show();
	$("#divOperaciones").empty();
	$("#divOperaciones").hide();
	$(".listaObjetos").hide();
	$(".detalleObjetos").hide();
	var url = 'reporteServicio';
	$(".divReporte").load(url);
};

function getConteoActasSij(){
	$(".divReporte").empty();
	$(".divReporte").show();
	$("#divOperaciones").empty();
	$("#divOperaciones").hide();
	$(".listaObjetos").hide();
	$(".detalleObjetos").hide();
	var url = 'getFormConteoActasSij';
	$(".divReporte").load(url);
};

function getRep(){
	$(".listaObjetos").hide();
	$(".detalleObjetos").hide();
	$("#divOperaciones").empty();
	$("#divOperaciones").hide();
	$(".divReporte").show();
	var url = 'reportePersonalCDM';
	$(".divReportePersonal").load(url);
};
function getRepGen(){
	$(".divReporte").empty();
	$(".divReporte").show();
	$("#divOperaciones").empty();
	$("#divOperaciones").hide();
	$(".listaObjetos").hide();
	$(".detalleObjetos").hide();
	var url = 'reporteGeneralCDM';
	$(".divReporte").load(url);
};
function getRepGenDet(){
	$(".divReporte").empty();
	$("#divOperaciones").empty();
	$("#divOperaciones").hide();
	$(".divReporte").show();
	$(".listaObjetos").hide();
	$(".detalleObjetos").hide();
	var url = 'reporteDetalladoCDM';
	$(".divReporte").load(url);
};
function getReporteCDG(){
	$(".divReporte").empty();
	$("#divOperaciones").empty();
	$("#divOperaciones").hide();
	$(".divReporte").show();
	$(".listaObjetos").hide();
	$(".detalleObjetos").hide();
	var url = 'reporteCDG';
	$(".divReporte").load(url);
};
function getReporteSIJ(){
	$(".divReporte").empty();
	$("#divOperaciones").empty();
	$("#divOperaciones").hide();
	$(".divReporte").show();
	$(".listaObjetos").hide();
	$(".detalleObjetos").hide();
	var url = 'reporteSIJ';
	$(".divReporte").load(url);
};
function getBusCDM(){	
	$.ajax({
		url: "PENDIENTESCDM",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        $(".listaObjetos").html(result);
        $(".detalleObjetos").empty();
   	}});
};

function getAbo(){	
	$.ajax({
		url: "abogados",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        $(".listaObjetos").html(result);
        $(".detalleObjetos").empty();
   	}});
};
function getSolExp(){
	$.ajax({
		url: "solicitudExpedientes",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        $(".listaObjetos").html(result);
        $(".detalleObjetos").empty();
   	}});
};


function getBuscarSen(){	
	$(".listaObjetos").hide(); $(".detalleObjetos").hide();
	$(".contenido").show();
	$(".divReporte").hide();
	$("#divOperaciones").empty();
	$("#divOperaciones").hide();
    $("#divListaSentenciados").empty();
    $("#divDetalleReglas").empty();
    $("#divDetalleReglas").hide();
    $("#divReglas").show();
    //$("#formReglas").val("B");
    $("#txtFormReglas").html("BUSQUEDA DE SENTENCIADOS"); $("#txtBuscarSen").val("");
    $('#listaSedes').hide(); $('#txtBuscarSen').show(); $('#btnBuscarSen').show();
};

function getRepReglas(){
	$(".divReporte").empty();
	$(".divReporte").show();
	$(".contenido").hide();
	$("#divOperaciones").empty();
	$("#divOperaciones").show();
	var url = 'reporteReglas';
	$(".divReporte").load(url);
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
         	html += '<option value="0">SELECCIONE ... </option>';
         	for (var i = 0; i < len; i++) {
         		html += '<option value="' + data[i].c_sede + '">' + data[i].s_sede + '</option>';
	        }
	        html += '</option>';
	        $('#listaSedes').html(html);
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

$('#listaSedes').change(function(){
	var id = $(this).val();
	var form = $('#formReglas').val();
	$.get( "sentenciados", { id: id, form: form, idSen: 0})
	.done(function(data) {
		$("#divListaSentenciados").html(data);
		toastr.clear();
		toastr.options = {
			"closeButton":true,
			"progressBar": true,
			"positionClass":"toast-topcenter-right"
		};
		toastr.success("DATOS RECUPERADOS!!");
	})
	.fail( function() {
		Swal.fire({
		  	icon: 'error', title: 'ATENCIÓN', text: 'Algo salió mal, no se pudo obtener la información. Actualice toda la página!!',
		});
	});
});

$('#btnBuscarSen').click(function(){
	var id = $('#txtBuscarSen').val();
	
	if(id == ''){
		toastr.clear();
		toastr.options = {
			"closeButton":true,
			"progressBar": true,
			"positionClass":"toast-top-right"
		};
		toastr.warning("DEBE INGRESAR ALGUN DATO!!");
		$('#txtBuscarSen').focus();
		return false;
	}
	
	var form = $('#formReglas').val();
	$.get( "sentenciados", { id: id, idSen: 0})
	.done(function(data) {
		$("#divListaSentenciados").html(data);
	})
	.fail( function() {
		Swal.fire({
		  	icon: 'error', title: 'ATENCIÓN', text: 'Algo salió mal, no se pudo obtener la información. Actualice toda la página!!',
		});
	});
});

function getReglasPen(){	
	var json = {
		   	"id" : 1
		};
		$.ajax({
		    type : "POST",
	   		contentType : "application/json",
	   		url : "obtPendientes",
	   		data : JSON.stringify(json),
	        cache : false,
	        timeout : 600000,
	        success : function(data) {
	        	if(data == '0'){
	        		Swal.fire({
					  	icon: 'info',
					  	title: 'Sin registros por liberar.',
					  	text: '',
					});
	        	}
				else{
					Swal.fire({
		  			  title: 'Existen ' + data + ' registros por liberar.',
		  			  text: "Desea liberar ahora?",
		  			  icon: 'question',
		  			  showCancelButton: true,
		  			  confirmButtonColor: '#3085d6',
		  			  cancelButtonColor: '#d33',
		  			  confirmButtonText: 'SI, LIBERAR!'
		  			}).then((result) => {
		  				if (result.value) { 
		  			      	$.ajax({
		  			       		type : "POST",
		  			       		contentType : "application/json",
		  			       		url : "liberarReglas",
		  				        dataType : 'json',
		  				        cache : false,
		  				        success : function(response) {
		  				        	if(response.Status == 200){ 
		  					        	Swal.fire(
		  									'LIBERACIÓN EXITOSA!!',
		  									'Click en OK para continuar!',
		  									'success'
		  								);
		  							}
		  				        	else{
		  				        		Swal.fire({
		  									icon: 'error',
		  									title: 'ATENCIÓN',
		  									text: 'Algo salió mal, no se pudo liberar.!!',
		  									  
		  								});
		  				        	}
		  				        }
		  			      	});
		  				}
		  			})
				}
	        },
	    	error : function(e) {
	    	 	Swal.fire({
				  	icon: 'error',
				  	title: 'ATENCIÓN',
				  	text: 'Ooops, algo salió mal, no se pudo cargar la informacion.!!',
				});
	     	}
	  	});
};
function getAge(){	
	var url='verAgenda';
	window.open(url,'nuevaVentana','width=1200, height=700');
};
function getAgeReg(){	
	var url='verAgendaReglas';
	window.open(url,'nuevaVentana','width=1200, height=700');
};
/*function getUsu(){	
	$.ajax({
		url: "usuarios",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'No se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};*/

function getUsu(){	
	$.ajax({
		url: "usuarios",
        type: 'GET',
        async : false,
        success: function (result) {
        	$("#divOperaciones").empty();
        	$("#divOperaciones").show();
        	$(".divReporte").hide();
        	$(".listaObjetos").hide();
        	$(".detalleObjetos").hide();
        	$("#divOperaciones").html(result);
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'No se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getProgramacion(){	
	$.ajax({
		url: "programacion",
        type: 'GET',
        async : false,
        success: function (result) {
        	$("#divOperaciones").empty();
        	$("#divOperaciones").show();
        	$(".divReporte").hide();
        	$(".listaObjetos").hide();
        	$(".detalleObjetos").hide();
        	$("#divOperaciones").html(result);
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'No se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function getLib(){	
	mostrarDivs();
	$(".detalleObjetos").empty();
	var url = 'redistribucion';
	$(".listaObjetos").load(url);
};
function getSalas(){	
	toastr.clear();
	toastr.options = {"closeButton":true, "progressBar": true, "positionClass":"toast-topcenter-right"};
	toastr.warning("MUY PRONTO ... !!");
};
function getCon(){	
	$.ajax({
		url: "parametros",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        	$(".listaObjetos").html(result); $(".detalleObjetos").empty();
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'Ooops, algo salió mal, no se pudo obtener la información.!!',
			});
	 	}
	});
};
function mostrarDivs(){
	$(".divReporte").hide();
	$("#divOperaciones").hide();
	$(".listaObjetos").empty();
	$(".detalleObjetos").empty();
	$(".listaObjetos").show();
	$(".detalleObjetos").show();
	$("#divDetalleReglas").empty();
    $("#divDetalleReglas").hide();
    $("#divReglas").hide();
};

function getLibCafae(){	
	$.ajax({
		url: "liberacionCafae",
        type: 'GET',
        async : false,
        success: function (result) {mostrarDivs();
        $(".listaObjetos").html(result);
        $(".detalleObjetos").empty();
   	}});
};

function getEscCafae(){	
	$(".divReporte").empty();
	$(".divReporte").show();
	$(".listaObjetos").hide();
	$(".detalleObjetos").hide();
	var url = 'escrutinioCafae';
	$(".divReporte").load(url);
};

function getRepCafae(){	
	$(".divReporte").empty();
	$(".divReporte").show();
	$(".listaObjetos").hide();
	$(".detalleObjetos").hide();
	var url = 'reporteCafae';
	$(".divReporte").load(url);
};

$(".btnCamCla").click(function(){
	if ($("#clave").val().length < 6){ 
		
		toastr.clear();
		toastr.options = {
			"closeButton":true,
			"progressBar": true,
			"positionClass":"toast-topcenter-right"
		};
		toastr.warning("La longitud mínima para la clave debe ser 6 caracteres!");
		
		$("#clave").value=""
		$("#clave").focus() 
      	return false; 
   	}
	
	Swal.fire({
		  title: 'Está seguro de cambiar la clave?',
		  text: "",
		  icon: 'question',
		  showCancelButton: true,
		  confirmButtonColor: '#3085d6',
		  cancelButtonColor: '#d33',
		  confirmButtonText: 'SI, Cambiar!'
		}).then((result) => {
			if (result.value) {
				
				$(function () {
				    var token = $("input[name='_csrf']").val();
				    var header = "X-CSRF-TOKEN";
				    $(document).ajaxSend(function(e, xhr, options) {
				        xhr.setRequestHeader(header, token);
				    });
				});
				
				  $.ajax({
						type:"post",
						url: "cambiarClave",
						cache: false,				
						data:"clave=" + $("#clave").val(),
						success: function(response){
							Swal.fire(
								'Clave cambiada con éxito!!',
								'Click en OK para continuar!',
								'success'
							)
							$("#staticBackdrop").modal('hide');//ocultamos el modal
							$('body').removeClass('modal-open');//eliminamos la clase del body para poder hacer scroll
							$('.modal-backdrop').remove();
						},
						error: function(){						
							Swal.fire({
								  icon: 'info',
								  title: 'ATENCIÓN',
								  text: 'Oops. Algo salió mal!!',
								  
								})
						}
					});


			  }
		})
});

$(".btnGuardarColor").click(function(){
	
	Swal.fire({
		  title: 'Está seguro de cambiar el color?',
		  text: "",
		  icon: 'question',
		  showCancelButton: true,
		  confirmButtonColor: '#3085d6',
		  cancelButtonColor: '#d33',
		  confirmButtonText: 'SI, Cambiar!'
		}).then((result) => {
			if (result.value) {
				
				$(function () {
				    var token = $("input[name='_csrf']").val();
				    var header = "X-CSRF-TOKEN";
				    $(document).ajaxSend(function(e, xhr, options) {
				        xhr.setRequestHeader(header, token);
				    });
				});
				
				  $.ajax({
						type:"post",
						url: "cambiarColor",
						cache: false,				
						data:"color=" + $(".txtColor").val(),
						success: function(response){
							if(response.Status == 200){ 
								$("#modalColorAgenda").modal('hide');//ocultamos el modal
								$('body').removeClass('modal-open');//eliminamos la clase del body para poder hacer scroll
								$('.modal-backdrop').remove();
								Swal.fire(
									'Color establecido con éxito, los cambios se reflejarán al abrir la agenda!!',
									'Click en OK para continuar!',
									'success'
								);
							}
						},
						error: function(){						
							Swal.fire({
								icon: 'info',
								title: 'ATENCIÓN',
								text: 'Oops. Algo salió mal!!',	  
							})
						}
					});
			  }
		})
});

$(".btnCambiarClaveCafae").click(function(){
	if ($("#oldClaveCafae").val() == ''){ 
		toastr.clear();
		toastr.options = {
			"closeButton":true,
			"progressBar": true,
			"positionClass":"toast-topcenter-right"
		};
		toastr.warning("Debe ingresar clave antigua!");
		
		$("#oldClaveCafae").focus(); 
      	return false; 
   	}
	if ($("#newClaveCafae").val().length < 8){ 
		toastr.clear();
		toastr.options = {
			"closeButton":true,
			"progressBar": true,
			"positionClass":"toast-topcenter-right"
		};
		toastr.warning("La longitud mínima para la clave debe ser 8 caracteres!");
		
		$("#newClaveCafae").focus(); 
      	return false; 
   	}
	
	var json = {
			"codigo" : $("#oldClaveCafae").val(),
			"valor" : $("#newClaveCafae").val()
		};
	
	Swal.fire({
		  title: 'Está seguro de cambiar la clave del proceso de elecciones?',
		  text: "",
		  icon: 'question',
		  showCancelButton: true,
		  confirmButtonColor: '#3085d6',
		  cancelButtonColor: '#d33',
		  confirmButtonText: 'SI, Cambiar!'
		}).then((result) => {
			if (result.value) {
				  $.ajax({
					  type : "POST",
				   		contentType : "application/json",
				   		url : "cambiarClaveCafae",
				   		data : JSON.stringify(json),
				        dataType : 'json',
				        cache : false,
						success: function(response){
							if(response.Status == 200){ 
								$("#oldClaveCafae").val('');
								$("#newClaveCafae").val('');
								$("#modalCafae").modal('hide');
								$('body').removeClass('modal-open');
								$('.modal-backdrop').remove();
								Swal.fire(
									'Clave cambiada con éxito!!',
									'Click en OK para continuar!',
									'success'
								);
							}
							else if(response.Status == 400){
								Swal.fire({
									  icon: 'info',
									  title: 'ATENCIÓN',
									  text: 'Clave antigua no coincide, intente nuevamente!!',
									  
								});
							}
							
						},
						error: function(){						
							Swal.fire({
								  icon: 'info',
								  title: 'ATENCIÓN',
								  text: 'Oops. Algo salió mal!!',
								  
								})
						}
					});


			  }
		})
});

$(function () {
    $(".create_excel").click(function () {
        $(".reporteSolicitudes").table2excel({
            filename: "Reporte.xls"
        });
    });
});

$( function() {
    $(".x_fecha_reporte").change( function() {
        if ($(this).val() == "0") {
        	$(".x_inicio_reporte").prop("disabled", true);
        	$(".x_fin_reporte").prop("disabled", true);
        	$(".x_inicio_reporte").val("");
        	$(".x_fin_reporte").val("");
        }
        else if ($(this).val() != "0") {
        	$(".x_inicio_reporte").prop("disabled", false);
        	$(".x_fin_reporte").prop("disabled", false);
        	$(".x_inicio_reporte").val("");
        	$(".x_fin_reporte").val("");
        }
    });
});

function buscarReporte(){
	var x_inicio = $('.x_inicio_reporte').val();
	var x_fin = $('.x_fin_reporte').val();
	var x_f_rep = $('.x_fecha_reporte').val();
	if(x_f_rep != "0" && (x_inicio == "" || x_fin == "")){
		toastr.clear();
		toastr.options = {
			"closeButton":true,
			"progressBar": true,
			"positionClass":"toast-topcenter-right"
		};
		toastr.warning("INGRESE FECHA DE INICIO Y FIN!!");
		
	}
	else{
		if(x_f_rep == "0"){
			x_inicio = "0";
			x_fin = "0";
		}
		var filtro = $('.x_fecha_reporte').val();
		var x_tipo = $('.x_tipo_reporte').val();

    	$.get( "filtrarReportePersonalCDM", { filtro:filtro, inicio:x_inicio, fin:x_fin, tipo:x_tipo})
	    .done(function( data ) {
	    	$(".divReportePersonal").html(data);
	    })
	}
}
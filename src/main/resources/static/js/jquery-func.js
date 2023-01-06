/**
 * 
 */

function buscarSolicitud(){
	$(".detalleObjetos").empty();
	var formulario = $('.formulario').val();
	var nombres = $('.x_nombres').val();
	var inicio = $('.x_inicio').val();
	var fin = $('.x_fin').val();
	var url;
	
	if(nombres == "" && inicio == "" && fin == ""){
		toastr.clear();
		toastr.options = {
			"closeButton":true,
			"progressBar": true,
			"positionClass":"toast-topcenter-right"
		};
		toastr.error("INGRESE NOMBRE O FILTRE POR FECHAS!!");
		return false;
	}
	if(nombres == "" && ((inicio != "" && fin == "") || (inicio == "" && fin != ""))){
		toastr.clear();
		toastr.options = {
			"closeButton":true,
			"progressBar": true,
			"positionClass":"toast-topcenter-right"
		};
		toastr.error("INGRESE FECHA DE INICIO Y FIN!!");
		return false;
	}
	if(nombres != "" && inicio == "" && fin == ""){
   		$.get( "buscarSolicitudNombre", { formulario:formulario, nombres:nombres})
	    .done(function( data ) {
	    	$(".listaObjetos").html(data);
	    });
	}
	else if(nombres == "" && inicio != "" && fin != ""){
   		$.get( "buscarSolicitudFecha", { formulario:formulario, inicio:inicio, fin:fin})
	    .done(function( data ) {
	    	$(".listaObjetos").html(data);
	    });
	}
	else if(nombres != "" && inicio != "" && fin != ""){
		$.get( "buscarSolicitud", { formulario:formulario, nombres:nombres, inicio:inicio, fin:fin})
	    .done(function( data ) {
	    	$(".listaObjetos").html(data);
	    });
	}

};
$("#tablaSolicitudes #filasSolicitudes").click(function(){
	$(this).addClass('selected').siblings().removeClass('selected');	 

	var estado = null; 
	estado = $(this).find('td:eq(1)').html();
   	var value=$(this).find('td:first').html();

   	$(".detalleObjetos").empty();
	$.get( "detalleObjetos", { id: value})
		.done(function(data) {
			$(".detalleObjetos").html(data);
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

	if(estado == 'N'){
		var url2 = $('.formulario').val();
		$.ajax({
			url: url2,
	        type: 'GET',
	        async : false,
	        success: function (result) {
	        	$(".listaObjetos").html(result);
	        }
		});
	}
});	

function actualizar(){
	$(".detalleObjetos").empty();
	var url3 = $('.formulario').val();
		$(".listaObjetos").load(url3);
		toastr.clear();
	toastr.options = {
		"closeButton":true,
		"progressBar": true,
		"positionClass":"toast-topcenter-right"
	};
	toastr.success("REGISTROS ACTUALIZADOS!!");
};

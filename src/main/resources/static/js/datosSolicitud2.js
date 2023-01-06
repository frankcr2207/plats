$(document).ready(function () {
	$('.nBtn, .eBtn').on('click', function (event) {
    	event.preventDefault();
		        var href = $(this).attr('href');
		        
		            $.get(href, function (solicitud, status) {
		            	$('.datosSolicitante #id').val(solicitud.id);
		                $('.datosSolicitante #fecha').val(solicitud.fecha);
		                $('.datosSolicitante #correo').val(solicitud.correo);
		                $('.datosSolicitante #nombres').val(solicitud.nombres + ' ' + solicitud.apellidos);
		                $('.datosSolicitante #documento').val(solicitud.documento);
		                $('.datosSolicitante #nacimiento').val(solicitud.nacimiento);
		                $('.datosSolicitante #celular').val(solicitud.celular);
		                $('.datosSolicitante #cdm').val(solicitud.cdm);
		                $('.datosSolicitante #instancia').val(solicitud.instancia);
		                $('.datosSolicitante #expediente').val(solicitud.expediente);
		                $('.modalRespuesta #solicitanteRespuesta').val(solicitud.nombres + ' ' + solicitud.apellidos);
		                $('.modalRespuesta #correoRespuesta').val(solicitud.correo);
		                $('.modalRespuesta #asuntoRespuesta').val(solicitud.cdm);
		                $('.modalRespuesta #referenciaRespuesta').val(solicitud.expediente + ' - '+ solicitud.instancia);
		            });
				
			});
		})
		
	
/**
 * 
 */
$(document).ready(function () {
	
			    	$("#tableSolicitudes #filasSolicitudes").click(function(){
					$(this).addClass('selected').siblings().removeClass('selected');    
					var value = null; 
					value=$(this).find('td:first').html();
					alert(value);
					var url='obtenerDatos';
		    		url = url + '/' + $('#documento').val();
			    	event.preventDefault();
			        var href = $(this).attr(url);
			        
			            $.get(href, function (solicitud, status) {
			            	$('#id').val(solicitud.id);
			                $('#fecha').val(solicitud.fecha);
			                $('#correo').val(solicitud.correo);
			                $('#nombres').val(solicitud.nombres + ' ' + solicitud.apellidos);
			                $('#documento').val(solicitud.documento);
			                $('#nacimiento').val(solicitud.nacimiento);
			                $('#celular').val(solicitud.celular);
			                $('#cdm').val(solicitud.cdm);
			                $('#instancia').val(solicitud.instancia);
			                $('#expediente').val(solicitud.expediente);
			            });


			    });
			    
			    

			});
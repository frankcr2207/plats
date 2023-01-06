/**
 * 
 */
/**
 * 
 */
$(document).ready(function () {
	
    $('.btnUsuario').on('click', function (event) {
    	
        var href = $(this).attr('href');
        event.preventDefault();
        $.get(href, function (usuario, status) {
        	$('.dniUsuario').val(usuario.dni);
            $('.nombresUsuario').val(usuario.nombres);
            $('.paternoUsuario').val(usuario.paterno);
            $('.maternoUsuario').val(usuario.materno);
            $('.usuarioUsuario').val(usuario.user);
            $('.estadoUsuario').val(usuario.estado);
            $('.perfilUsuario').val(usuario.perfil);
            $('.estado').val(usuario.estado);
        });
        
        $('.modalUsuario').modal();
    });
});

function actualizarUsuarios(){
	$.ajax({
		url: "usuarios",
        type: 'GET',
        async : false,
        success: function (result) {
        	$("#divInhibiciones").empty();
        	$("#divInhibiciones").show();
        	$(".divReporte").hide();
        	$(".listaObjetos").hide();
        	$(".detalleObjetos").hide();
        	$("#divInhibiciones").html(result);
        },
		error : function(e) {
		 	Swal.fire({
			  	icon: 'error', title: 'ATENCIÓN', text: 'No se pudo obtener la información, cierre sesión y vuelva a ingresar.!!',
			});
	 	}
	});
};

function editarUsuario(c_usuario){
	$.get('buscarUsuario/?id='+c_usuario, function (usuario, status) {
    	$('.dniUsuario').val(usuario.dni);
        $('.nombresUsuario').val(usuario.nombres);
        $('.apellidosUsuario').val(usuario.paterno + ' '+ usuario.materno);
        $('.usuarioUsuario').val(usuario.user);
        $('.estadoUsuario').val(usuario.estado);
        $('.perfilUsuario').val(usuario.perfil);
        $('.estado').val(usuario.estado);
        $('.carga').val(usuario.carga);
        $('.correoUsuario').val(usuario.correo);
        $('.telefonoUsuario').val(usuario.telefono);
        $('.especialidadUsuario').val(usuario.especialidad);
        if(usuario.perfil == '8' || usuario.perfil == '5'){
        	$('.divCarga').show();
        }else{
        	$('.divCarga').hide();
        }  	
    });
    
    $('.modalUsuario').modal();
}

$(document).ready(function () {  
    (function ($) {
        $('#txtBuscarUsuario').keyup(function () {
             var rex = new RegExp($(this).val(), 'i');
             $('.buscar tr').hide();
             $('.buscar tr').filter(function () {
               return rex.test($(this).text());
             }).show();
        })
    }(jQuery));
});



$(".btnBusReniec").click(function(){
	$.get('buscarPersonaReniec/?id='+ $('.dniNuevoUsuario').val(), 
		function (usuario, status) {
    	$('#dniNuevo').val(usuario.dni);
        $('#nombresNuevo').val(usuario.nombres);
        $('#apellido_paterno_Nuevo').val(usuario.paterno);
        $('#apellido_materno_Nuevo').val(usuario.materno); 	
    });
	$('.dniNuevoUsuario').val(''); 
});

function lettersonlywithoutspace(event){
	var regex = new RegExp("^[a-zA-Z_]+$");
	var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
	if (!regex.test(key)) {
	    event.preventDefault();
		return false;
	}

}

$(".btnGuardarNuevoUsuario").click(function() {
	var usuario = $('.c_usuarioNuevo').val();
	var perfil= $('.perfilUsuarioNuevo').val();
	var nombres = $('.nombresUsuarioNuevo').val() + ' ' + $('.paternoUsuarioNuevo').val() + ' '+ $('.maternoUsuarioNuevo').val();
	if(usuario.length < 4 || $('.nombresUsuarioNuevo').val() == 'DNI ERROR'){
		toastr.clear();
		toastr.options = {
			"closeButton":true,
			"progressBar": true,
			"positionClass":"toast-topcenter-right"
		};
		toastr.warning("Usuario inválido!!");
		return false;
	}

	Swal.fire({
		title: 'Seguro guardar nuevo usuario?',
		text: "",
		icon: 'question',
		showCancelButton: true,
		confirmButtonColor: '#3085d6',
		cancelButtonColor: '#d33',
		confirmButtonText: 'SI, GUARDAR!'
	}).then((result) => {
		if (result.value) {
				
				$(function () {
				    var token = $("input[name='_csrf']").val();
				    var header = "X-CSRF-TOKEN";
				    $(document).ajaxSend(function(e, xhr, options) {
				        xhr.setRequestHeader(header, token);
				    });
				});
				var form = document.getElementById('formNuevoUsuario');
				var formData = new FormData(form);
				$.ajax({
					type:"POST",
					url:"guardarNuevoUsuario",
					data:formData,
					cache:false,
					dataType:"json",
					contentType:false,
					processData:false,
					success:function(response) {
						if(response.Status == 200){ 
							$('#modalNuevoUsuario').modal('hide');
							if(perfil == '10'){
								Swal.fire({
									title: 'Usuario registrado con éxito!!',
									text: "Desea agregar instancias ahora?",
									icon: 'success',
									showCancelButton: true,
									confirmButtonColor: '#3085d6',
									cancelButtonColor: '#d33',
									confirmButtonText: 'SI, Agregar!'
								}).then((result) => {
									if (result.value) {
										verPermisosSecretario(usuario, nombres);
									}
								})
							}
							if(perfil == '5'){
								Swal.fire({
									  icon: 'info',
									  title: 'Usuario agregado con exito, puede gestionar ...',
									  showDenyButton: true,
									  showCancelButton: true,
									  confirmButtonText: `CDM`,
									  denyButtonText: `CDG`,
									}).then((result) => {
									  if (result.isConfirmed) {
										  verPermisosCDM(usuario, nombres);
									  } else if (result.isDenied) {
										  verPermisosCDG(usuario, nombres);
									  }
									})
							}
							else{
								Swal.fire(
									'Usuario registrado con éxito!!',
									'Click en OK para continuar!',
									'success'
								);
							}
							
						}
						else if(response.Status == 400){

							Swal.fire({
								  icon: 'info',
								  title: 'ATENCIÓN',
								  text: 'NOMBRE DE USUARIO YA EXISTE, VERIFICAR!!',
								  
								});
						}
						else {

							Swal.fire({
								  icon: 'error',
								  title: 'ATENCIÓN',
								  text: 'No se pudo guardar la información.!!',
								  
								});
						}
					}
				
				});

		}
	})

});

$(".actualizarUsuario").on("submit", function(e) {
	e.preventDefault();
	var formData = new FormData(this);
	$.ajax({
		type:"POST",
		url:"actualizarUsuario",
		data:formData,
		cache:false,
		dataType:"json",
		contentType:false,
		processData:false,
		success:function(response) {
			if(response.Status == 200){ 
				$(".modalUsuario").modal('hide');//ocultamos el modal
				$('body').removeClass('modal-open');//eliminamos la clase del body para poder hacer scroll
				$('.modal-backdrop').remove();//e
				$("#divInhibiciones").empty();
				$("#divInhibiciones").load('usuarios');
				
				Swal.fire(
						  'ACTUALIZACIÓN EXITOSA!!',
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
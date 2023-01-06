/**
 * 
 */
$(document).ready(function () {
	$('.btnReniec').on('click', function (event) {
    	event.preventDefault();
        var href = $(this).attr('href');
        
            $.get(href, function (usuario, status) {
            	$('.nombresReniec').val(usuario.nombres);
                $('.fechaNacReniec').val(usuario.nacimiento);
                $('.padreReniec').val(usuario.paterno);
                $('.madreReniec').val(usuario.materno);
                $('.departamentoReniec').val(usuario.perfil);
                
            });
            $('.modalReniec').modal();
	});
	
	$('.restClave').on('click', function (event) {
        event.preventDefault();
        var href = $(this).attr('href');
        $('#myModal #delRef').attr('href', href);
        $('#myModal').modal();
    });
})
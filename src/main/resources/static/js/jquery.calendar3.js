/**
 * 
 */
 function obtenerTurnos(sede) {
  
    var calendarEl = document.getElementById('calendar');
  	var calendar = new FullCalendar.Calendar(calendarEl, {
	    plugins: [ 'interaction', 'dayGrid', 'timeGrid', 'list' ,'bootstrap'],
	    locale: 'es',
	    timeZone: 'local',
	    defaultView: 'dayGridMonth',
	    themeSystem: 'bootstrap',
	    header: {
	      left: 'prev,next today',
	      center: 'title',
	      right: ''
	    },
	    firstDay: 0,
	    defaultDate: new Date(),
	    hiddenDays: [],
	    //minTime: '08:00:00',
	    //maxTime: '16:01:00',
	    slotEventOverlap:false,
	    allDaySlot: false,
	    slotLabelFormat: [
	        {
	            hour: '2-digit',
	            minute: '2-digit',
	            hour12:false
	        }
	    ],
	    displayEventTime: false,
	    eventBorderColor:'white',
	    //slotDuration:'00:05:00',
	    navLinks: false, // can click day/week names to navigate views
	
	    weekNumbers: false,
	    weekNumbersWithinDays: true,
	    weekNumberCalculation: 'ISO',
	
	    editable: false,
	    eventLimit: false, // allow "more" link when too many events
	    eventColor: '#FFFFFF',
	    events: {
			url: 'obtenerTurnos/' + sede,
		},
		eventClick: function(info) {
			$('#txtIdTurno').val(info.event.id);
			$('.divTituloEvento').html('<strong>USUARIO:</strong> ' + info.event.title);
			$('.divFechaEvento').html('<strong>FECHA: </strong>' + moment(info.event.start).format('DD/MM/YYYY'));
			$('.modalTituloEvento').modal('show');
		},
		dateClick: function(info){
			/*var fecha = calendar.formatDate(info.dateStr, {
				day: '2-digit',  
				month: '2-digit',
				year: 'numeric',
			})
			var hora = calendar.formatDate(info.dateStr, {
				hour: '2-digit',
				minute: '2-digit',
			})*/
			//document.getElementById('fecha').value=fecha;
			//document.getElementById('inicio').value=hora;
		}
  });

  calendar.render();
};
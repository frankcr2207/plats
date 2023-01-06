/**
 * 
 */

document.addEventListener('DOMContentLoaded', function() {
  
    var calendarEl = document.getElementById('calendar');
  	var calendar = new FullCalendar.Calendar(calendarEl, {
    plugins: [ 'interaction', 'dayGrid', 'timeGrid', 'list' ,'bootstrap'],
    locale: 'es',
    timeZone: 'local',
    defaultView: 'Semana',
    themeSystem: 'bootstrap',
    header: {
      left: 'prev,next today',
      center: 'title',
      right: 'Semana,timeGridDay'
    },
    defaultDate: new Date(),
    hiddenDays: [ 0, 6 ],
    minTime: '08:00:00',
    maxTime: '16:01:00',
    slotEventOverlap:false,
    allDaySlot: false,
    slotLabelFormat: [
        {
            hour: '2-digit',
            minute: '2-digit',
            hour12:false
        }
        ],

    eventBorderColor:'white',
    slotDuration:'00:05:00',
    navLinks: true, // can click day/week names to navigate views

    weekNumbers: true,
    weekNumbersWithinDays: true,
    weekNumberCalculation: 'ISO',

    editable: false,
    eventLimit: false, // allow "more" link when too many events
    eventColor: '#FFFFFF',
    events: {
		url: 'all',
	},
	eventClick: function(info) {
		$('.divTituloEvento').html(info.event.title);
		$('.modalTituloEvento').modal('show');
		
		    //ssalert('Event: ' + info.event.title);
		    //alert('Coordinates: ' + info.jsEvent.pageX + ',' + info.jsEvent.pageY);
		    //alert('View: ' + info.view.type);

		    // change the border color just for fun
		    //info.el.style.borderColor = 'red';
		  },
		dateClick: function(info){
			var fecha = calendar.formatDate(info.dateStr, {
				day: '2-digit',  
				month: '2-digit',
				year: 'numeric',
				//hour: '2-digit',
				//minute: '2-digit',
			})
			var hora = calendar.formatDate(info.dateStr, {
				hour: '2-digit',
				minute: '2-digit',
			})
			document.getElementById('fecha').value=fecha;
			document.getElementById('inicio').value=hora;
		}
  });

  calendar.render();
});
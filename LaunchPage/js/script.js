var currentDate = new Date();
var futureDate  = new Date(2014, 10, 7, 0);
var diff = futureDate.getTime() / 1000 - currentDate.getTime() / 1000;

$('#clock').FlipClock(diff, {
  clockFace: 'DailyCounter',
  countdown: true
});

// Email form
$('#submit').click(function (event) {
  event.preventDefault();
  if (!/[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}/.test($('#email').val()))
    return;

  $.post('create.php', { email: $('#email').val() }, function (result) {
    if (result) {
      $('.phone-mic').css('opacity', '0');
      $('.notification').html("<h2>&nbsp;<br>We'll notify you soon.</h2>");
      $('#phone-info').html("Stay tuned...<br>&nbsp;");
    } else {
      console.log('An error occurred. Please try again.');
    }
  });
});

$('#mobile-submit').click(function (event) {
  event.preventDefault();
  if (!/[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}/.test($('#email').val()))
    return;

  $.post('create.php', { email: $('#mobile-email').val() }, function (result) {
    if (result) {
      $('.mobile-notification').html("<h2>Stay tuned... <br> We'll notify you soon.</h2>");
    } else {
      console.log('An error occurred. Please try again.');
    }
  });
});

$('#show-form').click(function() {
  $(this).fadeOut(200, function () {
      $('.notification').fadeIn(200);
      $('#email').focus();
  });
});


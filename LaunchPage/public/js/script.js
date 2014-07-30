// Email form
$('#submit').click(function (event) {
  event.preventDefault();
  $.post('/', { email: $('#email').val() }, function (result) {
    if (result) {
      $('.notification').html("<h2>Thanks for your interest! We'll notify you when we're ready.</h2>");
    } else {
      console.log('An error occurred. Please try again.');
    }
  });
});

$('#mobile-submit').click(function (event) {
  event.preventDefault();
  $.post('/', { email: $('#mobile-email').val() }, function (result) {
    if (result) {
      $('.mobile-notification').html("<h2>Thanks for your interest! We'll notify you when we're ready.</h2>");
    } else {
      console.log('An error occurred. Please try again.');
    }
  });
});

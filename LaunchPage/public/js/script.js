// Smooth scrolling
$('a[href*=#]:not([href=#])').click(function() {
  if (location.pathname.replace(/^\//,'') == this.pathname.replace(/^\//,'') && location.hostname == this.hostname) {
    var target = $(this.hash);
    target = target.length ? target : $('[name=' + this.hash.slice(1) +']');
    if (target.length) {
      $('html,body').animate({
        scrollTop: target.offset().top
      }, 1000);
      return false;
    }
  }
});

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

<jsp:useBean id="display" scope="request" type="ch.unartig.studioserver.beans.DisplayBean"/>
<%@ taglib prefix="html" uri="http://jakarta.apache.org/struts/tags-html" %>
<script src="<html:rewrite page="/js/swiper.min.js"/>"></script>
<script>
    var mySwiper = new Swiper('.swiper-container', {
        // Disable preloading of all images
        preloadImages: false,
        // Enable lazy loading
        lazy: true,

        /*

                // Optional parameters
                direction: 'vertical',
                loop: true,


                // If we need pagination
                pagination: {
                    el: '.swiper-pagination',
                },
        */

        // Navigation arrows
        navigation: {
            nextEl: '.swiper-button-next',
            prevEl: '.swiper-button-prev'
        }

        /*
                // And if we need scrollbar
                scrollbar: {
                    el: '.swiper-scrollbar',
                },
        */
    });


    mySwiper.on('slideChange', function () {
        console.log('slide changed - which direction?');
    });

    mySwiper.on('slideNextTransitionEnd', function () {
        console.log('slide changed - forward');
        // get next photo URL / REST Service ? deliver URLs with request scope?
        mySwiper.appendSlide('<div class="swiper-slide"><img data-src="${display.nextPhoto.displayUrl}" class="swiper-lazy"><div class="swiper-lazy-preloader"></div></div>');
        console.log('slide added');

    });

    mySwiper.on('slidePrevTransitionEnd', function () {
        console.log('slide changed - backwards');
    });


/*

    Append / prepend slides after slide transitions (listen to events):

    mySwiper.appendSlide('<div class="swiper-slide">Slide 10"</div>')

    mySwiper.prependSlide('<div class="swiper-slide">Slide 0"</div>')


    mySwiper.update(); --> necessary? see api



*/




</script>
<jsp:useBean id="display" scope="request" type="ch.unartig.studioserver.beans.DisplayBean"/>
<%@ taglib prefix="html" uri="http://jakarta.apache.org/struts/tags-html" %>
<%@ taglib prefix="logic" uri="http://jakarta.apache.org/struts/tags-logic" %>


<script src="<html:rewrite page="/js/swiper.min.js"/>"></script>
<script>
    var currentPhotoIndex;

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
        if (currentPhotoIndex+2 < displayPhotos.photos.length) { // length = max index +1
            console.log('adding : ' + displayPhotos.photos[currentPhotoIndex]);
            console.log("PhotoIndex for appending : " + currentPhotoIndex +2);
            // todo refactor : extract generalized method with offset
            mySwiper.appendSlide('<div class="swiper-slide"><img data-src=' + displayPhotos.photos[currentPhotoIndex + 2].displayURL + ' class="swiper-lazy"><div class="swiper-lazy-preloader"></div></div>');
            // todo add a-tag with masterURL
            console.log('slide added');
            currentPhotoIndex = Number(currentPhotoIndex)+1; // increase next photo index
        } else {
            console.log("Reached the end of the array");
        }

        // todo: check if array of photos available

    });

    mySwiper.on('slidePrevTransitionEnd', function () {
        console.log('slide changed - backwards');

        if (currentPhotoIndex-2 >= 0) {
            console.log("PhotoIndex for prepending : " + currentPhotoIndex-2);

            // todo refactor : extract generalized method with offset
            mySwiper.prependSlide('<div class="swiper-slide"><img data-src=' + displayPhotos.photos[currentPhotoIndex-2].displayURL + ' class="swiper-lazy"><div class="swiper-lazy-preloader"></div></div>');

        }
        currentPhotoIndex = Number(currentPhotoIndex)-1; // decrease photo index

    });


    // todo in case of startnummer search
    // initial call
    var eventCategoryId = "${display.albumBean.album.eventCategory.eventCategoryId}";
    var initialPhotoId = "${display.displayPhotoId}";
    var displayPhotos = {eventCategoryId:eventCategoryId,photos:undefined};
    console.log("initializing - calling photos service (eventcategoryid " + eventCategoryId+" on page");
    callPhotosService();

    function setInitialLeft() {
        if (currentPhotoIndex-1 >= 0) {
            console.log("PhotoIndex for setting initial left : " + currentPhotoIndex-1);
            // todo refactor : extract generalized method with offset
            mySwiper.prependSlide('<div class="swiper-slide"><img data-src=' + displayPhotos.photos[currentPhotoIndex-1].displayURL + ' class="swiper-lazy"><div class="swiper-lazy-preloader"></div></div>');
            // todo add a-tag with masterURL

        }

    }



    function getCurrentPhotoIndex() {

        console.log("getCurrentPhotoIndex is called");

        for (var i = 0; i < displayPhotos.photos.length; i++) {
            // console.log("photoID = " + displayPhotos.photos[i].photoID);
            // console.log("displayURL = " + displayPhotos.photos[i].displayURL);
            // console.log("masterURL = " + displayPhotos.photos[i].masterURL);
            var photoID = displayPhotos.photos[i].photoID;
            if (photoID===initialPhotoId) {
                console.log("found photoID : " + photoID);
                console.log("type of photoID : " + typeof photoID);
                console.log("Number i : "+ Number(i));
                console.log("Number i +1 : "+ i + 1);
                console.log("Number photoID +1 : "+ (1+ photoID));
                test = +photoID+1;
                console.log("test = +photoID +1: " + test);
                return Number(i); // return index of current photo
            }
        }

        return undefined;
    }



/*

    Append / prepend slides after slide transitions (listen to events):

    mySwiper.appendSlide('<div class="swiper-slide">Slide 10"</div>')

    mySwiper.prependSlide('<div class="swiper-slide">Slide 0"</div>')


    mySwiper.update(); --> necessary? see api



*/

    function callPhotosService() {
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function () {
            var jsonResponse;
            if (this.readyState == 4 && this.status == 200) {
                console.log('done loading photo data')
                // todo : append correct slide / navigate to

                jsonResponse = this.responseText;
                // console.log(jsonResponse);
                displayPhotos.photos = JSON.parse(jsonResponse); // store array of URLs for current display (eventcategory / startnumber);
                console.log("number of photos  : " + displayPhotos.photos.length);
                // append(photos[0].displayURL);

                // we have to wait to call getCurrentPhotoIndex until call has returned:
                currentPhotoIndex = getCurrentPhotoIndex();
                console.log("setting currentPhotoIndex to :" + currentPhotoIndex);
                console.log("displayPhotos.photos is array ? " + Array.isArray(displayPhotos.photos));

                setInitialLeft();

            }
        };
        xhttp.open('GET', '${display.webApplicationURL}/api/sportsalbum/photos.html', true);

        xhttp.send();
    }



</script>
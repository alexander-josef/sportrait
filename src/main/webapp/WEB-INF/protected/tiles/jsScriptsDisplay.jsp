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
        lazy: {
            loadPrevNext: true,
            loadOnTransitionStart: true
        },
        autoHeight : true,

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



    // todo : is this used?
    function setCurrentPhotoDimension() {
        // transition ended, make sure width and height are set correctly
        if (displayPhotos.photos[currentPhotoIndex].orientationPortrait) {
            mySwiper.width = 250;
            mySwiper.height = 380;
        } else { // landscape
            mySwiper.width = 380;
            mySwiper.height = 250;
        }

        // get slide html element
    }

    mySwiper.on('slideNextTransitionEnd', function () {
        console.log('slide changed - forward');
        if (currentPhotoIndex+2 < displayPhotos.photos.length) { // length = max index +1
            console.log('adding : ' + displayPhotos.photos[currentPhotoIndex]);
            console.log("PhotoIndex for appending : " + currentPhotoIndex +2);
            // todo refactor : extract generalized method with offset
            mySwiper.appendSlide(getPhotoSlideHTML(+2));
            console.log('slide added');
            currentPhotoIndex = Number(currentPhotoIndex)+1; // increase next photo index
        } else {
            console.log("Reached the end of the array");
        }


    });

    mySwiper.on('slidePrevTransitionEnd', function () {
        console.log('slide changed - backwards');

        if (currentPhotoIndex-2 >= 0) {
            console.log("PhotoIndex for prepending : " + currentPhotoIndex-2);

            // todo refactor : extract generalized method with offset
            mySwiper.prependSlide(getPhotoSlideHTML(-2));
            currentPhotoIndex = Number(currentPhotoIndex)-1; // decrease photo index
        }



    });


    // todo in case of startnummer search
    // initial call
    var eventCategoryId = "${display.albumBean.album.eventCategory.eventCategoryId}";
    var initialPhotoId = "${display.displayPhotoId}";
    var displayPhotos = {eventCategoryId:eventCategoryId,photos:undefined};
    console.log("initializing - calling photos service (eventcategoryid " + eventCategoryId+" on page");
    initDisplayView();


    function getPhotoSlideHTML(photoArrayIndexOffset) {
        // todo add a-tag with masterURL
        var photoIndex = currentPhotoIndex + photoArrayIndexOffset;
        console.log("Reading from photo index : " + photoIndex);
        return '<div class="swiper-slide"><img data-src=' + displayPhotos.photos[photoIndex].displayURL + ' class="swiper-lazy"><div class="swiper-lazy-preloader"></div></div>';
    }

    function setInitialPhotos() {
        // first set initial active photo
        console.log("Setting active photo - photoIndex for setting active photo : " + currentPhotoIndex);
        mySwiper.appendSlide(getPhotoSlideHTML(0))

        // then set initial left photo
        if (currentPhotoIndex-1 >= 0) {
            console.log("PhotoIndex for setting initial left : " + currentPhotoIndex-1);
            // todo refactor : extract generalized method with offset
            mySwiper.prependSlide(getPhotoSlideHTML(-1));

        }

        // then set initial right photo
        if (currentPhotoIndex< displayPhotos.photos.length) {
            console.log("setting right photo - ");
            // todo refactor : extract generalized method with offset
            mySwiper.appendSlide(getPhotoSlideHTML(+1));

        }

        // update at the end of setup phase
        mySwiper.update();

    }



    function getCurrentPhotoIndex() {

        console.log("getCurrentPhotoIndex is called");

        for (var i = 0; i < displayPhotos.photos.length; i++) {
            // console.log("photoID = " + displayPhotos.photos[i].photoID);
            // console.log("displayURL = " + displayPhotos.photos[i].displayURL);
            // console.log("masterURL = " + displayPhotos.photos[i].masterURL);
            var photoID = displayPhotos.photos[i].photoID;
            if (photoID===initialPhotoId) {
                console.log("found index for current photo - index = " + Number(i));
                return Number(i); // return index of current photo
            }
        }

        return undefined;
    }



    function initDisplayView() {
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function () {
            var jsonResponse;
            if (this.readyState == 4 && this.status == 200) {
                console.log('done loading photo data')
                // response now ready

                jsonResponse = this.responseText;
                // console.log(jsonResponse);
                displayPhotos.photos = JSON.parse(jsonResponse); // store array of URLs for current display (eventcategory / startnumber);
                console.log("number of photos  : " + displayPhotos.photos.length);
                // append(photos[0].displayURL);

                // we have to wait to call getCurrentPhotoIndex until call has returned:
                currentPhotoIndex = getCurrentPhotoIndex();
                console.log("setting currentPhotoIndex to :" + currentPhotoIndex);
                console.log("displayPhotos.photos is array ? " + Array.isArray(displayPhotos.photos));

                setInitialPhotos();

            }
        };
        xhttp.open('GET', '${display.webApplicationURL}/api/sportsalbum/photos.html', true);

        xhttp.send();
    }



</script>
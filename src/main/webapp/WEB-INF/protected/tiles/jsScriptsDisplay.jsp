<jsp:useBean id="display" scope="request" type="ch.unartig.studioserver.beans.DisplayBean"/>
<%@ taglib prefix="html" uri="http://jakarta.apache.org/struts/tags-html" %>
<%@ taglib prefix="logic" uri="http://jakarta.apache.org/struts/tags-logic" %>


<script src="<html:rewrite page="/js/swiper.min.js"/>"></script>
<script>
    var nextPhotoIndex;

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
        console.log('adding : ' + displayPhotos.photos[nextPhotoIndex]);
        // get next photo URL / REST Service ? deliver URLs with request scope?
        append();
        console.log('slide added');
        // todo: check if array of photos available
        //if not, make call for JSON service to retrieve array with photos (ID, display and master URL?)

        /*
                if (displayPhotos.eventCategoryId === ????) {
            callPhotosService();
        }
*/

    });

    mySwiper.on('slidePrevTransitionEnd', function () {
        console.log('slide changed - backwards');
    });


    function append() {
        console.log("nextPhotoIndex for appending : " + nextPhotoIndex);
        mySwiper.appendSlide('<div class="swiper-slide"><img data-src=' + displayPhotos.photos[nextPhotoIndex].displayURL + ' class="swiper-lazy"><div class="swiper-lazy-preloader"></div></div>');
        // todo add a-tag with masterURL
        nextPhotoIndex = Number(nextPhotoIndex)+1; // increase next photo index
    }

    // todo in case of startnummer search
    // initial call
    var eventCategoryId = "${display.albumBean.album.eventCategory.eventCategoryId}";
    var initialPhotoId = "${display.displayPhotoId}";
    var displayPhotos = {eventCategoryId:eventCategoryId,photos:undefined};
    console.log("initializing - calling photos service (eventcategoryid " + eventCategoryId+" on page");
    callPhotosService();


    function getNextPhotoIndex() {

        console.log("getNextPhotoIndex is called");

        for (var i = 0; i < displayPhotos.photos.length; i++) {
            console.log("photoID = " + displayPhotos.photos[i].photoID);
            console.log("displayURL = " + displayPhotos.photos[i].displayURL);
            console.log("masterURL = " + displayPhotos.photos[i].masterURL);
            var photoID = displayPhotos.photos[i].photoID;
            if (photoID===initialPhotoId) {
                console.log("found photoID : " + photoID);
                console.log("type of photoID : " + typeof photoID);
                console.log("Number i : "+ Number(i));
                console.log("Number i +1 : "+ i + 1);
                console.log("Number photoID +1 : "+ (1+ photoID));
                test = +photoID+1;
                console.log("test = +photoID +1: " + test);
                return Number(i) + 1.0; // return index of next photo
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
                console.log(jsonResponse);
                displayPhotos.photos = JSON.parse(jsonResponse); // store array of URLs for current display (eventcategory / startnumber);
                console.log("number of photos  : " + displayPhotos.photos.length);
                // append(photos[0].displayURL);

                // we have to wait to call getNextPhotoIndex until call has returned:
                nextPhotoIndex = getNextPhotoIndex();
                console.log("setting nextPhotoIndex to :" + nextPhotoIndex);
                console.log("displayPhotos.photos is array ? " + Array.isArray(displayPhotos.photos));


            }
        };
        xhttp.open('GET', '${display.webApplicationURL}/api/sportsalbum/photos.html', true);

        xhttp.send();
    }



</script>
<%--@elvariable id="albumBean" type="ch.unartig.studioserver.beans.SportsAlbumBean"--%>
<jsp:useBean id="display" scope="request" type="ch.unartig.studioserver.beans.DisplayBean"/>
<%@ taglib prefix="html" uri="http://jakarta.apache.org/struts/tags-html" %>
<%@ taglib prefix="logic" uri="http://jakarta.apache.org/struts/tags-logic" %>


<script src="<html:rewrite page="/js/swiper.min.js"/>"></script>
<script>
    var currentPhotoIndex;

    var mySwiper = new Swiper('.swiper-container', {
        // Enable preloading of all images
        preloadImages: true,

        // Enable lazy loading
        lazy: {
            loadPrevNext: true,
            loadOnTransitionStart: true
        },


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

    // after swiper has changed the display slide, change needed elements on page
    function changeHTMLafterSlideTransition() {
        //
        // Update photoId - needed if new photo metadata needs to be fetched from REST service

        photoId = displayPhotos.photos[currentPhotoIndex].photoId;
        console.log("Updated photoId to :" + photoId);

        document.getElementById("displayPhotoTime").innerHTML = displayPhotos.photos[currentPhotoIndex].time;
        document.getElementById("displayPhotoTitle").innerHTML = displayPhotos.photos[currentPhotoIndex].displayTitle;
        document.getElementById("displayImageCaption").innerHTML = displayPhotos.photos[currentPhotoIndex].displayTitle + ' -- ' + displayPhotos.photos[currentPhotoIndex].time;
        document.getElementById("displayDownloadButtonLink").setAttribute('href', "/downloadPhoto.html?photoId=" + photoId);
        // document.getElementById("fbShareButton").setAttribute('data-href','/display/' + displayPhotos.photos[currentPhotoIndex].photoId + '/display.html'); // for facebook sharing
        document.getElementById("metaTagUrl").setAttribute('content', '/display/' + photoId + '/display.html'); // for facebook sharing
        dataLayer.push({'photoId': photoId}); // update photoId in dataLayer
        dataLayer.push({'event': 'displayView'});

        // previous / next thumbnails.
        if (!mySwiper.isBeginning) {

            var previousPhotoThumbnail = document.getElementById("previousPhotoThumbnail");
            var previousPhotoLink = document.getElementById("previousPhotoLink");
            var previousPhotoTextLink = document.getElementById("previousPhotoTextLink");
            previousPhotoThumbnail.src = displayPhotos.photos[currentPhotoIndex - 1].thumbnailURL1x;
            if (dataLayer[0].eventYear >= 2018) { // only for images after chagne to image service
                previousPhotoThumbnail.srcset = displayPhotos.photos[currentPhotoIndex - 1].thumbnailURL1x + ' 1x,' + //  use src-set to support 2x and 3x resolution displays, but only for images after introduction of image service
                    displayPhotos.photos[currentPhotoIndex - 1].thumbnailURL2x + ' 2x,' +
                    displayPhotos.photos[currentPhotoIndex - 1].thumbnailURL3x + ' 3x';
            }
            previousPhotoThumbnail.className = displayPhotos.photos[currentPhotoIndex - 1].orientation;
            previousPhotoLink.href = '/display/' + displayPhotos.photos[currentPhotoIndex - 1].photoId + '/display.html';
            previousPhotoTextLink.href = '/display/' + displayPhotos.photos[currentPhotoIndex - 1].photoId + '/display.html';
            document.getElementById("previousSlideLeft").style.display = "unset";
        } else {
            // hide previous preview slide
            console.log("beginning of swiper - hide preview");
            document.getElementById("previousSlideLeft").style.display = "none";
        }


        if (!mySwiper.isEnd) {
            var nextPhotoThumbnail = document.getElementById("nextPhotoThumbnail");
            nextPhotoThumbnail.src = displayPhotos.photos[currentPhotoIndex + 1].thumbnailURL1x;
            var nextPhotoLink = document.getElementById("nextPhotoLink");
            var nextPhotoTextLink = document.getElementById("nextPhotoTextLink");
            if (dataLayer[0].eventYear >= 2018) { // only for images after chagne to image service
                nextPhotoThumbnail.srcset = displayPhotos.photos[currentPhotoIndex + 1].thumbnailURL1x + ' 1x,' + //  use src-set to support 2x and 3x resolution displays, but only for images after introduction of image service
                    displayPhotos.photos[currentPhotoIndex + 1].thumbnailURL2x + ' 2x,' +
                    displayPhotos.photos[currentPhotoIndex + 1].thumbnailURL3x + ' 3x';
            }
            nextPhotoThumbnail.class = displayPhotos.photos[currentPhotoIndex + 1].orientation;
            nextPhotoLink.href = '/display/' + displayPhotos.photos[currentPhotoIndex + 1].photoId + '/display.html';
            nextPhotoTextLink.href = '/display/' + displayPhotos.photos[currentPhotoIndex + 1].photoId + '/display.html';
            document.getElementById("nextSlideRight").style.display = "unset";
        } else {
            // hide next preview slide
            console.log("end of swiper - hide next");
            document.getElementById("nextSlideRight").style.display = "none";
        }
    }


    // when navigating right (forward) is done, update photo array index and
    // check if we are on the last slide and whether there are more photos
    // in the array the can be added (appended) to the right of the swiper
    mySwiper.on('slideNextTransitionEnd', function () {
        console.log('slide changed - forward - updating photo array index');
        currentPhotoIndex = Number(currentPhotoIndex) + 1; // increase next photo index
        console.log('current index : ' + currentPhotoIndex);
        console.log('size of array : ' + displayPhotos.photos.length);

        // anonymous function - change to global if needed
        function fetchMoreRightFromRestApi() {
            var xhttp = new XMLHttpRequest();
            xhttp.onreadystatechange = function () {
                var jsonResponse;
                if (this.readyState == 4 && this.status == 200) {
                    console.log('done loading photo data')
                    // response returned and is now ready

                    jsonResponse = this.responseText;
                    console.log(jsonResponse); // caution - can cause huge log output
                    displayPhotos.photos = JSON.parse(jsonResponse); // store array of URLs for current display (eventcategory / startnumber);
                    console.log("displayPhotos.photos is array ? " + Array.isArray(displayPhotos.photos));
                    setCurrentPhotoIndex()


                }
            };
            console.log('fetching JSON data from REST service for photos to the right of photo ['+photoId+']');
            xhttp.open('GET', '/api/sportsalbum/photos.html?photoId='+photoId+'&eventCategoryId='+eventCategoryId+'&startNumber=' + startNumber+'&direction=right', true);
            xhttp.send();
        }

        if (mySwiper.isEnd) { // only add if we're at the end of the slides
            // check for more photos in the array
            if (currentPhotoIndex + 1 < displayPhotos.photos.length) { // length = max index +1
                // there are more photos in the array to the right
                console.log('adding Photo with ID: ' + displayPhotos.photos[currentPhotoIndex].photoId);
                console.log("PhotoIndex for appending : " + (Number(currentPhotoIndex) + 1));
                mySwiper.appendSlide(getPhotoSlideHTMLfromOffset(+1));
                console.log('slide added');
                // this.mySwiper.update();
                // mySwiper.updateAutoHeight(1000);
            }

            if (currentPhotoIndex + 1===displayPhotos.photos.length) {
                console.log("Reached the end of the selection - swiper is at the end");
            } else if (currentPhotoIndex + 2 === displayPhotos.photos.length) { // looking two images ahead, do we need to fetch more data?
                // try to fetch more photos
                console.log("trying to fetch more ...");
                fetchMoreRightFromRestApi();
            }

        }
        changeHTMLafterSlideTransition();
        triggerDisplayPhotoEvent();


    });

    // when navigating left (backwards) is done, update photo array index and
    // check if we are on the 1st slide and whether there are more photos
    // in the array that can be added (prepended) to the left of the swiper
    // fetch more photos from REST API if necessary
    mySwiper.on('slidePrevTransitionEnd', function () {
        console.log('slide changed - backwards - updating photo array index');
        currentPhotoIndex = Number(currentPhotoIndex) - 1; // decrease photo index

        // anonymous function - change to global if needed
        function fetchMoreLeftFromRestApi() {
            var xhttp = new XMLHttpRequest();
            xhttp.onreadystatechange = function () {
                var jsonResponse;
                if (this.readyState == 4 && this.status == 200) {
                    console.log('done loading photo data')
                    // response returned and is now ready

                    jsonResponse = this.responseText;
                    console.log(jsonResponse); // caution - can cause huge log output
                    displayPhotos.photos = JSON.parse(jsonResponse); // store array of URLs for current display (eventcategory / startnumber);
                    console.log("displayPhotos.photos is array ? " + Array.isArray(displayPhotos.photos));
                    setCurrentPhotoIndex()


                }
            };
            console.log('fetching JSON data from REST service for photos to the left of photo ['+photoId+']');
            xhttp.open('GET', '/api/sportsalbum/photos.html?photoId='+photoId+'&eventCategoryId='+eventCategoryId+'&startNumber=' + startNumber+'&direction=left', true);
            xhttp.send();
        }

        if (mySwiper.isBeginning) {
            if (currentPhotoIndex - 1 >= 0) {
                // there are more photos to the left
                console.log("PhotoIndex for prepending : " + currentPhotoIndex - 1);
                mySwiper.prependSlide(getPhotoSlideHTMLfromOffset(-1));
                console.log('slide added to the left');

            }
            else {
                console.log("Reached left end of the array");
                console.log("PhotoIndex - 1 === 0 ? -> currentPhotoindex : " + currentPhotoIndex); // cannot prepend slide anymore
            }

        }

        // need to fetch more photos to the left from REST API? This is independent from swiper location
        if (currentPhotoIndex - 2 === 0) {
            console.log("currentPhotoIndex - 2 === 0");
            console.log("trying to fetch more ...");
            fetchMoreLeftFromRestApi();
            console.log("adding to the left after fetching more from REST API");
        }
        changeHTMLafterSlideTransition();
        triggerDisplayPhotoEvent();

    });


    // initial call
    var eventCategoryId = "${albumBean.eventCategoryId}";
    var startNumber = "${albumBean.startNumber}";
    console.log("eventCategoryId : " + eventCategoryId);
    console.log("startNumber : " + startNumber);
    // define displayPhotos as an array - [eventCategoryId,photos] - photos = array of photo object
    var displayPhotos = {eventCategoryId: eventCategoryId, photos: undefined};
    console.log("initializing - calling photos service (eventcategoryid " + eventCategoryId + " on page)");

    initDisplayView();


    function getPhotoSlideHTMLfromOffset(photoArrayIndexOffset) {
        var photoIndex = currentPhotoIndex + photoArrayIndexOffset;
        console.log("Reading from photo index : " + photoIndex);
        var imgSrcset;
        if (dataLayer[0].eventYear >= 2018) {
            imgSrcset = 'srcset="' + displayPhotos.photos[photoIndex].displayURL1x + ' 1x,' + //  use src-set to support 2x and 3x resolution displays, but only for images taken after introdcution of image service (2018)
                displayPhotos.photos[photoIndex].displayURL2x + ' 2x,' +
                displayPhotos.photos[photoIndex].displayURL3x + ' 3x"';
        } else {
            imgSrcset = '';
        }
        // console.log("srcSet = ",imgSrcset);
        var htmlString = '<div class="swiper-slide" style="width: 250px;height: 380px">' +
            '<html:link action="/downloadPhoto?photoId=' + displayPhotos.photos[photoIndex].photoId + '" title="BILD HERUNTERLADEN - Datei wird nur als gratis Download angeboten"  onclick="highresDownloadEvent()"> ' +
            '<img ' + imgSrcset +
            'src="' + displayPhotos.photos[photoIndex].displayURL1x + '" >' +
            ' </html:link>' +
            '</div>';
        // console.log("returning htmlString : ",htmlString);
        return htmlString;
    }

    function highresDownloadEvent() {
        /* event tracking in google tag manager -- album or eventcategory ID as variable in data layer*/
        dataLayer.push({'event': 'highresDownload'});

        // _gaq.push(['_trackEvent', '${display.albumFromPhoto.event.longTitle} / ${display.albumFromPhoto.longTitle}', 'download_free_highres', 'album_ID', '${display.albumFromPhoto.genericLevelId}']);
    }


    function triggerDisplayPhotoEvent() {
        /* event tracking in google tag manager -- album or eventcategory ID as variable in data layer*/
        dataLayer.push({'event': 'displayPhoto'});
    }


    function setInitialPhotos() {
        // first set initial active photo
        console.log("Setting active photo - photoIndex for setting active photo : " + currentPhotoIndex);
        mySwiper.appendSlide(getPhotoSlideHTMLfromOffset(0));
        dataLayer.push({'event': 'displayView'});

        // then set initial left photo in case there are photos to the left
        if (currentPhotoIndex - 1 >= 0) {
            console.log("PhotoIndex for setting initial left : " + currentPhotoIndex - 1);
            mySwiper.prependSlide(getPhotoSlideHTMLfromOffset(-1));

        }

        // then set initial right photo
        if (currentPhotoIndex < displayPhotos.photos.length) {
            console.log("setting right photo - ");
            mySwiper.appendSlide(getPhotoSlideHTMLfromOffset(+1));

        }


    }


    /**
     * loop through displayPhotos.photos array and find initial photoId -> determine the index of the photo in the array
     * @returns {*}
     */
    function setCurrentPhotoIndex() {

        console.log("setCurrentPhotoIndex is called");

        console.log('current photo id : ' + photoId);

        for (var i = 0; i < displayPhotos.photos.length; i++) {
            var photoIdFromArray = Number(displayPhotos.photos[i].photoId);
            console.log('photo id from array : ' + photoIdFromArray);
            console.log('current photo id type : ' + typeof photoId);
            console.log('photoId type in Array: ' + typeof photoIdFromArray);
            if (photoIdFromArray === Number(photoId)) {
                console.log("found index for current photo - index = " + Number(i));
                currentPhotoIndex = Number(i);
                return true;
            }
        }
        // photo not defined in array - what to do? call Service?
        console.log('!!! index for current photo not found !!!');
        return false;
    }


    function initDisplayView() {
        console.log('checking for stored data available for eventcategory ID : ', eventCategoryId);
        if (sessionStorage.getItem(eventCategoryId)) { // if there is an entry with key = this event category, fill in stored JSON
            console.log('Reading from session storage ...');
            displayPhotos.photos = JSON.parse(sessionStorage.getItem(eventCategoryId));
            setCurrentPhotoIndex();
            setInitialPhotos();
            console.log('done, initial photos set');
        } else { // JSON not stored, (is this possible??? -> yes, if pre-loading in album.jsp fails or has not match!) - call REST service
            var xhttp = new XMLHttpRequest();
            xhttp.onreadystatechange = function () {
                var jsonResponse;
                if (this.readyState == 4 && this.status == 200) {
                    console.log('done loading photo data')
                    // response now ready

                    jsonResponse = this.responseText;
                    console.log(jsonResponse); // caution - can cause huge log output
                    displayPhotos.photos = JSON.parse(jsonResponse); // store array of URLs for current display (eventcategory / startnumber);
                    console.log("number of photos  : " + displayPhotos.photos.length);
                    // append(photos[0].displayURL);

                    // we have to wait to call getCurrentPhotoIndex until call has returned:
                    setCurrentPhotoIndex();
                    console.log("setting currentPhotoIndex to :" + currentPhotoIndex);
                    console.log("displayPhotos.photos is array ? " + Array.isArray(displayPhotos.photos));

                    setInitialPhotos();

                }
            };
            // add initial (!) photoId in the request here from display (displayBean)
            // add eventCategoryId and make it stateless (behind CDN - no session available)
            // add startnumber
            photoId = ${display.displayPhotoId};
            xhttp.open('GET', '/api/sportsalbum/photos.html?photoId='+photoId + '&eventCategoryId='+eventCategoryId+'&startNumber=' + startNumber, true); // todo : how is the service called and what does it return? everything??

            console.log('reading JSON data from REST service ....');
            xhttp.send();
        }
    }


</script>
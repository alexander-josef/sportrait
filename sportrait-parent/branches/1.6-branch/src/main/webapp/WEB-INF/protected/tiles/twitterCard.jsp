<jsp:useBean id="display" type="ch.unartig.studioserver.beans.DisplayBean" scope="request"/>

<meta name="twitter:card" content="summary_large_image">
<%-- todo what's that? --%>
<meta name="twitter:site" content="@sportrait">
<meta name="twitter:title" content="ASVZ SOLA L�uferfoto">
<%-- todo: insert description --%>
<meta name="twitter:description" content="Alle Fotos der ASVZ Sola-Staffette auf sportrait.com">
<%-- todo: replace by sportrait twitter account --%>
<meta name="twitter:creator" content="@sportrait">
<%-- todo: use an image - generated on the fly ? - that delivers the top third of a picture in 1200 x 675 px --%>
<%--<meta name="twitter:image" content="https://s3-eu-central-1.amazonaws.com/dev.photos.sportrait.com/web-images/215/display/sola17_e01_aj_0003.JPG">--%>
<%--<meta name="twitter:image" content="http://sportrait.imgix.net/fine-images/216/fine/sola15_e12_dn_0015.JPG?w=1000&h=500&fit=crop&crop=top%2Cleft">--%>
<meta name="twitter:image" content="http://sportrait.imgix.net/fine-images/${display.albumFromPhoto.genericLevelId}/fine/${display.displayPhoto.filename}?w=1000&h=500&fit=crop&crop=top%2Cleft">

<%@page import="java.util.ArrayList"%>
<%@page import="com.nyu.tweetmap.TLocation"%>
<%@page import="com.nyu.tweetmap.DynamoDataRetrieval"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="java.io.File"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.net.URL"%>
<%@page import="java.io.FileReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.util.List"%>
<%@page import="java.nio.file.Files"%>
<%@page import="java.nio.charset.StandardCharsets"%>
<%@page import="java.util.Arrays"%>
<%@page import="com.nyu.tweetmap.DynamoDataRetrieval.*" %>
				<%!public static String toJavascriptArray(List arr){
				    StringBuffer sb = new StringBuffer();
				    sb.append("[");
				    for(int i=0; i<arr.size(); i++){
				        sb.append("\"").append(arr.get(i)).append("\"");
				        if(i+1 < arr.size()){
				            sb.append(",");
				        }
				    }
				    sb.append("]");
				    return sb.toString();
				} %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Simple Map</title>
<meta name="viewport" content="initial-scale=1.0">
<meta charset="utf-8">
<style>
		html { height: 100% ; margin: 0px; padding: 0px}
        body { height: 100%; margin: 0px; padding: 0px }
        #map { position: absolute;  height: 100%; width: 100%; }
        #categories_map { position: absolute; top: 310px; left: 10px; z-index: 99; }
</style>
<script>
function doSomething(){
	var select = document.getElementById("tag");
	var option = select.options[select.selectedIndex];
	document.getElementById("tagValue").value = option.value;
	document.myform.submit();
}
</script>
</head>
<body>
<form name="myform" method="post" action=LoadingServlet> 	
<div id="wrapper"> 
	<div id="map"></div>
	<div id="categories_map">
        <p>Select Trending Categories !</p>
          <select id="tag" name="hashTag" onchange="if(this.selectedIndex) doSomething();">
			<option value="-1" selected>--</option>
			<option value="sport">sports</option>
			<option value="#halloween">Halloween</option>
			<option value="music">Music</option>
			<option value="game">Games</option>
			<option value="job">Jobs</option>
			<option value="worldcup">World Cup</option>
			<option value="thanksgiving">Thanks Giving</option>
			<option value="usa">USA</option>
			<option value="food">Food</option>
			<option value="restaurant">Restaurant</option>
		</select>
            <br><br>
            <input type="hidden" name="nextServlet" value="mapServlet" />	 
		<input type="hidden" name="tagSelected" id="tagValue" value="">
       </div>
</div>
	<%
		

		//        String str = "{lat: 32.885353, lng: 13.180161}#{lat: 32.885353, lng: 123.180161}";
	%>
	<script>
var map,i
var myStyle = [
               {
                 featureType: "administrative",
                 elementType: "labels",
                 stylers: [
                   { visibility: "off" }
                 ]
               }
             ];

		function initMap() {
			var locatn = {
				lat : 0,
				lng : 0
			};
			var locations =<%=request.getAttribute("locs")%>;
			console.log(locations);
			document.write("hello");
			map = new google.maps.Map(document.getElementById('map'), {
				center : locatn,
				mapTypeControl : false,
				streetViewControl : false,
				zoom : 3,
				mapTypeId : 'mystyle'
			});
			map.mapTypes.set('mystyle', new google.maps.StyledMapType(myStyle,
					{
						name : 'My Style'
					}));

			for (i = 0; i < locations.length; i++) {
				var cityCircle = new google.maps.Circle({
					strokeColor : '#FF0000',
					strokeOpacity : 0.8,
					strokeWeight : 1.0,
					fillColor : '#FF42FF',
					fillOpacity : 0.8,
					map : map,
					center : locations[i],
					radius : 10000
				});
			}

		}
	</script>
	<script
		src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCwlbP2acdR5AnfaqLlSf6MqLpxhOEMsZc&callback=initMap"
		async defer></script>
		</form>
</body>
</html>
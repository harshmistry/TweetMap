<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Tweets mapper</title>
<style>
*{
margin:0;
padding:0;
}
body{
  background:#000;
  height:100%;
  weidth:100%;
}
select{
position:relative;
z-index: 1000;
}
</style>
<script
src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false"></script>
<script>
var map;
function initialize() {
  var mapOptions = {
    zoom: 12,
   // center: new google.maps.LatLng(-34.397, 150.644)
  };
  map = new google.maps.Map(document.getElementById('map-canvas'),
      mapOptions);
}

google.maps.event.addDomListener(window, 'load', initialize);
</script>
<script>
function doSomething(){
var select = document.getElementById("tag");
var option = select.options[select.selectedIndex];
document.getElementById("tagValue").value = option.text;
document.myform.submit();
}
</script>
</head>
<body>
<form name="myform" method="post" action=LoadingServlet>  
<div id="map-canvas" style="height:300px; width:500px"></div>
		<select id="tag" name="hashTag" onchange="if(this.selectedIndex) doSomething();">
			<option value="-1" selected>--</option>
			<option value="sport">sports</option>
			<option value="#halloween">Halloween</option>
			<option value="music">Music</option>
			<option value="game">Games</option>
			<option value="job">Jobs</option>
			<option value="worldcup">World Cup</option>
			<option value="mobile">Mobile</option>
			<option value="us">USA</option>
			<option value="food">Food</option>
			<option value="restaurant">Restaurant</option>
		</select>

<input type="hidden" name="nextServlet" value="mapServlet" /> 
<input type="hidden" name="tagSelected" id="tagValue" value="">
</form>
</body>
</html>

<?php
if( isset($_GET["lineRef"]) )
	$json = file_get_contents('http://data.itsfactory.fi/siriaccess/vm/json?lineRef=' . $_GET["lineRef"]);
else
	$json = file_get_contents('http://data.itsfactory.fi/siriaccess/vm/json');

	
$obj = json_decode($json);
echo $json; 

?>
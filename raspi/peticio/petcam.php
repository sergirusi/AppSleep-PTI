<?php

$ch = curl_init();

$url = "http://52.50.79.248:8080/cam/".$argv[1];


curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type:application/json'));
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

$server_output = curl_exec($ch);
echo $server_output;

curl_close($ch);

if($server_output == "true") {
	return true;
}
else if($server_output == "false") {
	return false;
}
else {
	//echo "Error";
}

?>

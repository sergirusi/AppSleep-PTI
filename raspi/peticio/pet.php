<?php

$ch = curl_init();

$data = json_encode(array("user" => $argv[1]));

curl_setopt($ch, CURLOPT_URL, "http://52.50.79.248:8080/user_exist");
curl_setopt($ch, CURLOPT_POST, 1);
curl_setopt($ch, CURLOPT_POSTFIELDS, $data);
curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type:application/json'));

curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

$server_output = curl_exec($ch);

curl_close($ch);

if($server_output == "OK") {
	$error = 'Usuari no existeix';
	echo "L'usuari no existeix";
	throw new Exception($error);
}
else {
	echo "L'usuari existeix";
}

?>

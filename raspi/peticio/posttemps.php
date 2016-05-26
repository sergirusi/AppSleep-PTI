<?php

$ch = curl_init();

$i = ($argv[2]/1);

$data = json_encode(array("user" => $argv[1], "temps" => $i));

echo $data;

curl_setopt($ch, CURLOPT_URL, "http://52.50.79.248:8080/temps");
curl_setopt($ch, CURLOPT_POST, 1);
curl_setopt($ch, CURLOPT_POSTFIELDS, $data);
curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type:application/json'));

curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

$server_output = curl_exec($ch);

curl_close($ch);



?>
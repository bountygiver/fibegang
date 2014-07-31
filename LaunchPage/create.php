<?php

$myFile = "emails.txt";
$fh = fopen($myFile, 'a') or die("can't open file");
var_dump($_POST);
$stringData = $_POST["email"] ."\n";
fwrite($fh, $stringData);
fclose($fh);
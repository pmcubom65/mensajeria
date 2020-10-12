<?php 

if($_SERVER["REQUEST_METHOD"]=="POST"){
	require 'login.php';
	nuevousuario();
}


function nuevousuario(){
	global $connect;

	$nombre = $_POST["nombre"];
	$telefono=$_POST["telefono"];

	$query=" Insert into usuarios(nombre, telefono) values ('$nombre', '$telefono');";

	mysqli_query($connect, $query) or die (mysqli_error($connect));
	mysqli_close($connect);
}




?>
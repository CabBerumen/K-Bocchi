<?php
include 'conexion.php';
$correo=$_POST['usuario'];
$contrasena=$_POST['password'];

//correo="a19100031@ceti.mx";
//contrasena="Caleb21";

$sentencia=$conexion->prepare("SELECT * FROM usuarios WHERE correo=? AND contrasena=?");
$sentencia->bind_param('ss',$correo,$contrasena);
$sentencia->execute();

$resultado = $sentencia->get_result();
if ($fila = $resultado->fetch_assoc()) {
         echo json_encode($fila,JSON_UNESCAPED_UNICODE);     
}
$sentencia->close();
$conexion->close();


?>
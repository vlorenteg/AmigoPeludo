<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");
include "config.php";
include "utils.php";
$dbConn = connect($db);

if ($_SERVER['REQUEST_METHOD'] === 'GET' && isset($_GET['fechaCita']) && isset($_GET['idServicioFK'])) {
    $fecha = $_GET['fechaCita'];
    $idServicio = $_GET['idServicioFK'];

    $sql = $dbConn->prepare("SELECT horaCita FROM Citas WHERE fechaCita = :fecha AND idServicioFK = :servicio");
    $sql->bindValue(':fecha', $fecha);
    $sql->bindValue(':servicio', $idServicio);
    $sql->execute();

    $horasOcupadas = $sql->fetchAll(PDO::FETCH_COLUMN);
    echo json_encode(["ocupadas" => $horasOcupadas]);
    http_response_code(200);
    exit();
}

http_response_code(400);
echo json_encode(["error" => "Parámetros requeridos: fechaCita, idServicioFK"]);

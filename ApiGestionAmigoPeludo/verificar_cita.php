<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");

include "config.php";
include "utils.php";

$dbConn = connect($db);

if (
    isset($_GET['fechaCita']) &&
    isset($_GET['horaCita']) &&
    isset($_GET['idServicioFK'])
) {
    $sql = "SELECT COUNT(*) as total FROM Citas 
            WHERE fechaCita = :fecha 
            AND horaCita = :hora 
            AND idServicioFK = :servicio";

    if (isset($_GET['idCita'])) {
        $sql .= " AND idCita != :idCita";
    }

    $stmt = $dbConn->prepare($sql);
    $stmt->bindValue(':fecha', $_GET['fechaCita']);
    $stmt->bindValue(':hora', $_GET['horaCita']);
    $stmt->bindValue(':servicio', $_GET['idServicioFK']);

    if (isset($_GET['idCita'])) {
        $stmt->bindValue(':idCita', $_GET['idCita'], PDO::PARAM_INT);
    }

    $stmt->execute();
    $result = $stmt->fetch(PDO::FETCH_ASSOC);

    echo json_encode(["disponible" => ($result['total'] == 0)]);
    http_response_code(200);
    exit();
}

echo json_encode(["error" => "Faltan parámetros"]);
http_response_code(400);
exit();


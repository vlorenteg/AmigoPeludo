<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

include "config.php";
include "utils.php";
$dbConn = connect($db);

// Recoger input desde JSON, PUT form data, URL o POST
$input = json_decode(file_get_contents("php://input"), true);
if (!$input) {
    parse_str(file_get_contents("php://input"), $input);
}
if (!$input) {
    $input = $_POST;
}

if ($_SERVER['REQUEST_METHOD'] === 'GET') {

    if (isset($_GET['idServicio'])) {
        $sql = $dbConn->prepare("SELECT * FROM Servicios WHERE idServicio = :idServicio");
        $sql->bindValue(':idServicio', $_GET['idServicio'], PDO::PARAM_INT);
        $sql->execute();
        echo json_encode($sql->fetch(PDO::FETCH_ASSOC));
        http_response_code(200);
        exit();
    }

    if (isset($_GET['idProfesional'])) {
        $sql = $dbConn->prepare("SELECT * FROM Servicios WHERE idProfesionalFK = :idProfesional");
        $sql->bindValue(':idProfesional', $_GET['idProfesional'], PDO::PARAM_INT);
        $sql->execute();
        echo json_encode($sql->fetchAll(PDO::FETCH_ASSOC));
        http_response_code(200);
        exit();
    }

    elseif (isset($_GET['nombreProfesional'])) {
        $sql = $dbConn->prepare("
        SELECT S.* 
        FROM Servicios S 
        JOIN Usuarios U ON S.idProfesionalFK = U.idUsuario 
        WHERE U.nombreUsuario = :nombre
    ");
    $sql->bindValue(':nombre', $_GET['nombreProfesional']);
    $sql->execute();
    echo json_encode($sql->fetchAll(PDO::FETCH_ASSOC));
    http_response_code(200);
    exit();
}


    // Por defecto: todos los servicios
    $sql = $dbConn->prepare("SELECT * FROM Servicios");
    $sql->execute();
    echo json_encode($sql->fetchAll(PDO::FETCH_ASSOC));
    http_response_code(200);
    exit();
}


// ✅ POST — CREAR
elseif ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (
        isset($input['nombreServicio']) &&
        isset($input['descripcionServicio']) &&
        isset($input['importeServicio']) &&
        isset($input['idProfesionalFK'])
    ) {
        $sql = "INSERT INTO Servicios (nombreServicio, descripcionServicio, importeServicio, idProfesionalFK)
                VALUES (:nombre, :descripcion, :importe, :idProfesional)";
        $stmt = $dbConn->prepare($sql);
        $stmt->bindValue(':nombre', $input['nombreServicio']);
        $stmt->bindValue(':descripcion', $input['descripcionServicio']);
        $stmt->bindValue(':importe', $input['importeServicio']);
        $stmt->bindValue(':idProfesional', $input['idProfesionalFK']);

        if ($stmt->execute()) {
            $input['idServicio'] = $dbConn->lastInsertId();
            echo json_encode(["success" => true, "servicio" => $input]);
            http_response_code(201);
        } else {
            echo json_encode(["success" => false, "error" => "No se pudo crear."]);
            http_response_code(500);
        }
        exit();
    } else {
        echo json_encode(["success" => false, "error" => "Faltan datos."]);
        http_response_code(400);
        exit();
    }
}

// ✅ PUT — MODIFICAR
elseif ($_SERVER['REQUEST_METHOD'] === 'PUT') {
    // Aceptar parámetros desde GET si $input viene vacío
    if (!$input) {
        $input = $_GET;
    }

    if (
        isset($input['idServicio']) &&
        isset($input['nombreServicio']) &&
        isset($input['descripcionServicio']) &&
        isset($input['importeServicio'])
    ) {
        $sql = "UPDATE Servicios
                SET nombreServicio = :nombre,
                    descripcionServicio = :descripcion,
                    importeServicio = :importe
                WHERE idServicio = :id";
        $stmt = $dbConn->prepare($sql);
        $stmt->bindValue(':nombre', $input['nombreServicio']);
        $stmt->bindValue(':descripcion', $input['descripcionServicio']);
        $stmt->bindValue(':importe', $input['importeServicio']);
        $stmt->bindValue(':id', $input['idServicio'], PDO::PARAM_INT);

        if ($stmt->execute()) {
            echo json_encode(["success" => true]);
            http_response_code(200);
        } else {
            echo json_encode(["success" => false, "error" => "Error al actualizar."]);
            http_response_code(500);
        }
        exit();
    } else {
        echo json_encode(["success" => false, "error" => "Faltan datos."]);
        http_response_code(400);
        exit();
    }
}

// ✅ DELETE — ELIMINAR
elseif ($_SERVER['REQUEST_METHOD'] === 'DELETE') {
    // Aceptar ID desde la URL o cuerpo
    $id = $_GET['idServicio'] ?? ($input['idServicio'] ?? null);

    if ($id) {
        $sql = $dbConn->prepare("DELETE FROM Servicios WHERE idServicio = :id");
        $sql->bindValue(':id', $id, PDO::PARAM_INT);

        if ($sql->execute()) {
            echo json_encode(["success" => true]);
            http_response_code(200);
        } else {
            echo json_encode(["success" => false, "error" => "Error al eliminar."]);
            http_response_code(500);
        }
        exit();
    } else {
        echo json_encode(["success" => false, "error" => "Falta ID."]);
        http_response_code(400);
        exit();
    }
}

// ❌ Método no permitido
http_response_code(405);
echo json_encode(["success" => false, "error" => "Método no permitido"]);
exit();





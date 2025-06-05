<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

include "config.php";
include "utils.php";
$dbConn = connect($db);

// Leer datos del cuerpo (PUT y POST pueden venir como JSON o como form-urlencoded)
$input = json_decode(file_get_contents("php://input"), true);
if (!$input) {
    parse_str(file_get_contents("php://input"), $input);
}
if (!$input) {
    $input = $_POST;
}

if ($_SERVER['REQUEST_METHOD'] === 'GET') {

    if (isset($_GET['tipo']) && $_GET['tipo'] === 'profesional') {
        $sql = $dbConn->prepare("SELECT idUsuario, nombreUsuario FROM Usuarios WHERE tipoUsuario = 'profesional'");
        $sql->execute();
        echo json_encode($sql->fetchAll(PDO::FETCH_ASSOC));
        http_response_code(200);
        exit();
    }

    if (isset($_GET['idCita'])) {
        $sql = $dbConn->prepare("
            SELECT C.*, S.nombreServicio, U.nombreUsuario AS nombreProfesional
            FROM Citas C
            JOIN Servicios S ON C.idServicioFK = S.idServicio
            JOIN Usuarios U ON S.idProfesionalFK = U.idUsuario
            WHERE C.idCita = :idCita
        ");
        $sql->bindValue(':idCita', $_GET['idCita'], PDO::PARAM_INT);
        $sql->execute();
        echo json_encode($sql->fetch(PDO::FETCH_ASSOC));
        http_response_code(200);
        exit();
    }

    if (isset($_GET['idCliente'])) {
        $sql = $dbConn->prepare("
            SELECT C.*, S.nombreServicio, U.nombreUsuario AS nombreProfesional
            FROM Citas C
            JOIN Servicios S ON C.idServicioFK = S.idServicio
            JOIN Usuarios U ON S.idProfesionalFK = U.idUsuario
            WHERE C.idClienteFK = :idCliente
        ");
        $sql->bindValue(':idCliente', $_GET['idCliente'], PDO::PARAM_INT);
        $sql->execute();
        echo json_encode($sql->fetchAll(PDO::FETCH_ASSOC));
        http_response_code(200);
        exit();
    }

    if (isset($_GET['idProfesional'])) {
        $sql = $dbConn->prepare("
            SELECT C.*, U.nombreUsuario AS nombreCliente, S.nombreServicio
            FROM Citas C
            JOIN Servicios S ON C.idServicioFK = S.idServicio
            JOIN Usuarios U ON C.idClienteFK = U.idUsuario
            WHERE S.idProfesionalFK = :idProfesional
        ");
        $sql->bindValue(':idProfesional', $_GET['idProfesional'], PDO::PARAM_INT);
        $sql->execute();
        echo json_encode($sql->fetchAll(PDO::FETCH_ASSOC));
        http_response_code(200);
        exit();
    }

    // Por defecto: obtener todas las citas
    $sql = $dbConn->prepare("SELECT * FROM Citas");
    $sql->execute();
    echo json_encode($sql->fetchAll(PDO::FETCH_ASSOC));
    http_response_code(200);
    exit();
}

// MÉTODO POST — CREAR CITA
elseif ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (
        isset($input['fechaCita']) &&
        isset($input['horaCita']) && // nuevo campo
        isset($input['estadoCita']) &&
        isset($input['idClienteFK']) &&
        isset($input['idServicioFK'])
    ) {
        $sql = "INSERT INTO Citas (fechaCita, horaCita, estadoCita, idClienteFK, idServicioFK)
                VALUES (:fecha, :hora, :estado, :idCliente, :idServicio)";
        $stmt = $dbConn->prepare($sql);
        $stmt->bindValue(':fecha', $input['fechaCita']);
        $stmt->bindValue(':hora', $input['horaCita']); // nuevo
        $stmt->bindValue(':estado', $input['estadoCita']);
        $stmt->bindValue(':idCliente', $input['idClienteFK'], PDO::PARAM_INT);
        $stmt->bindValue(':idServicio', $input['idServicioFK'], PDO::PARAM_INT);

        if ($stmt->execute()) {
            $input['idCita'] = $dbConn->lastInsertId();
            echo json_encode(["success" => true, "cita" => $input]);
            http_response_code(201);
            exit();
        } else {
            echo json_encode(["success" => false, "error" => "No se pudo insertar la cita"]);
            http_response_code(500);
            exit();
        }
    } else {
        echo json_encode(["success" => false, "error" => "Datos incompletos"]);
        http_response_code(400);
        exit();
    }
}

// MÉTODO PUT — MODIFICAR CITA
elseif ($_SERVER['REQUEST_METHOD'] === 'PUT') {
    if (!$input) {
        $input = $_GET;
    }

    if (
        isset($input['idCita']) &&
        isset($input['fechaCita']) &&
        isset($input['horaCita']) && // nuevo campo
        isset($input['estadoCita']) &&
        isset($input['idClienteFK']) &&
        isset($input['idServicioFK'])
    ) {
        $sql = "UPDATE Citas 
                SET fechaCita = :fecha, 
                    horaCita = :hora, 
                    estadoCita = :estado, 
                    idClienteFK = :idCliente, 
                    idServicioFK = :idServicio
                WHERE idCita = :id";
        $stmt = $dbConn->prepare($sql);
        $stmt->bindValue(':fecha', $input['fechaCita']);
        $stmt->bindValue(':hora', $input['horaCita']); // nuevo
        $stmt->bindValue(':estado', $input['estadoCita']);
        $stmt->bindValue(':idCliente', $input['idClienteFK'], PDO::PARAM_INT);
        $stmt->bindValue(':idServicio', $input['idServicioFK'], PDO::PARAM_INT);
        $stmt->bindValue(':id', $input['idCita'], PDO::PARAM_INT);

        if ($stmt->execute()) {
            echo json_encode(["success" => true]);
            http_response_code(200);
            exit();
        } else {
            echo json_encode(["success" => false, "error" => "Error al actualizar cita"]);
            http_response_code(500);
            exit();
        }
    } else {
        echo json_encode(["success" => false, "error" => "Datos incompletos para actualizar"]);
        http_response_code(400);
        exit();
    }
}

// MÉTODO DELETE — ELIMINAR
elseif ($_SERVER['REQUEST_METHOD'] === 'DELETE') {
    $id = $_GET['idCita'] ?? ($input['idCita'] ?? null);

    if ($id) {
        $sql = "DELETE FROM Citas WHERE idCita=:id";
        $stmt = $dbConn->prepare($sql);
        $stmt->bindValue(':id', $id, PDO::PARAM_INT);

        if ($stmt->execute()) {
            echo json_encode(["success" => true]);
            http_response_code(200);
        } else {
            echo json_encode(["success" => false, "error" => "Error al eliminar cita"]);
            http_response_code(500);
        }
        exit();
    } else {
        echo json_encode(["success" => false, "error" => "Falta el ID de la cita"]);
        http_response_code(400);
        exit();
    }
}

// Si ningún método coincide
http_response_code(405);
echo json_encode(["success" => false, "error" => "Método no permitido"]);
exit();
?>





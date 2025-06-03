<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

include "config.php";
include "utils.php";
$dbConn = connect($db);

// Leer entrada JSON o form
$input = json_decode(file_get_contents("php://input"), true);
if (!$input) {
    $input = $_POST;
}

// ✅ MÉTODO GET
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    if (isset($_GET['idUsuario'])) {
        $sql = $dbConn->prepare("SELECT * FROM Usuarios WHERE idUsuario = :idUsuario");
        $sql->bindValue(':idUsuario', $_GET['idUsuario'], PDO::PARAM_INT);
        $sql->execute();
        header("HTTP/1.1 200 OK");
        echo json_encode($sql->fetch(PDO::FETCH_ASSOC));
        exit();
    }

    // ✅ Profesionales con sus servicios formateados
    elseif (isset($_GET['tipo']) && $_GET['tipo'] === 'profesional') {
        $sql = $dbConn->prepare("
            SELECT 
                U.idUsuario, 
                U.nombreUsuario, 
                U.telefonoUsuario, 
                U.emailUsuario,
                GROUP_CONCAT(CONCAT(S.nombreServicio, ' - ', S.importeServicio, '€') SEPARATOR '\n') AS servicios
            FROM Usuarios U
            LEFT JOIN Servicios S ON U.idUsuario = S.idProfesionalFK
            WHERE U.tipoUsuario = 'profesional'
            GROUP BY U.idUsuario
        ");
        $sql->execute();
        header("HTTP/1.1 200 OK");
        echo json_encode($sql->fetchAll(PDO::FETCH_ASSOC));
        exit();
    }

    // Todos los usuarios
    else {
        $sql = $dbConn->prepare("SELECT * FROM Usuarios");
        $sql->execute();
        header("HTTP/1.1 200 OK");
        echo json_encode($sql->fetchAll(PDO::FETCH_ASSOC));
        exit();
    }
}

// ✅ POST — Crear usuario
elseif ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (
        isset($input['nombreUsuario']) &&
        isset($input['telefonoUsuario']) &&
        isset($input['emailUsuario']) &&
        isset($input['contraseñaUsuario']) &&
        isset($input['tipoUsuario'])
    ) {
        $sql = "INSERT INTO Usuarios (nombreUsuario, telefonoUsuario, emailUsuario, contraseñaUsuario, tipoUsuario)
                VALUES (:nombre, :telefono, :email, :pass, :tipo)";
        $stmt = $dbConn->prepare($sql);
        $stmt->bindValue(':nombre', $input['nombreUsuario']);
        $stmt->bindValue(':telefono', $input['telefonoUsuario']);
        $stmt->bindValue(':email', $input['emailUsuario']);
        $stmt->bindValue(':pass', $input['contraseñaUsuario']);
        $stmt->bindValue(':tipo', $input['tipoUsuario']);

        $stmt->execute();
        $postId = $dbConn->lastInsertId();

        echo json_encode(["success" => true, "idUsuario" => $postId]);
        header("HTTP/1.1 201 Created");
        exit();
    } else {
        echo json_encode(["success" => false, "error" => "Faltan campos."]);
        header("HTTP/1.1 400 Bad Request");
        exit();
    }
}

// ✅ PUT — Actualizar usuario
elseif ($_SERVER['REQUEST_METHOD'] === 'PUT') {
    if (isset($input['idUsuario'])) {
        $sql = "UPDATE Usuarios 
                SET nombreUsuario=:nombre, telefonoUsuario=:telefono, emailUsuario=:email, 
                    contraseñaUsuario=:pass, tipoUsuario=:tipo
                WHERE idUsuario=:id";
        $stmt = $dbConn->prepare($sql);
        $stmt->bindValue(':nombre', $input['nombreUsuario']);
        $stmt->bindValue(':telefono', $input['telefonoUsuario']);
        $stmt->bindValue(':email', $input['emailUsuario']);
        $stmt->bindValue(':pass', $input['contraseñaUsuario']);
        $stmt->bindValue(':tipo', $input['tipoUsuario']);
        $stmt->bindValue(':id', $input['idUsuario']);

        $stmt->execute();
        echo json_encode(["success" => true]);
        header("HTTP/1.1 200 OK");
        exit();
    } else {
        echo json_encode(["success" => false, "error" => "Falta idUsuario"]);
        header("HTTP/1.1 400 Bad Request");
        exit();
    }
}

// ✅ DELETE — Eliminar usuario
elseif ($_SERVER['REQUEST_METHOD'] === 'DELETE') {
    if (isset($input['idUsuario'])) {
        $sql = $dbConn->prepare("DELETE FROM Usuarios WHERE idUsuario = :id");
        $sql->bindValue(':id', $input['idUsuario']);
        $sql->execute();
        echo json_encode(["success" => true]);
        header("HTTP/1.1 200 OK");
        exit();
    } else {
        echo json_encode(["success" => false, "error" => "Falta idUsuario"]);
        header("HTTP/1.1 400 Bad Request");
        exit();
    }
}

// ❌ Método no permitido
header("HTTP/1.1 405 Method Not Allowed");
echo json_encode(["success" => false, "error" => "Método no permitido"]);
exit();
?>




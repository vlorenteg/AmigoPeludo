-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: gestionamigopeludo
-- ------------------------------------------------------
-- Server version	8.0.36

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `citas`
--

DROP TABLE IF EXISTS `citas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `citas` (
  `idCita` int NOT NULL AUTO_INCREMENT,
  `fechaCita` date NOT NULL,
  `horaCita` time NOT NULL,
  `estadoCita` enum('pendiente','cancelada','finalizada') NOT NULL,
  `idClienteFK` int NOT NULL,
  `idServicioFK` int NOT NULL,
  PRIMARY KEY (`idCita`),
  KEY `idClienteFK` (`idClienteFK`),
  KEY `idServicioFK` (`idServicioFK`),
  CONSTRAINT `citas_ibfk_1` FOREIGN KEY (`idClienteFK`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `citas_ibfk_2` FOREIGN KEY (`idServicioFK`) REFERENCES `servicios` (`idServicio`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `citas`
--

LOCK TABLES `citas` WRITE;
/*!40000 ALTER TABLE `citas` DISABLE KEYS */;
INSERT INTO `citas` VALUES (13,'2025-05-25','14:00:00','finalizada',10,9),(14,'2025-05-29','10:00:00','finalizada',10,9),(15,'2025-05-28','19:00:00','finalizada',10,15),(16,'2025-05-29','16:00:00','finalizada',10,18),(17,'2025-05-29','19:00:00','finalizada',10,21);
/*!40000 ALTER TABLE `citas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `servicios`
--

DROP TABLE IF EXISTS `servicios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `servicios` (
  `idServicio` int NOT NULL AUTO_INCREMENT,
  `nombreServicio` varchar(100) NOT NULL,
  `descripcionServicio` text,
  `importeServicio` decimal(10,2) NOT NULL,
  `idProfesionalFK` int NOT NULL,
  PRIMARY KEY (`idServicio`),
  KEY `idProfesionalFK` (`idProfesionalFK`),
  CONSTRAINT `servicios_ibfk_1` FOREIGN KEY (`idProfesionalFK`) REFERENCES `usuarios` (`idUsuario`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `servicios`
--

LOCK TABLES `servicios` WRITE;
/*!40000 ALTER TABLE `servicios` DISABLE KEYS */;
INSERT INTO `servicios` VALUES (8,'Baño y cepillado','Limpieza profunda y cepillado del pelaje',25.00,5),(9,'Guardería diurna','Cuidado por 8 horas con juegos y paseos',40.00,5),(10,'Corte de uñas','Corte y limpieza suave de uñas',10.00,5),(11,'Consulta veterinaria general','Revisión médica completa',30.00,6),(12,'Vacunación','Aplicación de vacunas según plan',20.00,6),(13,'Adiestramiento básico','Sesiones de obediencia y conducta',50.00,6),(14,'Spa relajante','Baño con aromaterapia y masaje',60.00,7),(15,'Tratamiento antipulgas','Aplicación especializada',35.00,7),(16,'Perfume natural','Fragancia ligera para mascotas',8.00,7),(17,'Chequeo geriátrico','Evaluación especializada para mascotas mayores',45.00,8),(18,'Desparasitación interna','Tratamiento completo',25.00,8),(19,'Emergencias 24/7','Atención veterinaria urgente',100.00,8),(20,'Paseos diarios','Caminata de 45 minutos',15.00,9),(21,'Entrenamiento deportivo','Agilidad, salto y resistencia',70.00,9),(22,'Transporte a domicilio','Recolección y entrega segura',20.00,9);
/*!40000 ALTER TABLE `servicios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `idUsuario` int NOT NULL AUTO_INCREMENT,
  `nombreUsuario` varchar(100) NOT NULL,
  `telefonoUsuario` varchar(15) DEFAULT NULL,
  `emailUsuario` varchar(100) NOT NULL,
  `contraseñaUsuario` varchar(255) NOT NULL,
  `tipoUsuario` enum('Cliente','Profesional') NOT NULL,
  PRIMARY KEY (`idUsuario`),
  UNIQUE KEY `emailUsuario` (`emailUsuario`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (5,'Patitas Felices','632547856','contacto@patitasfelices.com','patitasfelices123','Profesional'),(6,'Mundo Peludo','666333214','info@mundopeludo.com','mundopeludo123','Profesional'),(7,'PetSpa Delux','699654254','reservas@petspadelux.com','petspa123','Profesional'),(8,'Amigo Fiel Vet','622114523','citas@amigofielvet.com','amigofiel123','Profesional'),(9,'Colitas en Movimiento','630058210','contacto@colitasmov.com','colitasmov123','Profesional'),(10,'Javier Perez Martin','658741147','javier.perez@gmail.com','javierperez123','Cliente'),(11,'Manuel Dominguez Jurado','687549965','manuel.dominguez@gmail.com','manuel123','Cliente'),(12,'Ana Rodriguez Gutierrez','622352211','anarodriguez@gmail.com','anarod123','Cliente'),(13,'Pilar Herrera Garcia','668745639','pilarherrera@gmail.com','pilarherrera123','Cliente'),(14,'Pablo Jimenez Reyes','633201542','pablojimenez@gmail.com','pablojimenez123','Cliente');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-03 18:26:34

# AmigoPeludo

Aplicación móvil desarrollada como **Proyecto Final de DAM** para la gestión de servicios y citas de cuidado de mascotas.

La aplicación conecta a **clientes** que buscan servicios para sus mascotas con **proveedores** que ofrecen esos servicios, permitiendo gestionar usuarios, servicios y citas mediante una app Android conectada a una API y base de datos.

---

## Tecnologías utilizadas

### Aplicación móvil
- Java
- Android Studio

### Backend / API
- PHP

### Base de datos
- MySQL

### Arquitectura
- App Android como cliente
- API REST en PHP para la lógica de comunicación
- Base de datos MySQL para persistencia de datos

---

## Funcionalidades principales

- Registro de usuarios
- Inicio de sesión
- Gestión de roles de usuario:
  - **Cliente**
  - **Proveedor**
- Interfaces y funcionalidades diferentes según el tipo de usuario
- Gestión de servicios ofrecidos por proveedores
- Gestión de citas entre clientes y proveedores
- Operaciones **CRUD completas** conectadas a base de datos mediante API

---

## Tipos de usuario

### Cliente
El usuario cliente puede:

- Registrarse e iniciar sesión
- Consultar proveedores disponibles
- Solicitar y gestionar citas
- Visualizar sus citas y datos asociados

### Proveedor
El usuario proveedor puede:

- Registrarse e iniciar sesión
- Crear, editar y eliminar servicios
- Gestionar citas con diferentes clientes
- Visualizar la información relacionada con sus servicios y reservas

---

## Estructura general del proyecto

El proyecto está dividido en tres partes principales:

### 1. Aplicación Android
Interfaz móvil desarrollada en Java desde Android Studio, encargada de:

- mostrar pantallas y vistas
- gestionar la experiencia del usuario
- enviar peticiones a la API
- mostrar datos recuperados desde la base de datos

### 2. API en PHP
Encargada de:

- recibir peticiones desde la app
- procesar operaciones de negocio
- conectar con la base de datos
- devolver respuestas a la aplicación

### 3. Base de datos MySQL
Encargada de almacenar:

- usuarios
- roles
- servicios
- citas

---

## Funcionalidades técnicas implementadas

- Autenticación de usuarios
- Diferenciación de permisos y vistas según rol
- Conexión entre app y base de datos a través de API
- Inserción, consulta, actualización y eliminación de datos
- Gestión de relaciones entre clientes, proveedores, servicios y citas

---

## Objetivo del proyecto

El objetivo de **AmigoPeludo** es ofrecer una solución sencilla para conectar usuarios que necesitan ayuda en el cuidado de sus mascotas con personas que ofrecen ese servicio, facilitando la gestión de citas y servicios desde una aplicación móvil.

Además, el proyecto sirve como demostración práctica de conocimientos en:

- desarrollo de aplicaciones móviles
- consumo de APIs
- diseño y gestión de bases de datos
- desarrollo backend
- lógica de negocio según roles de usuario

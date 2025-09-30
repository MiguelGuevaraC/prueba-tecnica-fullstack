# 📝 Prueba Técnica Full Stack - Productos y Categorías

Este proyecto implementa un sistema de gestión de **Productos, Categorías y Usuarios**.  
Fue desarrollado como parte de la **Prueba Técnica Full Stack Junior**, utilizando **Spring Boot (Backend)**, **Angular (Frontend)** y **MySQL/PostgreSQL (Base de Datos)**.  

---

## 🚀 Tecnologías Utilizadas
- **Backend:** Java 11, Spring Boot, Spring Data JPA, DTOs, JUnit/Mockito  
- **Frontend:** Angular 15, RxJS, Routing, Formularios reactivos, Bootstrap  
- **Base de Datos:** PostgreSQL

---

## 📌 Funcionalidades Implementadas

### 🔹 Backend
- CRUD de productos  
- CRUD de categorías  
- CRUD de usuarios (con login básico)  
- Búsqueda de productos por nombre, precio y categoría (filtros combinables)  
- Validación de datos y manejo de errores HTTP  
- Conexión a BD con JPA/Hibernate  

### 🔹 Frontend
- Pantalla de **login**  
- Lista de productos con **filtros dinámicos**  
- Formulario para **crear/editar productos** con selector de categorías  
- Vista de detalle del producto  
- Alertas de éxito/error  
- Navegación entre vistas (`/login`, `/products`, `/categories`)  

### 🔹 Base de Datos
- **productos** (id, nombre, descripción, precio, stock, categoria_id)  
- **categorias** (id, nombre, descripción)  
- **usuarios** (id, nombre, usuario, clave)  
- Relación: una categoría tiene muchos productos  

---

## ⚙️ Cómo Ejecutar

### Backend
```bash
cd backend
./mvnw spring-boot:run

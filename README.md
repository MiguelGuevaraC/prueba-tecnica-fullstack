# Prueba Técnica Full Stack — Productos, Categorías y Usuarios

> **Desarrollado end-to-end (Full Stack) por _Miguel Guevara_.**

Sistema de gestión con **Spring Boot + Angular + PostgreSQL** que incluye autenticación con roles, interceptores/guards en el front y CRUD de **usuarios**, **categorías** y **productos**.

---

## Tecnologías Implementadas
- Backend: Java 11, Spring Boot, Spring Data JPA, DTO Pattern, JUnit/Mockito, Spring Security (JWT)
- Frontend: Angular 15+, RxJS, Routing, Formularios Reactivos, Guards, Interceptor HTTP, Bootstrap
- Base de Datos: PostgreSQL + PgAdmin
- Extras: Swagger/OpenAPI (documentación), Docker Compose

---

## Arranque Rápido con Docker
```bash
docker compose up -d
```

**Semilla de datos (con backend arriba en :8080):**
```http
POST http://localhost:8080/api/seed?force=true
```

**Usuarios demo:**
- ADMIN → `admin` / `admin123`
- USER  → `usuario` / `user123`

---

## Frontend
```bash
cd frontend
npm install
ng serve -o   # http://localhost:4200
```
Incluye: login, guards, interceptor, sidebar y CRUD de usuarios, categorías y productos.

---

## Backend
```bash
cd backend
./mvn clean install
./mvn spring-boot:run   # http://localhost:8080
```

**Configuración (`application.properties`):**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/prueba_tecnica
spring.datasource.username=admin
spring.datasource.password=admin123
spring.jpa.hibernate.ddl-auto=update
```

**Swagger (si está activo):**
- UI: http://localhost:8080/swagger-ui/index.html  
- Docs: http://localhost:8080/v3/api-docs  

---

## Endpoints Clave
- Auth: `POST /api/auth/login`
- Seed: `GET /api/seed?force=true`
- Categorías: `GET/POST/PUT/DELETE /api/categories`
- Productos: `GET/POST/PUT/DELETE /api/products`
- Usuarios: `GET/POST/PUT/DELETE /api/users`

---

## Arquitectura (Backend)
- Controller → REST API  
- Service → Lógica de negocio  
- Repository/DAO → Persistencia con JPA  
- DTO → Entrada/Salida de datos  
- Manejo de errores HTTP (200, 400/401/403/404/500).  

---

## Resumen
1. Levantar la base de datos: `docker compose up -d`  
2. Ejecutar backend: `./mvn spring-boot:run`  
3. Sembrar datos: `POST /api/seed?force=true`  
4. Ejecutar frontend: `ng serve -o`  
5. Login: `admin/admin123` o `usuario/user123`  

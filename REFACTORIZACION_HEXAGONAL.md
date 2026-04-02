# Refactorización: Reordenar Controllers por Módulo

## Objetivo

Eliminar la carpeta `api/` y mover los controladores a sus respectivos módulos en `infrastructure/web/`.

---

## Controladores a mover

| # | Origen | Destino |
|---|--------|---------|
| 1 | `api/publico/ReservaController.java` | `core/reserva/infrastructure/web/ReservaPublicController.java` |
| 2 | `api/admin/AdminReservaController.java` | `core/reserva/infrastructure/web/ReservaAdminController.java` |
| 3 | `api/publico/HotelController.java` | `core/hotel/infrastructure/web/HotelPublicController.java` |
| 4 | `api/admin/AdminHotelController.java` | `core/hotel/infrastructure/web/HotelAdminController.java` |
| 5 | `api/publico/HabitacionConsultaController.java` | `core/habitacion/infrastructure/web/HabitacionController.java` |
| 6 | `api/publico/DepartamentoController.java` | `core/departamento/infrastructure/web/DepartamentoPublicController.java` |
| 7 | `api/admin/AdminDepartamentoController.java` | `core/departamento/infrastructure/web/DepartamentoAdminController.java` |
| 8 | `api/admin/AdminDashboardController.java` | `core/dashboard/infrastructure/web/DashboardController.java` |
| 9 | `api/publico/ContactoController.java` | `shared/infraestructure/web/ContactoController.java` |

---

## Pasos a seguir

### 1. Crear carpetas `infrastructure/web` por módulo

```bash
mkdir -p src/main/java/com/osen/sistema_reservas/core/reserva/infrastructure/web
mkdir -p src/main/java/com/osen/sistema_reservas/core/hotel/infrastructure/web
mkdir -p src/main/java/com/osen/sistema_reservas/core/habitacion/infrastructure/web
mkdir -p src/main/java/com/osen/sistema_reservas/core/departamento/infrastructure/web
mkdir -p src/main/java/com/osen/sistema_reservas/core/dashboard/infrastructure/web
mkdir -p src/main/java/com/osen/sistema_reservas/shared/infraestructure/web
```

### 2. Copiar y renombrar cada controlador

Para cada archivo:
1. Copiar el contenido
2. Cambiar el package:
   ```java
   // Antes
   package com.osen.sistema_reservas.api.publico;
   
   // Después (ejemplo reserva público)
   package com.osen.sistema_reservas.core.reserva.infrastructure.web;
   ```
3. Guardar en la nueva ubicación

### 3. Completar MercadoPagoController

`core/payment/infrastructure/web/MercadoPagoController.java` está vacío - completar según necesidades.

### 4. Eliminar carpeta `api/`

```bash
rm -rf src/main/java/com/osen/sistema_reservas/api
```

### 5. Verificar compilación

```bash
./mvnw clean compile
```

---

## Estructura final

```
src/main/java/com/osen/sistema_reservas/
├── auth/infraestructure/web/AuthController.java  (ya existe)
├── core/
│   ├── reserva/infrastructure/web/
│   │   ├── ReservaPublicController.java
│   │   └── ReservaAdminController.java
│   ├── hotel/infrastructure/web/
│   │   ├── HotelPublicController.java
│   │   └── HotelAdminController.java
│   ├── habitacion/infrastructure/web/
│   │   └── HabitacionController.java
│   ├── tipoHabitacion/infrastructure/web/
│   ├── departamento/infrastructure/web/
│   │   ├── DepartamentoPublicController.java
│   │   └── DepartamentoAdminController.java
│   ├── dashboard/infrastructure/web/
│   │   └── DashboardController.java
│   └── payment/infrastructure/web/
│       └── MercadoPagoController.java
└── shared/infraestructure/web/
    └── ContactoController.java
```

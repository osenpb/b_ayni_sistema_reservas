-- =====================================================
-- SCRIPT DE MIGRACIÓN: Fusionar Cliente en User
-- =====================================================
-- Este script migrará los datos existentes de la tabla
-- 'cliente' a los nuevos campos en 'users' y luego
-- actualizará la tabla 'reserva' para usar user_id.
--
-- INSTRUCCIONES:
-- 1. Hacer backup de la base de datos antes de ejecutar
-- 2. Ejecutar en orden
-- 3. Verificar los resultados al final
-- =====================================================

-- =====================================================
-- PASO 1: Agregar columnas a users (si no existen)
-- =====================================================
-- Hibernate creará automáticamente las columnas si usa
-- spring.jpa.hibernate.ddl-auto=update
-- Pero si usas validate o none, ejecuta manualmente:

-- ALTER TABLE users ADD COLUMN nombre VARCHAR(255) NOT NULL DEFAULT '';
-- ALTER TABLE users ADD COLUMN apellido VARCHAR(255);
-- ALTER TABLE users ADD COLUMN dni VARCHAR(20) UNIQUE;

-- =====================================================
-- PASO 2: Migrar datos de cliente a users
-- =====================================================
-- Para usuarios que ya tienen un cliente asociado

UPDATE users u
INNER JOIN cliente c ON c.user_id = u.id
SET 
    u.nombre = COALESCE(c.nombre, 'Usuario'),
    u.apellido = COALESCE(c.apellido, ''),
    u.dni = COALESCE(c.dni, CONCAT('DNI', u.id))
WHERE u.nombre IS NULL OR u.nombre = '';

-- Para usuarios que NO tienen cliente pero tienen email
-- Actualizar con datos del email o valores por defecto
UPDATE users u
SET 
    u.nombre = COALESCE(u.nombre, 'Usuario'),
    u.apellido = COALESCE(u.apellido, ''),
    u.dni = COALESCE(u.dni, CONCAT('DNI', u.id))
WHERE u.nombre IS NULL OR u.nombre = '';

-- =====================================================
-- PASO 3: Agregar columna user_id a reserva (si no existe)
-- =====================================================
-- ALTER TABLE reserva ADD COLUMN user_id BIGINT;

-- =====================================================
-- PASO 4: Migrar reservas existentes
-- =====================================================
-- Migrar user_id desde cliente a reserva

UPDATE reserva r
INNER JOIN cliente c ON c.id = r.cliente_id
SET r.user_id = c.user_id
WHERE r.user_id IS NULL AND c.user_id IS NOT NULL;

-- Para reservas sin cliente o cliente sin user_id,
-- asignar al admin (user_id = 1) o crear un usuario "Sistema"
UPDATE reserva r
SET r.user_id = 1
WHERE r.user_id IS NULL;

-- =====================================================
-- PASO 5: Hacer user_id NOT NULL (después de migrar)
-- =====================================================
-- ALTER TABLE reserva MODIFY COLUMN user_id BIGINT NOT NULL;

-- =====================================================
-- PASO 6: Eliminar columna cliente_id de reserva
-- =====================================================
-- ALTER TABLE reserva DROP COLUMN cliente_id;

-- =====================================================
-- PASO 7: Eliminar tabla cliente (después de verificar)
-- =====================================================
-- DROP TABLE cliente;

-- =====================================================
-- VERIFICACIÓN POST-MIGRACIÓN
-- =====================================================

-- Verificar usuarios con datos migrados
-- SELECT id, username, email, nombre, apellido, dni FROM users LIMIT 10;

-- Verificar reservas con user_id
-- SELECT id, user_id FROM reserva WHERE user_id IS NOT NULL LIMIT 10;

-- Contar registros
-- SELECT COUNT(*) as total_users FROM users WHERE nombre IS NOT NULL AND nombre != '';
-- SELECT COUNT(*) as reservas_con_user FROM reserva WHERE user_id IS NOT NULL;
-- SELECT COUNT(*) as reservas_sin_user FROM reserva WHERE user_id IS NULL;

-- =====================================================
-- ROLLBACK (si algo sale mal)
-- =====================================================
-- Restaurar desde backup:
-- mysql -u root -p dawiapp < backup_pre_migracion.sql

-- =====================================================
-- NOTAS FINALES
-- =====================================================
-- 1. Después de ejecutar este script, puedes eliminar
--    los INSERT de cliente en data.sql (ya no existen)
-- 2. Asegúrate de que el frontend actualice el registro
--    para incluir nombre, apellido, dni
-- 3. Actualizar las interfaces TypeScript del frontend

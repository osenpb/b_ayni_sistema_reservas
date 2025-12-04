---- =============================================
---- OSWALDO, ANTES DE REGISTRARTE INSERTA AL ADMIN, Y
----- Y TODO SCRIPT QUE TENES ACA, LA CLAVE DEL ADMIN HA ESTA HARCODEADA
----- TAL VEZ DUERMA HASTA TARDE XD NO TE RINDAS HERMNO
---- =============================================

-- Admin (password: admin123)
--INSERT INTO users (id, username, email, password, role_id, telefono, activo, fecha_creacion)
--VALUES (1, 'Administrador', 'admin@hoteles.com',
--'$2a$12$rEQanJSFNeDlUU1yhbUNcuuMvgJuB87TiCB6QCvBGtLRGL39mQ0Ia',
--1, '999999999', TRUE, NOW());

insert into departamento (id,nombre,detalle)
values (1,'Lima','La capital del peru'),
(2,'Cuzco','La ciuad del sol'),
(3,'Arequipa','La ciuad blanca'),
(4,'Ancash','El corazon del Peru'),
(5,'Loreto','Selva central');

insert into hotel (id,direccion,nombre,departamento_id)
values (1,'Av. Larco 123, Miraflores','Hotel Miraflores',1),
(2,'Calle Siete Culebras 321, Cuzco','Hotel Centro Historico',2),
(3,'Plaza de Armas 456, Arequipa','Hotel Plaza',3),
(4,'Plaza de Huaraz 225, Huaraz','Hotel Baños del Inca',4),
(5,'Jose Olaya 126, Shinagui','Hotel Calidad',5);

insert into tipo_habitacion(id,capacidad,descripcion,nombre)
values (1,1,'Habitacion simple con cama individual','Simple'),
(2,2,'Habitacion doble con dos camas','Doble'),
(3,4,'Habitacion suite con sala y jacuzzi','Suite');

insert into habitacion(id,estado,numero,precio,hotel_id,tipo_id)
values (1,'DISPONIBLE','101',150,1,1),
		(2,'OCUPADA','102',200,1,2),
        (4,'DISPONIBLE','103',350,2,3),
        (5,'DISPONIBLE','101',150,2,1),
        (6,'MANTENIMIENTO','102',200,3,2),
        (7,'DISPONIBLE','103',350,3,3),
        (8,'DISPONIBLE','101',350,4,3),
        (9,'MANTENIMIENTO','103',200,5,2);

-- NUEVOS DEPARTAMENTOS
insert into departamento (id,nombre,detalle)
values (6,'Piura','La tierra del eterno verano'),
(7,'Trujillo','La ciudad de la eterna primavera'),
(8,'Ica','Capital vitivinicola del Peru'),
(9,'Puno','Altiplano y Lago Titicaca'),
(10,'Cajamarca','Tierra del Inca Atahualpa');

-- HOTELES PARA LIMA (departamento_id = 1) - 4 hoteles adicionales
insert into hotel (id,direccion,nombre,departamento_id)
values (6,'Av. Jose Pardo 850, Miraflores','Hotel Costa Verde',1),
(7,'Calle Schell 452, Miraflores','Hotel Boutique Miraflores',1),
(8,'Av. Arequipa 1020, San Isidro','Hotel Ejecutivo San Isidro',1),
(9,'Calle Los Eucaliptos 590, San Isidro','Hotel Garden San Isidro',1);

-- HOTELES PARA CUZCO (departamento_id = 2) - 4 hoteles adicionales
insert into hotel (id,direccion,nombre,departamento_id)
values (10,'Plaza Nazarenas 144, Cuzco','Hotel Monasterio',2),
(11,'Calle Ruinas 472, Cuzco','Hotel Ruinas',2),
(12,'Av. Sol 954, Cuzco','Hotel Sol Imperial',2),
(13,'Calle Saphi 848, San Blas','Hotel San Blas Colonial',2);

-- HOTELES PARA AREQUIPA (departamento_id = 3) - 4 hoteles adicionales
insert into hotel (id,direccion,nombre,departamento_id)
values (14,'Calle Melgar 108, Arequipa','Hotel Casa Andina',3),
(15,'Portal de Flores 116, Arequipa','Hotel Libertador',3),
(16,'Calle Santa Catalina 210, Arequipa','Hotel Katari',3),
(17,'Av. Bolivar 425, Arequipa','Hotel Villa Arequipa',3);

-- HOTELES PARA ANCASH (departamento_id = 4) - 4 hoteles adicionales
insert into hotel (id,direccion,nombre,departamento_id)
values (18,'Jr. Jose Olaya 718, Huaraz','Hotel Andino',4),
(19,'Av. Luzuriaga 646, Huaraz','Hotel Huascaran',4),
(20,'Jr. Amadeo Figueroa 1257, Huaraz','Hotel El Patio',4),
(21,'Av. Centenario 830, Huaraz','Hotel Cordillera Blanca',4);

-- HOTELES PARA LORETO (departamento_id = 5) - 4 hoteles adicionales
insert into hotel (id,direccion,nombre,departamento_id)
values (22,'Calle Napo 258, Iquitos','Hotel Victoria Regia',5),
(23,'Malecón Tarapacá 1009, Iquitos','Hotel El Dorado Plaza',5),
(24,'Calle Putumayo 184, Iquitos','Hotel Amazon Plaza',5),
(25,'Jr. Huallaga 210, Iquitos','Hotel Casa Morey',5);

-- HOTELES PARA PIURA (departamento_id = 6) - 5 hoteles
insert into hotel (id,direccion,nombre,departamento_id)
values (26,'Av. Grau 845, Piura','Hotel Los Portales',6),
(27,'Calle Lima 875, Piura','Hotel San Miguel',6),
(28,'Jr. Libertad 432, Piura','Hotel Colonial',6),
(29,'Av. Sanchez Cerro 411, Piura','Hotel Costa del Sol',6),
(30,'Calle Tacna 690, Piura','Hotel Piura Plaza',6);

-- HOTELES PARA TRUJILLO (departamento_id = 7) - 5 hoteles
insert into hotel (id,direccion,nombre,departamento_id)
values (31,'Jr. Independencia 485, Trujillo','Hotel Libertador Trujillo',7),
(32,'Av. America Norte 1540, Trujillo','Hotel Costa del Sol Trujillo',7),
(33,'Jr. Gamarra 747, Trujillo','Hotel Colonial Inn',7),
(34,'Calle San Martin 749, Trujillo','Hotel Bracamonte',7),
(35,'Av. Mansiche 1405, Trujillo','Hotel El Gran Marqués',7);

-- HOTELES PARA ICA (departamento_id = 8) - 5 hoteles
insert into hotel (id,direccion,nombre,departamento_id)
values (36,'Av. Los Maestros 294, Ica','Hotel Las Dunas Sun Resort',8),
(37,'Jr. Lima 265, Ica','Hotel Villa Jazmin',8),
(38,'Calle Callao 256, Ica','Hotel Siesta',8),
(39,'Av. Municipalidad 150, Ica','Hotel Sol de Ica',8),
(40,'Jr. Callao 171, Ica','Hotel Mossone',8);

-- HOTELES PARA PUNO (departamento_id = 9) - 5 hoteles
insert into hotel (id,direccion,nombre,departamento_id)
values (41,'Jr. Deustua 297, Puno','Hotel Libertador Lago Titicaca',9),
(42,'Jr. Teodoro Valcarcel 145, Puno','Hotel Sonesta Posadas del Inca',9),
(43,'Jr. Grau 265, Puno','Hotel Plaza Mayor',9),
(44,'Av. Sesquicentenario 1970, Puno','Hotel Casa Andina',9),
(45,'Jr. Lambayeque 142, Puno','Hotel Balsa Inn',9);

-- HOTELES PARA CAJAMARCA (departamento_id = 10) - 5 hoteles
insert into hotel (id,direccion,nombre,departamento_id)
values (46,'Jr. Cruz de Piedra 707, Cajamarca','Hotel Costa del Sol Cajamarca',10),
(47,'Jr. Lima 773, Cajamarca','Hotel Laguna Seca',10),
(48,'Av. Via de Evitamiento Norte 1611, Cajamarca','Hotel Sierra Galana',10),
(49,'Jr. Dos de Mayo 311, Cajamarca','Hotel Cajamarca',10),
(50,'Jr. Del Comercio 644, Cajamarca','Hotel El Portal del Marqués',10);

-- HABITACIONES PARA TODOS LOS HOTELES NUEVOS (del 6 al 50)
-- Cada hotel tendrá 3 habitaciones: una Simple, una Doble y una Suite

-- HABITACIONES PARA HOTELES DE LIMA (hoteles 6-9)
insert into habitacion(id,estado,numero,precio,hotel_id,tipo_id)
values
-- Hotel Costa Verde (id=6)
(10,'DISPONIBLE','101',150,6,1), (11,'OCUPADA','102',200,6,2), (12,'DISPONIBLE','103',350,6,3),
-- Hotel Boutique Miraflores (id=7)
(13,'DISPONIBLE','101',150,7,1), (14,'MANTENIMIENTO','102',200,7,2), (15,'DISPONIBLE','103',350,7,3),
-- Hotel Ejecutivo San Isidro (id=8)
(16,'DISPONIBLE','101',150,8,1), (17,'DISPONIBLE','102',200,8,2), (18,'OCUPADA','103',350,8,3),
-- Hotel Garden San Isidro (id=9)
(19,'DISPONIBLE','101',150,9,1), (20,'DISPONIBLE','102',200,9,2), (21,'DISPONIBLE','103',350,9,3);

-- HABITACIONES PARA HOTELES DE CUZCO (hoteles 10-13)
insert into habitacion(id,estado,numero,precio,hotel_id,tipo_id)
values
-- Hotel Monasterio (id=10)
(22,'DISPONIBLE','101',150,10,1), (23,'DISPONIBLE','102',200,10,2), (24,'MANTENIMIENTO','103',350,10,3),
-- Hotel Ruinas (id=11)
(25,'OCUPADA','101',150,11,1), (26,'DISPONIBLE','102',200,11,2), (27,'DISPONIBLE','103',350,11,3),
-- Hotel Sol Imperial (id=12)
(28,'DISPONIBLE','101',150,12,1), (29,'DISPONIBLE','102',200,12,2), (30,'DISPONIBLE','103',350,12,3),
-- Hotel San Blas Colonial (id=13)
(31,'DISPONIBLE','101',150,13,1), (32,'OCUPADA','102',200,13,2), (33,'DISPONIBLE','103',350,13,3);

-- HABITACIONES PARA HOTELES DE AREQUIPA (hoteles 14-17)
insert into habitacion(id,estado,numero,precio,hotel_id,tipo_id)
values
-- Hotel Casa Andina (id=14)
(34,'DISPONIBLE','101',150,14,1), (35,'DISPONIBLE','102',200,14,2), (36,'DISPONIBLE','103',350,14,3),
-- Hotel Libertador (id=15)
(37,'MANTENIMIENTO','101',150,15,1), (38,'DISPONIBLE','102',200,15,2), (39,'DISPONIBLE','103',350,15,3),
-- Hotel Katari (id=16)
(40,'DISPONIBLE','101',150,16,1), (41,'DISPONIBLE','102',200,16,2), (42,'OCUPADA','103',350,16,3),
-- Hotel Villa Arequipa (id=17)
(43,'DISPONIBLE','101',150,17,1), (44,'DISPONIBLE','102',200,17,2), (45,'DISPONIBLE','103',350,17,3);

-- HABITACIONES PARA HOTELES DE ANCASH (hoteles 18-21)
insert into habitacion(id,estado,numero,precio,hotel_id,tipo_id)
values
-- Hotel Andino (id=18)
(46,'DISPONIBLE','101',150,18,1), (47,'OCUPADA','102',200,18,2), (48,'DISPONIBLE','103',350,18,3),
-- Hotel Huascaran (id=19)
(49,'DISPONIBLE','101',150,19,1), (50,'DISPONIBLE','102',200,19,2), (51,'MANTENIMIENTO','103',350,19,3),
-- Hotel El Patio (id=20)
(52,'DISPONIBLE','101',150,20,1), (53,'DISPONIBLE','102',200,20,2), (54,'DISPONIBLE','103',350,20,3),
-- Hotel Cordillera Blanca (id=21)
(55,'OCUPADA','101',150,21,1), (56,'DISPONIBLE','102',200,21,2), (57,'DISPONIBLE','103',350,21,3);

-- HABITACIONES PARA HOTELES DE LORETO (hoteles 22-25)
insert into habitacion(id,estado,numero,precio,hotel_id,tipo_id)
values
-- Hotel Victoria Regia (id=22)
(58,'DISPONIBLE','101',150,22,1), (59,'DISPONIBLE','102',200,22,2), (60,'DISPONIBLE','103',350,22,3),
-- Hotel El Dorado Plaza (id=23)
(61,'DISPONIBLE','101',150,23,1), (62,'MANTENIMIENTO','102',200,23,2), (63,'DISPONIBLE','103',350,23,3),
-- Hotel Amazon Plaza (id=24)
(64,'OCUPADA','101',150,24,1), (65,'DISPONIBLE','102',200,24,2), (66,'DISPONIBLE','103',350,24,3),
-- Hotel Casa Morey (id=25)
(67,'DISPONIBLE','101',150,25,1), (68,'DISPONIBLE','102',200,25,2), (69,'OCUPADA','103',350,25,3);

-- HABITACIONES PARA HOTELES DE PIURA (hoteles 26-30)
insert into habitacion(id,estado,numero,precio,hotel_id,tipo_id)
values
-- Hotel Los Portales (id=26)
(70,'DISPONIBLE','101',150,26,1), (71,'DISPONIBLE','102',200,26,2), (72,'DISPONIBLE','103',350,26,3),
-- Hotel San Miguel (id=27)
(73,'MANTENIMIENTO','101',150,27,1), (74,'DISPONIBLE','102',200,27,2), (75,'DISPONIBLE','103',350,27,3),
-- Hotel Colonial (id=28)
(76,'DISPONIBLE','101',150,28,1), (77,'OCUPADA','102',200,28,2), (78,'DISPONIBLE','103',350,28,3),
-- Hotel Costa del Sol (id=29)
(79,'DISPONIBLE','101',150,29,1), (80,'DISPONIBLE','102',200,29,2), (81,'DISPONIBLE','103',350,29,3),
-- Hotel Piura Plaza (id=30)
(82,'DISPONIBLE','101',150,30,1), (83,'DISPONIBLE','102',200,30,2), (84,'MANTENIMIENTO','103',350,30,3);

-- HABITACIONES PARA HOTELES DE TRUJILLO (hoteles 31-35)
insert into habitacion(id,estado,numero,precio,hotel_id,tipo_id)
values
-- Hotel Libertador Trujillo (id=31)
(85,'OCUPADA','101',150,31,1), (86,'DISPONIBLE','102',200,31,2), (87,'DISPONIBLE','103',350,31,3),
-- Hotel Costa del Sol Trujillo (id=32)
(88,'DISPONIBLE','101',150,32,1), (89,'DISPONIBLE','102',200,32,2), (90,'DISPONIBLE','103',350,32,3),
-- Hotel Colonial Inn (id=33)
(91,'DISPONIBLE','101',150,33,1), (92,'MANTENIMIENTO','102',200,33,2), (93,'DISPONIBLE','103',350,33,3),
-- Hotel Bracamonte (id=34)
(94,'DISPONIBLE','101',150,34,1), (95,'DISPONIBLE','102',200,34,2), (96,'OCUPADA','103',350,34,3),
-- Hotel El Gran Marqués (id=35)
(97,'DISPONIBLE','101',150,35,1), (98,'DISPONIBLE','102',200,35,2), (99,'DISPONIBLE','103',350,35,3);

-- HABITACIONES PARA HOTELES DE ICA (hoteles 36-40)
insert into habitacion(id,estado,numero,precio,hotel_id,tipo_id)
values
-- Hotel Las Dunas Sun Resort (id=36)
(100,'DISPONIBLE','101',150,36,1), (101,'DISPONIBLE','102',200,36,2), (102,'MANTENIMIENTO','103',350,36,3),
-- Hotel Villa Jazmin (id=37)
(103,'OCUPADA','101',150,37,1), (104,'DISPONIBLE','102',200,37,2), (105,'DISPONIBLE','103',350,37,3),
-- Hotel Siesta (id=38)
(106,'DISPONIBLE','101',150,38,1), (107,'DISPONIBLE','102',200,38,2), (108,'DISPONIBLE','103',350,38,3),
-- Hotel Sol de Ica (id=39)
(109,'DISPONIBLE','101',150,39,1), (110,'DISPONIBLE','102',200,39,2), (111,'OCUPADA','103',350,39,3),
-- Hotel Mossone (id=40)
(112,'DISPONIBLE','101',150,40,1), (113,'MANTENIMIENTO','102',200,40,2), (114,'DISPONIBLE','103',350,40,3);

-- HABITACIONES PARA HOTELES DE PUNO (hoteles 41-45)
insert into habitacion(id,estado,numero,precio,hotel_id,tipo_id)
values
-- Hotel Libertador Lago Titicaca (id=41)
(115,'DISPONIBLE','101',150,41,1), (116,'DISPONIBLE','102',200,41,2), (117,'DISPONIBLE','103',350,41,3),
-- Hotel Sonesta Posadas del Inca (id=42)
(118,'OCUPADA','101',150,42,1), (119,'DISPONIBLE','102',200,42,2), (120,'DISPONIBLE','103',350,42,3),
-- Hotel Plaza Mayor (id=43)
(121,'DISPONIBLE','101',150,43,1), (122,'DISPONIBLE','102',200,43,2), (123,'MANTENIMIENTO','103',350,43,3),
-- Hotel Casa Andina (id=44)
(124,'DISPONIBLE','101',150,44,1), (125,'DISPONIBLE','102',200,44,2), (126,'DISPONIBLE','103',350,44,3),
-- Hotel Balsa Inn (id=45)
(127,'DISPONIBLE','101',150,45,1), (128,'OCUPADA','102',200,45,2), (129,'DISPONIBLE','103',350,45,3);

-- HABITACIONES PARA HOTELES DE CAJAMARCA (hoteles 46-50)
insert into habitacion(id,estado,numero,precio,hotel_id,tipo_id)
values
-- Hotel Costa del Sol Cajamarca (id=46)
(132,'DISPONIBLE','103',350,46,3),
-- Hotel Laguna Seca (id=47)
(135,'DISPONIBLE','103',350,47,3),
-- Hotel Sierra Galana (id=48)
(137,'OCUPADA','102',200,48,2),
-- Hotel Cajamarca (id=49)
(141,'DISPONIBLE','103',350,49,3),
-- Hotel El Portal del Marqués (id=50)
(144,'MANTENIMIENTO','103',350,50,3);
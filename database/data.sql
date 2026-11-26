INSERT INTO rol (id_rol, nombre) VALUES
(1, 'ADMIN'),
(2, 'VENDEDOR'),
(3, 'CLIENTE');

INSERT INTO region (id_region, nombre) VALUES
(1, 'Arica y Parinacota'),
(2, 'Tarapacá'),
(3, 'Antofagasta'),
(4, 'Atacama'),
(5, 'Coquimbo'),
(6, 'Valparaíso'),
(7, 'Metropolitana de Santiago'),
(8, 'Libertador General Bernardo O''Higgins'),
(9, 'Maule'),
(10, 'Ñuble'),
(11, 'Biobío'),
(12, 'La Araucanía'),
(13, 'Los Ríos'),
(14, 'Los Lagos'),
(15, 'Aysén del General Carlos Ibáñez del Campo'),
(16, 'Magallanes y de la Antártica Chilena');

INSERT INTO comuna (id_comuna, nombre, id_region) VALUES
(1, 'Arica', 1),
(2, 'Iquique', 2),
(3, 'Pozo Almonte', 2),
(4, 'Antofagasta', 3),
(5, 'Copiapó', 4),
(6, 'La Serena', 5),
(7, 'Valparaíso', 6),
(8, 'Viña del Mar', 6),
(9, 'Quintero', 6),
(10, 'Santiago', 7),
(11, 'Providencia', 7),
(12, 'Independencia', 7),
(13, 'Quinta Normal', 7),
(14, 'Pirque', 7),
(15, 'Ñuñoa', 7),
(16, 'Maipú', 7),
(17, 'Puente Alto', 7),
(18, 'Las Condes', 7),
(19, 'Rancagua', 8),
(20, 'Graneros', 8),
(21, 'Talca', 9),
(22, 'Chillán', 10),
(23, 'Concepción', 11),
(24, 'Temuco', 12),
(25, 'Valdivia', 13),
(26, 'Puerto Montt', 14),
(27, 'Quemchi', 14),
(28, 'Castro', 14),
(29, 'Coyhaique', 15),
(30, 'Punta Arenas', 16);

INSERT INTO usuario (id_usuario, run, nombre, apellidos, correo, contrasena, direccion, id_comuna, id_rol, activo, fecha_registro) VALUES
(1, '11111111-1', 'Isidora', 'Vega Rojas', 'admin@casasembrujadas.cl', '$2a$10$LUSqdrJb/r56wCeYgjLhs.1B8xRNIP9evLjqLL145wtfAl6M6b7xC', 'Moneda 1010', 10, 1, TRUE, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '10 months'),
(2, '22222222-2', 'Matías', 'Soto Fuentes', 'vendedor@casasembrujadas.cl', '$2a$10$RO0rxJaXZDJvrggROjAn3ehhrNzD9iv0TPluHED9ubEhhOJeSO4gO', 'Av. Matta 520', 10, 2, TRUE, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '9 months'),
(3, '33333333-3', 'Camila', 'Reyes Pinto', 'cliente@casasembrujadas.cl', '$2a$10$e8/RSd1WPifVXLJnYrefI.D8wQIxm4PGR5apTznj7G78X9ZFjP3vO', 'Av. Italia 1200', 11, 3, TRUE, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '8 months'),
(4, '44444444-4', 'Benjamín', 'Torres Muñoz', 'benjamin.torres@casasembrujadas.cl', '$2a$10$mSP9Fm2mEaO70HX9mdCq0.TJsqfQqV7iSRlD9DTawMeeei3fbN8d2', 'Cerro Alegre 45', 7, 3, TRUE, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '7 months'),
(5, '55555555-5', 'Antonia', 'Herrera Díaz', 'antonia.herrera@casasembrujadas.cl', '$2a$10$PTnHiMp/rGsLgs93f6Bb8ucQKndcXXRfeclER/oipHKqKtg0MRh4m', 'Barros Arana 880', 23, 3, TRUE, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '6 months'),
(6, '66666666-6', 'Tomás', 'Fuentealba Lagos', 'tomas.fuentealba@casasembrujadas.cl', '$2a$10$noagMj.Dqp3IgN3r1Uz7u.dEnR70bNXtUlRUZHK4vF8m57VhBWgTO', 'Blanco 310', 28, 3, TRUE, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '5 months');

INSERT INTO categoria (id_categoria, nombre, descripcion) VALUES
(1, 'Palacios y casonas', 'Residencias históricas con pasado ilustre y visitas que nadie invitó.'),
(2, 'Edificios públicos', 'Bibliotecas, teatros y hospitales donde alguien sigue cumpliendo turno.'),
(3, 'Pueblos fantasma', 'Oficinas salitreras abandonadas en medio del desierto.'),
(4, 'Islas y cuevas', 'Lugares apartados de Chiloé, entre el mar y la leyenda.'),
(5, 'Apariciones', 'Figuras, sombras y voces relatadas por testigos.'),
(6, 'Brujería y diablos', 'Pactos, maleficios y sociedades secretas.'),
(7, 'Espiritismo y portales', 'Sesiones, médiums y puertas a otros mundos.'),
(8, 'Faros y fuertes', 'Categoría en preparación: todavía no tiene lugares asociados.');

INSERT INTO producto (id_producto, titulo, descripcion, precio, direccion, metros_cuadrados, dormitorios, banos, nivel_embrujo, estado, latitud, longitud, fecha_publicacion, id_comuna) VALUES
(1, 'Casona Dubois', 'Casona de comienzos del siglo XX rodeada de un parque, hoy centro cultural de la Municipalidad de Quinta Normal. Su gárgola, perdida durante décadas, volvió a su sitio en 2026. Ideal para quien busca jardín amplio y una leyenda de fertilidad con final trágico.', 1250000000, 'Ayuntamiento 1650', 650, 9, 4, 5, 'DISPONIBLE', -33.428288, -70.703004, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '240 days', 13),
(2, 'Biblioteca Nacional', 'Edificio monumental de la Alameda levantado sobre el terreno del antiguo convento de las monjas clarisas. Salones de lectura, depósitos de libros y pasillos donde, según los relatos, de noche se ven sombras. Se entrega con silencio garantizado.', 24500000000, 'Av. Libertador Bernardo O''Higgins 651', 20000, 0, 26, 3, 'DISPONIBLE', -33.442064, -70.645665, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '235 days', 10),
(3, 'Ex Hospital San José', 'Antiguo lazareto construido entre 1841 y 1872 junto al Cementerio General y declarado Monumento Histórico en 1999. Pabellones, capilla y largos corredores que terminan en el muro del cementerio. Vecinos muy tranquilos.', 6800000000, 'San José 1053', 9500, 120, 30, 5, 'DISPONIBLE', -33.414398, -70.653490, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '230 days', 12),
(4, 'Oficina Salitrera Humberstone', 'Pueblo salitrero abandonado en 1960 y declarado Patrimonio de la Humanidad en 2005 junto a Santa Laura. Incluye teatro, iglesia, hotel y calles completas en pleno desierto de Tarapacá. Los trabajadores del museo prefieren no acercarse al establo.', 9900000000, 'Ruta A-16, a 48 km de Iquique', 450000, 300, 60, 4, 'DISPONIBLE', -20.208581, -69.795474, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '225 days', 3),
(5, 'Cueva de Quicaví', 'Según la tradición chilota, la Casa Grande es la cueva donde sesionaba la Recta Provincia, la sociedad de brujos juzgada en Ancud en 1880. Nadie ha confirmado su ubicación exacta, así que se vende con las coordenadas del pueblo de Quicaví. Sin baños, sin ventanas y con vecinos reservados.', 320000000, 'Sector Quicaví', 400, 1, 0, 5, 'DISPONIBLE', -42.272079, -73.351852, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '220 days', 27),
(6, 'Palacio Cousiño', 'Palacio construido entre 1875 y 1878 por el arquitecto Paul Lathoud para la familia Cousiño Goyenechea, que vivió en él hasta 1928. Hoy es museo y Monumento Histórico. Salones de época, jardines diseñados por un paisajista y una dueña de casa que, dicen, nunca se fue.', 8400000000, 'Dieciocho 438', 2900, 12, 8, 3, 'DISPONIBLE', -33.452174, -70.656656, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '215 days', 10),
(7, 'Casona El Molino', 'Casona colonial en ruinas junto al que se considera el primer molino de Chile. Perteneció a Catalina de los Ríos y Lisperguer, la Quintrala, y después a Mateo de Toro Zambrano. La Municipalidad de Graneros la adquirió para convertirla en museo. Requiere restauración completa y cierto valor.', 480000000, 'Graneros (ubicación aproximada)', 1100, 10, 3, 4, 'VENDIDA', -34.064474, -70.726298, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '210 days', 20),
(8, 'Teatro Municipal de Santiago', 'Teatro inaugurado en 1857, destruido por un incendio en 1870 y vuelto a levantar. Sala principal, foso de orquesta y camarines. Los artistas que lo desafían en escena suelen arrepentirse.', 18700000000, 'Agustinas 794', 11000, 0, 40, 2, 'RESERVADA', -33.441167, -70.647567, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '205 days', 10),
(9, 'Oficina Salitrera Santa Laura', 'La oficina hermana de Humberstone, a un par de kilómetros, con sus estructuras industriales todavía en pie. Patrimonio de la Humanidad desde 2005. Para quien prefiere un pueblo fantasma más íntimo.', 3600000000, 'Ruta A-16, frente a Humberstone', 120000, 80, 12, 3, 'VENDIDA', -20.210600, -69.815163, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '200 days', 3),
(10, 'Isla Aucar', 'Islote unido a la Isla Grande de Chiloé por una pasarela de madera de unos 500 metros. Tiene capilla, cementerio y jardín botánico. Francisco Coloane la llamó la isla de las almas navegantes.', 750000000, 'Aucar, a 6 km de Quemchi', 15000, 0, 1, 2, 'VENDIDA', -42.161470, -73.479318, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '195 days', 27),
(11, 'Palacio Echeverría', 'Casona de 1910 en el sector de Barrio Italia donde vivió la escritora Inés Echeverría, Iris, y donde se realizaban sesiones espiritistas. La prensa la llama la casa de los espíritus. En la vida real también está a la venta.', 1480000000, 'Sector Barrio Italia (dirección aproximada)', 900, 14, 6, 4, 'DISPONIBLE', -33.445500, -70.624500, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '190 days', 11),
(12, 'Bodega Casillero del Diablo', 'Bodega subterránea de la viña que Melchor de Concha y Toro fundó en Pirque en 1883. Para frenar el robo de sus mejores botellas, el dueño hizo correr la voz de que ahí abajo vivía el Diablo. Temperatura estable todo el año.', 2900000000, 'Av. Virginia Subercaseaux 210', 1800, 0, 2, 3, 'INACTIVA', -33.635577, -70.574547, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '185 days', 14),
(13, 'Palacio de Piedra Rosa', 'Palacio de piedra rosa que, según la leyenda de Quintero, se levanta en otra dimensión en medio de la bahía Papagayo. Solo lo ve quien cree y mira con atención. La foto muestra la playa: el palacio no sale en cámara.', 5555000000, 'Playa El Papagayo', 3300, 33, 11, 5, 'VENDIDA', -32.779218, -71.534164, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '180 days', 9);

INSERT INTO producto_categoria (id_producto, id_categoria) VALUES
(1, 1), (1, 6),
(2, 2), (2, 5),
(3, 2), (3, 5),
(4, 3), (4, 5),
(5, 4), (5, 6),
(6, 1), (6, 5),
(7, 1), (7, 6),
(8, 2), (8, 5),
(9, 3), (9, 5),
(10, 4), (10, 5),
(11, 1), (11, 5), (11, 7),
(12, 6),
(13, 1), (13, 7);

INSERT INTO imagen (id_producto, url, orden, autor, licencia, url_fuente) VALUES
(1, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/b/ba/Casona_dubois_1.jpg/1280px-Casona_dubois_1.jpg', 1, 'Dii.Dii', 'CC BY-SA 4.0', 'https://commons.wikimedia.org/wiki/File:Casona_dubois_1.jpg'),
(1, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/d/d3/Centro_Cultural_Casona_Dubois%2C_Quinta_Normal%2C_Santiago_20231223_02.jpg/1280px-Centro_Cultural_Casona_Dubois%2C_Quinta_Normal%2C_Santiago_20231223_02.jpg', 2, 'Carlos yo', 'CC BY-SA 4.0', 'https://commons.wikimedia.org/wiki/File:Centro_Cultural_Casona_Dubois,_Quinta_Normal,_Santiago_20231223_02.jpg'),
(2, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/4/4d/Biblioteca_Nacional_de_Chile%2C_2012-09-08.jpg/1280px-Biblioteca_Nacional_de_Chile%2C_2012-09-08.jpg', 1, 'Pollolavin', 'CC BY-SA 3.0', 'https://commons.wikimedia.org/wiki/File:Biblioteca_Nacional_de_Chile,_2012-09-08.jpg'),
(2, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/f/ff/Biblioteca_Nacional_de_Chile%2C_Santiago_Chile_%282012-09-30%29.jpg/1280px-Biblioteca_Nacional_de_Chile%2C_Santiago_Chile_%282012-09-30%29.jpg', 2, 'Chofaska', 'CC BY-SA 3.0', 'https://commons.wikimedia.org/wiki/File:Biblioteca_Nacional_de_Chile,_Santiago_Chile_(2012-09-30).jpg'),
(3, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/6/62/Antiguo_Hospital_San_Jose_%284%29.jpg/1280px-Antiguo_Hospital_San_Jose_%284%29.jpg', 1, 'Lorna-Lorna', 'CC BY-SA 3.0', 'https://commons.wikimedia.org/wiki/File:Antiguo_Hospital_San_Jose_(4).jpg'),
(3, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/2/2b/Antiguo_Hospital_San_Jose_%281%29.jpg/1280px-Antiguo_Hospital_San_Jose_%281%29.jpg', 2, 'Lorna-Lorna', 'CC BY-SA 3.0', 'https://commons.wikimedia.org/wiki/File:Antiguo_Hospital_San_Jose_(1).jpg'),
(4, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/2/23/Officina_salitrera_Humberstone_01.jpg/1280px-Officina_salitrera_Humberstone_01.jpg', 1, 'Davide guallini', 'CC BY-SA 3.0', 'https://commons.wikimedia.org/wiki/File:Officina_salitrera_Humberstone_01.jpg'),
(4, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/a/ae/Clock_in_Oficina_Salitrera_Humberstone.jpg/1280px-Clock_in_Oficina_Salitrera_Humberstone.jpg', 2, 'Till Niermann', 'CC BY-SA 3.0', 'https://commons.wikimedia.org/wiki/File:Clock_in_Oficina_Salitrera_Humberstone.jpg'),
(5, '/img/placeholder-casa.svg', 1, 'Dinko Ocampo', 'Ilustración propia', NULL),
(6, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/4/4b/Palacio_Cousino_20171129_fRF01.jpg/1280px-Palacio_Cousino_20171129_fRF01.jpg', 1, 'Rodrigo Fernández', 'CC BY-SA 4.0', 'https://commons.wikimedia.org/wiki/File:Palacio_Cousino_20171129_fRF01.jpg'),
(6, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/c/cb/Palacio_Cousi%C3%B1o%2C_Santiago.JPG/1280px-Palacio_Cousi%C3%B1o%2C_Santiago.JPG', 2, 'Juan Carlos Saavedra Vergara', 'CC BY-SA 3.0', 'https://commons.wikimedia.org/wiki/File:Palacio_Cousi%C3%B1o,_Santiago.JPG'),
(7, '/img/placeholder-casa.svg', 1, 'Dinko Ocampo', 'Ilustración propia', NULL),
(8, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/c/ca/Teatro_Municipal%2C_Santiago_20230521_01.jpg/1280px-Teatro_Municipal%2C_Santiago_20230521_01.jpg', 1, 'Carlos Figueroa Rojas', 'CC BY-SA 4.0', 'https://commons.wikimedia.org/wiki/File:Teatro_Municipal,_Santiago_20230521_01.jpg'),
(8, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/5/5e/Interior_de_la_sala_principal_del_Teatro_Municipal_de_Santiago_Chile.jpg/1280px-Interior_de_la_sala_principal_del_Teatro_Municipal_de_Santiago_Chile.jpg', 2, 'NicoStgo98', 'CC0', 'https://commons.wikimedia.org/wiki/File:Interior_de_la_sala_principal_del_Teatro_Municipal_de_Santiago_Chile.jpg'),
(9, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/0/0d/Salitrera_Santa_Laura.jpg/1280px-Salitrera_Santa_Laura.jpg', 1, 'Claudiorodriguez', 'CC BY-SA 3.0', 'https://commons.wikimedia.org/wiki/File:Salitrera_Santa_Laura.jpg'),
(9, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/b/b0/Oficina_salitrera_Santa_Laura_-_panoramio.jpg/1280px-Oficina_salitrera_Santa_Laura_-_panoramio.jpg', 2, 'Jorge Manriquez P.', 'CC BY-SA 3.0', 'https://commons.wikimedia.org/wiki/File:Oficina_salitrera_Santa_Laura_-_panoramio.jpg'),
(10, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/8/82/Isla_Aucar%2C_comuna_de_Quemchi_A74095920240107.jpg/1280px-Isla_Aucar%2C_comuna_de_Quemchi_A74095920240107.jpg', 1, 'Rjcastillo', 'CC BY-SA 4.0', 'https://commons.wikimedia.org/wiki/File:Isla_Aucar,_comuna_de_Quemchi_A74095920240107.jpg'),
(10, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/7/78/Cementerio%2C_Isla_de_Aucar_A74096320240107.jpg/1280px-Cementerio%2C_Isla_de_Aucar_A74096320240107.jpg', 2, 'Rjcastillo', 'CC BY-SA 4.0', 'https://commons.wikimedia.org/wiki/File:Cementerio,_Isla_de_Aucar_A74096320240107.jpg'),
(11, '/img/placeholder-casa.svg', 1, 'Dinko Ocampo', 'Ilustración propia', NULL),
(12, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/a/a1/Casillero_del_diablo%2C_Vi%C3%B1a_Concha_y_Toro%2C_Pirque%2C_Chile.jpg/1280px-Casillero_del_diablo%2C_Vi%C3%B1a_Concha_y_Toro%2C_Pirque%2C_Chile.jpg', 1, 'Banfield', 'CC BY-SA 2.5 ar', 'https://commons.wikimedia.org/wiki/File:Casillero_del_diablo,_Vi%C3%B1a_Concha_y_Toro,_Pirque,_Chile.jpg'),
(12, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/2/22/Frente_Casa_Patronal_Concha_y_Toro.jpg/1280px-Frente_Casa_Patronal_Concha_y_Toro.jpg', 2, 'Rjcastillo', 'CC BY-SA 4.0', 'https://commons.wikimedia.org/wiki/File:Frente_Casa_Patronal_Concha_y_Toro.jpg'),
(13, 'https://thumb.wikimedia.org/wikipedia/commons/thumb/8/82/Playa_El_Papagayo%2C_Quintero_20230916.jpg/1280px-Playa_El_Papagayo%2C_Quintero_20230916.jpg', 1, 'Carlos yo', 'CC BY-SA 4.0', 'https://commons.wikimedia.org/wiki/File:Playa_El_Papagayo,_Quintero_20230916.jpg');

INSERT INTO mito (id_producto, titulo, relato, anio_origen, fuente) VALUES
(1, 'La gárgola de la fertilidad', 'A comienzos del siglo XX el ingeniero Andrés Dubois llegó a Santiago a trabajar en el alumbrado público. Cuenta la leyenda que, desesperado por tener un hijo varón, instaló en la casa una gárgola de la fertilidad por consejo de una bruja. El niño nació, pero murió junto a su madre, y Dubois perdió la razón.', NULL, 'https://www.chilevision.cl/noticias/tendencias/estos-son-los-8-lugares-embrujados-de-chile-que-puedes-visitar-en-halloween/'),
(1, 'El regreso de la gárgola', 'La figura estuvo perdida durante varias décadas. En mayo de 2026 volvió a su lugar y la casona la presentó en recorridos nocturnos guiados.', 2026, 'https://radio.uchile.cl/2026/05/30/los-locos-anos-20-quinta-normal-cerrara-el-fin-de-semana-de-los-patrimonios-con-gran-fiesta-de-epoca/'),
(2, 'Las clarisas de la Alameda', 'La biblioteca se levantó donde estuvo el convento de las monjas clarisas. Los relatos dicen que por las noches se ven sombras y figuras humanas en los pasillos.', NULL, 'https://www.chilevision.cl/noticias/solo-para-valientes-los-lugares-con-actividad-paranormal-mas'),
(2, 'Los ruidos del Fondo General', 'En 2011 la jefa del Fondo General contó que, cuando empezó a trabajar ahí, se hablaba de ruidos extraños y apariciones. Según ella, los fantasmas se fueron cuando se retiraron los funcionarios más bromistas, que de noche lanzaban una bolita por el pasillo.', 2011, 'https://www.memoriachilena.gob.cl/602/w3-article-123280.html'),
(3, 'Las monjas de la capilla', 'Un guardia relató en 2009 que dos religiosas aparecían fumando afuera de la antigua capilla. Decía que no parecían amenazantes.', 2009, 'https://prensamercosur.org/2026/10/02/los-fantasmas-del-antiguo-hospital-san-jose-en-chile/'),
(3, 'El médico que cruza el muro', 'Un hombre alto y delgado recorre un pasillo largo hasta el muro que separa el hospital del Cementerio General y lo atraviesa. Antiguamente había puertas que comunicaban ambos recintos.', 2009, 'https://prensamercosur.org/2026/10/02/los-fantasmas-del-antiguo-hospital-san-jose-en-chile/'),
(4, 'El establo', 'El director del museo contó en 2014 que los trabajadores dicen haber visto apariciones y escuchado voces. El lugar que más evitan es el establo, donde se han encontrado muñecas rellenas de ají y puertas que se cierran.', 2014, 'https://www.theclinic.cl/2014/11/16/solo-para-valientes-humberstone-abre-sus-puertas-para-visitas-nocturnas/'),
(5, 'La Casa Grande de los brujos', 'La cueva de Quicaví, llamada también Salamanca, aparece mencionada desde 1787. La tradición la señala como sede de la Recta Provincia, la sociedad secreta de brujos de Chiloé.', 1787, 'https://www.memoriachilena.gob.cl/602/w3-article-95271.html'),
(5, 'El juicio de 1880', 'En 1880 el gobernador Martiniano Rodríguez llevó a juicio en Ancud a los miembros de la Recta Provincia. El proceso duró un año y terminó en condenas por asociación ilícita; los crímenes que se les atribuían no pudieron probarse.', 1880, 'https://www.biobiochile.cl/noticias/sociedad/historia/2023/11/01/asesinatos-y-magia-oscura-cuando-el-estado-de-chile-llevo-a-juicio-a-los-brujos-de-chiloe.shtml'),
(6, 'La dueña de casa', 'Se dice que el espíritu de Isidora Goyenechea todavía recorre el palacio. Una versión cuenta que murió sin verlo terminado, pero las fechas no calzan: la obra concluyó en 1878 y la familia vivió ahí hasta 1928.', NULL, 'https://www.chilevision.cl/noticias/tendencias/estos-son-los-8-lugares-embrujados-de-chile-que-puedes-visitar-en-halloween/'),
(7, 'La Quintrala', 'Catalina de los Ríos y Lisperguer murió en 1665 mientras la Real Audiencia la investigaba por la muerte de más de cuarenta personas, entre esclavos y sirvientes. Benjamín Vicuña Mackenna convirtió su historia en leyenda en el siglo XIX. No se conocen relatos de apariciones propios de esta casona: su fama viene de quien fue su dueña.', 1665, 'https://www.emol.com/noticias/magazine/2010/01/16/394159/recuerdan-los-345-anos-de-la-muerte-de-la-quintrala.html'),
(8, 'El fantasma del Municipal', 'El actor Gabriel Cañas contó en 2022 que, tras pedirle ayuda al teatro apoyando las manos en sus muros, empezó a tener sueños inquietantes. Tiempo después desafió al fantasma en plena función y el micrófono cayó solo al foso. Dice que luego hicieron las paces.', 2022, 'https://www.eldinamo.cl/entretencion/2022/01/04/la-experiencia-paranormal-que-vivio-gabriel-canas-en-el-teatro-municipal/'),
(9, 'La hermana silenciosa', 'Santa Laura comparte con Humberstone la fama de pueblo fantasma y figura junto a ella en las listas de lugares embrujados de Chile. No tiene un relato propio documentado.', NULL, 'https://www.chilevision.cl/noticias/tendencias/estos-son-los-8-lugares-embrujados-de-chile-que-puedes-visitar-en-halloween/'),
(10, 'La isla de las almas navegantes', 'El escritor Francisco Coloane comparó la isla con un barco cuyos navegantes muertos esperan zarpar. El nombre quedó hasta hoy.', NULL, 'https://www.chile.travel/blog/cultura/tres-lugares-de-chiloe-llenos-de-misterios-que-debes-conocer'),
(11, 'Las sesiones de Iris', 'La casa fue uno de los centros del espiritismo chileno del siglo pasado. Se cuenta que la escritora Inés Echeverría contactaba espíritus en sus salones.', NULL, 'https://www.chilevision.cl/noticias/solo-para-valientes-los-lugares-con-actividad-paranormal-mas'),
(11, 'La niña de la ventana', 'Los relatos hablan de una niña que se asoma a la ventana principal del segundo piso, de objetos que caen solos y de fuego sin origen.', NULL, 'https://www.chilevision.cl/noticias/solo-para-valientes-los-lugares-con-actividad-paranormal-mas'),
(12, 'El Diablo en la bodega', 'En el siglo XIX desaparecían botellas de la bodega privada de Melchor de Concha y Toro, cerrada con una reja de hierro. Como no daba con el responsable, difundió el rumor de que ahí habitaba el Diablo. Los robos se acabaron.', NULL, 'https://conchaytoro.com/en/blog/the-legend-of-casillero-del-diablo-concha-y-toro/'),
(13, 'El palacio en otra dimensión', 'Un relato recogido en Quintero dice que las energías del Himalaya se trasladaron a los Andes y que una de sus líneas termina en la bahía Papagayo. Ahí, en otra dimensión, se alzaría un palacio de piedra rosa habitado por un maestro venido del Himalaya.', 2016, 'https://www.muniquintero.cl/wp-content/uploads/2023/12/HISTORIAS-Y-LEYENDAS-DE-QUINTERO.-EDICION-ESCOLAR.pdf'),
(13, 'El portal nocturno', 'De noche, la playa escondería un portal a una dimensión paralela.', NULL, 'https://www.chilevision.cl/noticias/tendencias/estos-son-los-8-lugares-embrujados-de-chile-que-puedes-visitar-en-halloween/');

INSERT INTO ficha_paranormal (id_producto, tipo_entidad, horario_actividad, fenomenos_reportados, ultimo_avistamiento) VALUES
(1, 'Maleficio familiar', 'De noche, en el parque', 'Gárgola asociada a un rito de fertilidad y a la tragedia de la familia Dubois.', '2026-05-29'),
(2, 'Sombras', 'De noche, tras el cierre', 'Sombras y figuras humanas en los pasillos; ruidos extraños en los depósitos.', '2022-10-27'),
(3, 'Apariciones', 'Madrugada', 'Dos religiosas junto a la capilla, un médico que atraviesa el muro hacia el cementerio, un fraile encapuchado, pasos y golpes.', '2026-10-02'),
(4, 'Apariciones y voces', 'De noche', 'Apariciones, voces y puertas que se cierran en el establo.', '2014-11-16'),
(5, 'Brujos de la Recta Provincia', 'Noches sin luna', 'Reuniones de la sociedad secreta de brujos según la tradición oral.', NULL),
(6, 'Espíritu residente', 'Al atardecer', 'Presencia de Isidora Goyenechea recorriendo los salones.', '2025-10-30'),
(7, 'Leyenda de la Quintrala', 'De noche', 'Sin relatos propios; la fama del lugar viene de su antigua dueña.', NULL),
(8, 'Fantasma de teatro', 'Ensayos y funciones', 'Escalofríos, sueños recurrentes y objetos que caen solos del escenario.', '2022-01-04'),
(9, 'Pueblo fantasma', 'De noche', 'Fama de aparecidos compartida con Humberstone.', '2025-10-30'),
(10, 'Almas navegantes', 'Con neblina', 'Las almas del cementerio esperan zarpar, según la imagen de Francisco Coloane.', NULL),
(11, 'Espíritus invocados', 'De noche', 'Una niña en la ventana del segundo piso, objetos que caen solos y fuego sin origen.', '2022-10-27'),
(12, 'El Diablo', 'De noche, en la bodega', 'Rumor de que el Diablo custodia el vino.', NULL),
(13, 'Portal dimensional', 'De noche', 'Palacio visible solo para quien cree; portal a una dimensión paralela.', '2025-10-30');

INSERT INTO favorito (id_usuario, id_producto, fecha) VALUES
(3, 6, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '40 days'),
(3, 1, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '38 days'),
(3, 5, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '20 days'),
(3, 8, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '2 days'),
(4, 6, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '60 days'),
(4, 4, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '45 days'),
(4, 13, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '35 days'),
(5, 6, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '90 days'),
(5, 1, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '25 days'),
(5, 3, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '12 days'),
(6, 5, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '70 days'),
(6, 6, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '15 days'),
(6, 4, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '14 days'),
(6, 10, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '5 days');

INSERT INTO reserva (id_reserva, id_producto, id_usuario, fecha_inicio, fecha_expiracion, estado) VALUES
(1, 13, 4, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '32 days', (now() AT TIME ZONE 'America/Santiago') - INTERVAL '31 days', 'CONVERTIDA'),
(2, 1, 5, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '20 days', (now() AT TIME ZONE 'America/Santiago') - INTERVAL '19 days', 'EXPIRADA'),
(3, 6, 6, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '10 days', (now() AT TIME ZONE 'America/Santiago') - INTERVAL '9 days', 'CANCELADA'),
(4, 8, 3, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '4 hours', (now() AT TIME ZONE 'America/Santiago') + INTERVAL '20 hours', 'ACTIVA');

INSERT INTO boleta (id_boleta, numero, fecha, neto, iva, total, estado, id_usuario) VALUES
(1, 1, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '6 months', 750000000, 142500000, 892500000, 'PAGADA', 3),
(2, 2, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '4 months', 4080000000, 775200000, 4855200000, 'PAGADA', 6),
(3, 3, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '3 months', 1480000000, 281200000, 1761200000, 'ANULADA', 5),
(4, 4, (now() AT TIME ZONE 'America/Santiago') - INTERVAL '31 days' + INTERVAL '2 hours', 5555000000, 1055450000, 6610450000, 'PAGADA', 4);

INSERT INTO detalle_boleta (id_boleta, id_producto, cantidad, precio_unitario, subtotal) VALUES
(1, 10, 1, 750000000, 750000000),
(2, 7, 1, 480000000, 480000000),
(2, 9, 1, 3600000000, 3600000000),
(3, 11, 1, 1480000000, 1480000000),
(4, 13, 1, 5555000000, 5555000000);

SELECT setval(pg_get_serial_sequence('rol', 'id_rol'), (SELECT MAX(id_rol) FROM rol));
SELECT setval(pg_get_serial_sequence('region', 'id_region'), (SELECT MAX(id_region) FROM region));
SELECT setval(pg_get_serial_sequence('comuna', 'id_comuna'), (SELECT MAX(id_comuna) FROM comuna));
SELECT setval(pg_get_serial_sequence('usuario', 'id_usuario'), (SELECT MAX(id_usuario) FROM usuario));
SELECT setval(pg_get_serial_sequence('categoria', 'id_categoria'), (SELECT MAX(id_categoria) FROM categoria));
SELECT setval(pg_get_serial_sequence('producto', 'id_producto'), (SELECT MAX(id_producto) FROM producto));
SELECT setval(pg_get_serial_sequence('imagen', 'id_imagen'), (SELECT MAX(id_imagen) FROM imagen));
SELECT setval(pg_get_serial_sequence('mito', 'id_mito'), (SELECT MAX(id_mito) FROM mito));
SELECT setval(pg_get_serial_sequence('reserva', 'id_reserva'), (SELECT MAX(id_reserva) FROM reserva));
SELECT setval(pg_get_serial_sequence('boleta', 'id_boleta'), (SELECT MAX(id_boleta) FROM boleta));
SELECT setval(pg_get_serial_sequence('detalle_boleta', 'id_detalle'), (SELECT MAX(id_detalle) FROM detalle_boleta));

AD04 - ACTIVA
Proyecto realizado para la tarea evaluativa AD04 de Acceso a Datos.

La aplicación permite gestionar mediante consola los monitores, actividades, clientes y reservas de ACTIVA utilizando Java, Maven, JPA/Hibernate y MySQL.

Tecnologías utilizadas
Java 17
Maven
JPA / Jakarta Persistence
Hibernate
MySQL
MySQL Connector/J
Eclipse IDE
Base de datos
La aplicación utiliza la base de datos dbactiva proporcionada en el enunciado.

El archivo dbactiva.sql crea las siguientes tablas:

monitor
actividad
cliente
reserva

1. Crear monitor
Permite introducir el nombre y la especialidad de un monitor.

Se comprueba que los dos campos contengan texto y, después de guardar el registro correctamente, se muestra el identificador generado.

2. Crear actividad
Permite crear una actividad indicando:

Nombre
Localidad
Fecha y hora
Plazas totales
Identificador del monitor
Se comprueba que el monitor exista y que el número de plazas sea positivo.

Al crear la actividad, las plazas disponibles coinciden inicialmente con las plazas totales.

3. Buscar actividades disponibles
Permite buscar actividades utilizando:

Localidad
Día
Número de plazas necesarias
La búsqueda se realiza mediante una consulta JPQL parametrizada.

La comparación de la localidad no distingue entre mayúsculas y minúsculas. También se filtra por el día completo y por el número mínimo de plazas disponibles.

Los resultados se ordenan por fecha y hora.

4. Crear cliente
Permite crear un cliente indicando nombre y correo electrónico.

El programa:

Rechaza nombres vacíos.
Comprueba un formato básico del correo.
Elimina espacios extremos.
Normaliza el correo a minúsculas.
Impide registrar un correo que ya exista.
El identificador se muestra únicamente después de guardar correctamente el cliente.

5. Crear reserva
Permite crear una reserva indicando:

Identificador del cliente
Identificador de la actividad
Número de plazas
El programa comprueba que el cliente y la actividad existan y que haya plazas suficientes.

La creación de la reserva y el descuento de plazas se realizan utilizando el mismo EntityManager y dentro de una única transacción.

Si la operación termina correctamente se ejecuta commit().

Si se produce un error se realiza rollback().

6. Cancelar reserva
Permite cancelar una reserva mediante su identificador.

Las plazas reservadas se devuelven a la actividad y la reserva se elimina mediante JPA.

La devolución de plazas y la eliminación de la reserva se realizan dentro de la misma transacción.

Una reserva ya cancelada no puede devolver sus plazas una segunda vez.

7. Listar actividades
Muestra todas las actividades ordenadas por fecha y hora.

Para cada actividad se muestran:

ID
Nombre
Localidad
Fecha y hora
Monitor
Plazas totales
Plazas disponibles
Compilación y ejecución
Antes de ejecutar el proyecto:

Tener instalado Java 17.
Tener instalado MySQL.
Ejecutar una sola vez dbactiva.sql.
Configurar localmente la contraseña de MySQL en persistence.xml.
Para compilar mediante Maven:

mvn clean compile

Para ejecutar mediante Maven:

mvn exec:java

También puede ejecutarse directamente Main.java como aplicación Java desde Eclipse.

Pruebas realizadas
Durante las pruebas se comprobaron las principales funcionalidades de la aplicación.

Alta de monitor
Se creó correctamente un nuevo monitor y se comprobó que la aplicación mostraba el identificador generado.

Alta de actividad
Se creó una nueva actividad asociada a un monitor existente y con el número de plazas indicado.

Búsqueda de actividades
Se realizó una búsqueda por localidad, fecha y plazas disponibles, obteniendo correctamente la actividad correspondiente.

Alta de cliente
Se creó correctamente un nuevo cliente y se mostró su identificador.

También se intentó registrar nuevamente un correo existente utilizando distintas mayúsculas y minúsculas. La aplicación rechazó correctamente el correo duplicado.

Creación de reserva
Se realizó una reserva de 2 plazas sobre una actividad que disponía de 8 plazas.

Después de confirmar la operación quedaron 6 plazas disponibles.

También se intentó realizar una reserva solicitando más plazas de las disponibles. La operación fue rechazada y el aforo de la actividad no fue modificado.

Cancelación de reserva
Se canceló la reserva creada anteriormente.

Las 2 plazas fueron recuperadas y la actividad volvió a tener 8 plazas disponibles.

También se intentó cancelar una segunda vez la misma reserva. La aplicación informó de que la reserva ya no existía y no modificó las plazas.

Listado de actividades
Se comprobó que el listado mostrase todas las actividades ordenadas por fecha y hora, incluyendo los datos del monitor y las plazas totales y disponibles.

Gestión de errores
La aplicación controla diferentes situaciones incorrectas, entre ellas:

Datos de texto vacíos.
Fechas con formato incorrecto.
Valores numéricos incorrectos.
Plazas iguales o inferiores a cero.
Monitor inexistente.
Cliente inexistente.
Actividad inexistente.
Email con formato incorrecto.
Email duplicado.
Reserva con plazas insuficientes.
Cancelación de una reserva inexistente.
Seguridad
El repositorio no debe contener credenciales reales de MySQL.

Antes de subir el proyecto a GitHub, la propiedad correspondiente a la contraseña debe quedar con un valor genérico como:

CAMBIAR_CONTRASENA

Tampoco debe subirse la carpeta target, ya que contiene archivos generados durante la compilación.

Autor
Joseph Gelrud

DAM - Acceso a Datos
Tarea evaluativa AD04
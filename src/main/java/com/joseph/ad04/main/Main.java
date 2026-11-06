package com.joseph.ad04.main;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import com.joseph.ad04.config.JPAUtil;
import com.joseph.ad04.domain.Actividad;
import com.joseph.ad04.domain.Cliente;
import com.joseph.ad04.domain.Monitor;
import com.joseph.ad04.domain.Reserva;
import com.joseph.ad04.service.ActivaService;

public class Main {

    private static final Scanner SC = new Scanner(System.in);
    private static final ActivaService SERVICIO = new ActivaService();

    private static final DateTimeFormatter FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {

        try {
            boolean salir = false;

            while (!salir) {
                mostrarMenu();

                try {
                    int opcion = leerInt("Opcion: ");

                    switch (opcion) {
                        case 1 -> crearMonitor();
                        case 2 -> crearActividad();
                        case 3 -> buscarActividades();
                        case 4 -> crearCliente();
                        case 5 -> crearReserva();
                        case 6 -> cancelarReserva();
                        case 7 -> listarActividades();

                        case 8 -> {
                            salir = true;
                            System.out.println("Hasta pronto.");
                        }

                        default ->
                            System.out.println("Opcion incorrecta.");
                    }

                } catch (NumberFormatException | DateTimeParseException e) {
                    System.out.println(
                            "Entrada no valida. Revisa los numeros y las fechas.");
                } catch (Exception e) {
                    System.out.println(
                            "No se pudo completar la operacion: "
                                    + e.getMessage());
                }
            }

        } finally {
            JPAUtil.cerrar();
            SC.close();
        }
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("=== ACTIVA - Gestion de actividades ===");
        System.out.println("1. Crear monitor");
        System.out.println("2. Crear actividad");
        System.out.println("3. Buscar actividades disponibles");
        System.out.println("4. Crear cliente");
        System.out.println("5. Crear reserva");
        System.out.println("6. Cancelar reserva");
        System.out.println("7. Listar actividades");
        System.out.println("8. Salir");
    }

    private static void crearMonitor() {
        String nombre = leer("Nombre: ");
        String especialidad = leer("Especialidad: ");

        Monitor monitor =
                SERVICIO.crearMonitor(nombre, especialidad);

        System.out.println(
                "Monitor creado. ID: " + monitor.getId());
    }

    private static void crearActividad() {
        String nombre = leer("Nombre: ");
        String localidad = leer("Localidad: ");

        LocalDateTime fechaHora = LocalDateTime.parse(
                leer("Fecha y hora (dd/MM/yyyy HH:mm): "),
                FECHA_HORA);

        int plazas = leerInt("Plazas totales: ");
        long monitorId = leerLong("ID del monitor: ");

        Actividad actividad = SERVICIO.crearActividad(
                nombre,
                localidad,
                fechaHora,
                plazas,
                monitorId);

        System.out.println(
                "Actividad creada. ID: " + actividad.getId());
    }

    private static void buscarActividades() {
        String localidad = leer("Localidad: ");

        LocalDate dia = LocalDate.parse(
                leer("Dia (dd/MM/yyyy): "),
                FECHA);

        int plazas = leerInt("Plazas necesarias: ");

        List<Actividad> actividades =
                SERVICIO.buscarDisponibles(
                        localidad,
                        dia,
                        plazas);

        if (actividades.isEmpty()) {
            System.out.println(
                    "No hay actividades disponibles.");
        } else {
            actividades.forEach(Main::mostrarBusqueda);
        }
    }

    private static void crearCliente() {
        String nombre = leer("Nombre: ");
        String email = leer("Email: ");

        Cliente cliente =
                SERVICIO.crearCliente(nombre, email);

        System.out.println(
                "Cliente creado. ID: " + cliente.getId());
    }

    private static void crearReserva() {
        long clienteId = leerLong("ID del cliente: ");
        long actividadId = leerLong("ID de la actividad: ");
        int plazas = leerInt("Numero de plazas: ");

        Reserva reserva = SERVICIO.crearReserva(
                clienteId,
                actividadId,
                plazas);

        System.out.println(
                "Reserva confirmada. ID: "
                        + reserva.getId()
                        + ". Plazas restantes: "
                        + reserva.getActividad()
                                 .getPlazasDisponibles());
    }

    private static void cancelarReserva() {
        long reservaId = leerLong("ID de la reserva: ");

        ActivaService.Cancelacion resultado =
                SERVICIO.cancelarReserva(reservaId);

        if (resultado == null) {
            System.out.println(
                    "La reserva no existe. "
                    + "No se modificaron datos.");
        } else {
            System.out.println(
                    "Reserva cancelada. Plazas recuperadas: "
                            + resultado.plazasRecuperadas()
                            + ". Disponibles: "
                            + resultado.plazasDisponibles());
        }
    }

    private static void listarActividades() {
        List<Actividad> actividades =
                SERVICIO.listarActividades();

        if (actividades.isEmpty()) {
            System.out.println("No hay actividades.");
        } else {
            actividades.forEach(Main::mostrarCompleta);
        }
    }

    private static void mostrarBusqueda(Actividad actividad) {
        System.out.printf(
                "ID %d | %s | %s | monitor: %s"
                        + " | disponibles: %d%n",
                actividad.getId(),
                actividad.getNombre(),
                actividad.getFechaHora().format(FECHA_HORA),
                actividad.getMonitor().getNombre(),
                actividad.getPlazasDisponibles());
    }

    private static void mostrarCompleta(Actividad actividad) {
        System.out.printf(
                "ID %d | %s | %s | %s | monitor: %s"
                        + " | totales: %d | disponibles: %d%n",
                actividad.getId(),
                actividad.getNombre(),
                actividad.getLocalidad(),
                actividad.getFechaHora().format(FECHA_HORA),
                actividad.getMonitor().getNombre(),
                actividad.getPlazasTotales(),
                actividad.getPlazasDisponibles());
    }

    private static String leer(String mensaje) {
        System.out.print(mensaje);
        return SC.nextLine();
    }

    private static int leerInt(String mensaje) {
        return Integer.parseInt(leer(mensaje).trim());
    }

    private static long leerLong(String mensaje) {
        return Long.parseLong(leer(mensaje).trim());
    }
}
package com.joseph.ad04.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.joseph.ad04.config.JPAUtil;
import com.joseph.ad04.domain.Actividad;
import com.joseph.ad04.domain.Cliente;
import com.joseph.ad04.domain.Monitor;
import com.joseph.ad04.domain.Reserva;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class ActivaService {

    public Monitor crearMonitor(String nombre, String especialidad) {

        validarTexto(nombre, "nombre");
        validarTexto(especialidad, "especialidad");

        Monitor monitor = new Monitor(
                nombre.trim(),
                especialidad.trim());

        return persistir(monitor);
    }

    public Actividad crearActividad(
            String nombre,
            String localidad,
            LocalDateTime fechaHora,
            int plazas,
            long monitorId) {

        validarTexto(nombre, "nombre");
        validarTexto(localidad, "localidad");

        if (fechaHora == null || plazas <= 0) {
            throw new IllegalArgumentException(
                    "Fecha valida y plazas positivas obligatorias.");
        }

        EntityManager em = JPAUtil.crearEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            Monitor monitor = em.find(
                    Monitor.class,
                    monitorId);

            if (monitor == null) {
                throw new IllegalArgumentException(
                        "El monitor no existe.");
            }

            Actividad actividad = new Actividad(
                    nombre.trim(),
                    localidad.trim(),
                    fechaHora,
                    plazas,
                    monitor);

            em.persist(actividad);

            tx.commit();

            return actividad;

        } catch (RuntimeException e) {

            rollback(tx);
            throw e;

        } finally {

            em.close();
        }
    }

    public Cliente crearCliente(
            String nombre,
            String correo) {

        validarTexto(nombre, "nombre");
        validarTexto(correo, "email");

        String email = correo.trim().toLowerCase();

        if (!email.matches(
                "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {

            throw new IllegalArgumentException(
                    "Formato de email no valido.");
        }

        EntityManager em =
                JPAUtil.crearEntityManager();

        try {

            long total = em.createQuery(
                    "select count(c) "
                    + "from Cliente c "
                    + "where lower(c.email) = :email",
                    Long.class)
                    .setParameter("email", email)
                    .getSingleResult();

            if (total > 0) {
                throw new IllegalArgumentException(
                        "El email ya existe.");
            }

        } finally {

            em.close();
        }

        Cliente cliente =
                new Cliente(nombre.trim(), email);

        return persistir(cliente);
    }

    public List<Actividad> buscarDisponibles(
            String localidad,
            LocalDate dia,
            int plazas) {

        validarTexto(localidad, "localidad");

        if (dia == null || plazas <= 0) {
            throw new IllegalArgumentException(
                    "Dia valido y plazas positivas obligatorios.");
        }

        EntityManager em =
                JPAUtil.crearEntityManager();

        try {

            return em.createQuery(
                    "select a "
                    + "from Actividad a "
                    + "join fetch a.monitor "
                    + "where lower(a.localidad) = lower(:localidad) "
                    + "and a.fechaHora >= :inicio "
                    + "and a.fechaHora < :fin "
                    + "and a.plazasDisponibles >= :plazas "
                    + "order by a.fechaHora",
                    Actividad.class)

                    .setParameter(
                            "localidad",
                            localidad.trim())

                    .setParameter(
                            "inicio",
                            dia.atStartOfDay())

                    .setParameter(
                            "fin",
                            dia.plusDays(1).atStartOfDay())

                    .setParameter(
                            "plazas",
                            plazas)

                    .getResultList();

        } finally {

            em.close();
        }
    }

    public List<Actividad> listarActividades() {

        EntityManager em =
                JPAUtil.crearEntityManager();

        try {

            return em.createQuery(
                    "select a "
                    + "from Actividad a "
                    + "join fetch a.monitor "
                    + "order by a.fechaHora",
                    Actividad.class)
                    .getResultList();

        } finally {

            em.close();
        }
    }

    public Reserva crearReserva(
            long clienteId,
            long actividadId,
            int plazas) {

        if (plazas <= 0) {
            throw new IllegalArgumentException(
                    "Las plazas deben ser positivas.");
        }

        EntityManager em =
                JPAUtil.crearEntityManager();

        EntityTransaction tx =
                em.getTransaction();

        try {

            tx.begin();

            Cliente cliente =
                    em.find(
                            Cliente.class,
                            clienteId);

            Actividad actividad =
                    em.find(
                            Actividad.class,
                            actividadId);

            if (cliente == null) {
                throw new IllegalArgumentException(
                        "El cliente no existe.");
            }

            if (actividad == null) {
                throw new IllegalArgumentException(
                        "La actividad no existe.");
            }

            if (actividad.getPlazasDisponibles()
                    < plazas) {

                throw new IllegalArgumentException(
                        "No hay plazas suficientes.");
            }

            Reserva reserva =
                    new Reserva(
                            LocalDateTime.now(),
                            plazas,
                            actividad,
                            cliente);

            em.persist(reserva);

            actividad.setPlazasDisponibles(
                    actividad.getPlazasDisponibles()
                            - plazas);

            tx.commit();

            return reserva;

        } catch (RuntimeException e) {

            rollback(tx);
            throw e;

        } finally {

            em.close();
        }
    }

    public Cancelacion cancelarReserva(
            long reservaId) {

        EntityManager em =
                JPAUtil.crearEntityManager();

        EntityTransaction tx =
                em.getTransaction();

        try {

            tx.begin();

            Reserva reserva =
                    em.find(
                            Reserva.class,
                            reservaId);

            if (reserva == null) {

                tx.rollback();

                return null;
            }

            Actividad actividad =
                    reserva.getActividad();

            int plazasRecuperadas =
                    reserva.getNumeroPlazas();

            actividad.setPlazasDisponibles(
                    actividad.getPlazasDisponibles()
                            + plazasRecuperadas);

            int plazasDisponibles =
                    actividad.getPlazasDisponibles();

            em.remove(reserva);

            tx.commit();

            return new Cancelacion(
                    plazasRecuperadas,
                    plazasDisponibles);

        } catch (RuntimeException e) {

            rollback(tx);
            throw e;

        } finally {

            em.close();
        }
    }

    private <T> T persistir(T entidad) {

        EntityManager em =
                JPAUtil.crearEntityManager();

        EntityTransaction tx =
                em.getTransaction();

        try {

            tx.begin();

            em.persist(entidad);

            tx.commit();

            return entidad;

        } catch (RuntimeException e) {

            rollback(tx);
            throw e;

        } finally {

            em.close();
        }
    }

    private static void validarTexto(
            String valor,
            String campo) {

        if (valor == null || valor.isBlank()) {

            throw new IllegalArgumentException(
                    "El campo "
                    + campo
                    + " debe contener texto.");
        }
    }

    private static void rollback(
            EntityTransaction tx) {

        if (tx.isActive()) {
            tx.rollback();
        }
    }

    public record Cancelacion(
            int plazasRecuperadas,
            int plazasDisponibles) {
    }
}
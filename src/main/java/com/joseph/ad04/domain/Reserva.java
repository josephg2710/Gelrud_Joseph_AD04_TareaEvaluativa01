package com.joseph.ad04.domain;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="reserva") public class Reserva {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="fecha_reserva",nullable=false) private LocalDateTime fechaReserva;
 @Column(name="numero_plazas",nullable=false) private int numeroPlazas;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="actividad_id",nullable=false) private Actividad actividad;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="cliente_id",nullable=false) private Cliente cliente;
 protected Reserva(){} public Reserva(LocalDateTime f,int n,Actividad a,Cliente c){fechaReserva=f;numeroPlazas=n;actividad=a;cliente=c;}
 public Long getId(){return id;} public LocalDateTime getFechaReserva(){return fechaReserva;} public int getNumeroPlazas(){return numeroPlazas;} public Actividad getActividad(){return actividad;} public Cliente getCliente(){return cliente;}
}
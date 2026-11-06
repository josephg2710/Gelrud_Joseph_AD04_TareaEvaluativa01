package com.joseph.ad04.domain;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="actividad") public class Actividad {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="nombre",length=120,nullable=false) private String nombre;
 @Column(name="localidad",length=100,nullable=false) private String localidad;
 @Column(name="fecha_hora",nullable=false) private LocalDateTime fechaHora;
 @Column(name="plazas_totales",nullable=false) private int plazasTotales;
 @Column(name="plazas_disponibles",nullable=false) private int plazasDisponibles;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="monitor_id",nullable=false) private Monitor monitor;
 protected Actividad(){} public Actividad(String n,String l,LocalDateTime f,int p,Monitor m){nombre=n;localidad=l;fechaHora=f;plazasTotales=p;plazasDisponibles=p;asignarMonitor(m);}
 public Long getId(){return id;} public String getNombre(){return nombre;} public String getLocalidad(){return localidad;} public LocalDateTime getFechaHora(){return fechaHora;} public int getPlazasTotales(){return plazasTotales;} public int getPlazasDisponibles(){return plazasDisponibles;} public Monitor getMonitor(){return monitor;}
 public void asignarMonitor(Monitor m){monitor=m;if(m!=null&&!m.getActividades().contains(this))m.getActividades().add(this);} public void setPlazasDisponibles(int p){plazasDisponibles=p;}
}
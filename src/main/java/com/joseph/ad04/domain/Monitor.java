package com.joseph.ad04.domain;
import jakarta.persistence.*; import java.util.ArrayList; import java.util.List;
@Entity @Table(name="monitor") public class Monitor {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="nombre",length=100,nullable=false) private String nombre;
 @Column(name="especialidad",length=100,nullable=false) private String especialidad;
 @OneToMany(mappedBy="monitor") private List<Actividad> actividades=new ArrayList<>();
 protected Monitor(){} public Monitor(String nombre,String especialidad){this.nombre=nombre;this.especialidad=especialidad;}
 public Long getId(){return id;} public String getNombre(){return nombre;} public String getEspecialidad(){return especialidad;} public List<Actividad> getActividades(){return actividades;}
 public void anadirActividad(Actividad a){if(a!=null&&!actividades.contains(a))actividades.add(a); if(a!=null&&a.getMonitor()!=this)a.asignarMonitor(this);}
}
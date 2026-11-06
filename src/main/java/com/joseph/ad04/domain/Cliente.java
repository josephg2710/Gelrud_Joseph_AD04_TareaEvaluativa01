package com.joseph.ad04.domain;
import jakarta.persistence.*;
@Entity @Table(name="cliente") public class Cliente {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="nombre",length=100,nullable=false) private String nombre;
 @Column(name="email",length=150,nullable=false,unique=true) private String email;
 protected Cliente(){} public Cliente(String n,String e){nombre=n;email=e;} public Long getId(){return id;} public String getNombre(){return nombre;} public String getEmail(){return email;}
}
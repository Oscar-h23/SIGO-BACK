package com.sigo.asistencia.entity;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="turnos") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Turno { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false,unique=true,length=1) private String codigo; @Column(length=30) private String nombre; @Column(name="personal_programado",nullable=false) private Integer personalProgramado; }

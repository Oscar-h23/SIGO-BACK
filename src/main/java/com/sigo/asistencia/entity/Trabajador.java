package com.sigo.asistencia.entity;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="trabajadores") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Trabajador { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false,unique=true) private Integer codigo; @Column(name="nombre_completo",nullable=false,length=150) private String nombreCompleto; @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="puesto_id",nullable=false) private Puesto puesto; @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="plaza_id") private Plaza plaza; @Column(nullable=false) private Boolean activo=true; }

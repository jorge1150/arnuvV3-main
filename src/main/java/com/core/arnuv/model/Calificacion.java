package com.core.arnuv.model;

import java.util.Date;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.ToString;

@Data
@Comment("Tabla que almacena la calificacion del paseador")
@Entity
public class Calificacion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private int id;
	
	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fecha;

	/*@ManyToOne()
	@Comment("Codigo de personas")
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "idpersonapasedor")
	@ToString.Exclude
	private Personadetalle idpersonapasedor;

	@ManyToOne()
	@Comment("Codigo de personas")
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "idpersonacliente")
	@ToString.Exclude
	private Personadetalle idpersonacliente;
	*/
	@ManyToOne()
	@Comment("Codigo de paseo")
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "idpaseo",  unique=true)
	@ToString.Exclude
	private Paseo idpaseo;
	
	
	@Column
	private int calificacion;
	
	@Transient
	private int paseoID;

}

package com.core.arnuv.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import com.core.arnuv.enums.TamanoPerroEnum;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Comment("Tabla que almacena detalle de informacion de una persona")
@Entity
@Table(name = "personadetalle")
public class Personadetalle implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Comment("Codigo de personas")
	@Column(name = "idpersona")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Comment("Codigo de usuario de ingreso.")
	@Column(name = "idusuarioing", length = 20)
	private String idusuarioing;

	@Comment("Codigo de usuario de modificacion")
	@Column(name = "idusuariomod", length = 20)
	private String idusuariomod;

	@Comment("Fecha de ingreso del registro")
	@Column(name = "fechaingreso")
	@Temporal(TemporalType.DATE)
	private Date fechaingreso;

	@Comment("Fecha de modificacion del registro")
	@Column(name = "fechamodificacion")
	@Temporal(TemporalType.DATE)
	private Date fechamodificacion;

	@Comment("Nombre de la persona")
	@Column(name = "nombres", length = 120)
	private String nombres;

	@Comment("Apellido de la persona")
	@Column(name = "apellidos", length = 120)
	private String apellidos;


	@Comment("Identificacion, cedula, ruc, pasaporte")
	@Column(name = "identificacion", length = 15, unique=true)
	private String identificacion;

	@Comment("Numero de celular")
	@Column(name = "celular", length = 20, unique=true)
	private String celular;

	@Comment("Correo electronico")
	@Column(name = "email", length = 150, unique=true)
	private String email;
	

    @Column(nullable = true)
    private String experienciaPrevia;
    

    @Column(nullable = true)
    private Integer aniosExperiencia;

	@OneToMany(mappedBy = "idpersona")
	private List<MascotaDetalle> mascotaDetalles;

	@OneToMany(mappedBy = "idpersonapasedor")
	private List<Paseo> paseospaseador;

	@OneToMany(mappedBy = "idpersonacliente")
	private List<Paseo> paseoscliente;

	@OneToMany(mappedBy = "idpersona")
	private List<Personadireccion> personadireccions;

	@OneToMany(mappedBy = "idpersona")
	private List<Usuariodetalle> usuariodetalles;
	// ubicacion
	@OneToMany(mappedBy = "idpersona")
	private List<Ubicacion> ubicaciones;
	
	@OneToMany(mappedBy = "persona")
	private List<RecordAcademico> recordAcademico;
   

	@Enumerated(EnumType.STRING) 
	private Set<TamanoPerroEnum> tamanosAceptados = new HashSet<>();

	/*
	@OneToMany(mappedBy = "idpersonapasedor")
	private List<Calificacion> calificacionpaseador;

	@OneToMany(mappedBy = "idpersonacliente")
	private List<Calificacion> calificacioncliente;
	*/
}

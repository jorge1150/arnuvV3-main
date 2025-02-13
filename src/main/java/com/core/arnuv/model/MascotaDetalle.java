package com.core.arnuv.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.web.multipart.MultipartFile;

import com.core.arnuv.enums.TamanoPerroEnum;


@Data
@Comment("Tabla que almacena el detalle de la mascota")
@Entity
@Table(name = "mascotadetalle")
public class MascotaDetalle  {
	
	//private static final long serialVersionUID = 1L;
	@Id
    @Comment("Codigo de la mascota")
    //@Column(name = "idmascota")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idmascota;

    @ManyToOne()
    @Comment("Codigo de personas")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "idpersona")
    @ToString.Exclude
    private Personadetalle idpersona;

    @ManyToOne()
    //@JoinColumns({ @JoinColumn(name = "idcatalogorasa", referencedColumnName = "iddetalle"),  @JoinColumn(name = "iddetallerasa", referencedColumnName = "idcatalogo")    })
    //@Comment("Codigo de catalogo")   // @OnDelete(action = OnDeleteAction.RESTRICT)    //@ToString.Exclude
    @JoinColumn(name="id_catalogodetalle")
    private CatalogoDetalle fkcatalogodetalle;

    @Comment("Nombre de la mascota")
    @Column(name = "nombre", length = 120)
    private String nombre;

    @Comment("Edad de la mascota")
    @Column(name = "edad", precision = 2)
    private Integer edad;
    
   
    @OneToMany(mappedBy = "idMascota")
	private List<Paseo> paseos= new ArrayList<>();

    @Comment("Url de la foto de la mascota")
    private String urlPhotoPet;

    @Comment("Foto de la mascota")
    @Transient
    private MultipartFile photoPet;
    
    private String observacion;
    
    @Enumerated(EnumType.STRING)
    private TamanoPerroEnum tamanoPerro;

}

package com.teknei.rest.coches.modelo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase para representar coches. Esta clase tiene los siguientes atributos:
 * <ul>
 * <li>id: <code>Long</code></li>
 * <li>modelo: <code>String</code></li>
 * <li>matricula: <code>String</code></li>
 * <li>marca: <code>Marcas</code>
 * </ul>
 * Los constructores (sin atributos y con todos), getters y setters, toString,
 * equals y HashCode han sido creados automáticamente con Lombok.
 * 
 * @author Lander Corral
 * @version 1.0
 */
@Entity
@Table(name = "COCHES")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coches implements Serializable {

	private static final long serialVersionUID = 8203505445878623750L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String modelo;

	@Column(nullable = false, length = 8)
	private String matricula;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "marca_id")
	private Marcas marca;
}

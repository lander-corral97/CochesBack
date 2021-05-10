package com.teknei.rest.coches.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
@Table(name = "Coches")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Coches {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String modelo;

	@Column(nullable = false, length = 8)
	private String matricula;

	@ManyToOne
	@JoinColumn(name = "marca_id")
	private Marcas marca;
}

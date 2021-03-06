package com.teknei.rest.coches.modelo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Clase para representar marcas de coches. Esta clase tiene los siguientes
 * atributos:
 * <ul>
 * <li>id: <code>Long</code></li>
 * <li>nombre: <code>String</code></li>
 * <li>coches: <code><b>Set<</b>Coches<b>></b></code>
 * </ul>
 * Los constructores (sin atributos y con todos), getters y setters, toString,
 * equals y HashCode han sido creados automáticamente con Lombok.
 * 
 * @author Lander Corral
 * @version 1.0
 */
@Entity
@Table(name = "MARCAS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Marcas implements Serializable {

	private static final long serialVersionUID = 2649017482411834190L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nombre;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonBackReference
	@OneToMany(mappedBy = "marca", fetch= FetchType.LAZY)
	private final Set<Coches> coches = new HashSet<>();

}

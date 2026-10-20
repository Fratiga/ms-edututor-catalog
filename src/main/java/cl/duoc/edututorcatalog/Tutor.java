package cl.duoc.edututorcatalog;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotBlank;

// Un tutor del directorio y los servicios (asignaturas) que puede dictar.
// ms-edututor-sessions guarda solo su id; el nombre se resuelve contra este
// catálogo.
@Entity
public class Tutor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String nombre;

	private boolean activo = true;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "tutor_servicio", joinColumns = @JoinColumn(name = "tutor_id"))
	@Column(name = "servicio_id")
	private Set<Long> servicioIds = new HashSet<>();

	protected Tutor() {
	}

	public Tutor(String nombre) {
		this.nombre = nombre;
	}

	public Long getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public Set<Long> getServicioIds() {
		return servicioIds;
	}

	public void setServicioIds(Set<Long> servicioIds) {
		this.servicioIds = servicioIds;
	}
}

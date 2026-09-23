package cl.duoc.edututorcatalog;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// Una asignatura/tutoría ofrecida por un tutor en un bloque horario, con cupo.
@Entity
public class Servicio {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String asignatura;

	@NotBlank
	private String tutor;

	@NotBlank
	private String bloqueHorario;

	@Min(0)
	private int cupoTotal;

	@Min(0)
	private int cupoDisponible;

	// Complementarios al modelo original de cupos: el frontend oficial los
	// necesita para el catálogo comercial (precio, categoría, descripción).
	private String descripcion;

	private String categoria;

	@Min(0)
	private double precioHora;

	@Min(0)
	private int duracionMinutos = 60;

	@Enumerated(EnumType.STRING)
	private EstadoServicio estado = EstadoServicio.ACTIVO;

	// Bloqueo optimista: evita que dos coordinadores confirmen el mismo
	// último cupo a la vez (lost update en escrituras concurrentes).
	@Version
	private long version;

	protected Servicio() {
	}

	public Servicio(String asignatura, String tutor, String bloqueHorario, int cupoTotal) {
		this.asignatura = asignatura;
		this.tutor = tutor;
		this.bloqueHorario = bloqueHorario;
		this.cupoTotal = cupoTotal;
		this.cupoDisponible = cupoTotal;
	}

	public Long getId() {
		return id;
	}

	public String getAsignatura() {
		return asignatura;
	}

	public void setAsignatura(String asignatura) {
		this.asignatura = asignatura;
	}

	public String getTutor() {
		return tutor;
	}

	public void setTutor(String tutor) {
		this.tutor = tutor;
	}

	public String getBloqueHorario() {
		return bloqueHorario;
	}

	public void setBloqueHorario(String bloqueHorario) {
		this.bloqueHorario = bloqueHorario;
	}

	public int getCupoTotal() {
		return cupoTotal;
	}

	public void setCupoTotal(int cupoTotal) {
		this.cupoTotal = cupoTotal;
	}

	public int getCupoDisponible() {
		return cupoDisponible;
	}

	public void setCupoDisponible(int cupoDisponible) {
		this.cupoDisponible = cupoDisponible;
	}

	public long getVersion() {
		return version;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public double getPrecioHora() {
		return precioHora;
	}

	public void setPrecioHora(double precioHora) {
		this.precioHora = precioHora;
	}

	public int getDuracionMinutos() {
		return duracionMinutos;
	}

	public void setDuracionMinutos(int duracionMinutos) {
		this.duracionMinutos = duracionMinutos;
	}

	public EstadoServicio getEstado() {
		return estado;
	}

	public void setEstado(EstadoServicio estado) {
		this.estado = estado;
	}
}

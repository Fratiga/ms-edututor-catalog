package cl.duoc.edututorcatalog;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/catalog/services")
public class ServicioController {

	private final ServicioRepository repository;

	public ServicioController(ServicioRepository repository) {
		this.repository = repository;
	}

	// GET /api/catalog/services
	@GetMapping
	public List<Servicio> listar() {
		return repository.findAll();
	}

	// POST /api/catalog/services
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Servicio crear(@Valid @RequestBody CrearServicioRequest req) {
		Servicio servicio = new Servicio(req.asignatura(), req.tutor(), req.bloqueHorario(), req.cupoTotal());
		servicio.setDescripcion(req.descripcion());
		servicio.setCategoria(req.categoria());
		if (req.precioHora() != null) {
			servicio.setPrecioHora(req.precioHora());
		}
		if (req.duracionMinutos() != null) {
			servicio.setDuracionMinutos(req.duracionMinutos());
		}
		return repository.save(servicio);
	}

	// PUT /api/catalog/services/{id} (cupo/bloque/comercial)
	@PutMapping("/{id}")
	public Servicio actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarServicioRequest req) {
		Servicio servicio = repository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Servicio no encontrado: " + id));

		if (req.bloqueHorario() != null) {
			servicio.setBloqueHorario(req.bloqueHorario());
		}
		if (req.cupoDisponible() != null) {
			servicio.setCupoDisponible(req.cupoDisponible());
		}
		if (req.descripcion() != null) {
			servicio.setDescripcion(req.descripcion());
		}
		if (req.categoria() != null) {
			servicio.setCategoria(req.categoria());
		}
		if (req.precioHora() != null) {
			servicio.setPrecioHora(req.precioHora());
		}
		if (req.duracionMinutos() != null) {
			servicio.setDuracionMinutos(req.duracionMinutos());
		}
		if (req.estado() != null) {
			servicio.setEstado(req.estado());
		}
		return repository.save(servicio);
	}

	public record CrearServicioRequest(String asignatura, String tutor, String bloqueHorario, int cupoTotal,
			String descripcion, String categoria, Double precioHora, Integer duracionMinutos) {
	}

	public record ActualizarServicioRequest(String bloqueHorario, Integer cupoDisponible, String descripcion,
			String categoria, Double precioHora, Integer duracionMinutos, EstadoServicio estado) {
	}
}

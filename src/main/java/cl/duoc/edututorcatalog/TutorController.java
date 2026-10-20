package cl.duoc.edututorcatalog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

// Directorio de tutores: quién puede dictar cada servicio. Lo consume la
// asignación de sesiones (el coordinador elige entre los tutores elegibles).
@RestController
@RequestMapping("/api/catalog/tutors")
public class TutorController {

	private final TutorRepository tutores;
	private final ServicioRepository servicios;

	public TutorController(TutorRepository tutores, ServicioRepository servicios) {
		this.tutores = tutores;
		this.servicios = servicios;
	}

	// GET /api/catalog/tutors (todos; el frontend filtra por servicio y estado)
	@GetMapping
	public List<TutorResponse> listar() {
		return tutores.findAll(Sort.by("nombre")).stream().map(TutorResponse::from).toList();
	}

	// POST /api/catalog/tutors
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TutorResponse crear(@Valid @RequestBody CrearTutorRequest req) {
		String nombre = req.nombre().trim();
		if (tutores.existsByNombreIgnoreCase(nombre)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un tutor con ese nombre: " + nombre);
		}
		Tutor tutor = new Tutor(nombre);
		tutor.setServicioIds(validarServicios(req.servicioIds()));
		return TutorResponse.from(tutores.save(tutor));
	}

	// PUT /api/catalog/tutors/{id} (nombre, estado y servicios; todo opcional)
	@PutMapping("/{id}")
	public TutorResponse actualizar(@PathVariable Long id, @RequestBody ActualizarTutorRequest req) {
		Tutor tutor = tutores.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor no encontrado: " + id));

		if (req.nombre() != null) {
			String nombre = req.nombre().trim();
			if (nombre.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre no puede estar vacío");
			}
			if (tutores.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un tutor con ese nombre: " + nombre);
			}
			tutor.setNombre(nombre);
		}
		if (req.activo() != null) {
			tutor.setActivo(req.activo());
		}
		if (req.servicioIds() != null) {
			tutor.setServicioIds(validarServicios(req.servicioIds()));
		}
		return TutorResponse.from(tutores.save(tutor));
	}

	private Set<Long> validarServicios(List<Long> ids) {
		Set<Long> resultado = new HashSet<>();
		if (ids == null) {
			return resultado;
		}
		for (Long servicioId : ids) {
			if (servicioId == null || !servicios.existsById(servicioId)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El servicio no existe: " + servicioId);
			}
			resultado.add(servicioId);
		}
		return resultado;
	}

	public record CrearTutorRequest(@NotBlank String nombre, List<Long> servicioIds) {
	}

	public record ActualizarTutorRequest(String nombre, Boolean activo, List<Long> servicioIds) {
	}

	// ids como String, igual que el resto del contrato del frontend.
	public record TutorResponse(String id, String nombre, boolean activo, List<String> servicioIds) {
		static TutorResponse from(Tutor t) {
			return new TutorResponse(String.valueOf(t.getId()), t.getNombre(), t.isActivo(),
				t.getServicioIds().stream().sorted().map(String::valueOf).toList());
		}
	}
}

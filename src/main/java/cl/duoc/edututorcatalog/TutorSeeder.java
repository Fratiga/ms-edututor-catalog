package cl.duoc.edututorcatalog;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

// Arma el directorio inicial a partir de los servicios ya sembrados: cada tutor
// que figura en un servicio queda habilitado para todos los servicios de las
// categorías que ya dicta (así hay más de un tutor elegible por asignatura).
// Solo corre si la tabla de tutores está vacía.
@Component
@Order(20)
public class TutorSeeder implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(TutorSeeder.class);

	private final TutorRepository tutores;
	private final ServicioRepository servicios;

	public TutorSeeder(TutorRepository tutores, ServicioRepository servicios) {
		this.tutores = tutores;
		this.servicios = servicios;
	}

	@Override
	public void run(String... args) {
		if (tutores.count() > 0) {
			return;
		}
		List<Servicio> todos = servicios.findAll(Sort.by("id"));

		Map<String, Set<String>> categoriasPorTutor = new LinkedHashMap<>();
		for (Servicio servicio : todos) {
			categoriasPorTutor.computeIfAbsent(servicio.getTutor(), k -> new LinkedHashSet<>())
				.add(servicio.getCategoria());
		}

		categoriasPorTutor.forEach((nombre, categorias) -> {
			Tutor tutor = new Tutor(nombre);
			tutor.setServicioIds(todos.stream()
				.filter(s -> categorias.contains(s.getCategoria()))
				.map(Servicio::getId)
				.collect(Collectors.toSet()));
			tutores.save(tutor);
		});

		log.info("Directorio sembrado con {} tutores", tutores.count());
	}
}

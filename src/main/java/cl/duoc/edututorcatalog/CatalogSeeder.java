package cl.duoc.edututorcatalog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// Sin esto, un Estudiante recién agrega el sistema y no tiene nada que ver ni
// agendar: solo corre si la tabla está vacía, así que no duplica datos en
// reinicios normales (solo aplica en un volumen de Oracle realmente nuevo).
@Component
public class CatalogSeeder implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(CatalogSeeder.class);

	private final ServicioRepository repository;

	public CatalogSeeder(ServicioRepository repository) {
		this.repository = repository;
	}

	@Override
	public void run(String... args) {
		if (repository.count() > 0) {
			return;
		}

		crear("Cálculo diferencial", "Valentina Ruiz", "Lun-Mié 15:00-16:00", 10,
			"Acompañamiento para dominar límites, derivadas y aplicaciones con práctica guiada.",
			"Matemáticas", 28.0, 60);
		crear("Inglés conversacional", "Andrés Molina", "Mar-Jue 13:00-13:45", 12,
			"Sesiones de conversación enfocadas en fluidez, pronunciación y confianza.",
			"Idiomas", 24.0, 45);
		crear("Química orgánica", "Sofía Benítez", "Vie 17:00-18:00", 8,
			"Resuelve problemas complejos con mapas visuales y explicaciones paso a paso.",
			"Ciencias", 31.0, 60);
		crear("Estadística aplicada", "Diego Serra", "Lun-Vie 09:00-10:00", 10,
			"De los datos a las decisiones: probabilidad, regresión y lectura crítica.",
			"Matemáticas", 26.0, 60);
		crear("Redacción académica", "Camila Torres", "Mié-Vie 16:00-16:50", 9,
			"Estructura argumentos claros y edita trabajos con criterio y precisión.",
			"Humanidades", 22.0, 50);

		log.info("Catálogo sembrado con {} servicios iniciales", repository.count());
	}

	private void crear(String asignatura, String tutor, String bloqueHorario, int cupoTotal,
			String descripcion, String categoria, double precioHora, int duracionMinutos) {
		Servicio servicio = new Servicio(asignatura, tutor, bloqueHorario, cupoTotal);
		servicio.setDescripcion(descripcion);
		servicio.setCategoria(categoria);
		servicio.setPrecioHora(precioHora);
		servicio.setDuracionMinutos(duracionMinutos);
		repository.save(servicio);
	}
}

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

		// El orden de los primeros 5 importa: SessionSeeder (ms-edututor-sessions)
		// referencia servicioId 1..5 por posición.
		crear("Cálculo Diferencial", "Valentina Ruiz", "Lun-Mié 15:00-16:00", 10,
			"Límites, derivadas y aplicaciones. Ejercicios prácticos y preparación de certámenes.",
			"Matemáticas", 28.0, 60);
		crear("Inglés Conversacional", "Andrés Molina", "Mar-Jue 13:00-13:45", 12,
			"Práctica de fluidez, pronunciación y vocabulario para situaciones cotidianas.",
			"Idiomas", 24.0, 45);
		crear("Química Orgánica", "Sofía Benítez", "Vie 17:00-18:00", 8,
			"Estructura, nomenclatura y reacciones de compuestos orgánicos. Enfoque en mecanismos.",
			"Ciencias", 31.0, 60);
		crear("Estadística Aplicada", "Diego Serra", "Lun-Vie 09:00-10:00", 10,
			"Descriptiva, probabilidades, inferencia y uso de software estadístico.",
			"Matemáticas", 26.0, 60);
		crear("Redacción Académica", "Camila Torres", "Mié-Vie 16:00-16:50", 9,
			"Estructura de textos argumentativos, citación y corrección de estilo.",
			"Humanidades", 22.0, 50);
		crear("Física Mecánica", "Carlos Mendoza", "Mar-Jue 10:00-11:00", 8,
			"Cinemática, dinámica, energía y momentum. Resolución de problemas tipo examen.",
			"Ciencias", 30.0, 60);
		crear("Programación en Python", "Laura Rojas", "Lun-Vie 18:00-19:00", 15,
			"Fundamentos, estructuras de datos, POO y proyectos prácticos.",
			"Tecnología", 35.0, 60);
		crear("Historia Universal", "Javier Ortiz", "Mié 14:00-15:30", 12,
			"Grandes procesos históricos, análisis de fuentes y ensayos.",
			"Humanidades", 20.0, 90);
		crear("Cálculo Integral", "Valentina Ruiz", "Mar-Jue 16:00-17:00", 10,
			"Integrales definidas e indefinidas, técnicas de integración y aplicaciones.",
			"Matemáticas", 28.0, 60);
		crear("Inglés para Negocios", "Andrés Molina", "Vie 11:00-12:00", 6,
			"Vocabulario corporativo, presentaciones y negociación en inglés.",
			"Idiomas", 32.0, 60);
		crear("Bioquímica", "Sofía Benítez", "Lun-Mié 09:00-10:00", 8,
			"Metabolismo, enzimas y biomoléculas. Casos clínicos y ejercicios.",
			"Ciencias", 33.0, 60);
		crear("Álgebra Lineal", "Diego Serra", "Mar-Vie 08:00-09:00", 10,
			"Matrices, espacios vectoriales, transformaciones lineales y valores propios.",
			"Matemáticas", 26.0, 60);
		crear("Escritura Creativa", "Camila Torres", "Jue 17:00-18:30", 10,
			"Narrativa, poesía y técnicas de escritura para proyectos personales.",
			"Humanidades", 25.0, 90);

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

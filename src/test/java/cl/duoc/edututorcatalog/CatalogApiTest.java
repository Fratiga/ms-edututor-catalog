package cl.duoc.edututorcatalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

// Pruebas básicas del catálogo que no necesitan base de datos: el repositorio se
// reemplaza por un doble y se comprueba el contrato HTTP y el filtro de identidad.
class CatalogApiTest {

	private ServicioRepository repositorio;
	private MockMvc mvc;

	@BeforeEach
	void preparar() {
		repositorio = mock(ServicioRepository.class);
		mvc = MockMvcBuilders.standaloneSetup(new ServicioController(repositorio)).build();
	}

	private static Servicio servicio(long id, String asignatura) {
		Servicio s = new Servicio(asignatura, "Tutora Ana", "LUN 10:00", 5);
		ReflectionTestUtils.setField(s, "id", id);
		return s;
	}

	@Test
	void listaLosServiciosConElContratoDelFrontend() throws Exception {
		when(repositorio.findAll()).thenReturn(List.of(servicio(7, "Cálculo")));

		mvc.perform(get("/api/catalog/services"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value("7"))
			.andExpect(jsonPath("$[0].nombre").value("Cálculo"))
			.andExpect(jsonPath("$[0].tutorNombre").value("Tutora Ana"))
			.andExpect(jsonPath("$[0].estado").value("ACTIVO"));
	}

	@Test
	void creaUnServicioYDevuelve201() throws Exception {
		when(repositorio.save(any(Servicio.class))).thenAnswer(i -> i.getArgument(0));

		mvc.perform(post("/api/catalog/services").contentType(MediaType.APPLICATION_JSON)
				.content("{\"asignatura\":\"Física\",\"tutor\":\"Luis\",\"bloqueHorario\":\"MAR 15:00\",\"cupoTotal\":4,"
					+ "\"precioHora\":12000}"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.nombre").value("Física"))
			.andExpect(jsonPath("$.precioHora").value(12000.0))
			.andExpect(jsonPath("$.cupoTotal").value(4));
	}

	@Test
	void actualizarUnServicioInexistenteDa404() throws Exception {
		when(repositorio.findById(99L)).thenReturn(Optional.empty());

		mvc.perform(put("/api/catalog/services/99").contentType(MediaType.APPLICATION_JSON)
				.content("{\"cupoDisponible\":1}"))
			.andExpect(status().isNotFound());
		verify(repositorio, never()).save(any());
	}

	@Test
	void actualizaSoloLosCamposEnviados() throws Exception {
		Servicio existente = servicio(3, "Química");
		when(repositorio.findById(3L)).thenReturn(Optional.of(existente));
		when(repositorio.save(any(Servicio.class))).thenAnswer(i -> i.getArgument(0));

		mvc.perform(put("/api/catalog/services/3").contentType(MediaType.APPLICATION_JSON)
				.content("{\"cupoDisponible\":2}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.cupoDisponible").value(2))
			.andExpect(jsonPath("$.nombre").value("Química"));
	}

	@Test
	void elFiltroExigeLaIdentidadQueAgregaElBff() throws Exception {
		RequireBffContextFilter filtro = new RequireBffContextFilter();

		MockHttpServletResponse sinCabecera = new MockHttpServletResponse();
		MockFilterChain cadenaSin = new MockFilterChain();
		filtro.doFilter(new MockHttpServletRequest("GET", "/api/catalog/services"), sinCabecera, cadenaSin);
		assertThat(sinCabecera.getStatus()).isEqualTo(401);
		assertThat(cadenaSin.getRequest()).as("la petición no debe llegar al controlador").isNull();

		MockHttpServletRequest conCabecera = new MockHttpServletRequest("GET", "/api/catalog/services");
		conCabecera.addHeader("X-User-Id", "u1");
		MockFilterChain cadenaCon = new MockFilterChain();
		filtro.doFilter(conCabecera, new MockHttpServletResponse(), cadenaCon);
		assertThat(cadenaCon.getRequest()).isNotNull();
	}
}

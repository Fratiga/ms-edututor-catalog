package cl.duoc.edututorcatalog;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Zero-Trust hacia adentro: este microservicio no valida el JWT (ya lo hizo
// el BFF), pero tampoco confía ciegamente en cualquiera que le llegue.
// Exige el contexto que solo el BFF propaga. En producción esto se refuerza
// además con el Security Group que solo permite tráfico desde el BFF.
@Component
public class RequireBffContextFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		if (request.getHeader("X-User-Id") == null) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Falta contexto de identidad del BFF (X-User-Id)");
			return;
		}
		chain.doFilter(request, response);
	}
}

package com.uvg.pooproyect.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
            throws ServletException, IOException {

        String path = req.getRequestURI();
        HttpSession sesion = req.getSession(false);
        String rol = sesion == null ? null : (String) sesion.getAttribute("rol");

        // "/" manda a cada quien a su pantalla
        if (path.equals("/")) {
            resp.sendRedirect(destinoSegunRol(rol));
            return;
        }
        if (path.equals("/login.html") || path.startsWith("/css/") || path.startsWith("/js/")
                || path.startsWith("/api/auth/") || path.equals("/favicon.ico")) {
            chain.doFilter(req, resp);
            return;
        }

        boolean esApi = path.startsWith("/api/");

        if (rol == null) {
            denegar(resp, esApi, 401, "/login.html");
            return;
        }

        // El administrador tiene acceso a todo el panel administrativo y APIs
        if (AuthController.ROL_ADMIN.equals(rol)) {
            chain.doFilter(req, resp);
            return;
        }

        // El estudiante solo tiene acceso a sus vistas y a sus endpoints autorizados
        if (AuthController.ROL_ESTUDIANTE.equals(rol)) {
            if (esApi) {
                if (estudiantePuedeUsarApi(req.getMethod(), path, sesion)) {
                    chain.doFilter(req, resp);
                } else {
                    denegar(resp, true, 403, null);
                }
                return;
            }

            // Páginas HTML permitidas al estudiante
            if (path.equals("/dashboard-estudiante.html") || path.equals("/cursos-estudiante.html")
                    || path.equals("/vista-estudiante.html")) {
                chain.doFilter(req, resp);
            } else {
                // Redirige al portal estudiantil si intenta acceder a páginas administrativas
                resp.sendRedirect("/dashboard-estudiante.html");
            }
            return;
        }

        denegar(resp, esApi, 403, "/login.html");
    }

    private boolean estudiantePuedeUsarApi(String metodo, String path, HttpSession sesion) {
        if (path.startsWith("/api/estudiantes/") || path.equals("/api/estudiantes")) {
            return true;
        }
        if (path.equals("/api/cursos") || path.equals("/api/secciones") || path.equals("/api/periodos")
                || path.equals("/api/carreras") || path.equals("/api/facultades")
                || path.startsWith("/api/auth/")) {
            return true;
        }
        return false;
    }

    private String destinoSegunRol(String rol) {
        if (AuthController.ROL_ADMIN.equals(rol)) return "/index.html";
        if (AuthController.ROL_ESTUDIANTE.equals(rol)) return "/dashboard-estudiante.html";
        return "/login.html";
    }

    private void denegar(HttpServletResponse resp, boolean esApi, int codigo, String redirect) throws IOException {
        if (esApi) {
            resp.setStatus(codigo);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"mensaje\":\"" + (codigo == 401 ? "Debes iniciar sesión." : "No tienes permiso.") + "\"}");
        } else {
            resp.sendRedirect(redirect);
        }
    }
}

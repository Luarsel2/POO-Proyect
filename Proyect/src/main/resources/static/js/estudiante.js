// Lógica para el Portal Estudiantil

document.addEventListener("DOMContentLoaded", async () => {
  // 1. Verificar sesión activa
  const respMe = await fetch("/api/auth/me");
  if (!respMe.ok) {
    window.location.href = "/login.html";
    return;
  }

  const usuario = await respMe.json();
  if (usuario.rol !== "ESTUDIANTE") {
    // Si no es estudiante, lo enviamos al portal que le corresponde
    window.location.href = "/index.html";
    return;
  }

  // 2. Configurar botón de cerrar sesión
  const btnLogout = document.getElementById("btn-cerrar-sesion");
  if (btnLogout) {
    btnLogout.addEventListener("click", async () => {
      try {
        await fetch("/api/auth/logout", { method: "POST" });
      } finally {
        window.location.href = "/login.html";
      }
    });
  }

  // 3. Colocar datos en el encabezado
  const elChipNombre = document.getElementById("chip-nombre");
  const elChipCarnet = document.getElementById("chip-carnet");
  const elAvatar = document.getElementById("avatar-iniciales");

  if (elChipNombre) elChipNombre.textContent = usuario.nombre || "Estudiante";
  if (elChipCarnet) elChipCarnet.textContent = "Carnet: " + (usuario.carnet || "---");
  if (elAvatar && usuario.nombre) {
    const partes = usuario.nombre.trim().split(" ");
    const iniciales = partes.length >= 2 
      ? (partes[0][0] + partes[1][0]).toUpperCase()
      : partes[0].substring(0, 2).toUpperCase();
    elAvatar.textContent = iniciales;
  }

  // 4. Si estamos en el dashboard, cargar la información académica del estudiante
  if (document.getElementById("txt-carrera")) {
    cargarDatosDashboard(usuario.carnet);
  }

  // 5. Si estamos en la página de consulta de cursos, inicializarla
  if (document.getElementById("contenedor-cursos-disponibles")) {
    cargarCursosDisponibles(usuario.carnet);
  }
});

async function cargarDatosDashboard(carnet) {
  try {
    const resp = await fetch(`/api/estudiantes/${carnet}/dashboard`);
    if (!resp.ok) {
      console.warn("No se pudo obtener información del dashboard:", resp.status);
      return;
    }

    const data = await resp.json();

    // Actualizar métricas del dashboard
    const elCarrera = document.getElementById("txt-carrera");
    const elFacultad = document.getElementById("txt-facultad");
    const elPeriodo = document.getElementById("txt-periodo");
    const elCantidadCursos = document.getElementById("txt-cantidad-cursos");
    const elTotalCreditos = document.getElementById("txt-total-creditos");

    if (elCarrera) elCarrera.textContent = data.carrera || "Ingeniería en Ciencias de la Computación";
    if (elFacultad) elFacultad.textContent = data.facultad || "Facultad de Ingeniería";
    if (elPeriodo) elPeriodo.textContent = data.periodoActual || "Segundo Semestre 2026";
    if (elCantidadCursos) elCantidadCursos.textContent = data.cantidadCursos || (data.cursosActuales ? data.cursosActuales.length : 0);
    if (elTotalCreditos) elTotalCreditos.textContent = data.totalCreditos || 0;

    // Actualizar tabla de cursos actuales
    const tbody = document.getElementById("tabla-cursos-cuerpo");
    if (!tbody) return;

    if (!data.cursosActuales || data.cursosActuales.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="7" style="text-align: center; color: var(--gris-secundario); padding: 28px;">
            Actualmente no tienes cursos asignados para este ciclo académico.
          </td>
        </tr>
      `;
      return;
    }

    tbody.innerHTML = data.cursosActuales.map(c => `
      <tr>
        <td><strong>${c.codigoCurso || '-'}</strong></td>
        <td><strong>${c.nombreCurso || '-'}</strong></td>
        <td>Sección ${c.codigoSeccion || '-'}</td>
        <td>${c.profesor || 'Por asignar'}</td>
        <td>${c.horarios || 'Pendiente'}</td>
        <td><span class="badge badge-info">${c.creditos || 0} créditos</span></td>
        <td><span class="badge badge-activo">${c.estado || 'Asignado'}</span></td>
      </tr>
    `).join("");

  } catch (err) {
    console.error("Error al cargar dashboard del estudiante:", err);
  }
}

let cursosOriginales = [];

async function cargarCursosDisponibles(carnet) {
  const contenedor = document.getElementById("contenedor-cursos-disponibles");
  const contador = document.getElementById("contador-cursos");
  const txtPeriodo = document.getElementById("txt-periodo-oferta");

  try {
    const resp = await fetch(`/api/estudiantes/${carnet}/cursos-disponibles`);
    if (!resp.ok) {
      contenedor.innerHTML = '<p class="subtexto">Error al cargar la oferta de cursos.</p>';
      return;
    }

    const data = await resp.json();
    cursosOriginales = data.cursos || [];

    if (txtPeriodo && data.periodo) {
      txtPeriodo.textContent = "Período: " + data.periodo;
    }

    renderizarCursos(cursosOriginales);

    // Configurar buscador
    const inputBuscar = document.getElementById("input-buscar-curso");
    if (inputBuscar) {
      inputBuscar.addEventListener("input", (e) => {
        const query = e.target.value.toLowerCase().trim();
        const filtrados = cursosOriginales.filter(c => {
          const matchNombre = (c.nombre || "").toLowerCase().includes(query);
          const matchCodigo = (c.codigo || "").toLowerCase().includes(query);
          const matchSecciones = (c.secciones || []).some(s => 
            String(s.codigo).toLowerCase().includes(query) ||
            (s.profesor || "").toLowerCase().includes(query)
          );
          return matchNombre || matchCodigo || matchSecciones;
        });
        renderizarCursos(filtrados);
      });
    }

  } catch (err) {
    console.error("Error al cargar cursos disponibles:", err);
    contenedor.innerHTML = '<p class="subtexto">Error de conexión al cargar cursos.</p>';
  }
}

function renderizarCursos(cursos) {
  const contenedor = document.getElementById("contenedor-cursos-disponibles");
  const contador = document.getElementById("contador-cursos");
  if (!contenedor) return;

  if (contador) {
    contador.textContent = `${cursos.length} curso(s) disponible(s)`;
  }

  if (cursos.length === 0) {
    contenedor.innerHTML = `
      <div style="grid-column: 1/-1; text-align: center; padding: 48px; background: white; border-radius: 12px; border: 1px solid var(--gris-borde);">
        <p style="font-size: 1.1rem; color: var(--gris-secundario); margin-bottom: 8px;">No se encontraron cursos que coincidan con la búsqueda.</p>
        <p style="font-size: 0.85rem; color: var(--gris-deshabilitado);">Verifica el nombre o código ingresado.</p>
      </div>
    `;
    return;
  }

  contenedor.innerHTML = cursos.map(curso => {
    const seccionesHtml = (curso.secciones && curso.secciones.length > 0)
      ? curso.secciones.map(sec => {
          const disponibles = sec.cupoMaximo - sec.cupoActual;
          const claseCupo = disponibles > 0 ? "cupo-disponible" : "cupo-agotado";
          const textoCupo = disponibles > 0 ? `${disponibles} cupos libres` : "Sin cupo";

          return `
            <div class="seccion-item">
              <div class="seccion-item-header">
                <span>Sección ${sec.codigo}</span>
                <span class="cupo-pill ${claseCupo}">${textoCupo} (${sec.cupoActual}/${sec.cupoMaximo})</span>
              </div>
              <div class="seccion-detalle">
                <div>👨‍🏫 <strong>Profesor:</strong> ${sec.profesor || "Sin profesor asignado"}</div>
                <div>🕒 <strong>Horario:</strong> ${sec.horarios || "Horario por definir"}</div>
              </div>
            </div>
          `;
        }).join("")
      : '<p class="subtexto" style="font-style: italic;">No hay secciones abiertas para este curso.</p>';

    return `
      <div class="tarjeta-curso">
        <div class="tarjeta-curso-header">
          <div>
            <h4 class="curso-titulo">${curso.nombre}</h4>
            <span style="font-size: 0.8rem; color: var(--gris-secundario);">Código: ${curso.codigo || '-'}</span>
          </div>
          <span class="curso-creditos">${curso.creditos} Créditos</span>
        </div>
        <div class="curso-meta">
          <span>📚 ${curso.carrera || 'Plan de estudios general'}</span>
        </div>
        <div>
          <div class="secciones-titulo">Secciones y Horarios</div>
          ${seccionesHtml}
        </div>
      </div>
    `;
  }).join("");
}


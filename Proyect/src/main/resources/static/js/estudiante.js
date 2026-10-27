document.addEventListener("DOMContentLoaded", async () => {
  const respMe = await fetch("/api/auth/me");
  if (!respMe.ok) {
    window.location.href = "/login.html";
    return;
  }

  const usuario = await respMe.json();
  if (usuario.rol !== "ESTUDIANTE") {
    window.location.href = "/index.html";
    return;
  }

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

  const carnet = usuario.carnet || 26547;
  window.carnetActual = carnet;

  function navegar(seccion) {
    const vistas = ["inicio", "asignacion", "retiro", "horario"];
    vistas.forEach(v => {
      const panel = document.getElementById("vista-" + v);
      const link = document.getElementById("link-nav-" + v);
      if (panel) panel.classList.toggle("activa", v === seccion);
      if (link) link.classList.toggle("activo", v === seccion);
    });

    const titulos = {
      inicio: { t: "Panel Principal", s: "Bienvenido a tu portal académico universitario" },
      asignacion: { t: "Asignación de Cursos", s: "Selecciona tus asignaturas y secciones oficiales" },
      retiro: { t: "Retiro de Cursos", s: "Desasignación oficial y liberación de cupos" },
      horario: { t: "Mi Horario Semanal", s: "Distribución visual de clases y recesos UVG" }
    };

    const headerT = document.getElementById("topbar-seccion-titulo");
    const headerS = document.getElementById("topbar-seccion-sub");
    if (headerT && titulos[seccion]) headerT.textContent = titulos[seccion].t;
    if (headerS && titulos[seccion]) headerS.textContent = titulos[seccion].s;

    if (seccion === "inicio") cargarDashboard(carnet);
    if (seccion === "asignacion") cargarAsignacion(carnet);
    if (seccion === "retiro") cargarRetiro(carnet);
    if (seccion === "horario") cargarHorario(carnet);

    window.location.hash = seccion;
  }

  window.addEventListener("hashchange", () => {
    const hash = window.location.hash.replace("#", "") || "inicio";
    navegar(hash);
  });

  const linksNav = [
    { id: "link-nav-inicio", s: "inicio" },
    { id: "link-nav-asignacion", s: "asignacion" },
    { id: "link-nav-retiro", s: "retiro" },
    { id: "link-nav-horario", s: "horario" }
  ];

  linksNav.forEach(item => {
    const el = document.getElementById(item.id);
    if (el) {
      el.addEventListener("click", (e) => {
        e.preventDefault();
        navegar(item.s);
      });
    }
  });

  const btnIrAsignacion = document.getElementById("btn-ir-asignacion");
  if (btnIrAsignacion) {
    btnIrAsignacion.addEventListener("click", () => navegar("asignacion"));
  }

  const btnIrHorario = document.getElementById("btn-ir-horario");
  if (btnIrHorario) {
    btnIrHorario.addEventListener("click", () => navegar("horario"));
  }

  const btnVolverAsignar = document.getElementById("btn-volver-asignar");
  if (btnVolverAsignar) {
    btnVolverAsignar.addEventListener("click", () => navegar("asignacion"));
  }

  const btnAsignarMat = document.getElementById("btn-asignar-matutina");
  if (btnAsignarMat) {
    btnAsignarMat.addEventListener("click", () => asignarHorario(carnet, "Matutina"));
  }

  const btnAsignarNoct = document.getElementById("btn-asignar-nocturna");
  if (btnAsignarNoct) {
    btnAsignarNoct.addEventListener("click", () => asignarHorario(carnet, "Nocturna"));
  }

  const btnRetirarTodo = document.getElementById("btn-retirar-todo");
  if (btnRetirarTodo) {
    btnRetirarTodo.addEventListener("click", () => retirarTodoElHorario(carnet));
  }

  const hashInicial = window.location.hash.replace("#", "") || "inicio";
  navegar(hashInicial);
});

function mostrarToast(mensaje, esError = false) {
  const cont = document.getElementById("toast-contenedor");
  if (!cont) return;
  const item = document.createElement("div");
  item.className = "toast-item" + (esError ? " error" : "");
  item.innerHTML = `<span>${esError ? "!" : ""}</span><span>${mensaje}</span>`;
  cont.appendChild(item);
  setTimeout(() => {
    item.style.opacity = "0";
    item.style.transform = "translateY(10px)";
    item.style.transition = "all 0.25s ease";
    setTimeout(() => item.remove(), 250);
  }, 3500);
}

async function cargarDashboard(carnet) {
  try {
    const resp = await fetch(`/api/estudiantes/${carnet}/dashboard`);
    if (!resp.ok) return;

    const data = await resp.json();

    const elCarrera = document.getElementById("txt-carrera");
    const elFacultad = document.getElementById("txt-facultad");
    const elPeriodo = document.getElementById("txt-periodo");
    const elCantidadCursos = document.getElementById("txt-cantidad-cursos");
    const elTotalCreditos = document.getElementById("txt-total-creditos");

    if (elCarrera) elCarrera.textContent = data.carrera || "Ingeniería en Ciencias de la Computación";
    if (elFacultad) elFacultad.textContent = data.facultad || "Facultad de Ingeniería";
    if (elPeriodo) elPeriodo.textContent = data.periodoActual || "Segundo Semestre 2026";
    if (elCantidadCursos) elCantidadCursos.textContent = data.cantidadCursos || 0;
    if (elTotalCreditos) elTotalCreditos.textContent = data.totalCreditos || 0;

    const tbody = document.getElementById("tabla-cursos-cuerpo");
    if (!tbody) return;

    if (!data.cursosActuales || data.cursosActuales.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="8" style="text-align: center; color: #7a897f; padding: 32px;">
            Actualmente no tienes cursos asignados. Haz clic en "Asignar cursos" para comenzar.
          </td>
        </tr>
      `;
      return;
    }

    tbody.innerHTML = data.cursosActuales.map(c => `
      <tr>
        <td>
          <span style="display:inline-block;width:10px;height:10px;border-radius:50%;background:${c.color || '#0b4d33'};margin-right:6px;"></span>
          <strong>${c.codigoCurso || '-'}</strong>
        </td>
        <td><strong>${c.nombreCurso || '-'}</strong></td>
        <td>Sección ${c.codigoSeccion || '-'}</td>
        <td>
          <span class="badge-jornada ${c.jornada === 'Matutina' ? 'jornada-matutina' : 'jornada-nocturna'}">
            ${c.jornada === 'Matutina' ? 'Matutina' : 'Nocturna'}
          </span>
        </td>
        <td>${c.profesor || 'Por asignar'}</td>
        <td style="font-size:0.83rem;">${c.horarios || 'Pendiente'}</td>
        <td><span class="badge badge-info">${c.creditos || 0} créditos</span></td>
        <td><span class="badge badge-activo">${c.estado || 'Inscrito'}</span></td>
      </tr>
    `).join("");

  } catch (err) {
    console.error("Error al cargar dashboard:", err);
  }
}

let catalogoCursos = [];
let filtroJornadaActual = "todos";

async function cargarAsignacion(carnet) {
  const grid = document.getElementById("grid-cursos-asignacion");
  if (!grid) return;

  try {
    const resp = await fetch(`/api/estudiantes/${carnet}/cursos-disponibles`);
    if (!resp.ok) {
      grid.innerHTML = '<p style="color:#b91c1c;padding:20px;">Error al cargar cursos disponibles.</p>';
      return;
    }

    const data = await resp.json();
    catalogoCursos = data.cursos || [];

    const btnsFiltro = document.querySelectorAll(".filtros-jornada .btn-filtro");
    btnsFiltro.forEach(btn => {
      btn.onclick = () => {
        btnsFiltro.forEach(b => b.classList.remove("activo"));
        btn.classList.add("activo");
        filtroJornadaActual = btn.getAttribute("data-filtro");
        renderizarAsignacion(carnet);
      };
    });

    const inputBuscar = document.getElementById("input-buscar-asignacion");
    if (inputBuscar) {
      inputBuscar.oninput = () => renderizarAsignacion(carnet);
    }

    renderizarAsignacion(carnet);

  } catch (err) {
    console.error("Error al cargar asignación:", err);
    grid.innerHTML = '<p style="color:#b91c1c;padding:20px;">Error de conexión.</p>';
  }
}

function renderizarAsignacion(carnet) {
  const grid = document.getElementById("grid-cursos-asignacion");
  const inputBuscar = document.getElementById("input-buscar-asignacion");
  const query = (inputBuscar ? inputBuscar.value : "").toLowerCase().trim();

  const cursosFiltrados = catalogoCursos.map(c => {
    const seccionesFiltradas = (c.secciones || []).filter(s => {
      const cumpleJornada = (filtroJornadaActual === "todos") || (s.jornada === filtroJornadaActual);
      const cumpleQuery = !query || 
        c.nombre.toLowerCase().includes(query) ||
        c.codigo.toLowerCase().includes(query) ||
        (s.profesor || "").toLowerCase().includes(query) ||
        (s.salon || "").toLowerCase().includes(query);
      return cumpleJornada && cumpleQuery;
    });

    return { ...c, secciones: seccionesFiltradas };
  }).filter(c => c.secciones.length > 0);

  if (cursosFiltrados.length === 0) {
    grid.innerHTML = `
      <div style="grid-column: 1/-1; text-align: center; padding: 48px; background: white; border-radius: 12px; border: 1px solid var(--gris-borde);">
        <p style="font-size: 1.1rem; color: #5b6b60; margin-bottom: 6px;">No se encontraron secciones para los filtros seleccionados.</p>
        <p style="font-size: 0.85rem; color: #7a897f;">Prueba seleccionando "Todos los cursos" o revisa el término de búsqueda.</p>
      </div>
    `;
    return;
  }

  grid.innerHTML = cursosFiltrados.map(curso => {
    const seccionesHtml = curso.secciones.map(sec => {
      const disponibles = sec.cupoMaximo - sec.cupoActual;
      const tieneCupo = disponibles > 0;
      const claseCupo = tieneCupo ? "cupo-disponible" : "cupo-agotado";
      const textoCupo = tieneCupo ? `${disponibles} cupos libres` : "Sin cupo";

      let botonHtml = "";
      if (sec.asignada) {
        botonHtml = `<button type="button" class="btn-accion-seccion ya-inscrito" disabled>Ya Inscrito</button>`;
      } else if (!tieneCupo) {
        botonHtml = `<button type="button" class="btn-accion-seccion sin-cupo" disabled>Sin cupo disponible</button>`;
      } else {
        botonHtml = `<button type="button" class="btn-accion-seccion asignar" data-carnet="${carnet}" data-id-seccion="${sec.idSeccion}" onclick="ejecutarAsignacion(${carnet}, ${sec.idSeccion})">+ Inscribir Sección ${sec.codigo}</button>`;
      }

      return `
        <div class="seccion-item" style="border-left: 4px solid ${sec.color || '#0b4d33'};">
          <div class="seccion-item-header">
            <span style="display:flex;align-items:center;gap:6px;">
              <span>Sección ${sec.codigo}</span>
              <span class="badge-jornada ${sec.jornada === 'Matutina' ? 'jornada-matutina' : 'jornada-nocturna'}">
                ${sec.jornada === 'Matutina' ? 'Matutina' : 'Nocturna'}
              </span>
            </span>
            <span class="cupo-pill ${claseCupo}">${textoCupo} (${sec.cupoActual}/${sec.cupoMaximo})</span>
          </div>
          <div class="seccion-detalle" style="margin-bottom:10px;">
            <div><strong>Profesor:</strong> ${sec.profesor || "Por asignar"}</div>
            <div><strong>Salón:</strong> ${sec.salon || "CIT"}</div>
            <div><strong>Horario:</strong> ${sec.horarios || "Por definir"}</div>
          </div>
          ${botonHtml}
        </div>
      `;
    }).join("");

    return `
      <div class="tarjeta-curso-asignacion">
        <div class="franja-color" style="background:${curso.secciones[0]?.color || '#0b4d33'};"></div>
        <div class="tarjeta-curso-header">
          <div>
            <h4 class="curso-titulo">${curso.nombre}</h4>
            <span style="font-size:0.80rem;color:#5b6b60;">Código: ${curso.codigo}</span>
          </div>
          <span class="curso-creditos">${curso.creditos} Créditos</span>
        </div>
        <div style="margin-top:10px;">
          <div class="secciones-titulo">Secciones Disponibles</div>
          ${seccionesHtml}
        </div>
      </div>
    `;
  }).join("");
}

async function asignarHorario(carnet, jornada) {
  const c = carnet || window.carnetActual;
  try {
    const resp = await fetch(`/api/estudiantes/${c}/asignar-horario`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ jornada })
    });
    const data = await resp.json();
    if (resp.ok && data.ok) {
      mostrarToast(data.mensaje || "Horario asignado exitosamente");
      await cargarAsignacion(c);
      await cargarDashboard(c);
      await cargarHorario(c);
    } else {
      mostrarToast(data.mensaje || "Error al asignar horario", true);
    }
  } catch (err) {
    mostrarToast("Error de conexión al asignar horario", true);
  }
}

async function retirarTodoElHorario(carnet) {
  const c = carnet || window.carnetActual;
  if (!confirm("¿Confirmas que deseas retirar todas las asignaturas de tu horario?")) {
    return;
  }
  try {
    const resp = await fetch(`/api/estudiantes/${c}/retirar-todos`, {
      method: "POST",
      headers: { "Content-Type": "application/json" }
    });
    const data = await resp.json();
    if (resp.ok && data.ok) {
      mostrarToast(data.mensaje || "Horario retirado exitosamente");
      await cargarRetiro(c);
      await cargarDashboard(c);
      await cargarHorario(c);
    } else {
      mostrarToast(data.mensaje || "Error al retirar horario", true);
    }
  } catch (err) {
    mostrarToast("Error de conexión al retirar horario", true);
  }
}

window.asignarHorario = asignarHorario;
window.retirarTodoElHorario = retirarTodoElHorario;

async function ejecutarAsignacion(carnet, idSeccion) {
  const c = carnet || window.carnetActual;
  const s = parseInt(idSeccion, 10);
  try {
    const resp = await fetch(`/api/estudiantes/${c}/asignar-curso`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ idSeccion: s })
    });
    const data = await resp.json();
    if (resp.ok && data.ok) {
      mostrarToast(data.mensaje || "Curso asignado exitosamente");
      await cargarAsignacion(c);
      await cargarDashboard(c);
      await cargarHorario(c);
    } else {
      mostrarToast(data.mensaje || "No se pudo realizar la asignación", true);
    }
  } catch (err) {
    mostrarToast("Error de conexión al asignar curso", true);
  }
}

window.ejecutarAsignacion = ejecutarAsignacion;

let seccionARetirar = null;
let carnetRetiro = null;

async function cargarRetiro(carnet) {
  carnetRetiro = carnet;
  const grid = document.getElementById("grid-cursos-retiro");
  const vacio = document.getElementById("contenedor-retiro-vacio");
  const barraRetiro = document.getElementById("barra-accion-retiro");
  if (!grid || !vacio) return;

  try {
    const resp = await fetch(`/api/estudiantes/${carnet}/dashboard`);
    if (!resp.ok) return;

    const data = await resp.json();
    const cursos = data.cursosActuales || [];

    if (cursos.length === 0) {
      grid.style.display = "none";
      vacio.style.display = "block";
      if (barraRetiro) barraRetiro.style.display = "none";
      return;
    }

    vacio.style.display = "none";
    grid.style.display = "grid";
    if (barraRetiro) barraRetiro.style.display = "block";

    grid.innerHTML = cursos.map(c => `
      <div class="card-retiro" style="border-left-color: ${c.color || '#0b4d33'};">
        <div>
          <div class="card-retiro-header">
            <div>
              <div class="card-retiro-titulo">${c.nombreCurso}</div>
              <span style="font-size:0.78rem;color:#5b6b60;">Código: ${c.codigoCurso} | Sección ${c.codigoSeccion}</span>
            </div>
            <span class="badge-jornada ${c.jornada === 'Matutina' ? 'jornada-matutina' : 'jornada-nocturna'}">
              ${c.jornada === 'Matutina' ? 'Matutina' : 'Nocturna'}
            </span>
          </div>
          <div class="card-retiro-meta">
            <div><strong>Profesor:</strong> ${c.profesor || 'Por asignar'}</div>
            <div><strong>Salón:</strong> ${c.salon || 'CIT'}</div>
            <div><strong>Horario:</strong> ${c.horarios || 'Pendiente'}</div>
            <div><strong>Créditos:</strong> ${c.creditos} créditos</div>
          </div>
        </div>
        <button type="button" class="btn-retirar" data-carnet="${carnet}" data-id-seccion="${c.idSeccion}" onclick="abrirModalRetiro(${carnet}, ${c.idSeccion}, '${c.nombreCurso.replace(/'/g, "\\'")}', ${c.codigoSeccion})">
          Desasignar / Retirar curso
        </button>
      </div>
    `).join("");

  } catch (err) {
    console.error("Error al cargar retiro:", err);
  }
}

function abrirModalRetiro(carnet, idSeccion, nombreCurso, codigoSeccion) {
  seccionARetirar = parseInt(idSeccion, 10);
  carnetRetiro = carnet || window.carnetActual;
  const modal = document.getElementById("modal-retiro");
  const txt = document.getElementById("modal-retiro-texto");
  if (txt) {
    txt.textContent = `¿Confirmas que deseas retirar la asignatura "${nombreCurso}" (Sección ${codigoSeccion})? Se liberará tu cupo para otros estudiantes.`;
  }
  if (modal) modal.style.display = "flex";

  const btnCancelar = document.getElementById("btn-cancelar-retiro");
  if (btnCancelar) {
    btnCancelar.onclick = () => {
      modal.style.display = "none";
      seccionARetirar = null;
    };
  }

  const btnConfirmar = document.getElementById("btn-confirmar-retiro");
  if (btnConfirmar) {
    btnConfirmar.onclick = async () => {
      modal.style.display = "none";
      if (!seccionARetirar) return;
      try {
        const resp = await fetch(`/api/estudiantes/${carnetRetiro}/retirar-curso`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ idSeccion: seccionARetirar })
        });
        const resData = await resp.json();
        if (resp.ok && resData.ok) {
          mostrarToast(resData.mensaje || "Curso retirado exitosamente");
          await cargarRetiro(carnetRetiro);
          await cargarDashboard(carnetRetiro);
          await cargarHorario(carnetRetiro);
        } else {
          mostrarToast(resData.mensaje || "Error al retirar curso", true);
        }
      } catch (err) {
        mostrarToast("Error de conexión al retirar curso", true);
      } finally {
        seccionARetirar = null;
      }
    };
  }
}

window.abrirModalRetiro = abrirModalRetiro;

document.addEventListener("click", (e) => {
  const btnAsignar = e.target.closest(".btn-accion-seccion.asignar");
  if (btnAsignar) {
    const sId = btnAsignar.getAttribute("data-id-seccion");
    const cId = btnAsignar.getAttribute("data-carnet") || window.carnetActual;
    if (sId && cId) {
      ejecutarAsignacion(parseInt(cId, 10), parseInt(sId, 10));
    }
  }
});

async function cargarHorario(carnet) {
  const tbody = document.getElementById("cuerpo-tabla-horario");
  const leyenda = document.getElementById("leyenda-horario");
  if (!tbody) return;

  try {
    const resp = await fetch(`/api/estudiantes/${carnet}/horario`);
    if (!resp.ok) return;

    const data = await resp.json();
    const bloques = data.bloques || [];

    const franjas = [
      { id: "m1", label: "07:00 - 08:30", tipo: "clase", inicio: "07:00", fin: "08:30" },
      { id: "m2", label: "08:45 - 10:15", tipo: "clase", inicio: "08:45", fin: "10:15" },
      { id: "receso", label: "10:15 - 10:40", tipo: "receso", inicio: "10:15", fin: "10:40" },
      { id: "m3", label: "10:40 - 12:00", tipo: "clase", inicio: "10:40", fin: "12:00" },
      { id: "m4", label: "12:00 - 13:00", tipo: "clase", inicio: "12:00", fin: "13:00" },
      { id: "n1", label: "15:00 - 17:00", tipo: "clase", inicio: "15:00", fin: "17:00" },
      { id: "n2", label: "17:15 - 19:00", tipo: "clase", inicio: "17:15", fin: "19:00" },
      { id: "n3", label: "19:15 - 21:00", tipo: "clase", inicio: "19:15", fin: "21:00" }
    ];

    const dias = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes"];

    function coincideBloque(b, dia, franja) {
      if (b.dia !== dia) return false;
      const bIni = parseInt(b.horaInicio.replace(":", ""), 10);
      const bFin = parseInt(b.horaFin.replace(":", ""), 10);
      const fIni = parseInt(franja.inicio.replace(":", ""), 10);
      const fFin = parseInt(franja.fin.replace(":", ""), 10);
      return (bIni < fFin && bFin > fIni);
    }

    let html = "";
    franjas.forEach(f => {
      if (f.tipo === "receso") {
        html += `
          <tr class="bloque-receso-fila">
            <td class="td-hora">${f.label}</td>
            <td colspan="5">
              <div class="badge-receso">Receso Académico UVG (10:15 - 10:40)</div>
            </td>
          </tr>
        `;
        return;
      }

      html += `<tr><td class="td-hora">${f.label}</td>`;

      dias.forEach(d => {
        const matches = bloques.filter(b => coincideBloque(b, d, f));
        if (matches.length > 0) {
          const c = matches[0];
          html += `
            <td>
              <div class="bloque-clase-item" style="background:${c.color || '#0b4d33'};">
                <span class="bloque-curso-nombre">${c.nombreCurso}</span>
                <span class="bloque-curso-sub">Sec. ${c.codigoSeccion} | Salón ${c.salon}</span>
                <span class="bloque-curso-sub">${c.profesor}</span>
                <span class="bloque-curso-sub" style="opacity:0.85;">${c.horaInicio} - ${c.horaFin}</span>
              </div>
            </td>
          `;
        } else {
          html += `<td><div class="bloque-vacio"></div></td>`;
        }
      });

      html += `</tr>`;
    });

    tbody.innerHTML = html;

    if (leyenda) {
      const cursosUnicos = [];
      const mapa = new Map();
      bloques.forEach(b => {
        if (!mapa.has(b.codigoCurso)) {
          mapa.set(b.codigoCurso, true);
          cursosUnicos.push({ nombre: b.nombreCurso, codigo: b.codigoCurso, color: b.color });
        }
      });

      if (cursosUnicos.length === 0) {
        leyenda.innerHTML = '<span style="color:#7a897f;font-size:0.82rem;font-style:italic;">No hay materias inscritas en tu horario actualmente.</span>';
      } else {
        leyenda.innerHTML = cursosUnicos.map(c => `
          <div class="leyenda-item">
            <span class="punto-color" style="background:${c.color};"></span>
            <span>${c.nombre} (${c.codigo})</span>
          </div>
        `).join("");
      }
    }

  } catch (err) {
    console.error("Error al cargar horario:", err);
  }
}

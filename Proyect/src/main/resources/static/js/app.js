// Funciones compartidas por todas las páginas del portal.
// Cada página llama a estas funciones para hablar con el backend
// (los controladores REST bajo /api/...).

const API = "/api";

// Hace un POST en JSON y devuelve la respuesta ya convertida a objeto.
async function postJSON(url, body) {
  const resp = await fetch(API + url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  });
  let data;
  try {
    data = await resp.json();
  } catch (e) {
    data = { mensaje: "Respuesta inesperada del servidor." };
  }
  return { ok: resp.ok, data };
}

// Hace un GET y devuelve la lista/objeto ya convertido.
async function getJSON(url) {
  const resp = await fetch(API + url);
  if (!resp.ok) return null;
  return await resp.json();
}

// Muestra un mensaje de éxito o error debajo de un formulario.
function mostrarMensaje(elementoId, texto, esError) {
  const el = document.getElementById(elementoId);
  if (!el) return;
  el.textContent = texto;
  el.className = "mensaje " + (esError ? "error" : "ok");
  el.style.display = "block";
}

// Arma el HTML de una tabla a partir de encabezados y filas (arrays de strings).
function construirTabla(encabezados, filas) {
  if (!filas || filas.length === 0) {
    return '<p class="vacio">Todavía no hay registros.</p>';
  }
  let html = "<table><thead><tr>";
  encabezados.forEach(h => html += `<th>${h}</th>`);
  html += "</tr></thead><tbody>";
  filas.forEach(fila => {
    html += "<tr>";
    fila.forEach(dato => html += `<td>${dato}</td>`);
    html += "</tr>";
  });
  html += "</tbody></table>";
  return html;
}

// Convierte un valor vacío/undefined en "-" para mostrar en tablas.
function v(valor) {
  return (valor === undefined || valor === null || valor === "") ? "-" : valor;
}

// Evita que un formulario recargue la página al enviarse.
function alEnviar(formId, callback) {
  const form = document.getElementById(formId);
  if (!form) return;
  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    await callback(new FormData(form));
  });
}

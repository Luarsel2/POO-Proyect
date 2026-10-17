
(async function () {
  const resp = await fetch("/api/auth/me");
  if (!resp.ok) {
    window.location.href = "/login.html";
    return;
  }
  const yo = await resp.json();

  const barra = document.querySelector("header.topbar");
  if (!barra) return;

  const caja = document.createElement("div");
  caja.className = "usuario-barra";
  const nombre = document.createElement("span");
  nombre.textContent = yo.nombre;          // textContent: nunca interpreta HTML
  const boton = document.createElement("button");
  boton.className = "btn-salir";
  boton.textContent = "Cerrar sesión";
  boton.addEventListener("click", async () => {
    await fetch("/api/auth/logout", { method: "POST" });
    window.location.href = "/login.html";
  });
  caja.append(nombre, boton);
  barra.appendChild(caja);
})();

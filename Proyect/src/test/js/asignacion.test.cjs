// Ejecutar con: node Proyect/src/test/js/asignacion.test.cjs
const assert = require('node:assert/strict');
const fs = require('node:fs');
const vm = require('node:vm');
const path = require('node:path');
const source = fs.readFileSync(path.join(__dirname, '../../main/resources/static/js/estudiante.js'), 'utf8');

async function probar({ respuestas = [], confirmar = true, dobleClic = false, fallo = false } = {}) {
  const solicitudes = [], avisos = [], eventos = {}, textos = [];
  const modal = {
    addEventListener(evento, callback) { this.cerrar = callback; },
    showModal() { textos.push(elementos['modal-traslape-texto'].textContent); this.returnValue = confirmar ? 'reemplazar' : 'cancelar'; this.cerrar(); }
  };
  const elementos = { 'modal-traslape': modal, 'modal-traslape-texto': {} };
  const contexto = vm.createContext({
    document: {
      addEventListener(evento, callback) { eventos[evento] = callback; },
      getElementById(id) { return elementos[id] || null; }
    },
    window: {}, console,
    fetch: async (url, opciones) => {
      solicitudes.push(JSON.parse(opciones.body));
      if (fallo) throw new Error('Sin conexión');
      const data = respuestas.shift() || { ok: true, mensaje: 'Asignado' };
      return { ok: data.ok, json: async () => data };
    }
  });
  vm.runInContext(source, contexto);
  contexto.mostrarToast = (mensaje, error = false) => avisos.push({ mensaje, error });
  contexto.cargarAsignacion = contexto.cargarDashboard = contexto.cargarHorario = async () => {};
  const primera = contexto.ejecutarAsignacion(42, 101);
  if (dobleClic) await contexto.ejecutarAsignacion(42, 101);
  await primera;
  return { solicitudes, avisos, textos, contexto, elementos };
}

(async () => {
  assert.doesNotMatch(source, /onclick="ejecutarAsignacion/);
  const simple = await probar({ dobleClic: true });
  assert.equal(simple.solicitudes.length, 1);
  assert.deepEqual(simple.avisos, [{ mensaje: 'Asignado', error: false }]);
  await simple.contexto.ejecutarAsignacion(42, 101);
  assert.equal(simple.solicitudes.length, 2, 'El bloqueo se libera al finalizar');
  const conflicto = { ok: false, tipo: 'TRASLAPE', conflictos: [103], mensaje: 'Curso A se traslapa con Curso B' };
  const cancelada = await probar({ respuestas: [conflicto], confirmar: false });
  assert.equal(cancelada.solicitudes.length, 1);
  assert.equal(cancelada.avisos.length, 0);
  assert.deepEqual(cancelada.textos, [conflicto.mensaje]);
  const aceptada = await probar({ respuestas: [conflicto] });
  assert.deepEqual(aceptada.solicitudes[1], { idSeccion: 101, reemplazarSecciones: [103] });
  assert.deepEqual(aceptada.avisos, [{ mensaje: 'Asignado', error: false }]);
  const sinCupo = await probar({ respuestas: [conflicto, { ok: false, mensaje: 'Sin cupo' }] });
  assert.deepEqual(sinCupo.avisos, [{ mensaje: 'Sin cupo', error: true }]);
  const red = await probar({ fallo: true });
  assert.equal(red.avisos.length, 1);
  assert.equal(red.avisos[0].error, true);
  console.log('6 comprobaciones de interfaz correctas: clic único, desbloqueo, cancelar, confirmar, sin cupo y conexión.');
})().catch(error => { console.error(error); throw error; });

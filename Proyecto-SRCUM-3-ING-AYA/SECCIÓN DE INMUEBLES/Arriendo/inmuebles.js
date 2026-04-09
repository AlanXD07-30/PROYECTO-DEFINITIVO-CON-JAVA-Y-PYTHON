// inmuebles.js - versión corregida y robusta
(() => {
  // Construye la URL base de forma segura (usa puerto actual si existe)
  const BASE_API = (() => {
    const protocol = location.protocol;
    const host = location.hostname;
    const port = location.port ? `:${location.port}` : '';
    return `${protocol}//${host}${port}/java/inmuebles`;
  })();

  let inmueblesGlobal = [];

  // Elementos del DOM (se obtienen al DOMContentLoaded)
  let gridEl, emptyEl, errorEl, errorMsgEl, selectCiudad, selectTipo;

  function q(id) { return document.getElementById(id); }

  // Utilidades
  function escapeHtml(str) {
    if (str === null || str === undefined) return '';
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  function formatoPrecio(valor) {
    if (valor === null || valor === undefined || valor === '') return 'Consultar';
    const num = Number(valor);
    if (Number.isNaN(num)) return String(valor);
    return num.toLocaleString('es-CO', { style: 'currency', currency: 'COP' });
  }

  function obtenerPrimeraImagen(inmueble) {
    if (!inmueble) return '/img/no-image.png';
    const imgs = inmueble.imagenes;
    if (Array.isArray(imgs) && imgs.length > 0) {
      // Si cada imagen es un objeto con url_imagen
      const first = imgs[0];
      if (first && typeof first === 'object') {
        return first.url_imagen || first.url || '/img/no-image.png';
      }
      // Si es string
      if (typeof first === 'string') return first;
    }
    return '/img/no-image.png';
  }

  function showLoading() {
    if (emptyEl) emptyEl.hidden = true;
    if (errorEl) errorEl.hidden = true;
    if (gridEl) gridEl.innerHTML = '<p class="loading">Cargando propiedades…</p>';
  }

  function showEmpty() {
    if (gridEl) gridEl.innerHTML = '';
    if (emptyEl) emptyEl.hidden = false;
    if (errorEl) errorEl.hidden = true;
  }

  function showError(msg) {
    if (gridEl) gridEl.innerHTML = '';
    if (emptyEl) emptyEl.hidden = true;
    if (errorEl) {
      errorEl.hidden = false;
      if (errorMsgEl) errorMsgEl.textContent = msg || 'Error al cargar propiedades.';
    }
  }

  // Renderizado de tarjetas
  function renderizar(items) {
    if (!gridEl) return;
    gridEl.innerHTML = '';

    if (!items || items.length === 0) {
      showEmpty();
      return;
    }

    emptyEl.hidden = true;
    errorEl.hidden = true;

    const fragment = document.createDocumentFragment();

    items.forEach(inm => {
      const article = document.createElement('article');
      article.className = 'card-inmueble';

      const imgUrl = obtenerPrimeraImagen(inm);
      const precio = formatoPrecio(inm.precio);
      const metraje = inm.metraje != null ? `${inm.metraje} m²` : '';
      // tu backend usa tipo_operacion; también toleramos tipoOperacion
      const tipoNombre = inm.tipo_operacion || inm.tipoOperacion || (inm.tipoInmuebleNombre || '');
      const direccion = inm.direccion || 'Dirección no disponible';
      const barrio = inm.barrio || '';
      const ciudad = inm.ciudad || '';
      const estado = inm.estado || '';

      article.innerHTML = `
        <div class="card-media">
          <img src="${escapeHtml(imgUrl)}" alt="Imagen de ${escapeHtml(direccion)}" loading="lazy" onerror="this.src='/img/no-image.png'"/>
        </div>
        <div class="card-body">
          <h3 class="card-title">${escapeHtml(tipoNombre)}</h3>
          <p class="card-address">${escapeHtml(direccion)}</p>
          <p class="card-location">${escapeHtml(barrio)} • ${escapeHtml(ciudad)}</p>
          <p class="card-meta">Metraje: ${escapeHtml(metraje)} ${estado ? '• ' + escapeHtml(estado) : ''}</p>
          <p class="card-price">${escapeHtml(precio)}</p>
        </div>
      `;

      fragment.appendChild(article);
    });

    gridEl.appendChild(fragment);
  }

  // Filtros (case-insensitive)
  function aplicarFiltros() {
    const ciudadSel = (selectCiudad?.value || 'todos').toLowerCase();
    const tipoSel = (selectTipo?.value || 'todos').toLowerCase();

    const filtrados = inmueblesGlobal.filter(i => {
      const ciudad = (i.ciudad || '').toLowerCase();
      // usamos tipo_operacion o tipoInmuebleNombre
      const tipo = ((i.tipo_operacion || i.tipoOperacion) || (i.tipoInmuebleNombre || '')).toLowerCase();
      const matchCiudad = ciudadSel === 'todos' || ciudad === ciudadSel;
      const matchTipo = tipoSel === 'todos' || tipo === tipoSel;
      return matchCiudad && matchTipo;
    });

    renderizar(filtrados);
  }

  // Carga desde backend
  async function cargarInmuebles(tipoOperacion = 'ARRIENDO') {
    showLoading();
    try {
      const url = `${BASE_API}?tipo=${encodeURIComponent(tipoOperacion)}`;
      const res = await fetch(url, { credentials: 'same-origin' });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();
      inmueblesGlobal = Array.isArray(data) ? data : [];
      aplicarFiltros();
    } catch (err) {
      console.error('Error cargando inmuebles:', err);
      showError('No se pudieron cargar las propiedades. Intenta más tarde.');
    }
  }

  // Inicialización
  document.addEventListener('DOMContentLoaded', () => {
    gridEl = q('gridInmuebles');
    emptyEl = q('emptyState');
    errorEl = q('errorState');
    errorMsgEl = q('errorMessage');
    selectCiudad = q('ciudad');
    selectTipo = q('tipo');

    if (selectCiudad) selectCiudad.addEventListener('change', aplicarFiltros);
    if (selectTipo) selectTipo.addEventListener('change', aplicarFiltros);

    // Cargar inicialmente ARRIENDO
    cargarInmuebles('ARRIENDO');
  });

  // API pública para pruebas
  window.INMUEBLES = {
    setItems(items) {
      inmueblesGlobal = Array.isArray(items) ? items : [];
      aplicarFiltros();
    },
    reload(tipo = 'ARRIENDO') {
      cargarInmuebles(tipo);
    }
  };
})();

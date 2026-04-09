// IIFE para evitar contaminación global

  "use strict";

  // ===============================
  // ALERTAS (SweetAlert2)
  // ===============================
  function alertaInmueble(event) {
    if (event && typeof event.preventDefault === "function") event.preventDefault();

    const swalWithBootstrapButtons = Swal.mixin({
      customClass: {
        confirmButton: "btn btn-success",
        cancelButton: "btn btn-danger"
      },
      buttonsStyling: false
    });

    swalWithBootstrapButtons.fire({
      title: "¿Quieres volver atrás?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Sí",
      cancelButtonText: "No",
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {
        window.history.back();
      }
    });
  }

  function confirmarSobreNosotros(event) {
    if (event && typeof event.preventDefault === "function") event.preventDefault();

    Swal.fire({
      title: "¿Quieres ver más sobre nosotros?",
      text: "Puedes ver más sobre nosotros",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Confirmar"
    }).then((result) => {
      if (result.isConfirmed) {
        // Recomendación: evita espacios en rutas; si existen, codifícalas con encodeURI
        window.location.href = "../SOBRE%20NOSOTROS/sobrenosotros.html";
      }
    });
  }

  function confirmarInmuebles(event) {
    if (event && typeof event.preventDefault === "function") event.preventDefault();

    Swal.fire({
      title: "¿Quieres ver nuestros inmuebles?",
      text: "Puedes ver nuestros inmuebles",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3b82f6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Confirmar"
    }).then((result) => {
      if (result.isConfirmed) {
        window.location.href = "../SECCIÓN%20DE%20INMUEBLES/inmuebles.html";
      }
    });
  }

  function confirmarNosotros(event) {
    if (event && typeof event.preventDefault === "function") event.preventDefault();

    Swal.fire({
      title: "¿Quieres ver cómo contactarnos?",
      text: "Puedes ver cómo contactarnos",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Confirmar"
    }).then((result) => {
      if (result.isConfirmed) {
        window.location.href = "../CONTACTANOS/contactanos.html";
      }
    });
  }

  
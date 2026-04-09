package com.example.demo.controller;

import com.example.demo.model.Inmueble;
import com.example.demo.repository.InmuebleRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;

@Controller
public class InmuebleController {

    private final InmuebleRepository inmuebleRepository;

    public InmuebleController(InmuebleRepository inmuebleRepository) {
        this.inmuebleRepository = inmuebleRepository;
    }

    // ====================== PÁGINA PRINCIPAL ======================
    @GetMapping("/")
    public String paginaPrincipal(Model model) {
        List<Inmueble> inmuebles = inmuebleRepository
                .findByEstadoAndTipoOperacionIn("DISPONIBLE", Arrays.asList("VENTA", "ARRIENDO"));

        model.addAttribute("inmuebles", inmuebles);
        return "index";           // ← nombre de tu plantilla Thymeleaf (index.html)
    }

    // (Opcional) Si quieres una ruta específica para inmuebles
    @GetMapping("/inmuebles")
    public String listarInmuebles(Model model) {
        List<Inmueble> inmuebles = inmuebleRepository
                .findByEstadoAndTipoOperacionIn("DISPONIBLE", Arrays.asList("VENTA", "ARRIENDO"));

        model.addAttribute("inmuebles", inmuebles);
        return "inmuebles";       // si tienes una plantilla inmuebles.html
    }
}
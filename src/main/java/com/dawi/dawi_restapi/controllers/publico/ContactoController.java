package com.dawi.dawi_restapi.controllers.publico;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/public/contacto")
public class ContactoController {
    @GetMapping("/contacto")
    public String mostrarContacto() {

        //aqui no se que poner D:
        return "usuario/contacto";
    }
}

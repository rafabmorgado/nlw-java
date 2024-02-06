package dev.morgadorafa.certification_nlw.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/firstcontroller")
public class FirstController {

    @GetMapping("/returnfirstcontroller")    
    public String returnFirstController() {
        return "Create first controller";
    }

    @GetMapping("/returnuser")
    public Usuario returnUser(){
        var usuario = new Usuario("Rafa", 42);
        return usuario;
    }

    record Usuario(String nome, int idade){
        
    }
    
}

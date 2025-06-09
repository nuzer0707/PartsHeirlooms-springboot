package com.example.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = {"/products"})
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8002"},allowCredentials = "true")
public class ProductController {

}

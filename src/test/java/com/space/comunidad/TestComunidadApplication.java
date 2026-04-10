package com.space.comunidad;

import org.springframework.boot.SpringApplication;

public class TestComunidadApplication {

	public static void main(String[] args) {
		SpringApplication.from(ComunidadApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

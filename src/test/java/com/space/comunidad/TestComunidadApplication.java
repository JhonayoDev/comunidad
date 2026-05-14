package com.space.comunidad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public class TestComunidadApplication {

  public static void main(String[] args) {
    SpringApplication.from(ComunidadApplication::main).with(TestcontainersConfiguration.class).run(args);
  }

}

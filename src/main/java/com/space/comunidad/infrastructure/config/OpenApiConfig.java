package com.space.comunidad.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

  private static final String SECURITY_SCHEME_NAME = "bearerAuth";

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Comunidad API")
            .description("""
                API REST del sistema de gestión de condominios **Comunidad**.

                Permite administrar:
                - Sectores y unidades del condominio
                - Residentes y propietarios
                - Visitas, vehículos
                - encomiendas *(próximamente)*
                - Gastos comunes y avisos *(próximamente)*

                **Autenticación:** todos los endpoints (excepto `/api/auth/**`) \
                requieren un token JWT en el header `Authorization: Bearer <token>`.
                """)
            .version("v1.0")
            .contact(new Contact()
                .name("Equipo Comunidad")))
        .addSecurityItem(new SecurityRequirement()
            .addList(SECURITY_SCHEME_NAME))
        .components(new Components()
            .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Token JWT obtenido en /api/auth/login o /api/auth/register")));
  }
}

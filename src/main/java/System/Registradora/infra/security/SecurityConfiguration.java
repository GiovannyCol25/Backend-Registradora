package System.Registradora.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/empleados/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/empleados/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/empleados").hasRole("ADMIN")//
                        .requestMatchers(HttpMethod.DELETE, "/empleados/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/productos/**").hasAnyRole("ADMIN", "ALMACENISTA")
                        .requestMatchers(HttpMethod.POST, "/productos").hasAnyRole("ADMIN", "ALMACENISTA")
                        .requestMatchers(HttpMethod.POST, "/ventas").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers(HttpMethod.GET,"/productos/**").hasAnyRole("ADMIN", "ALMACENISTA")
                        .requestMatchers(HttpMethod.GET, "/consultas/**").hasAnyRole( "ADMIN", "ALMACENISTA", "VENDEDOR")
                        .requestMatchers(HttpMethod.GET,"/ventas/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers(HttpMethod.POST, "/clientes").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers(HttpMethod.PUT, "/clientes/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers(HttpMethod.GET,"/clientes/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers(HttpMethod.DELETE, "/clientes/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers(HttpMethod.POST, "/compras").hasAnyRole("ADMIN", "ALMACENISTA")
                        .requestMatchers(HttpMethod.GET,"/compras/**").hasAnyRole("ADMIN", "ALMACENISTA")
                        .requestMatchers(HttpMethod.PUT, "/compras/**").hasAnyRole("ADMIN", "ALMACENISTA")
                        .requestMatchers(HttpMethod.POST,"/proveedores").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/proveedores/**").hasAnyRole("ADMIN", "ALMACENISTA")
                        .requestMatchers(HttpMethod.PUT,"/proveedores/**").hasAnyRole("ADMIN", "ALMACENISTA")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

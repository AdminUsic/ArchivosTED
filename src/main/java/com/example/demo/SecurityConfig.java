package com.example.demo;


/*import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

*/
//@Configuration
//@EnableMethodSecurity(prePostEnabled =true)
public class SecurityConfig  {

    /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity https) throws Exception{

        return https
        .httpBasic()
        .and()
        .authorizeHttpRequests().anyRequest().authenticated()
        .and()
        .build();
    }*/

    /*@Bean
    public UserDetailsService detailsService(String usuario, String contraseña, String rool){
        var adminUser = User.withUsername(usuario).password(contraseña).authorities(rool).build();
        var result = new InMemoryUserDetailsManager();
        result.createUser(adminUser);
        return result;
    }*/

   /*  @Bean
    public UserDetailsService detailsService(){
        var adminUser = User.withUsername("8408370")
        .password(passwordEncoder().encode("123"))
        .authorities("ARCHIVOS Y BIBLIOTECA").build();
        var result = new InMemoryUserDetailsManager();
        result.createUser(adminUser);
        return result;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }*/
}

/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@Configuration
@EnableWebSecurity
public class LcWebSecurityConfig {

   @Bean
   SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http.httpBasic();

      http.csrf().disable().authorizeRequests().anyRequest().permitAll();

      return http.build();
   }

   @Bean
   PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
   }
}

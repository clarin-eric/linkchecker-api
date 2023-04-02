/**
 * 
 */
package eu.clarin.linkchecker.web.conf;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import eu.clarin.linkchecker.persistence.model.Role;
import eu.clarin.linkchecker.persistence.repository.ClientRepository;


/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@Configuration
@EnableWebSecurity
public class LcWebSecurityConfig {
   
   @Autowired
   private ClientRepository clRep;

   @Bean
   SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      //enable HTTP Basic authentication
      http.httpBasic();

      http.csrf().disable()
         .authorizeRequests()
         .antMatchers("/admin/**").hasAuthority(Role.ADMIN.name())
         .antMatchers("/checkrequest/**").hasAnyAuthority(Role.ADMIN.name(), Role.USER.name());

      return http.build();
   }

   @Bean
   PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
   }
   
   @Bean
   UserDetailsService userDetailsService() {
      
      return new UserDetailsService() {

         @Override
         public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            return clRep
                  .findByName(username)
                  .map(user -> new UserDetails(){

                     private static final long serialVersionUID = 1L;

                     @Override
                     public Collection<? extends GrantedAuthority> getAuthorities() {return List.of(new SimpleGrantedAuthority(user.getRole().name()));}
                     @Override
                     public String getPassword() { return user.getPassword(); }
                     @Override
                     public String getUsername() { return user.getName(); }
                     @Override
                     public boolean isAccountNonExpired() { return true; }
                     @Override
                     public boolean isAccountNonLocked() { return true; }
                     @Override
                     public boolean isCredentialsNonExpired() { return true; }
                     @Override
                     public boolean isEnabled() { return true; }
                     }
                     
                  )
                  .orElseThrow(() -> new UsernameNotFoundException("the user " + username + " does not exist"));
         }         
      };      
   }
   
   @Bean
   AuthenticationProvider authenticationProvider() {
      
      return new AuthenticationProvider() {

         @Override
         public Authentication authenticate(Authentication authentication) throws AuthenticationException {

            String username = authentication.getName();
            String password = authentication.getCredentials().toString();

            UserDetails u = userDetailsService().loadUserByUsername(username);

            if (passwordEncoder().matches(password, u.getPassword())) {
               return new UsernamePasswordAuthenticationToken(username, password, u.getAuthorities());
            }
            else {
               throw new BadCredentialsException("Your login is not correct!");
            }
         }

         @Override
         public boolean supports(Class<?> authentication) {
            
            return UsernamePasswordAuthenticationToken.class
                     .isAssignableFrom(authentication);

         }
      };
   }
}

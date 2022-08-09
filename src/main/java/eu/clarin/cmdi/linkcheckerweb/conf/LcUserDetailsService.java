/**
 * 
 */
package eu.clarin.cmdi.linkcheckerweb.conf;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import eu.clarin.cmdi.cpa.model.User;
import eu.clarin.cmdi.cpa.repository.UserRepository;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
@Service
public class LcUserDetailsService implements UserDetailsService {
   
   @Autowired
   private UserRepository usRep;
   

   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      
      return usRep
               .findByUsername(username)
               .map(AuthenticatedUser::new)
               .orElseThrow(() -> new UsernameNotFoundException("the user " + username + " does not exist"));

   }
   
   public class AuthenticatedUser extends User implements UserDetails {
      
      private static final long serialVersionUID = 1L;
      
      private Collection<GrantedAuthority> authorities;
      /**
       * @param user
       */
      public AuthenticatedUser(User user) {
         super(user.getUsername(), user.getPassword(), user.getRole());
         
         this.setId(user.getId());
         
         this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
         
      }

      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
         
         return this.authorities;
      }

      @Override
      public boolean isAccountNonExpired() {

         return true;
      }

      @Override
      public boolean isAccountNonLocked() {

         return true;
      }

      @Override
      public boolean isCredentialsNonExpired() {

         return true;
      }

      @Override
      public boolean isEnabled() {

         return true;
      }      
   }
}

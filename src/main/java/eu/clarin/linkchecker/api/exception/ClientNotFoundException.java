/**
 * 
 */
package eu.clarin.linkchecker.api.exception;

/**
 * @author WolfgangWalter Sauer (wowasa)
 *
 */
public class ClientNotFoundException extends RuntimeException{
   
   private static final long serialVersionUID = 1L;

   public ClientNotFoundException(String message) {
      super(message);
   }

}

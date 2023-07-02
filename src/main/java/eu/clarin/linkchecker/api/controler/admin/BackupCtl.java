/**
 * @author Wolfgang Walter SAUER (wowasa) &lt;clarin@wowasa.com&gt;
 *
 */
package eu.clarin.linkchecker.api.controler.admin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@RestController
@RequestMapping("/admin/backup")
@Slf4j
public class BackupCtl {
   
   @Value("${linkchecker-api.directory.backups}")
   private String backupsDirStr;
   
   @GetMapping("/latest")
   @Operation(summary = "get latest backup", description = "get latest database backup in format *tar.gz")
   public ResponseEntity<Resource> getBackup(){
      

      try {
         Optional<File> latestBackup = Files.list(Path.of(backupsDirStr)).map(Path::toFile).filter(file -> file.toString().endsWith("tar.gz")).max(Comparator.comparingLong(File::lastModified));
         
         if(latestBackup.isPresent()) {
            
            FileSystemResource resource = new FileSystemResource(latestBackup.get());
            
            HttpHeaders headers = new HttpHeaders();
            
            headers.setContentType(
                  MediaTypeFactory
                     .getMediaType(resource)
                     .orElse(MediaType.APPLICATION_OCTET_STREAM)
                  );
            headers.setContentDisposition(
                  ContentDisposition
                     .inline()
                     .filename(resource.getFilename())
                     .build()
                  );
            
            return ResponseEntity.ok().headers(headers).body(resource);
         }
         else {
            return ResponseEntity.noContent().build();
         }                  
      }
      catch (IOException e) {

        log.error("IOException while reading files from backups directory {} - either the directory doesn't exist or the application user doesn't have the right to read it", this.backupsDirStr);
      }
      
      return ResponseEntity.internalServerError().build();
      
   }

}

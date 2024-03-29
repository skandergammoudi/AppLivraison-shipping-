package com.projet.pfe.Controller;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.projet.pfe.model.User;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projet.pfe.Service.UserService;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class UserController {
	 @Autowired
	    private UserService userService;
	 @Autowired  ServletContext context;
	
	 
	 @GetMapping("/users")
	    public List<User> list() {
		 System.out.println("Get all Users...");
	             return userService.getAll();
	   }
	 	 
	 @GetMapping("/users/{id}")
	 public ResponseEntity<User> post(@PathVariable long id) {
	        Optional<User> cat = userService.findById(id);
	        return cat.map(ResponseEntity::ok)
	                   .orElseGet(() -> ResponseEntity.notFound()
                                               .build());
	    }
	 @GetMapping("/users/verif/{email}")
	 public List<User> listUser(@PathVariable String email){
		 System.out.println("Get all Users...");
		 return userService.getAllByEmail(email);
		 
	 }
	 
	    
	 @GetMapping("/users/auth/{username}")
	 public ResponseEntity<User> login(@PathVariable String username) {
	        Optional<User> cat = userService.login(username);
	        return cat.map(ResponseEntity::ok)
	                   .orElseGet(() -> ResponseEntity.notFound()
                                               .build());
	    }
	 
	    
	 
	  /*  public long save(@RequestBody User User) {
		 System.out.println("Save all Users...");
	        return userService.save(User);
	    }*/

	 

	   
	   @PostMapping("/users")
		 public long createUser (@RequestParam("file") MultipartFile file,
				 @RequestParam("user") String user) throws JsonParseException , JsonMappingException , Exception
		 {
			
			User userr = new ObjectMapper().readValue(user, User.class);
			addUserImage(file);
		    String filename = file.getOriginalFilename();
		    String newFileName = FilenameUtils.getBaseName(filename)+"."+FilenameUtils.getExtension(filename);
		    userr.setFileName(newFileName);
		    return userService.save(userr);
		 }
		 
		   

		 @PutMapping("/users/{id}")
		    public void update(@PathVariable long id,@RequestParam("file") MultipartFile file,
					 @RequestParam("user") String user) throws JsonParseException , JsonMappingException , Exception {
		     User userr = new ObjectMapper().readValue(user, User.class);
		        	deleteUserImage(userr);
		        	 String filename = file.getOriginalFilename();
		     	    String newFileName = FilenameUtils.getBaseName(filename)+"."+FilenameUtils.getExtension(filename);
		     	    userr.setFileName(newFileName);
		            userService.update(id, userr);
		           
		            addUserImage(file);
		       
		    }

		    @DeleteMapping("/users/{id}")
		    public void delete(@PathVariable long id) {
		        userService.delete(id);
		    }
		     
		    
		    @GetMapping(path="/ImgUsers/{id}")
			 public byte[] getPhoto(@PathVariable("id") Long id) throws Exception{
		    	 System.out.println("Get all Users Images...");
				 User User   =userService.findById(id).get();
				 return Files.readAllBytes(Paths.get(context.getRealPath("/ImgUsers/")+User.getFileName()));
			 }
		    
		    private void addUserImage(MultipartFile file)
		    {
		    	boolean isExit = new File(context.getRealPath("/ImgUsers/")).exists();
			    if (!isExit)
			    {
			    	new File (context.getRealPath("/ImgUsers/")).mkdir();
			    	System.out.println("mk dir Imagess.............");
			    }
			    String filename = file.getOriginalFilename();
			    String newFileName = FilenameUtils.getBaseName(filename)+"."+FilenameUtils.getExtension(filename);
			    File serverFile = new File (context.getRealPath("/ImgUsers/"+File.separator+newFileName));
			    try
			    {
			    
			    	 FileUtils.writeByteArrayToFile(serverFile,file.getBytes());
			    	 
			    }catch(Exception e) {
			    	 System.out.println("Failed to Add Image User !!");
			    }
			    
		    	
		    }
		    
		    private void deleteUserImage(User user)
		    {
		    	System.out.println( " Delete User Image");
		         try { 
		        	 File file = new File (context.getRealPath("/ImgUsers/"+user.getFileName()));
		             System.out.println(user.getFileName());
		              if(file.delete()) { 
		                System.out.println(file.getName() + " is deleted!");
		             } else {
		                System.out.println("Delete operation is failed.");
		                }
		          }
		            catch(Exception e)
		            {
		                System.out.println("Failed to Delete image !!");
		            }
		    }
}

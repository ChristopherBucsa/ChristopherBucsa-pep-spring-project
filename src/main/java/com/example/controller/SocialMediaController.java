package com.example.controller;

import com.example.service.AccountService;
import com.example.entity.Account;
import com.example.exception.DuplicateUsernameException;
import com.example.exception.InvalidCredentialsException;
import com.example.repository.AccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */

 /**
  * This class will be responsible for handling API requests such as:
  * registering and posting messages.  This class also gives logic to the Services.
  */

/**
 * This annotation combines @controller and @response body - a useful Spring feature.
 * With the @RestController annotation, we simultaneously mark the class
 * as a controller that can handle HTTP requests and serialize the return value
 * of methods into JSON format.
 */
@RestController 
public class SocialMediaController {

    private AccountService accountService; //Reference to accountService
    private AccountRepository accountRepository;

    /**
     * Allow Spring to inject an instance of the AccountService class
     * into a SocialMediaController object, giving the object access to the methods
     * available in accountservice - and also to accountRepository since an instance
     * of AccountRepository is injected into the AccountService construcor.
     * Again, we use the @Autowired annotation to allow Spring to inject
     * such instances.
     * @param accountService
     */
    @Autowired
    public SocialMediaController(AccountService accountService){
        this.accountService = accountService;
    }

    /**
     * First, we need an endpoint definition for registering an account.
     * Using the POST keyword.  The @PostMapping annotation tells spring
     * that the following method will handle HTTP "Post" requests that are sent 
     * to the "/register" endpoint/URL. Hence, any post request made from
     * the user will be automatically "mapped" to /register. Meanwhile, @RequestBody
     * indicates to Spring that a JSON representation of Account will be passed in, and to
     * convert it to an account object.
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerAccount(@RequestBody Account account){
        try{
            accountService.addAccount(account);
            return new ResponseEntity<>("Account successfully created.", HttpStatus.OK);
        } 
        catch (DuplicateUsernameException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }    
        catch(InvalidCredentialsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This endpoint will relate to the times when a client attempts to login.
     * It simply verifies that their credentials (username and password) exist already within
     * the database.
     * @param An account object to verify its credentials
     * @return A JSON representation of the account including account ID
     */
    @PostMapping("/login")
    public ResponseEntity<Account> verifyLogin(@RequestBody Account account){
        if(accountService.credentialsExist(account)){
            return new ResponseEntity<>(accountRepository.findByUsername(account.getUsername()), HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }     
}

package com.example.controller;
import com.example.service.AccountService;
import com.example.service.MessageService;

import java.util.*;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.exception.DuplicateUsernameException;
import com.example.exception.InvalidCredentialsException;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private MessageService messageService;
    private MessageRepository messageRepository;

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
    public SocialMediaController(AccountService accountService, MessageService messageService, MessageRepository messageRepository, AccountRepository accountRepository) {
        this.accountService = accountService;
        this.messageService = messageService;
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository; 
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
    public ResponseEntity<Account> registerAccount(@RequestBody Account account){
        try{
            accountService.addAccount(account);
            return new ResponseEntity<>(account, HttpStatus.OK);
        } 
        catch (DuplicateUsernameException e){
            return new ResponseEntity<>(account, HttpStatus.CONFLICT);
        }    
        catch(InvalidCredentialsException e){
            return new ResponseEntity<>(account, HttpStatus.BAD_REQUEST);
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
    
    /**
     * Here, we will define the endpoint for submitting a new post.
     * It will be on the endpoint /messages.  The request body will contain
     * a JSON representation of a message, which will be persisted to the database
     * if the requirements are fulfilled - but will not contain a messageID. 
     */
    @PostMapping("/messages")
    public ResponseEntity<Message> submitMessage(@RequestBody Message message) {
        if (messageService.meetsRequirements(message.getMessageText()) && messageService.postedByCheck(message)) {
            messageRepository.save(message); 
            return new ResponseEntity<>(message, HttpStatus.OK);
        } 
        else{ 
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * We now need a method to retrieve all the messages from the database  
     * The response body will contain a JSON representation of a list containing all messages
     * retrieved from the database. If there are no messages, then the
     * list will be empty.
     */
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> retrieveMessages(){
        List<Message> retrievedMessages = messageRepository.findAll(); //find all method of jpa repository returns a list
        return new ResponseEntity<>(retrievedMessages, HttpStatus.OK);
    }

    /**
     * Now we need a method to retrieve a message by its given messageID,
     * which is the primary key of the message table. It will be a Get
     * request at the endpoint /messages/{messageId}
     */
   @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> retrieveMessageById(@PathVariable int messageId) {
    Optional<Message> message = messageRepository.findById(messageId); //findById may or may not contain a null value of type Optional
    return message.map(msg -> new ResponseEntity<>(msg, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.OK)); // Empty body, 200 OK
    }

    /**
     * Now, we need a way for a user to delete a message given by its
     * messageId.  The deleteion of an existing message will remove it from the 
     * database and the response body will contain the number of rows
     * updated in the message table. If the messageId does not exist
     * the response body will be empty.
     */
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<String> deleteMessageById(@PathVariable int messageId){
        if(messageRepository.existsById(messageId)){
            messageRepository.deleteById(messageId);
            return new ResponseEntity<>("1 row deleted from database", HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * Next, we need the API to be able to update a message object's
     * "messageText" identified by a message Id.  Request body should
     * contain a new messageText values to replace the message identified
     * by the messageId.  The request body can not be guaranteed to contain
     * any other information
     */
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<Integer> updateMessage(@PathVariable int messageId, @RequestBody Map<String, String> body){ 
        String newText = body.get("messageText");
        if(newText == null || newText.isBlank() || newText.length() > 255){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if(optionalMessage.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Message existingMessage = optionalMessage.get();
        existingMessage.setMessageText(newText);
        messageRepository.save(existingMessage);
        return new ResponseEntity<>(1, HttpStatus.OK);
    }

    
     //We now need a method to retrieve all message written by a particular user
     @GetMapping("/accounts/{accountId}/messages")
     public ResponseEntity<List<Message>> retrieveMessagesFromUser(@PathVariable int accountId) {
        List<Message> accountMessages = messageRepository.findByPostedBy(accountId);
        if (accountMessages == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(accountMessages, HttpStatus.OK);
    }

}

package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Message;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;

//This class, like the AccountService class, will communicate with the MessageRepository class to perform operations.

@Service //Indicates to Spring that this is a service-layer bean.
public class MessageService {

    private MessageRepository messageRepository; //Reference to MessageRepository interface to let Spring know what to inject
    private AccountRepository accountRepository; //Reference to AccountRepository interface

    @Autowired //Whenever an instance of this class is created, Spring will also implement an instance of the messageRepository class.
    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository){
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;  
    }

    /**
     * Let's create a method to check that the messageText fulfills the necessary requirements: 
     * It must not be blank, and it must also be less than or equal to 255 characters in length.
     * @param messageText - The message associated with the messageID that will be verified
     * @return - true if the message meets the necessary requirements, false otherwise.
     */
    public boolean meetsRequirements(String messageText){
        if(!messageText.isBlank() && messageText.length() <= 255){
            return true;
        }
        else{
            return false; 
        }
    }

    /**
     * We also need a method to check that "postedBy" attribute associated with a message refers to a real, existing
     * user in the database.  We can leverage the AccountService class here to check that. 
     * @param - a message object containing it's information.
     * @return - true if the given message is associated with a user, false if not
     */
    public boolean postedByCheck(Message message){
        if(accountRepository.existsById(message.getPostedBy())){ //postedBy references accountID (foreign key)
            return true;
        }
        else{
            return false;
        }
    }
}

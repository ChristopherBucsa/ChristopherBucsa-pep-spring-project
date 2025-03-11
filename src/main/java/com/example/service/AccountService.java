package com.example.service;

import com.example.repository.AccountRepository;
import com.example.entity.Account;
import com.example.exception.DuplicateUsernameException;
import com.example.exception.InvalidCredentialsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class will be responsible for handling "business logic", such as:
 * validating inputs, interacting with AccountRespository, and managing
 * account-related operations.
 */

 @Service //Let's Spring know that this is a Service-layer bean
public class AccountService {

    /**
     * Reference to the AccountRepository interface.  This reference was created to 
     * let Spring know what it's injecting into the constructor.
     */
    private AccountRepository accountRepository; 

    /**
     * Every time an AccountService class is created, we also have access to the
     * methods available in the AccountRepository interface through dependency
     * injection provided by Spring.  Need to use the Autowire annotation
     * in order for Spring to know where to inject AccountRepository.   
     */
     @Autowired
    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }
    

    /**
     * This method will return true is the provided username and password meets
     * the necessary requirements for it to be added to the database.  Falso otherwise.
     * @param username - String that must not be blank
     * @param password - String that must be at least 4 characters long
     * @return true if the above requirements are met, false otherwise.
     */
    public boolean meetsRequirements(String username, String password){
        if(!username.isBlank() && password.length() >= 4){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * This method checks if the provided username already exists in the database.
     * It uses an instance of hte accountRepository to call its
     * "findByUsername" method, which Spring automatically implements for us. 
     * @param username - Username to be searched in the database
     * @return true if the username exists in the database, false otherwise.
     */
    public boolean isDuplicateUsername(String username){
        if(accountRepository.findByUsername(username) != null){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * This method will add an account to the database by leveraging
     * the "meetsRequirements()" method, as well as the "isDuplicateUsername()" method
     * in oder to make sure that the accounts information is able to be added
     * to the database.  If not, we throw custom exceptions that will be handled
     * in the controller class.
     * @param account - the account to be verified
     * @return nothing, just add account to DB if requirements are met
     */
    public void addAccount(Account account){
        if(meetsRequirements(account.getUsername(), account.getPassword()) == true){
            if(isDuplicateUsername(account.getUsername()) == false){
                accountRepository.save(account);
            }
            else{
                throw new DuplicateUsernameException("Username already exists in the database");
            }
        }
        else{
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }

    /**
     * This method will interact with the Account Repository to check whether or not
     * a username and password exist in the database.  This is mainly for logging in,
     * to make sure the credentials are valid.  
     * @param account - Account object with a username and password to check
     * @return true if username and password exist in the database, false otherwise.
     */
    public boolean credentialsExist(Account account){
        if(accountRepository.findByUsernameAndPassword(account.getUsername(), account.getPassword()) != null){
            return true;
        }
        else{
            return false;
        }
    }
}

package com.example.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.Account;

/**
 * The Account Repository class will be in charge with interacting directly with the database.
 * This includes adding accounts to the database, checking to see if info is valid,
 * and seeing if certain criteria are met. This is an interface, so
 * only the definition of methods need to be added, but since we're leveraging
 * Spring, we have access to many methods that will automatically be implemented.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> { //"Account" let's the JpaRepository know what entity this class is dealing with, and Integer is the primary key type

    /**
     * Need a method to check is a username already exists in the account table.
     * Can leverage Spring to automatically implement this method for use, whenever it's
     * called upon by another class.
     */

     public Account findByUsername(String username);

     /**
      * Normally, we'd define a method for adding a record into the database,
      * but we can utilize Spring's ".save()" method to automatically do it
      * for us.
      */

      /**
       * We need a method in the repository to see if a username and passwrod
       * already exists in the database, not just the username.  This is mainly for
       * the "log-in" feature. 
       */
      public Account findByUsernameAndPassword(String username, String password);
}

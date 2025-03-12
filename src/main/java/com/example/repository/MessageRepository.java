package com.example.repository;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.Message;

/**
 * This class will be a repository that deals with the "message" table of our database.
 * It will be directly responsible for processing interactions with the database.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findPostedBy(int accountId);

}

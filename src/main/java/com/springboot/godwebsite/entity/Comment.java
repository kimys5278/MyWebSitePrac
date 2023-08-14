package com.springboot.godwebsite.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne
    private Board board;
    @ManyToOne // Many comments can be written by one user
    private User user;

    // Getter and setters for user
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public Long getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
        // getters, setters, etc.
    }

    public void setContent(String content) {
        this.content = content;


    }

    public String getContent() {
        return content;
    }

}

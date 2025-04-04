package com.ymanch.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PostMention {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long postMentionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private Posts post;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentionedUserId")
    private User mentionedUser;
   
    
    // Getters and Setters
}
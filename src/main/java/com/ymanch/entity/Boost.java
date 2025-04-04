package com.ymanch.entity;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ymanch.helper.Enums.Status;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Table(indexes = { 
	    @Index(name = "idx_boost_post_id_status", columnList = "post_id,boost_status"),
	    
})
@Getter
@Setter
@Entity
public class Boost {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boostId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;  // The admin user who is boosting the post

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "postId")
    private Posts post;  // The post that is being boosted

    @NotEmpty(message = "Boost start date should not be empty")
    private String startDate;  // Start date (String format like 'yyyy-MM-dd')

    @NotEmpty(message = "Boost start time should not be empty")
    private String startTime;  // Start time (String format like 'HH:mm:ss')

    
    @NotEmpty(message = "Boost end date should not be empty")
	private String endDate;

	@NotEmpty(message = "Boost start time should not be empty")
	private String endTime;
    
	@JsonIgnore
	@CreationTimestamp
	private LocalDateTime boostCreatedAt;

	@JsonIgnore
	@UpdateTimestamp
	private LocalDateTime boostUpdatedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "boost_status", nullable = false)
    private Status boostStatus;  // Status of the boost (ACTIVE/ENDED)

    // Method to combine start date and time into a LocalDateTime
   

    public Boost() {
    	this.boostStatus=Status.ENDED;
//    	this.durationInDays=1;
    }
    
    @JsonIgnore
    @ManyToMany
    @JoinTable(
      name = "Boost_Post_district", 
      joinColumns = @JoinColumn(name = "boostId"), 
      inverseJoinColumns = @JoinColumn(name = "districtId"))
    private List<District> districts;  // List of districts associated with this boost
    

    
}

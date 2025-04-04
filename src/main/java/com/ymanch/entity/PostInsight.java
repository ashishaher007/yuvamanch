package com.ymanch.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_insight", indexes = {
    @Index(name = "idx_post_insight_post", columnList = "post_id"),
    @Index(name = "idx_post_insight_user", columnList = "user_id"),
    @Index(name = "idx_post_insight_district", columnList = "district_id")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostInsight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postInsightId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @JsonIgnore
    private Posts post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    @JsonIgnore
    private District district;

    @Column(name = "reach_count")
	private Integer reachCount;  // Count of reach (views for >= 3 seconds)

	@Column(name = "view_count")
	private Integer viewCount;  
 
    @JsonIgnore
	@CreationTimestamp
	private LocalDateTime postInsightCreatedAt;

	@JsonIgnore
	@UpdateTimestamp
	private LocalDateTime postInsightUpdateAt;
}

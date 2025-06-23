package com.haru.SwipeStyle.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_clothing", indexes = {
        @Index(name = "idx_user_clothing", columnList = "user_id, clothing_id"),
        @Index(name = "idx_saved_at", columnList = "savedAt")
    },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_clothing_interaction",
                        columnNames = {"user_id", "clothing_id", "interactionType"})
        }

)
public class UserClothing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clothing_id", nullable = false)
    @JsonBackReference
    private Clothing clothing;

    @Enumerated(EnumType.STRING)
    @Column(name = "interaction_type")
    private InteractionType interactionType;

    @Column(name = "saved_at", nullable = false)
    private LocalDateTime savedAt;

    @PrePersist
    protected void onCreate() {
        savedAt = LocalDateTime.now();
    }
}

package com.yoyodev.starter.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity to store OAuth2 provider information for users.
 * This allows a single user to be linked to multiple OAuth2 providers.
 */
@Setter
@Getter
@Entity
@Table(name = "user_oauth2_provider", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"provider_id", "provider_name"}))
@AllArgsConstructor
@NoArgsConstructor
public class UserOAuth2Provider extends AbstractAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * The OAuth2 provider name (e.g., "google", "github")
     */
    @Column(name = "provider_name", nullable = false)
    private String providerName;
    
    /**
     * The user ID from the provider
     */
    @Column(name = "provider_id", nullable = false)
    private String providerId;
    
    /**
     * The user's email from the provider
     */
    @Column(name = "provider_email")
    private String providerEmail;
    
    /**
     * Additional attributes from the provider, stored as JSON
     */
    @Column(name = "provider_attributes", columnDefinition = "TEXT")
    private String providerAttributes;
}
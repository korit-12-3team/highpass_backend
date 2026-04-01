package com.example.highpass_backend.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
        name="oauth2_users",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"provider", "provider_id"}
        )
)
public class OAuth2User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at" ,nullable = false)
    private LocalDate created_at;

    @Column(unique = true, nullable = false, length = 50)
    private String provider;

    @Column(name= "provider_id" ,unique = true, nullable = false, length = 255)
    private String provider_id;

    @PrePersist
    protected void onCreate() {
        this.created_at = LocalDate.now();
    }

    public static OAuth2User create(User user, String provider, String providerId, String accessToken) {
        OAuth2User oAuth2User = new OAuth2User();
        oAuth2User.user = user;
        oAuth2User.provider = provider;
        oAuth2User.provider_id = providerId;
        return oAuth2User;
    }
}

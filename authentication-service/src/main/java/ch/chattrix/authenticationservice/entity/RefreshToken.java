package ch.chattrix.authenticationservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID refreshTokenUuid;

    private UUID userUuid;

    private String token;

    private Date expiresAt;

    private Date createdAt;
}

package ch.chattrix.authenticationservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_credential")
public class UserCredential {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userCredentialUuid;

    private UUID userUuid;

    @Column(unique = true)
    private String email;

    private String passwordHash;

    private Date createdAt = new Date();

    private Date updatedAt;
}

package ch.chattrix.userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    private UUID userUuid;

    @Column(length = 20, nullable = false, unique = true)
    private String username;

    private Date createdAt;

    private Date updatedAt;
}

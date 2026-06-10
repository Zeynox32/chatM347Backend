package ch.bbw.chattrix.entity.mariadb;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String displayName;

    @Column(name = "e_mail", nullable = false)
    private String eMail;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SessionToken> sessionTokens;

    public User(String displayName, String eMail, String password) {
        this.displayName = displayName;
        this.eMail = eMail;
        this.password = password;
    }
}

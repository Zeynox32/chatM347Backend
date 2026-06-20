package ch.chattrix.shared.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserData {
    private UUID userUuid;
    private String username;
    private String email;
    private Date createdAt;
}

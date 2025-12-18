package com.buyone.userservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data               // shortcut to combining @Getter, @Setter, @ToString, and @EqualsAndHashCode
@NoArgsConstructor  // generates a constructor with no arguments (empty body).
@AllArgsConstructor // Generates a constructor with one argument for every field in the class.
@Builder            // implements Builder pattern
                    // User user = User.builder().name("Joon").email(j@k.kr").password("pw").build()

@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private String avatar;
}

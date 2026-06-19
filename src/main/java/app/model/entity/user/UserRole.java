package app.model.entity.user;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("Admin"),
    USER("User");

    private final String displayRole;

    UserRole(String displayRole) {
        this.displayRole = displayRole;
    }

}

package com.kenis.usermanager.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * This class represents a user of the application. It contains information such as the user's id, first and last name,
 * <p>
 * username, password, email, profile image URL, last login date, join date, role, authorities, and whether the user is
 * <p>
 * active and not locked. It also overrides the equals and hashCode methods and includes a no-args constructor and a
 * <p>
 * constructor with all properties as arguments.
 *
 * @author Mohamed Ali Kenis
 * @version 1.0
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "user_details", uniqueConstraints = {
        @UniqueConstraint(name = "user_details_email_unique", columnNames = "email"),
        @UniqueConstraint(name = "user_details_user_id_unique", columnNames = "userId")
})
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            nullable = false,
            updatable = false)
    private Long id;


    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;


    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String profileImageUrl;

    @Column(nullable = true)
    private Date lastLoginDate;

    @Column(nullable = true)
    private Date lastLoginDateDisplay;

    @Column(nullable = false)
    private Date joinDate;

    @Column(nullable = false)
    private String role; //ROLE_USER{delete, update, create}, ROLE_ADMIN, ROLE_SUPERADMIN // TODO replace the [] with SET or List

    @Column(nullable = false)
    private String[] authorities; // TODO replace the [] with SET or List

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private Boolean isNotLocked;

    //= Constructors ==

    public User(String userId, String firstName, String lastName, String username, String password, String email, String profileImageUrl, Date lastLoginDate, Date lastLoginDateDisplay, Date joinDate, String role, String[] authorities, Boolean isActive, Boolean isNotLocked) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.lastLoginDate = lastLoginDate;
        this.lastLoginDateDisplay = lastLoginDateDisplay;
        this.joinDate = joinDate;
        this.role = role;
        this.authorities = authorities;
        this.isActive = isActive;
        this.isNotLocked = isNotLocked;
    }

    //= Equal & hashCode ==

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(userId, user.userId) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(email, user.email) && Objects.equals(profileImageUrl, user.profileImageUrl) && Objects.equals(lastLoginDate, user.lastLoginDate) && Objects.equals(lastLoginDateDisplay, user.lastLoginDateDisplay) && Objects.equals(joinDate, user.joinDate) && Objects.equals(role, user.role) && Arrays.equals(authorities, user.authorities) && Objects.equals(isActive, user.isActive) && Objects.equals(isNotLocked, user.isNotLocked);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, userId, firstName, lastName, username, password, email, profileImageUrl, lastLoginDate, lastLoginDateDisplay, joinDate, role, isActive, isNotLocked);
        result = 31 * result + Arrays.hashCode(authorities);
        return result;
    }
}

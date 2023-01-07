package com.kenis.supportportal.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

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
    private Long id ;


    @Column(nullable = false)
    private String userId ;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    @JsonIgnore
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

    public User(String userId, String firstName, String lastName, String userName, String password, String email, String profileImageUrl, Date lastLoginDate, Date lastLoginDateDisplay, Date joinDate, String role, String[] authorities, Boolean isActive, Boolean isNotLocked) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
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
        return Objects.equals(id, user.id) && Objects.equals(userId, user.userId) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(userName, user.userName) && Objects.equals(password, user.password) && Objects.equals(email, user.email) && Objects.equals(profileImageUrl, user.profileImageUrl) && Objects.equals(lastLoginDate, user.lastLoginDate) && Objects.equals(lastLoginDateDisplay, user.lastLoginDateDisplay) && Objects.equals(joinDate, user.joinDate) && Objects.equals(role, user.role) && Arrays.equals(authorities, user.authorities) && Objects.equals(isActive, user.isActive) && Objects.equals(isNotLocked, user.isNotLocked);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, userId, firstName, lastName, userName, password, email, profileImageUrl, lastLoginDate, lastLoginDateDisplay, joinDate, role, isActive, isNotLocked);
        result = 31 * result + Arrays.hashCode(authorities);
        return result;
    }
}

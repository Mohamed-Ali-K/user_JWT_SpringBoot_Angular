package com.kenis.supportportal.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

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

    @Column(nullable = false)
    private Date lastLoginDate;

    @Column(nullable = false)
    private Date lastLoginDateDisplay;

    @Column(nullable = false)
    private Date joinDate;

    @Column(nullable = false)
    private String[] roles; //ROLE_USER{delete, update, create}, ROLE_ADMIN, ROLE_SUPERADMIN

    @Column(nullable = false)
    private String[] authorities;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private Boolean isNotLocked;

    //= Constructors ==

    public User(String userId, String firstName, String lastName, String userName, String password, String email, String profileImageUrl, Date lastLoginDate, Date lastLoginDateDisplay, Date joinDate, String[] roles, String[] authorities, Boolean isActive, Boolean isNotLocked) {
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
        this.roles = roles;
        this.authorities = authorities;
        this.isActive = isActive;
        this.isNotLocked = isNotLocked;
    }

    public User() {
    }

    //=Getter & Setter ==

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Date getLastLoginDateDisplay() {
        return lastLoginDateDisplay;
    }

    public void setLastLoginDateDisplay(Date lastLoginDateDisplay) {
        this.lastLoginDateDisplay = lastLoginDateDisplay;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String[] getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String[] authorities) {
        this.authorities = authorities;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getNotLocked() {
        return isNotLocked;
    }

    public void setNotLocked(Boolean notLocked) {
        isNotLocked = notLocked;
    }

    //= Equal & hashcode ==

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!Objects.equals(id, user.id)) return false;
        if (!Objects.equals(userId, user.userId)) return false;
        if (!Objects.equals(firstName, user.firstName)) return false;
        if (!Objects.equals(lastName, user.lastName)) return false;
        if (!Objects.equals(userName, user.userName)) return false;
        if (!Objects.equals(password, user.password)) return false;
        if (!Objects.equals(email, user.email)) return false;
        if (!Objects.equals(profileImageUrl, user.profileImageUrl))
            return false;
        if (!Objects.equals(lastLoginDate, user.lastLoginDate))
            return false;
        if (!Objects.equals(lastLoginDateDisplay, user.lastLoginDateDisplay))
            return false;
        if (!Objects.equals(joinDate, user.joinDate)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(roles, user.roles)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(authorities, user.authorities)) return false;
        if (!Objects.equals(isActive, user.isActive)) return false;
        return Objects.equals(isNotLocked, user.isNotLocked);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (profileImageUrl != null ? profileImageUrl.hashCode() : 0);
        result = 31 * result + (lastLoginDate != null ? lastLoginDate.hashCode() : 0);
        result = 31 * result + (lastLoginDateDisplay != null ? lastLoginDateDisplay.hashCode() : 0);
        result = 31 * result + (joinDate != null ? joinDate.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(roles);
        result = 31 * result + Arrays.hashCode(authorities);
        result = 31 * result + (isActive != null ? isActive.hashCode() : 0);
        result = 31 * result + (isNotLocked != null ? isNotLocked.hashCode() : 0);
        return result;
    }

    //= to String ==

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", lastLoginDate=" + lastLoginDate +
                ", lastLoginDateDisplay=" + lastLoginDateDisplay +
                ", joinDate=" + joinDate +
                ", roles=" + Arrays.toString(roles) +
                ", authorities=" + Arrays.toString(authorities) +
                ", isActive=" + isActive +
                ", isNotLocked=" + isNotLocked +
                '}';
    }

    //= methods ==
}

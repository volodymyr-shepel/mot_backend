package com.mot.model;

import com.mot.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
public class AppUser implements UserDetails {

    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1)

    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "user_sequence")
    private Integer id;

    @Email
    @Column(name = "email",unique = true)
    private String email;

    @NotBlank(message = "first name can not be blank")
    @Column(name = "first_name",length = 50)
    private String firstName;

    @NotBlank(message = "last name can not be blank")
    @Column(name = "last_name",length = 50)
    private String lastName;


    @Column(name = "password",length = 100)
    private String password;

    @NotNull(message = "role can not be null")
    @Column(name = "role",length = 30)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @NotNull
    @Column(name = "is_enabled")
    private Boolean isEnabled = false;

    @NotNull
    @Column(name = "is_locked")
    private Boolean isLocked = false;

    //Getters
    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public UserRole getRole() {
        return role;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    // Constructors
    public AppUser(){}

    public AppUser(String email, String firstName, String lastName, String password, UserRole role) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        GrantedAuthority authority = new SimpleGrantedAuthority(role.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    // Account can not expire
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.isLocked;
    }

    // Credentials can not expire
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

}
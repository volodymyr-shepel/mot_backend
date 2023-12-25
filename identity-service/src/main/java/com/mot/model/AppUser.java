package com.mot.model;

import com.mot.enums.UserRole;
import com.mot.model.token.VerificationToken;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue
    private UUID id;

    @Email
    @Column(unique = true)
    private String email;

    @NotBlank(message = "first name can not be blank")
    @Column(length = 50)
    private String firstName;

    @NotBlank(message = "last name can not be blank")
    @Column(length = 50)
    private String lastName;

    @Column(length = 100)
    private String password;

    @NotNull(message = "role can not be null")
    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @NotNull
    @Column
    private Boolean isEnabled = false;

    @NotNull
    @Column
    private Boolean isLocked = false;

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL)
    private Set<VerificationToken> verificationTokens = new HashSet<>();

    public AppUser(){

    }

    public AppUser(String email, String firstName, String lastName, String password, UserRole role) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.role = role;
    }

    public AppUser(UUID id,
                   String email,
                   String firstName,
                   String lastName,
                   String password,
                   UserRole role,
                   Boolean isEnabled,
                   Boolean isLocked) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.role = role;
        this.isEnabled = isEnabled;
        this.isLocked = isLocked;
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


    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public UserRole getRole() {
        return role;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public Boolean getLocked() {
        return isLocked;
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

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public Set<VerificationToken> getVerificationTokens() {
        return verificationTokens;
    }

}
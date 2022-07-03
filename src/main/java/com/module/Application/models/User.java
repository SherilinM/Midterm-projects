package com.module.Application.models;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    /** Propiedades **/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    protected String name;

    @Column(unique=true)
    protected String username;

    protected String password;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "user")
    private Set<Role> roles = new HashSet<>();

    /** Constructor **/

    /** Constructor vacio **/
    public User() {
    }

    /** Constructor**/
    public User(String name, String username, String password) {
        setName(name);
        setUsername(username);
        setPassword(password);
    }

    /** Getters y Setters **/

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }
    public Set<Role> getRoles() {
        return roles;
    }
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    public void addRole(Role role){
        this.roles.add(role);
    }

    @Override
    public String toString() {
        return  id + "\n" +
                username + "\n" +
                name + "\n" ;
    }

}
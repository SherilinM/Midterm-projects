package com.module.Application.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.module.Application.enums.SystemRole;

import javax.persistence.*;

@Entity
@Table
public class Role {

    /** --------------------Properties-------------------- **/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private SystemRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    /** --------------------Constructors---------------------- **/

    /** Empty Constructor **/
    public Role() {}

    /** Constructor **/
    public Role(SystemRole role, User user) {
        setRole(role);
        setUser(user);
    }

    /** -----------------Getters & Setters------------------ **/
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public SystemRole getRole() {
        return role;
    }
    public void setRole(SystemRole role) {
        this.role = role;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user)
    {this.user = user;
    }

    @Override
    public String toString() {
        return "Models.Role{" +
                "id=" + id +
                ", authority='" + role + '\'';
    }
}

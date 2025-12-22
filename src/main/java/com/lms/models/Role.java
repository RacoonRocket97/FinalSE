package com.lms.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role extends BaseEntity implements GrantedAuthority {

    @Column(name = "role_name", unique = true, nullable = false)
    private String roleName;

    @Override
    public String getAuthority() {
        return roleName;
    }
}
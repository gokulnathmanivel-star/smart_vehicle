package com.svsbas.modules.user.dto;

import com.svsbas.modules.user.entity.Role;
import com.svsbas.modules.user.entity.User;
import com.svsbas.modules.user.entity.UserStatus;

public class UserResponse {
    private Long id;
    private String email;
    private String phone;
    private String fullName;
    private Role role;
    private UserStatus status;

    public UserResponse() {}

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.fullName = user.getFullName();
        this.role = user.getRole();
        this.status = user.getStatus();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
}

package com.svsbas.modules.mechanic.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class MechanicCreateRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String specialization;

    private String password;

    public MechanicCreateRequest() {
    }

    public MechanicCreateRequest(String fullName, String email, String phone, String specialization, String password) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

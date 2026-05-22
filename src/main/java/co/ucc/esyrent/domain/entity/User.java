package co.ucc.esyrent.domain.entity;

import co.ucc.esyrent.domain.enums.UserRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Property> properties = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", fetch = FetchType.LAZY)
    private List<Contract> contracts = new ArrayList<>();

    protected User() {
    }

    public User(String fullName, String email, String password, String phone, UserRole role) {
        this.fullName = requireText(fullName, "Full name");
        this.email = requireText(email, "Email");
        this.password = requireText(password, "Password");
        this.phone = requireText(phone, "Phone");
        this.role = role == null ? UserRole.TENANT : role;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public UserRole getRole() {
        return role;
    }

    public List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    public List<Contract> getContracts() {
        return Collections.unmodifiableList(contracts);
    }

    public void updateProfile(String fullName, String phone) {
        this.fullName = requireText(fullName, "Full name");
        this.phone = requireText(phone, "Phone");
    }

    public void changePassword(String encodedPassword) {
        this.password = requireText(encodedPassword, "Password");
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public boolean isOwner() {
        return role == UserRole.OWNER;
    }

    public boolean isTenant() {
        return role == UserRole.TENANT;
    }

    void addProperty(Property property) {
        if (!properties.contains(property)) {
            properties.add(property);
        }
    }

    void addContract(Contract contract) {
        if (!contracts.contains(contract)) {
            contracts.add(contract);
        }
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }
}

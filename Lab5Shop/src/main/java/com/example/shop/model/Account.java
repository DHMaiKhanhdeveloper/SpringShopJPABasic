package com.example.shop.model;

import java.util.Date;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Accounts", uniqueConstraints = {
@UniqueConstraint (columnNames = {"email"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
	@Id
	@Column(length = 30)
	private String username;
	
	@Column(length = 20)
	private String password;
	
	@Column(columnDefinition = "nvarchar(50) not null")
	private String fullname;
	
	@Column(columnDefinition = "nvarchar(100) not null")
	private String email;
	
	@Column(columnDefinition = "nvarchar(255) not null")
	private String photo;
	
	@Temporal (TemporalType.DATE)
	private Date createdDate;
	
	@Temporal (TemporalType.DATE)
	private Date updatedDate;
	
	private AccountStatus status;
	
	private AccountRole role;
	
	@OneToMany(mappedBy = "account")
	private Set<Order> orders;
	
	@PrePersist
	public void preCreate() {
		createdDate = new Date();
	}
	@PreUpdate
	public void preUpdate() {
		updatedDate = new Date();
	}
}
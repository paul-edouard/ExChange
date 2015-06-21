package com.munch.exchange.model.jpa.entity;

import java.io.Serializable;
import java.lang.String;


import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

/**
 * Entity implementation class for Entity: Student
 *
 */
@Entity
@NamedQuery(name="Student.getAll",query="SELECT s FROM Student s")
public class Student implements Serializable {

	   
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@NotNull
	@Length(max=20)
	private String firstName;
	
	@NotNull
	@Length(max=20)
	private String lastName;
	
	@NotNull
	@Length(max=5)
	private String Standert;
	
	@NotNull
	@Length(max=5)
	private String phone;
	
	
	private static final long serialVersionUID = 1L;

	public Student() {
		super();
	}   
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}   
	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}   
	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}   
	public String getStandert() {
		return this.Standert;
	}

	public void setStandert(String Standert) {
		this.Standert = Standert;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
	this.phone = phone;
	}
	
	
	
   
}

package com.munch.exchange.services.ejb.beans;

import java.util.List;

import javax.ejb.Local;

import com.munch.exchange.model.jpa.entity.Student;


@Local
public interface StudentDAOLocal {
	
	public Student create(Student student);
	public Student update(Student student);
	public void remove(int id);
	public Student getStudent(int id);
	public List<Student> getAllStudents();

}

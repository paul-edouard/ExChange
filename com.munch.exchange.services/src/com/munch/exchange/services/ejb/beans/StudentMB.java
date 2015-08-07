package com.munch.exchange.services.ejb.beans;

import javax.annotation.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import com.munch.exchange.model.jpa.entity.Student;


public class StudentMB {
	
	public void test() throws NamingException{
		StudentDAORemote studentDAORemote=BeanRemote.doLookUp();
		System.out.println("Hallo");
		System.out.println(studentDAORemote);
		Student student =new Student();
		student.setFirstName("Paul6");
		student.setLastName("Munch");
		student.setStandert("X");
		student.setPhone("Phone");
		studentDAORemote.create(student);
		
	}
	
	public static void main(String[] args) {
		//new StudentMB().test();
		StudentMB studentMB=new StudentMB();
		try {
			studentMB.test();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

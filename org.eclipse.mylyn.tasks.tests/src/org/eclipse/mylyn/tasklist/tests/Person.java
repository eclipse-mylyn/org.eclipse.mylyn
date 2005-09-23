/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.tests;

import java.util.ArrayList;
import java.util.List;

public class Person {
	private String name;
	private String emailAddress;
	private String phoneNumber;
	private int age;
	private int id;
	private Job job;
	
	private List<Person> list = new ArrayList<Person>();
	
	public Person() {
		name = "Ken Sueda";
		id = 1;
		age = 22;
		emailAddress = "ksueda@hotmail.com";
		phoneNumber = "123-4567";
		job = null;
	}
	
	public Person(String name, String email, String phone, int age, int id) {
		this.name = name;
		this.emailAddress = email;
		this.phoneNumber = phone;
		this.age = age;
		this.id = id;
		this.job = null;		
	}

	public void add(Person p) {
		list.add(p);
	}
	
	public boolean equals(Person p) {
		boolean result = true;
		result = result && p.name == this.name;
		result = result && p.emailAddress == this.emailAddress;
		result = result && p.phoneNumber == this.phoneNumber;
		result = result && p.age == this.age;
		result = result && p.id == this.id;
		result = result && p.job.equals(this.job);
		return result;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}
}

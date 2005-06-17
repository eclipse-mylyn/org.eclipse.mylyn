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
package org.eclipse.mylar.tasks.tests;

public class Job {
	private String company;
	private String position;
	private int salary;
	private String email;
	private String supervisor;	
	
	public static String foo = "TESTING STATIC FOR XSTREAM";
	
	public Job() {
		this.company = "Foo";
		this.position = "slave";
		this.salary = 100;
		this.email = "a@bc.com";
		//this.supervisor = "bill gates";
	}
	
	public Job(String comp, String pos, int salary, String email, String supervisor) {
		this.company = comp;
		this.position = pos;
		this.salary = salary;
		this.email = email;
		this.supervisor = supervisor;		
	}
	
	public boolean equals(Job j) {
		boolean result = true;
		result = result && j.company == this.company;
		result = result && j.position == this.position;
		result = result && j.salary == this.salary;
		result = result && j.email == this.email;
		result = result && j.supervisor == this.supervisor;
		return result;
	}
	
	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}
}

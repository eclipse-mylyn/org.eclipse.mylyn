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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class People {
	private List<Person> list = new ArrayList<Person>();
	
	public People() {
		// don't need to do any initialization
	}
	
	public void createDefault() {
		Person p = new Person("Ken Sueda", "ksueda@hotmail.com", "123-4567", 22, 1);
		Job j = new Job("UBCCS", "developer", 1000, "ksueda@cs.ubc.ca", "foo");
		p.setJob(j);
		list.add(p);
		
		p = new Person("Shawn Minto", "minto@hotmail.com", "798-1234", 23, 2);
		j = new Job("UBCCS", "grad student", 3000, "minto@cs.ubc.ca", "foo");
		p.setJob(j);
		list.add(p);
		
		
		p = new Person("Mik Kersten", "kersten@hotmail.com", "456-7891", 24, 3);
		j = new Job("UBCCS", "PhD", 1000000, "kersten@cs.ubc.ca", "foo");
		p.setJob(j);
		list.add(p);
		
		p = new Person("Gail Murphy", "murphy@hotmail.com", "987-6543", 25, 4);
		j = new Job("UBCCS", "Professor", 100000000, "", "");
		p.setJob(j);
		list.add(p);
	}
	
	public boolean equals(People people) {
		boolean result = true;		
		if (list.size() == people.list.size()) {
			Iterator<Person> itr = list.iterator();
			Iterator<Person> itr2 = people.list.iterator();
			
			while(itr.hasNext()) {
				Person p = itr.next();
				Person p2 = itr2.next();
				result = result && p.equals(p2);
				if (!result) {
					break;
				}
			}
		} else {
			result = false;
		}
		return result;
	}
}

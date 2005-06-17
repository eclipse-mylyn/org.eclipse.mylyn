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
/*
 * Created on Dec 26, 2004
  */
package org.eclipse.mylar.tasks;

import java.io.Serializable;


/**
 * @author Mik Kersten
 */
public class Category implements Serializable {

    private static final long serialVersionUID = 3834024740813027380L;
    
//    private List<ITask> tasks = new ArrayList<ITask>();
    private String name = "";
    
    public Category(String name) {
        this.name = name;
    }
    
//    public void addTask(ITask task) {
//        tasks.add(task);
//    }
//    
//    public void removeTask(Task task) {
//        tasks.remove(task);
//    }
//    
//    public List<ITask> getTasks() {
//        return tasks;
//    }

    @Override
    public String toString() {
        return name;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String label) {
        this.name = label;
    }
   
    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (object instanceof Category) {
           Category compare = (Category)object;
           return this.getName().equals(compare.getName());
        } else {
            return false;
        }
    }
}

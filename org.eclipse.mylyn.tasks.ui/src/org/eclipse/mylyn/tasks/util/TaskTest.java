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
 * Created on Jan 24, 2005
 */
package org.eclipse.mylar.tasks.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sminto
 */
public class TaskTest {
    private int id = -1;

    private String name = "";

    private List<String> categories = new ArrayList<String>();

    private List<TaskTest> taskList = new ArrayList<TaskTest>();

    @Override
    public String toString() {
        return "Task";
    }

    public boolean isEqual(TaskTest otherTask) {

        boolean result = true;
        result = result && (this.id == otherTask.id);
        if (!result)
            System.out.println("ID's are different");
        int compare = (this.name.compareTo(otherTask.name));
        if (compare != 0)
            result = false;
        if (!result)
            System.out.println("this: " + this.name + " otherName: "
                    + otherTask.name + " a");

        if (this.categories.size() == otherTask.categories.size()) {
            for (int i = 0; i < this.categories.size(); i++) {
                compare = this.categories.get(i).compareTo(
                        otherTask.categories.get(i));
                if (compare != 0) {
                    System.err.println(this.categories.get(i) + " vs "
                            + otherTask.categories.get(i));
                    result = false;
                    break;
                }
            }
        } else {
            System.err.println("Category length different");
            System.err.println("length: " + this.categories.size()
                    + " VS Length: " + otherTask.categories.size());
            result = false;
        }
        if (this.taskList.size() == otherTask.taskList.size()) {
            for (int i = 0; i < this.taskList.size(); i++) {
                result = result
                        && (this.taskList.get(i).isEqual(otherTask.taskList
                                .get(i)));
            }
        } else {
            System.err.println("TaskList size is different!");
            result = false;
        }
        return result;
    }

    public static void printTask(TaskTest t, String tab) {
        System.out.println(tab + "TaskID: " + t.id);
        System.out.println(tab + "Name: " + t.name);
        System.out.println(tab + "Categories: ");
        for (int i = 0; i < t.categories.size(); i++) {
            System.out.println(tab + "\t " + t.categories.get(i));
        }
        for (int i = 0; i < t.taskList.size(); i++) {
            printTask(t.taskList.get(i), tab + "\t");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //    public List<String> getCategories() {
    //        return categories;
    //    }
    //    public void setCategories(List<String> categories) {
    //        this.categories = categories;
    //    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addCategory(String category) {
        this.categories.add(category);
    }

    /**
     * @return Returns the categories.
     */
    public List<String> getCategories() {
        return categories;
    }

    /**
     * @param categories The categories to set.
     */
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    /**
     * @return Returns the taskList.
     */
    public List<TaskTest> getTaskList() {
        return taskList;
    }

    /**
     * @param taskList The taskList to set.
     */
    public void setTaskList(List<TaskTest> taskList) {
        this.taskList = taskList;
    }

    public void addSubTask(TaskTest sub) {
        this.taskList.add(sub);
    }
}

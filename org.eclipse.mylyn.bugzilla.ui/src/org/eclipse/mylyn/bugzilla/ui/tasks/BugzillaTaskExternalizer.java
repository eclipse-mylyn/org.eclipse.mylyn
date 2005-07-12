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

package org.eclipse.mylar.bugzilla.ui.tasks;

import java.util.Date;
import java.util.List;

import org.eclipse.mylar.bugzilla.ui.tasks.BugzillaTask.BugTaskState;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasks.AbstractCategory;
import org.eclipse.mylar.tasks.ITask;
import org.eclipse.mylar.tasks.Task;
import org.eclipse.mylar.tasks.TaskCategory;
import org.eclipse.mylar.tasks.TaskList;
import org.eclipse.mylar.tasks.util.DefaultTaskListExternalizer;
import org.eclipse.mylar.tasks.util.ITaskListExternalizer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class BugzillaTaskExternalizer extends DefaultTaskListExternalizer {

	private static final String BUGZILLA = "Bugzilla";
	private static final String LAST_DATE = "LastDate";
	private static final String DIRTY = "Dirty";
	private static final String URL = "URL";
	private static final String DESCRIPTION = "Description";
	
	private static final String TAG_BUGZILLA_CATEGORY = "BugzillaQuery" + TAG_CATEGORY;
	private static final String TAG_TASK = "BugzillaReport";
	
	@Override
	public boolean canReadCategory(Node node) {
		return node.getNodeName().equals(getCategoryTagName());
	}

	@Override
	public void readCategory(Node node, TaskList tlist) {
		Element e = (Element) node;
		BugzillaQueryCategory cat = new BugzillaQueryCategory(e.getAttribute(DESCRIPTION), e.getAttribute(URL));
		tlist.addCategory(cat);
	}

	public boolean canCreateElementFor(AbstractCategory category) {
		return category instanceof BugzillaQueryCategory;
	}
	
	public Element createCategoryElement(AbstractCategory category, Document doc, Element parent) {
		BugzillaQueryCategory queryCategory = (BugzillaQueryCategory)category;
		Element node = doc.createElement(getCategoryTagName());
		node.setAttribute(DESCRIPTION, queryCategory.getDescription(false));
		node.setAttribute(URL, queryCategory.getUrl());
		parent.appendChild(node);
		return node;
	}

	public boolean canCreateElementFor(ITask task) {
		return task instanceof BugzillaTask;
	}
	
	public Element createTaskElement(ITask task, Document doc, Element parent) {
		Element node = super.createTaskElement(task, doc, parent);
		BugzillaTask bt = (BugzillaTask) task;
		node.setAttribute(BUGZILLA, TRUE);
		if (bt.getLastRefresh() != null) {
			node.setAttribute(LAST_DATE, new Long(bt.getLastRefreshTime()
					.getTime()).toString());
		} else {
			node.setAttribute(LAST_DATE, new Long(new Date().getTime()).toString());
		}
		
		if (bt.isDirty()) {
			node.setAttribute(DIRTY, TRUE);
		} else {
			node.setAttribute(DIRTY, FALSE);
		}
		bt.saveBugReport(false);
		return node;
	}

	@Override
	public boolean canReadTask(Node node) {
		return node.getNodeName().equals(getTaskTagName());
	}

	@Override
	public ITask readTask(Node node, TaskList tlist, AbstractCategory category, ITask parent) {
		Element element = (Element) node;
		String handle = element.getAttribute(HANDLE);		
		String label = element.getAttribute(LABEL);
		BugzillaTask task = new BugzillaTask(handle, label, true);		
		readTaskInfo(task, tlist, element, category, parent);
				
		task.setState(BugTaskState.FREE);
		task.setLastRefresh(new Date(new Long(element.getAttribute("LastDate"))
				.longValue()));
		if (element.getAttribute("Dirty").compareTo("true") == 0) {
			task.setDirty(true);
		} else {
			task.setDirty(false);
		}
		if (task.readBugReport() == false) {
			MylarPlugin.log("Failed to read bug report", null);
		}
		return task;
	}
	
	@Override
	public String getCategoryTagName() {
		return TAG_BUGZILLA_CATEGORY;
	}

	@Override
	public String getTaskTagName() {
		return TAG_TASK;
	}
}

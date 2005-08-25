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

package org.eclipse.mylar.bugzilla.ui.tasklist;

import java.util.Date;

import org.eclipse.mylar.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaTask.BugTaskState;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.ICategory;
import org.eclipse.mylar.tasklist.IQuery;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskHandler;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.internal.DefaultTaskListExternalizer;
import org.eclipse.mylar.tasklist.internal.MylarExternalizerException;
import org.eclipse.mylar.tasklist.internal.TaskCategory;
import org.eclipse.mylar.tasklist.internal.TaskList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The wierd thing here is that the registry gets read in as a normal
 * category, but gets written out by createRegistry
 * 
 * @author Mik Kersten and Ken Sueda
 */
public class BugzillaTaskExternalizer extends DefaultTaskListExternalizer {

	public static final String BUGZILLA_ARCHIVE_LABEL = "Archived Reports <automatic>";
	private static final String BUGZILLA = "Bugzilla";
	private static final String LAST_DATE = "LastDate";
	private static final String DIRTY = "Dirty";
	private static final String DESCRIPTION = "Description";
	private static final String URL = "URL";

	private static final String BUGZILLA_TASK_REGISTRY = "BugzillaTaskRegistry" + TAG_CATEGORY;
	private static final String TAG_BUGZILLA_CATEGORY = "BugzillaQuery" + TAG_CATEGORY;
	
	private static final String TAG_BUGZILLA_QUERY_HIT = "Bugzilla" + TAG_QUERY_HIT;
	private static final String TAG_BUGZILLA_QUERY = "Bugzilla" + TAG_QUERY;
	private static final String TAG_BUGZILLA_CUSTOM_QUERY = "BugzillaCustom" + TAG_QUERY;
	
	private static final String TAG_TASK = "BugzillaReport";
	
	@Override
	public void createRegistry(Document doc, Node parent) {
		Element node = doc.createElement(BUGZILLA_TASK_REGISTRY);
		for (BugzillaTask task : BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().getBugzillaTaskRegistry().values()) {
			try {
				createTaskElement(task, doc, node);
			} catch (Exception e) {
				MylarPlugin.log(e, e.getMessage());
			}
			
		}
		parent.appendChild(node);
	} 
	
	@Override
	public boolean canReadCategory(Node node) {
		return node.getNodeName().equals(getCategoryTagName())
			|| node.getNodeName().equals(BUGZILLA_TASK_REGISTRY);
	}

	@Override
	public void readCategory(Node node, TaskList taskList)  throws MylarExternalizerException {
		Element e = (Element) node;
		if (e.getNodeName().equals(BUGZILLA_TASK_REGISTRY)) {
			readRegistry(node, taskList);
		} else {
			BugzillaQueryCategory cat = new BugzillaQueryCategory(e.getAttribute(DESCRIPTION), e.getAttribute(URL), e.getAttribute(MAX_HITS));
			taskList.internalAddQuery(cat);
		}
	}

	public String getQueryTagNameForElement(IQuery query) {
		if(query instanceof BugzillaCustomQuery){
			return TAG_BUGZILLA_CUSTOM_QUERY;
		} else if(query instanceof BugzillaQueryCategory){
			return TAG_BUGZILLA_QUERY;
		}
		return "";
	}
	
	public boolean canReadQuery(Node node) {
		return node.getNodeName().equals(TAG_BUGZILLA_CUSTOM_QUERY) || node.getNodeName().equals(TAG_BUGZILLA_QUERY);
	}

	public void readQuery(Node node, TaskList tlist) throws MylarExternalizerException {
		boolean hasCaughtException = false;
		Element element = (Element) node;
		IQuery cat = null;
		if(node.getNodeName().equals(TAG_BUGZILLA_CUSTOM_QUERY)){
			cat = new BugzillaCustomQuery(element.getAttribute(NAME), element.getAttribute(QUERY_STRING), element.getAttribute(MAX_HITS));
		} else if(node.getNodeName().equals(TAG_BUGZILLA_QUERY)){
			cat = new BugzillaQueryCategory(element.getAttribute(NAME), element.getAttribute(QUERY_STRING), element.getAttribute(MAX_HITS));
		}
		if(cat != null){
			tlist.internalAddQuery(cat);
		}
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			try {
				readQueryHit(child, tlist, cat);
			} catch (MylarExternalizerException e) {
				hasCaughtException = true;
			}
		}
		if (hasCaughtException) throw new MylarExternalizerException("Failed to load all tasks");
	}
	
	public void readRegistry(Node node, TaskList taskList)  throws MylarExternalizerException {
		boolean hasCaughtException = false;
		NodeList list = node.getChildNodes();
		TaskCategory cat = new TaskCategory(BUGZILLA_ARCHIVE_LABEL);
		cat.setIsArchive(true);
		taskList.internalAddCategory(cat);
		BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().setTaskRegistyCategory(cat);
		for (int i = 0; i < list.getLength(); i++) {
			try {
				Node child = list.item(i);
				ITask task = readTask(child, taskList, null, null);
				if (task instanceof BugzillaTask) {
					BugzillaUiPlugin.getDefault().getBugzillaTaskListManager()
							.addToBugzillaTaskRegistry((BugzillaTask) task);
				}
			} catch (MylarExternalizerException e) {
				hasCaughtException = true;
			}
		}
		
		if (hasCaughtException) throw new MylarExternalizerException("Failed to restore all tasks");
	}
	
	public boolean canCreateElementFor(ICategory cat) {
		return false;
	}

	public boolean canCreateElementFor(ITask task) {
		return task instanceof BugzillaTask;
	}
	
	public Element createTaskElement(ITask task, Document doc, Element parent) {
		Element node = super.createTaskElement(task, doc, parent);
		BugzillaTask bt = (BugzillaTask) task;
		node.setAttribute(BUGZILLA, TRUE);
		if (bt.getLastRefresh() != null) {
			node.setAttribute(LAST_DATE, new Long(bt.getLastRefresh()
					.getTime()).toString());
		} else {
			node.setAttribute(LAST_DATE, new Long(new Date().getTime()).toString());
		}
		
		if (bt.isDirty()) {
			node.setAttribute(DIRTY, TRUE);
		} else {
			node.setAttribute(DIRTY, FALSE);
		}
//		bt.saveBugReport(false); // XXX don't think that this needs to be done, should be handled already
		return node;
	}

	@Override
	public boolean canReadTask(Node node) {
		return node.getNodeName().equals(getTaskTagName());
	}

	@Override
	public ITask readTask(Node node, TaskList tlist, ICategory category, ITask parent)  throws MylarExternalizerException{
		Element element = (Element) node;
		String handle;
		String label;
		if (element.hasAttribute(HANDLE)) {
			handle = element.getAttribute(HANDLE);
		} else {
			throw new MylarExternalizerException("Handle not stored for bug report");
		}
		if (element.hasAttribute(LABEL)) {
			label = element.getAttribute(LABEL);
		} else {
			throw new MylarExternalizerException("Description not stored for bug report");
		}
		BugzillaTask task = new BugzillaTask(handle, label, true, false);		
		readTaskInfo(task, tlist, element, category, parent);
				
		task.setState(BugTaskState.FREE);
		task.setLastRefresh(new Date(new Long(element.getAttribute("LastDate"))
				.longValue()));
		if (element.getAttribute("Dirty").compareTo("true") == 0) {
			task.setDirty(true);
		} else {
			task.setDirty(false);
		}
		try {
			if (task.readBugReport() == false) {
				MylarPlugin.log("Failed to read bug report", null);
			}
		} catch(Exception e) {
			MylarPlugin.log(e, "Failed to read bug report");
		}
		
		ITaskHandler taskHandler = MylarTasklistPlugin.getDefault().getTaskHandlerForElement(task);
	    if(taskHandler != null){
    		ITask addedTask = taskHandler.taskAdded(task);
    		if(addedTask instanceof BugzillaTask) task = (BugzillaTask)addedTask;
    	}
		return task;
	}
	
	
	
	public boolean canReadQueryHit(Node node) {
		return node.getNodeName().equals(getQueryHitTagName());
	}

	public void readQueryHit(Node node, TaskList tlist, IQuery query) throws MylarExternalizerException {
		Element element = (Element) node;
		String handle;
		String label;
		String priority;
		String status;
		if (element.hasAttribute(HANDLE)) {
			handle = element.getAttribute(HANDLE);
		} else {
			throw new MylarExternalizerException("Handle not stored for bug report");
		}
		if (element.hasAttribute(NAME)) {
			label = element.getAttribute(NAME);
		} else {
			throw new MylarExternalizerException("Description not stored for bug report");
		}
		if (element.hasAttribute(PRIORITY)) {
			priority = element.getAttribute(PRIORITY);
		} else {
			throw new MylarExternalizerException("Description not stored for bug report");
		}
		if (element.hasAttribute(COMPLETE)) {
			status = element.getAttribute(COMPLETE);
			if(status.equals(TRUE))
				status = "RESO";
			else
				status = "NEW";
		} else {
			throw new MylarExternalizerException("Description not stored for bug report");
		}
		BugzillaHit hit = new BugzillaHit(label, priority, BugzillaTask.getBugId(handle), null, status);
		query.addHit(hit);
	}
	
	@Override
	public String getCategoryTagName() {
		return TAG_BUGZILLA_CATEGORY;
	}

	@Override
	public String getTaskTagName() {
		return TAG_TASK;
	}
	
	@Override
	public String getQueryHitTagName(){
		return TAG_BUGZILLA_QUERY_HIT;
	}
}

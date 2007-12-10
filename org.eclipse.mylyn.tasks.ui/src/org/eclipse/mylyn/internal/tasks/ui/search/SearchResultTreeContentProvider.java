/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.tasks.core.Person;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.search.RepositorySearchResult;

/**
 * This implementation of <code>SearchResultContentProvider</code> is used for the table view of a Bugzilla search
 * result.
 * 
 * @author Rob Elves (moved into task.ui)
 */
public class SearchResultTreeContentProvider extends SearchResultContentProvider {

	/** The page the Bugzilla search results are displayed in */
	private RepositorySearchResultView searchResultsPage;

	private List<Object> elements = new ArrayList<Object>();

	private Map<String, Person> owners = new HashMap<String, Person>();
	
	private boolean groupByOwner = false;
	
	
	/**
	 * Constructor
	 * 
	 * @param page
	 *            The page the Bugzilla search results are displayed in
	 */
	public SearchResultTreeContentProvider(RepositorySearchResultView page) {
		searchResultsPage = page;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof RepositorySearchResult) {
			searchResult = (RepositorySearchResult) newInput;
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof RepositorySearchResult) {
			if(groupByOwner){
				return owners.values().toArray();
			} else {
				return elements.toArray();
			}
		} else {
			return EMPTY_ARR;
		}
	}

	public Object[] getChildren(Object parentElement) {
		if(groupByOwner && parentElement instanceof Person){
			Set<AbstractTask> children = ((Person)parentElement).getChildren();
			if(children != null){
				return children.toArray();
			} else {
				return EMPTY_ARR;	
			}
		} else {
			return EMPTY_ARR;
		}
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		if(groupByOwner && element instanceof String){
			Set<AbstractTask> children = ((Person)element).getChildren();
			if(children != null){
				return !children.isEmpty();
			} else {
				return false;	
			}
		} else {
			return !(element instanceof AbstractTask);
		}
	}

	@Override
	public void elementsChanged(Object[] updatedElements) {
		for (Object object : updatedElements) {
			elements.add(object);
			
			if(object instanceof AbstractTask){
				AbstractTask task = ((AbstractTask)object);
				String owner = task.getOwner();
				if(owner == null){
					owner = "UNKNOWN";
				}
				Person person = owners.get(owner);
				if(person == null){
					person = new Person(owner, task.getConnectorKind(), task.getRepositoryUrl());
					owners.put(owner, person);
				}
				person.internalAddChild(task);
				
			}
		}

		searchResultsPage.getViewer().refresh();
////		boolean tableLimited = SearchPreferencePage.isTableLimited();
//		for (int i = 0; i < updatedElements.length; i++) {
//			if (searchResult.getMatchCount(updatedElements[i]) > 0) {
//				if (viewer.testFindItem(updatedElements[i]) != null)
//					viewer.update(updatedElements[i], null);
//				else {
////					if (!tableLimited || viewer.getTable().getItemCount() < SearchPreferencePage.getTableLimit())
//					viewer.add(updatedElements[i]);
//				}
//			} else
//				viewer.remove(updatedElements[i]);
//		}
	}

	@Override
	public void clear() {
		elements.clear();
		owners.clear();
		searchResultsPage.getViewer().refresh();
	}

	public boolean getGroupByOwner() {
		return groupByOwner;
	}
	
	public void setGroupByOwner(boolean grouping) {
		this.groupByOwner = grouping;
		searchResultsPage.getViewer().setInput(searchResultsPage.getViewer().getInput());
	}
}

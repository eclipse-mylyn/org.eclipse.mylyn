/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.Person;
import org.eclipse.mylyn.internal.tasks.core.TaskGroup;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;

/**
 * This implementation of <code>SearchResultContentProvider</code> is used for the table view of a Bugzilla search result.
 *
 * @author Rob Elves (moved into task.ui)
 * @author Mik Kersten
 */
public class SearchResultTreeContentProvider extends SearchResultContentProvider {

	private final Set<Object> elements = new LinkedHashSet<>();

	private final Map<String, Person> owners = new HashMap<>();

	private final Map<String, TaskGroup> completeState = new HashMap<>();

	public enum GroupBy {
		NONE, OWNER, COMPLETION;
	}

	private GroupBy selectedGroup;

	public SearchResultTreeContentProvider() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof RepositorySearchResult) {
			searchResult = (RepositorySearchResult) newInput;
			clear();
			elementsChanged(searchResult.getElements());
		} else {
			searchResult = null;
			clear();
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement == searchResult) {
			if (selectedGroup == GroupBy.OWNER) {
				return owners.values().toArray();
			} else if (selectedGroup == GroupBy.COMPLETION) {
				return completeState.values().toArray();
			} else {
				return elements.toArray();
			}
		} else {
			return EMPTY_ARR;
		}
	}

	@Override
	public Object[] getChildren(Object parent) {
		if (parent instanceof TaskGroup || parent instanceof Person) {
			return ((ITaskContainer) parent).getChildren().toArray();
		} else {
			return EMPTY_ARR;
		}
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	@Override
	public void elementsChanged(Object[] updatedElements) {
		for (Object object : updatedElements) {
			boolean inResult = false;
			Object[] resultElements = searchResult.getElements();
			for (Object resultObject : resultElements) {
				if (resultObject.equals(object)) {
					inResult = true;
				}

			}
			if (inResult) {
				boolean added = elements.add(object);
				if (added && object instanceof ITask) {
					AbstractTask task = (AbstractTask) object;
					String owner = task.getOwner();
					if (owner == null) {
						owner = Messages.SearchResultTreeContentProvider__unknown_;
					}
					Person person = owners.get(owner);
					if (person == null) {
						person = new Person(owner, task.getConnectorKind(), task.getRepositoryUrl());
						owners.put(owner, person);
					}
					person.internalAddChild(task);

					TaskGroup completeIncomplete = null;
					if (task.isCompleted()) {
						completeIncomplete = completeState.get(Messages.SearchResultTreeContentProvider_Complete);
						if (completeIncomplete == null) {
							completeIncomplete = new TaskGroup("group-complete", //$NON-NLS-1$
									Messages.SearchResultTreeContentProvider_Complete, GroupBy.COMPLETION.name());
							completeState.put(Messages.SearchResultTreeContentProvider_Complete, completeIncomplete);
						}
					} else {
						completeIncomplete = completeState.get(Messages.SearchResultTreeContentProvider_Incomplete);
						if (completeIncomplete == null) {
							completeIncomplete = new TaskGroup("group-incomplete", //$NON-NLS-1$
									Messages.SearchResultTreeContentProvider_Incomplete, GroupBy.COMPLETION.name());
							completeState.put(Messages.SearchResultTreeContentProvider_Incomplete, completeIncomplete);
						}
					}
					completeIncomplete.internalAddChild(task);
				}
			} else if (object instanceof ITask) {
				AbstractTask task = (AbstractTask) object;
				elements.remove(task);
				String owner = task.getOwner();
				if (owner == null) {
					owner = Messages.SearchResultTreeContentProvider__unknown_;
				}
				Person person = owners.get(owner);
				person.internalRemoveChild(task);

				TaskGroup completeIncomplete = null;
				if (task.isCompleted()) {
					completeIncomplete = completeState.get(Messages.SearchResultTreeContentProvider_Complete);
				} else {
					completeIncomplete = completeState.get(Messages.SearchResultTreeContentProvider_Incomplete);
				}
				completeIncomplete.internalRemoveChild(task);
			}
		}
	}

	@Override
	public void clear() {
		elements.clear();
		owners.clear();
		completeState.clear();
	}

	public GroupBy getSelectedGroup() {
		return selectedGroup;
	}

	public void setSelectedGroup(GroupBy selectedGroup) {
		this.selectedGroup = selectedGroup;
	}

}

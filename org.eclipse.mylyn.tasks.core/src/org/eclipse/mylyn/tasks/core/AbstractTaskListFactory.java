/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Collections;
import java.util.Set;

import org.w3c.dom.Element;

/**
 * Responsible for storing and creating task list elements.
 * 
 * When overriding these methods be sure not to perform network access since the Task List is read and written
 * frequently.
 * 
 * @author Mik Kersten
 * @author Ken Sueda
 * @since 2.0
 */
// API 3.0 move to deprecated package
public abstract class AbstractTaskListFactory {

	public static final String KEY_QUERY = "Query";

	public static final String KEY_QUERY_STRING = "QueryString";

	public static final String KEY_TASK = "Task";

	/**
	 * Returns true if factory can create an XML element to store given {@link AbstractTask}.
	 * <p>
	 * The name of the XML element is taken from the {@link #getTaskElementName()} method and additional task attributes
	 * can be stored in {@link #setAdditionalAttributes(AbstractTask, Element)} method.
	 * 
	 * @param task
	 * 		a task instance to create XML element for
	 * 
	 * @return true if factory can create XML element to store given {@link AbstractTask}.
	 * 
	 * @see #getTaskElementName()
	 * @see #setAdditionalAttributes(AbstractTask, Element)
	 */
	public abstract boolean canCreate(AbstractTask task);

	/**
	 * Returns true if factory can create an XML element to store given {@link AbstractRepositoryQuery}.
	 * <p>
	 * The name of the XML element is taken from the {@link #getQueryElementName(AbstractRepositoryQuery)} method and
	 * additional query attributes can be stored in {@link #setAdditionalAttributes(AbstractRepositoryQuery, Element)}.
	 * 
	 * @param query
	 * 		a query instance to create an XML element for
	 * 
	 * @return true if factory can create XML element to store given {@link AbstractTask}.
	 * 
	 * @see #getQueryElementName(AbstractRepositoryQuery)
	 * @see #setAdditionalAttributes(AbstractRepositoryQuery, Element)
	 */
	public boolean canCreate(AbstractRepositoryQuery query) {
		return false;
	}

	/**
	 * Creates an {@link AbstractRepositoryQuery} instance from given XML element matching one of the names returned by
	 * {@link #getQueryElementNames()}.
	 * <p>
	 * Concrete implementation should populate required query configuration using method parameters and content of the
	 * passed XML element. Children tasks for this query instance will be created by the caller of this method.
	 * 
	 * @param repositoryUrl
	 * 		an url for the corresponding task repository
	 * @param queryString
	 * 		a query string, e.g. connector-specific url used for query request
	 * @param label
	 * 		a query label or name
	 * @param element
	 * 		an XML element containing query data
	 * @return instance of the {@link AbstractRepositoryQuery}
	 * 
	 * @see #getQueryElementNames()
	 */
	public AbstractRepositoryQuery createQuery(String repositoryUrl, String queryString, String label, Element element) {
		return null;
	}

	/**
	 * Creates an {@link AbstractTask} instance from given XML element matching name returned by {@link
	 * #getTaskElementName()}.
	 * <p>
	 * Concrete implementation should populate required task data using method parameters and content of the passed XML
	 * element. Children tasks of this task instance will be created by the caller of this method.
	 * 
	 * @param repositoryUrl
	 * 		an url for the corresponding task repository
	 * @param queryString
	 * 		a query string, e.g. connector-specific url used for query request
	 * @param label
	 * 		a query label or name
	 * @param element
	 * 		an XML element containing query data
	 * @return instance of the {@link AbstractRepositoryQuery}
	 * 
	 * @see #getTaskElementName()
	 */
	public abstract AbstractTask createTask(String repositoryUrl, String taskId, String label, Element element);

	/**
	 * Returns name of the XML element used to store given query instance if {@link #canCreate(AbstractRepositoryQuery)}
	 * return true for given query instance.
	 * 
	 * @param query
	 * 		a query instance to get the name for
	 * 
	 * @return name for the XML element to store given query instance or null if factory doesn't support given {@link
	 * 	AbstractRepositoryQuery} instance.
	 * 
	 * @see #canCreate(AbstractRepositoryQuery)
	 */
	public String getQueryElementName(AbstractRepositoryQuery query) {
		return "";
	}

	/**
	 * Returns names for all query elements.
	 * <p>
	 * This collection is used to determine if this factory can create {@link AbstractRepositoryQuery} instance from the
	 * XML element using {@link #createQuery(String, String, String, Element)} method.
	 * 
	 * @return a <code>Set</code> of query element names
	 * 
	 * @see #createQuery(String, String, String, Element)
	 */
	public Set<String> getQueryElementNames() {
		return Collections.emptySet();
	}

	/**
	 * Returns name for the XML element used to store subclass of the {@link AbstractTask} used by this factory. This
	 * value is used to create an XML element when storing given {@link AbstractTask} as well as to determine if this
	 * factory can read XML element with content of the task.
	 * 
	 * @return name of the task element
	 * 
	 * @see #canCreate(AbstractTask)
	 */
	public abstract String getTaskElementName();

	/**
	 * Adds additional attributes to an XML element used to store given {@link AbstractRepositoryQuery}.
	 * 
	 * @param query
	 * 		a query instance being stored
	 * @param node
	 * 		an XML element used to store given query instance
	 */
	public void setAdditionalAttributes(AbstractRepositoryQuery query, Element node) {
		// ignore
	}

	/**
	 * Adds additional attributes to an XML element used to store given {@link AbstractTask}
	 * 
	 * @param task
	 * 		a task instance being stored
	 * @param node
	 * 		an XML element used to store given task instance
	 */
	public void setAdditionalAttributes(AbstractTask task, Element element) {
		// ignore
	}
}

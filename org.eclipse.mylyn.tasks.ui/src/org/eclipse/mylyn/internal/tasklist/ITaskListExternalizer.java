/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Used to externalize things like tasks and bug reports along with the task
 * list so that information about them can be persisted across invocations.
 * 
 * @author Mik Kersten
 * @author Ken Sueda
 * 
 * TODO: consider merging tasks and categories
 */
public interface ITaskListExternalizer {

	public abstract AbstractRepositoryClient getRepositoryClient();
	
	/**
	 * Note that registries get read as a normal category, but get written out
	 * first.
	 */
	public abstract void createRegistry(Document doc, Node parent);

	public abstract String getCategoryTagName();

	public abstract String getTaskTagName();

	public abstract String getQueryTagNameForElement(AbstractRepositoryQuery query);

	public abstract String getQueryHitTagName();

	public abstract boolean canCreateElementFor(ITaskCategory category);

	/**
	 * @return the element that was created, null if failed
	 */
	public abstract Element createCategoryElement(ITaskCategory category, Document doc, Element parent);

	public abstract boolean canCreateElementFor(ITask task);

	/**
	 * @return the element that was created, null if failed
	 */
	public abstract Element createTaskElement(ITask task, Document doc, Element parent);

	public abstract boolean canReadCategory(Node node);

	public abstract void readCategory(Node node, TaskList tlist) throws TaskExternalizationException;

	public abstract boolean canReadTask(Node node);

	public abstract ITask readTask(Node node, TaskList tlist, ITaskCategory category, ITask parent)
			throws TaskExternalizationException;

	public abstract boolean canCreateElementFor(AbstractRepositoryQuery category);

	public abstract Element createQueryElement(AbstractRepositoryQuery query, Document doc, Element parent);

	public abstract boolean canReadQuery(Node node);

	public abstract void readQuery(Node node, TaskList tlist) throws TaskExternalizationException;

	public abstract boolean canCreateElementFor(IQueryHit queryHit);

	public abstract Element createQueryHitElement(IQueryHit queryHit, Document doc, Element parent);

	public abstract boolean canReadQueryHit(Node node);

	public abstract void readQueryHit(Node node, TaskList tlist, AbstractRepositoryQuery query)
			throws TaskExternalizationException;
}

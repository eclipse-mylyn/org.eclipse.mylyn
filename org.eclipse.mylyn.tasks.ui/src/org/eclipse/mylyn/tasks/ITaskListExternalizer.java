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

package org.eclipse.mylar.tasks;

import org.eclipse.mylar.tasks.internal.TaskList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Mik Kersten and Ken Sueda
 * 
 * TODO: consider merging tasks and categories
 */
public interface ITaskListExternalizer {
	
	/**
	 * Note that registries get read as a normal category, but get 
	 * written out first.
	 */
	public abstract void createRegistry(Document doc, Node parent);
	
	public abstract String getCategoryTagName();
	
	public abstract String getTaskTagName();
	
	public abstract boolean canCreateElementFor(AbstractCategory category);
	
	/**
	 * @return the element that was created, null if failed
	 */
	public abstract Element createCategoryElement(AbstractCategory category, Document doc, Element parent);

	public abstract boolean canCreateElementFor(ITask task);
	
	/**
	 * @return the element that was created, null if failed
	 */
	public abstract Element createTaskElement(ITask task, Document doc, Element parent);

	public abstract boolean canReadCategory(Node node);
	
	public abstract void readCategory(Node node, TaskList tlist);
	
	public abstract boolean canReadTask(Node node);
	
	public abstract ITask readTask(Node node, TaskList tlist, AbstractCategory category, ITask parent);
}

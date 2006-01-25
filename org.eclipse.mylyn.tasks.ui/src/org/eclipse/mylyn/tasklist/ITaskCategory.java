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
package org.eclipse.mylar.tasklist;

import java.util.List;

import org.eclipse.mylar.internal.tasklist.ui.ITaskListElement;

/**
 * Manipulate categories via TaskListManager
 * 
 * @author Mik Kersten
 */
public interface ITaskCategory extends ITaskListElement {

	public List<ITask> getChildren();

	public boolean isArchive();

	public void setIsArchive(boolean isArchive);

}

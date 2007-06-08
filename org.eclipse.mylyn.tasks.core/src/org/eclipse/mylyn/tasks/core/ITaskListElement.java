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
package org.eclipse.mylyn.tasks.core;


/**
 * @author Mik Kersten
 */
public interface ITaskListElement extends Comparable<ITaskListElement> {

	public abstract String getPriority();

	public abstract String getSummary();

	public abstract String getHandleIdentifier();

//	public abstract void setHandleIdentifier(String taskId);

}


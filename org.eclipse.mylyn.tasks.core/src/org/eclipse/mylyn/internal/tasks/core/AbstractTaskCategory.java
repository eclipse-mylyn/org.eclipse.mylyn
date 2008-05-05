/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;


/**
 * A container that stores tasks from any repository. A task can only have a single AbstractTaskCategory parent (only be
 * in one category at a time).
 * 
 * @author Mik Kersten
 * @since 2.0
 */
//API 3.0 move to internal package
public abstract class AbstractTaskCategory extends AbstractTaskContainer {

	public AbstractTaskCategory(String handleAndDescription) {
		super(handleAndDescription);
	}

	/**
	 * Override to return true for categories that the user creates, deletes, and renames. Return false for categories
	 * that are managed
	 * 
	 * @deprecated
	 * API-3.0: Use AbstractTaskContainer.isUserManaged
	 */
	@Deprecated
	public abstract boolean isUserDefined();

}

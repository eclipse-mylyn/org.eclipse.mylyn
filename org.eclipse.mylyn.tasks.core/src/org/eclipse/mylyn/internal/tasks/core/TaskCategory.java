/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Dec 26, 2004
 */
package org.eclipse.mylyn.internal.tasks.core;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskElement;

/**
 * NOTE: this class is likely to change or become API for 3.0
 * 
 * @author Mik Kersten
 */
public final class TaskCategory extends AbstractTaskCategory {

	public TaskCategory(String handleAndDescription) {
		super(handleAndDescription);
	}

	@Override
	public boolean isUserDefined() {
		return true;
	}

	/**
	 * null if no parent category
	 */
	public static AbstractTaskCategory getParentTaskCategory(ITask task) {
		AbstractTaskCategory category = null;
		if (task != null) {
			for (ITaskElement container : task.getParentContainers()) {
				if (container instanceof AbstractTaskCategory) {
					category = (AbstractTaskCategory) container;
				}
			}
		}
		return category;
	}
}

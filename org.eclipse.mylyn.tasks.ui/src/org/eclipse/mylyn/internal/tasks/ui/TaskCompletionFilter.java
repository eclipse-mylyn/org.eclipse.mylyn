/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.mylyn.tasks.core.AbstractTask;

/**
 * @author Mik Kersten
 */
public class TaskCompletionFilter extends AbstractTaskListFilter {

	@Override
	public boolean select(Object parent, Object element) {
		if (element instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) element;
			return !task.isCompleted();
		}
		return true;
	}
}

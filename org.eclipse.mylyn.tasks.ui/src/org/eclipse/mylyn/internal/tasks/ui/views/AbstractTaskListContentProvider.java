/*******************************************************************************
 * Copyright (c) 2004, 2013 Eugene Kuleshov and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreePathContentProvider;

/**
 * @author Eugene Kuleshov
 * @author Mik Kersten
 */
public abstract class AbstractTaskListContentProvider implements ITreeContentProvider, ITreePathContentProvider {

	protected AbstractTaskListView taskListView;

	public AbstractTaskListContentProvider(AbstractTaskListView view) {
		this.taskListView = view;
	}

}

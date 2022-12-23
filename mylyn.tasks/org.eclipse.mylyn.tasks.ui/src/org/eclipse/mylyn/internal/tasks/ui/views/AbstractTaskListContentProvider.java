/*******************************************************************************
 * Copyright (c) 2004, 2013 Eugene Kuleshov and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
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

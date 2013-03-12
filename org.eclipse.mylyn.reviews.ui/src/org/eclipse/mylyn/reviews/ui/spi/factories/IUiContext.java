/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.ui.spi.factories;

import org.eclipse.mylyn.reviews.core.spi.remote.AbstractRemoteFactoryProvider;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.swt.widgets.Shell;

/**
 * Provides access to user interface and data elements needed by UI factory implementations. This allows the same
 * factory to be used in editors as well as other interface contexts such as handlers. Except where noted, all
 * implementors are expected to return valid values for all methods.
 * 
 * @author Miles Parker
 */
public interface IUiContext {

	Shell getShell();

	/**
	 * May return null, e.g. in the case where a factory was used outside of an editor context.
	 * 
	 * @return
	 */
	TaskEditor getEditor();

	ITask getTask();

	TaskData getTaskData();

	TaskRepository getTaskRepository();

	AbstractRemoteFactoryProvider getRemoteFactoryProvider();
}

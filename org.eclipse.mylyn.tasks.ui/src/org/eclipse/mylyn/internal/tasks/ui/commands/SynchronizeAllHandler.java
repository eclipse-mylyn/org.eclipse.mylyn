/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;

/**
 * @author Steffen Pingel
 */
public class SynchronizeAllHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		TasksUiInternal.synchronizeAllRepositories(true);
		return null;
	}

}

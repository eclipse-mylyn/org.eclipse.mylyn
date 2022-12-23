/*******************************************************************************
 * Copyright (c) 2004, 2011 Willian Mitsuda and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Willian Mitsuda - initial API and implementation
 *     Tasktop Technologies - improvements
 *     Peter Stibrany - fix for parameter name (bug 247077)
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.mylyn.internal.tasks.ui.actions.AddRepositoryAction;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Displays a wizard dialog for adding a new task repository.
 * 
 * @author Willian Mitsuda
 * @author Steffen Pingel
 */
public class AddTaskRepositoryHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		if (shell != null && !shell.isDisposed()) {
			String connectorKind = event.getParameter("connectorKind"); //$NON-NLS-1$
			return new AddRepositoryAction().showWizard(shell, connectorKind);
		}
		return null;
	}

	/**
	 * @deprecated invoke <code>new AddRepositoryAction().showWizard(shell, connectorKind)</code> instead
	 */
	@Deprecated
	public static TaskRepository showWizard(Shell shell, String connectorKind) {
		return new AddRepositoryAction().showWizard(shell, connectorKind);
	}

}

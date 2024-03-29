/*******************************************************************************
 * Copyright (c) 2012 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.views.UpdateRepositoryConfigurationAction;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Frank Bwcker
 */
public class UpdateRepositoryConfiguration extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		UpdateRepositoryConfigurationAction action = new UpdateRepositoryConfigurationAction();
		action.selectionChanged((IStructuredSelection) selection);
		action.run();
		return null;
	}

}

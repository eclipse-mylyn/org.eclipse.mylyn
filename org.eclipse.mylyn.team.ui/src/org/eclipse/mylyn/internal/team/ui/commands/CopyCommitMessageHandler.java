/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui.commands;

import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.ui.ClipboardCopier;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.team.ui.TeamUiUtil;
import org.eclipse.ui.handlers.HandlerUtil;

public class CopyCommitMessageHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		if (selection == null) {
			selection = HandlerUtil.getCurrentSelection(event);
		}
		Optional<ITask> task = getTask(selection);
		if (task.isPresent()) {
			String comment = TeamUiUtil.getComment(false, task.get(), null);
			ClipboardCopier.getDefault().copy(comment);
		}
		return null;
	}

	private Optional<ITask> getTask(ISelection selection) {
		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			if (structuredSelection.getFirstElement() instanceof ITask) {
				return Optional.of((ITask) structuredSelection.getFirstElement());
			}
		}
		return Optional.empty();
	}

}

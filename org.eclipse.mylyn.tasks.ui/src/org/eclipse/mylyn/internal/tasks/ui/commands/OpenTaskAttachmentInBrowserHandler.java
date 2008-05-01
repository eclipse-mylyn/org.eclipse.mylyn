/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.tasks.core.data.ITaskAttachment2;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenTaskAttachmentInBrowserHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		//ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			List<?> items = ((IStructuredSelection) selection).toList();
			for (Object item : items) {
				if (item instanceof ITaskAttachment2) {
					TasksUiUtil.openUrl(((ITaskAttachment2) item).getUrl());
				}
			}
		}
		return null;
	}

}

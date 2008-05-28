/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractTaskAttachmentCommandHandler extends AbstractHandler {

	public AbstractTaskAttachmentCommandHandler() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			Object[] items = ((IStructuredSelection) selection).toArray();
			for (Object item : items) {
				if (item instanceof ITaskAttachment) {
					execute(event, (ITaskAttachment) item);
				}
			}
		}
		return null;
	}

	protected void execute(ExecutionEvent event, ITaskAttachment attachment) {
	}

}
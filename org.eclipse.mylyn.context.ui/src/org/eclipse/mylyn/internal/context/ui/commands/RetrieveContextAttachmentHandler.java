/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.mylyn.internal.tasks.ui.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class RetrieveContextAttachmentHandler extends AbstractTaskAttachmentCommandHandler {

	@Override
	protected void execute(ExecutionEvent event, ITaskAttachment attachment) {
		AttachmentUtil.downloadContext(attachment.getTask(), attachment, PlatformUI.getWorkbench().getProgressService());
	}

}

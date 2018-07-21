/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.context;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
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

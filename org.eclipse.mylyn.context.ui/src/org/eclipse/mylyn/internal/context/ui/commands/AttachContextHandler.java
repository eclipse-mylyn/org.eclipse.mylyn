/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.context.ui.wizards.ContextAttachWizard;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.ui.commands.AbstractTaskHandler;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Steffen Pingel
 */
public class AttachContextHandler extends AbstractTaskHandler {

	@Override
	protected void execute(ExecutionEvent event, ITask task) throws ExecutionException {
		ContextAttachWizard wizard = new ContextAttachWizard(task);
		WizardDialog dialog = new WizardDialog(WorkbenchUtil.getShell(), wizard);
		dialog.create();
		dialog.setBlockOnOpen(true);
		if (dialog.open() == Window.CANCEL) {
			dialog.close();
			return;
		}
	}

}

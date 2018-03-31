/*******************************************************************************
 * Copyright (c) 2011, 2012 Torkild U. Resheim.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.docs.epub.ui.commands;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.docs.epub.ui.ConvertFromMarkupWizard;
import org.eclipse.mylyn.wikitext.ui.commands.AbstractMarkupResourceHandler;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class ConvertMarkupToEPUB extends AbstractMarkupResourceHandler {

	@Override
	protected void handleFile(final IFile file, String name)
			throws ExecutionException {
		final IFile newFile = file.getParent()
				.getFile(new Path(name + ".epub")); //$NON-NLS-1$
		if (newFile.exists()) {
			if (!MessageDialog.openQuestion(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(),
					Messages.ConvertMarkupToEPUB_overwrite, NLS.bind(
							Messages.ConvertMarkupToEPUB_fileExistsOverwrite,
							new Object[] { newFile.getFullPath() }))) {
				return;
			}
		}

		ConvertFromMarkupWizard wizard = new ConvertFromMarkupWizard();
		wizard.init(file, newFile, markupLanguage);
		// Instantiates the wizard container with the wizard and opens it
		WizardDialog dialog = new WizardDialog(Display.getDefault().getActiveShell(), wizard);
		dialog.create();
		dialog.open();

	}
}

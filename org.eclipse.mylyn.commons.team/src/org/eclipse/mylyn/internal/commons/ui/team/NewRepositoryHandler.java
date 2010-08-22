/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui.team;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.commons.ui.team.wizards.NewRepositoryWizard;
import org.eclipse.mylyn.internal.provisional.commons.ui.dialogs.ValidatableWizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.LegacyResourceSupport;
import org.eclipse.ui.internal.util.Util;

/**
 * @author STeffen Pingel
 */
public class NewRepositoryHandler extends AbstractHandler implements IHandler {

	/**
	 * The wizard dialog width
	 */
	private static final int SIZING_WIZARD_WIDTH = 500;

	/**
	 * The wizard dialog height
	 */
	private static final int SIZING_WIZARD_HEIGHT = 500;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow workbenchWindow = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		return openDialog(workbenchWindow, null);
	}

	public static int openDialog(IWorkbenchWindow workbenchWindow, String categoryId) {
		NewRepositoryWizard wizard = new NewRepositoryWizard();
		wizard.setCategoryId(categoryId);
		wizard.setWindowTitle("New Repository");

		ISelection selection = workbenchWindow.getSelectionService().getSelection();
		IStructuredSelection selectionToPass = StructuredSelection.EMPTY;
		if (selection instanceof IStructuredSelection) {
			selectionToPass = (IStructuredSelection) selection;
		} else {
			// @issue the following is resource-specific legacy code
			// Build the selection from the IFile of the editor
			Class resourceClass = LegacyResourceSupport.getResourceClass();
			if (resourceClass != null) {
				IWorkbenchPart part = workbenchWindow.getPartService().getActivePart();
				if (part instanceof IEditorPart) {
					IEditorInput input = ((IEditorPart) part).getEditorInput();
					Object resource = Util.getAdapter(input, resourceClass);
					if (resource != null) {
						selectionToPass = new StructuredSelection(resource);
					}
				}
			}
		}

		wizard.init(workbenchWindow.getWorkbench(), selectionToPass);

		IDialogSettings workbenchSettings = TeamUiPlugin.getDefault().getDialogSettings();
		IDialogSettings wizardSettings = workbenchSettings.getSection("NewWizardAction"); //$NON-NLS-1$
		if (wizardSettings == null) {
			wizardSettings = workbenchSettings.addNewSection("NewWizardAction"); //$NON-NLS-1$
		}
		wizard.setDialogSettings(wizardSettings);
		wizard.setForcePreviousAndNextButtons(true);

		Shell parent = workbenchWindow.getShell();
		ValidatableWizardDialog dialog = new ValidatableWizardDialog(parent, wizard);
		dialog.create();
		dialog.getShell().setSize(Math.max(SIZING_WIZARD_WIDTH, dialog.getShell().getSize().x), SIZING_WIZARD_HEIGHT);
		//PlatformUI.getWorkbench().getHelpSystem().setHelp(dialog.getShell(), IWorkbenchHelpContextIds.NEW_WIZARD);
		return dialog.open();
	}

}

/*******************************************************************************
 * Copyright (c) 2009, 2011 Tasktop Technologies.
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
package org.eclipse.mylyn.internal.tasks.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.discovery.ui.wizards.ConnectorDiscoveryWizard;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * A command that causes the {@link ConnectorDiscoveryWizard} to appear in a dialog.
 * 
 * @author David Green
 * @author Steffen Pingel
 */
public class ShowTasksConnectorDiscoveryWizardCommandHandler extends AbstractHandler {

	private static final String ID_P2_INSTALL_UI = "org.eclipse.equinox.p2.ui.sdk/org.eclipse.equinox.p2.ui.sdk.install"; //$NON-NLS-1$

	public Object execute(ExecutionEvent event) throws ExecutionException {

		// check to make sure that the p2 install ui is enabled
		if (WorkbenchUtil.allowUseOf(ID_P2_INSTALL_UI)) {
			ConnectorDiscoveryWizard wizard = new ConnectorDiscoveryWizard();
			WizardDialog dialog = new WizardDialog(WorkbenchUtil.getShell(), wizard) {
				@Override
				protected void createButtonsForButtonBar(Composite parent) {
					super.createButtonsForButtonBar(parent);
					((GridLayout) parent.getLayout()).numColumns++;
					final Button button = new Button(parent, SWT.CHECK);
					button.setSelection(TasksUiPlugin.getDefault()
							.getPreferenceStore()
							.getBoolean(ITasksUiPreferenceConstants.SERVICE_MESSAGES_ENABLED));
					button.setText(Messages.ShowTasksConnectorDiscoveryWizardCommandHandler_Notify_when_updates_are_available_Text);
					button.setFont(JFaceResources.getDialogFont());
					button.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent event) {
							TasksUiPlugin.getDefault()
									.getPreferenceStore()
									.setValue(ITasksUiPreferenceConstants.SERVICE_MESSAGES_ENABLED,
											button.getSelection());
						}
					});
					button.moveAbove(null);
				}
			};
			dialog.open();
		} else {
			MessageDialog.openWarning(WorkbenchUtil.getShell(),
					Messages.ShowTasksConnectorDiscoveryWizardCommandHandler_Install_Connectors,
					Messages.ShowTasksConnectorDiscoveryWizardCommandHandler_Unable_to_launch_connector_install);
		}

		return null;
	}
}

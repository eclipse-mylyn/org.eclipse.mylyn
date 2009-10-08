/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.discovery.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.discovery.ui.util.DiscoveryUiUtil;
import org.eclipse.mylyn.internal.discovery.ui.wizards.ConnectorDiscoveryWizard;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;

/**
 * A command that causes the {@link ConnectorDiscoveryWizard} to appear in a dialog.
 * 
 * @author David Green
 */
public class ShowConnectorDiscoveryWizardCommandHandler extends AbstractHandler {

	private static final String ID_P2_INSTALL_UI = "org.eclipse.equinox.p2.ui.sdk/org.eclipse.equinox.p2.ui.sdk.install"; //$NON-NLS-1$

	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		// check to make sure that the p2 install ui is enabled
		if (WorkbenchUtil.allowUseOf(ID_P2_INSTALL_UI)) {
			ConnectorDiscoveryWizard wizard = new ConnectorDiscoveryWizard();
			WizardDialog dialog = new WizardDialog(DiscoveryUiUtil.getShell(), wizard);
			dialog.open();
		} else {
			MessageDialog.openWarning(
					DiscoveryUiUtil.getShell(),
					Messages.ShowConnectorDiscoveryWizardCommandHandler_Install_Connectors,
					Messages.ShowConnectorDiscoveryWizardCommandHandler_Unable_To_Install_No_P2);
		}

		return null;
	}
}

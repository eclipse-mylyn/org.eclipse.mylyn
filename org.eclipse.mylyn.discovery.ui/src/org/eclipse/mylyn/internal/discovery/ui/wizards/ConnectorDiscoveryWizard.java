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
package org.eclipse.mylyn.internal.discovery.ui.wizards;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.discovery.ui.DiscoveryUi;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonsUiUtil;
import org.eclipse.mylyn.internal.provisional.commons.ui.ICoreRunnable;

/**
 * A wizard for performing discovery of connectors and selecting connectors to
 * install. When finish is pressed, selected connectors are downloaded and
 * installed.
 * 
 * @see InstallConnectorsJob
 * @see ConnectorDiscoveryWizardMainPage
 * 
 * @author David Green
 */
public class ConnectorDiscoveryWizard extends Wizard {

	private ConnectorDiscoveryWizardMainPage mainPage;

	public ConnectorDiscoveryWizard() {
		setWindowTitle("Connector Discovery");
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		addPage(mainPage = new ConnectorDiscoveryWizardMainPage());
	}

	@Override
	public boolean performFinish() {
		try {
			ICoreRunnable job = new InstallConnectorsJob(mainPage
					.getInstallableConnectors());
			if (getContainer() != null) {
				CommonsUiUtil.run(getContainer(), job);
			} else {
				CommonsUiUtil.busyCursorWhile(job);
			}

		} catch (CoreException e) {
			IStatus status = new Status(
					IStatus.ERROR,
					DiscoveryUi.BUNDLE_ID,
					MessageFormat
							.format(
									"Problems occurred while performing installation: {0}", e.getMessage()), e); //$NON-NLS-1$
			DiscoveryUi.logAndDisplayStatus("Cannot complete installation",
					status);
		} catch (OperationCanceledException e) {
			// canceled
		}
		return true;
	}

}

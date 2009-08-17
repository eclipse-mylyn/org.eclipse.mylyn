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
package org.eclipse.mylyn.internal.discovery.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.internal.provisional.p2.ui.IProvHelpContextIds;
import org.eclipse.equinox.internal.provisional.p2.ui.QueryableMetadataRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.ui.dialogs.PreselectedIUInstallWizard;
import org.eclipse.equinox.internal.provisional.p2.ui.dialogs.ProvisioningWizardDialog;
import org.eclipse.equinox.internal.provisional.p2.ui.policy.Policy;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.discovery.core.model.ConnectorDescriptor;
import org.eclipse.mylyn.internal.discovery.ui.util.DiscoveryUiUtil;
import org.eclipse.mylyn.internal.discovery.ui.wizards.Messages;
import org.eclipse.mylyn.internal.discovery.ui.wizards.PrepareInstallProfileJob;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author David Green
 */
@SuppressWarnings("restriction")
public abstract class DiscoveryUi {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.discovery.ui"; //$NON-NLS-1$

	private DiscoveryUi() {
	}

	public static boolean install(List<ConnectorDescriptor> descriptors, IRunnableContext context) {
		try {
			final PrepareInstallProfileJob job = new PrepareInstallProfileJob(descriptors);
			context.run(true, true, job);

			if (job.getPlannerResolutionOperation() != null
					&& job.getPlannerResolutionOperation().getProvisioningPlan() != null) {
				Display.getCurrent().asyncExec(new Runnable() {
					public void run() {
						PreselectedIUInstallWizard wizard = new PreselectedIUInstallWizard(Policy.getDefault(),
								job.getProfileId(), job.getIUs(), job.getPlannerResolutionOperation(),
								new QueryableMetadataRepositoryManager(Policy.getDefault().getQueryContext(), false));
						WizardDialog dialog = new ProvisioningWizardDialog(DiscoveryUiUtil.getShell(), wizard);
						dialog.create();
						PlatformUI.getWorkbench().getHelpSystem().setHelp(dialog.getShell(),
								IProvHelpContextIds.INSTALL_WIZARD);

						dialog.open();
					}
				});
			}
		} catch (InvocationTargetException e) {
			IStatus status = new Status(IStatus.ERROR, DiscoveryUi.ID_PLUGIN, NLS.bind(
					Messages.ConnectorDiscoveryWizard_installProblems, new Object[] { e.getCause().getMessage() }),
					e.getCause());
			DiscoveryUiUtil.logAndDisplayStatus(Messages.ConnectorDiscoveryWizard_cannotInstall, status);
			return false;
		} catch (InterruptedException e) {
			// canceled
		}
		return true;
	}

}

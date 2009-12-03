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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.discovery.core.model.ConnectorDescriptor;
import org.eclipse.mylyn.internal.discovery.ui.util.DiscoveryUiUtil;
import org.eclipse.mylyn.internal.discovery.ui.wizards.Messages;
import org.eclipse.mylyn.internal.discovery.ui.wizards.PrepareInstallProfileJob;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;

/**
 * @author David Green
 */
public abstract class DiscoveryUi {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.discovery.ui"; //$NON-NLS-1$

	private DiscoveryUi() {
	}

	public static boolean install(List<ConnectorDescriptor> descriptors, IRunnableContext context) {
		try {
			IRunnableWithProgress runner = null;
			Bundle bundle = Platform.getBundle("org.eclipse.equinox.p2.engine"); //$NON-NLS-1$
			if (bundle != null && new VersionRange("[1.0.0,1.1.0)").isIncluded(bundle.getVersion())) { //$NON-NLS-1$
				// load class for Eclipse 3.5
				try {
					Class<?> clazz = Class.forName("org.eclipse.mylyn.internal.discovery.ui.wizards.PrepareInstallProfileJob_e_3_5"); //$NON-NLS-1$
					Constructor<?> c = clazz.getConstructor(List.class);
					runner = (IRunnableWithProgress) c.newInstance(descriptors);
				} catch (Throwable t) {
					StatusHandler.log(new Status(
							IStatus.ERROR,
							DiscoveryUi.ID_PLUGIN,
							"Errors occured while initializing provisioning framework, falling back to default implementation. This make cause discovery install to fail.", //$NON-NLS-1$
							t));
				}
			}
			if (runner == null) {
				runner = new PrepareInstallProfileJob(descriptors);
			}
			context.run(true, true, runner);
		} catch (InvocationTargetException e) {
			IStatus status = new Status(IStatus.ERROR, DiscoveryUi.ID_PLUGIN, NLS.bind(
					Messages.ConnectorDiscoveryWizard_installProblems, new Object[] { e.getCause().getMessage() }),
					e.getCause());
			DiscoveryUiUtil.logAndDisplayStatus(Messages.ConnectorDiscoveryWizard_cannotInstall, status);
			return false;
		} catch (InterruptedException e) {
			// canceled
			return false;
		}
		return true;
	}

}

/*******************************************************************************
 * Copyright (c) 2009, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.discovery.ui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.internal.discovery.core.model.ConnectorDescriptor;
import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryFeedbackJob;
import org.eclipse.mylyn.internal.discovery.ui.wizards.Messages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * @author David Green
 */
public abstract class DiscoveryUi {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.discovery.ui"; //$NON-NLS-1$

	public static final String PREF_LAST_INSTALLED = "lastInstalled"; //$NON-NLS-1$

	private DiscoveryUi() {
	}

	public static AbstractInstallJob createInstallJob() {
		List<ConnectorDescriptor> emptyList = Collections.emptyList();
		return createInstallJob(emptyList);
	}

	public static AbstractInstallJob createInstallJob(List<ConnectorDescriptor> descriptors) {
		return new PrepareInstallProfileJob(descriptors);
	}

	public static boolean install(List<ConnectorDescriptor> descriptors, IRunnableContext context) {
		try {
			IRunnableWithProgress runner = createInstallJob(descriptors);
			context.run(true, true, runner);

			// update stats
			new DiscoveryFeedbackJob(descriptors).schedule();
			recordInstalled(descriptors);
		} catch (InvocationTargetException e) {
			IStatus status = new Status(IStatus.ERROR, DiscoveryUi.ID_PLUGIN, NLS.bind(
					Messages.ConnectorDiscoveryWizard_installProblems, new Object[] { e.getCause().getMessage() }),
					e.getCause());
			StatusManager.getManager().handle(status, StatusManager.SHOW | StatusManager.BLOCK | StatusManager.LOG);
			return false;
		} catch (InterruptedException e) {
			// canceled
			return false;
		}
		return true;
	}

	private static void recordInstalled(List<ConnectorDescriptor> descriptors) {
		StringBuilder sb = new StringBuilder();
		for (ConnectorDescriptor descriptor : descriptors) {
			if (sb.length() > 0) {
				sb.append(","); //$NON-NLS-1$
			}
			sb.append(descriptor.getId());
		}
		ScopedPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), ID_PLUGIN);
		store.putValue(PREF_LAST_INSTALLED, sb.toString());
		try {
			store.save();
		} catch (IOException e) {
			// ignore
		}
	}

}

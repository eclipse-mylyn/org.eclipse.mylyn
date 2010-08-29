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

package org.eclipse.mylyn.internal.commons.ui.notifications;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Steffen Pingel
 */
public class NotificationsPlugin extends AbstractUIPlugin {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.commons.notifications"; //$NON-NLS-1$

	public static final String PREF_NOTICATIONS_ENABLED = "notifications.enabled"; //$NON-NLS-1$

	private static NotificationsPlugin instance;

	public static NotificationsPlugin getDefault() {
		return instance;
	}

	private NotificationModel model;

	private NotificationService service;

	public NotificationModel getModel() {
		if (model == null) {
			IMemento memento = null;
			File file = getModelFile().toFile();
			if (file.exists()) {
				try {
					FileReader reader = new FileReader(file);
					try {
						memento = XMLMemento.createReadRoot(reader);
					} finally {
						reader.close();
					}
				} catch (IOException e) {
					getLog().log(
							new Status(IStatus.ERROR, ID_PLUGIN, "Unexpected error restoring notification state", e));
				} catch (WorkbenchException e) {
					getLog().log(
							new Status(IStatus.ERROR, ID_PLUGIN, "Unexpected error restoring notification state", e));
				}
			}
			model = new NotificationModel(memento);
		}
		return model;
	}

	protected IPath getModelFile() {
		IPath stateLocation = Platform.getStateLocation(getBundle());
		IPath cacheFile = stateLocation.append("notifications.xml"); //$NON-NLS-1$
		return cacheFile;
	}

	public NotificationService getService() {
		if (service == null) {
			service = new NotificationService();
		}
		return service;
	}

	public void saveModel() {
		if (model != null && model.isDirty()) {
			XMLMemento memento = XMLMemento.createWriteRoot("notifications");
			model.save(memento);
			FileWriter writer;
			try {
				writer = new FileWriter(getModelFile().toFile());
				try {
					memento.save(writer);
				} finally {
					writer.close();
				}
			} catch (IOException e) {
				getLog().log(new Status(IStatus.ERROR, ID_PLUGIN, "Unexpected error saving notification state", e));
			}
		}
	}

	@Override
	public void start(BundleContext context) throws Exception {
		instance = this;
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		instance = null;
	}

}

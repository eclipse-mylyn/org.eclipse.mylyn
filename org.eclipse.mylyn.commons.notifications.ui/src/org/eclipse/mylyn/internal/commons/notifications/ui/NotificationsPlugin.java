/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.notifications.ui;

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

	private static final String FILE_NOTIFICATION_STATE = "notifications.xml"; //$NON-NLS-1$

	public static final String ID_PLUGIN = "org.eclipse.mylyn.commons.notifications.ui"; //$NON-NLS-1$

	public static final String PREF_NOTICATIONS_ENABLED = "notifications.enabled"; //$NON-NLS-1$

	private static NotificationsPlugin instance;

	public static NotificationsPlugin getDefault() {
		return instance;
	}

	private NotificationModel model;

	private NotificationService service;

	public NotificationModel createModelWorkingCopy() {
		migrateFile_0_8();

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
				getLog().log(new Status(IStatus.ERROR, ID_PLUGIN, "Unexpected error restoring notification state", e)); //$NON-NLS-1$
			} catch (WorkbenchException e) {
				getLog().log(new Status(IStatus.ERROR, ID_PLUGIN, "Unexpected error restoring notification state", e)); //$NON-NLS-1$
			}
		}
		return new NotificationModel(memento);
	}

	private void migrateFile_0_8() {
		IPath file_0_8 = getModelFile_0_8();
		if (file_0_8.toFile().exists() && !getModelFile().toFile().exists()) {
			file_0_8.toFile().renameTo(getModelFile().toFile());
		}
	}

	public NotificationModel getModel() {
		if (model == null) {
			model = createModelWorkingCopy();
		}
		return model;
	}

	public NotificationService getService() {
		if (service == null) {
			service = new NotificationService();
		}
		return service;
	}

	public void saveModel() {
		if (model != null && model.isDirty()) {
			save(model);
		}
	}

	public void saveWorkingCopy(NotificationModel workingCopy) {
		XMLMemento memento = save(workingCopy);
		if (this.model != null) {
			// reload model
			this.model.initialize(memento);
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

	private XMLMemento save(NotificationModel model) {
		XMLMemento memento = XMLMemento.createWriteRoot("notifications"); //$NON-NLS-1$
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
			getLog().log(new Status(IStatus.ERROR, ID_PLUGIN, "Unexpected error saving notification state", e)); //$NON-NLS-1$
		}
		return memento;
	}

	/**
	 * Returns the location of file that stores the notification state.
	 */
	protected IPath getModelFile() {
		IPath stateLocation = Platform.getStateLocation(getBundle());
		IPath cacheFile = stateLocation.append(FILE_NOTIFICATION_STATE);
		return cacheFile;
	}

	/**
	 * Returns the location of the state file that was in use in version 0.8.
	 * 
	 * @see #getModelFile()
	 */
	protected IPath getModelFile_0_8() {
		IPath stateLocation = Platform.getStateLocation(getBundle());
		stateLocation = stateLocation.removeLastSegments(1).append("org.eclipse.mylyn.commons.notifications"); //$NON-NLS-1$
		return stateLocation.append(FILE_NOTIFICATION_STATE);
	}

}

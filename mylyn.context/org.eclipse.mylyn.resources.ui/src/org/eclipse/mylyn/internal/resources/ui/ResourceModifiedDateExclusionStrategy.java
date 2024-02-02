/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.resources.ui;

import java.util.Date;

import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextCore;

/**
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public class ResourceModifiedDateExclusionStrategy extends AbstractContextListener
		implements IResourceExclusionStrategy, IPropertyChangeListener, IOperationHistoryListener {

	private transient Date lastActivatedDate = null;

	private boolean isEnabled = false;

	private boolean performingChange;

	@Override
	public void dispose() {
		ContextCore.getContextManager().removeListener(this);
		ResourcesUiBridgePlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		OperationHistoryFactory.getOperationHistory().removeOperationHistoryListener(this);
	}

	@Override
	public void init() {
		ResourcesUiBridgePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
		isEnabled = ResourcesUiBridgePlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(ResourcesUiPreferenceInitializer.PREF_MODIFIED_DATE_EXCLUSIONS);
		ContextCore.getContextManager().addListener(this);
		if (ContextCore.getContextManager().isContextActive()) {
			lastActivatedDate = new Date();
		}
		OperationHistoryFactory.getOperationHistory().addOperationHistoryListener(this);
	}

	@Override
	public void update() {
		// ignore
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Override
	public boolean isExcluded(IResource resource) {
		if (isEnabled() && !performingChange) {
			return resource instanceof IFile && !wasModifiedAfter(resource, lastActivatedDate);
		}
		return false;
	}

	public boolean wasModifiedAfter(IResource resource, Date date) {
		if (date == null) {
			return false;
		}
		long modificationStamp = resource.getLocalTimeStamp();
		if (modificationStamp > 0 && modificationStamp != IResource.NULL_STAMP) {
			Date resourceDate = new Date(modificationStamp);
			return resourceDate.equals(date) || resourceDate.after(date);
		} else {
		}

		return false;
	}

	/**
	 * For testing purposes
	 */
	public Date getLastActivatedDate() {
		return lastActivatedDate;
	}

	@Override
	public void contextChanged(ContextChangeEvent event) {
		switch (event.getEventKind()) {
			case ACTIVATED:
				// some OS's round the file time down to the nearest second, so we need to round the
				// activation time down as well to ensure that modified files within the first second are
				// properly captured
				long currentTime = new Date().getTime();
				currentTime -= currentTime % 1000d;
				lastActivatedDate = new Date(currentTime);
				break;
			case DEACTIVATED:
				lastActivatedDate = null;
				break;
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (ResourcesUiPreferenceInitializer.PREF_MODIFIED_DATE_EXCLUSIONS.equals(event.getProperty())) {
			Object newValue = event.getNewValue();
			if (newValue instanceof Boolean) {
				setEnabled((Boolean) newValue);
			} else if (newValue instanceof String) {
				setEnabled(Boolean.parseBoolean((String) newValue));
			}
		}
	}

	@Override
	public void historyNotification(OperationHistoryEvent event) {
		if (event.getEventType() == OperationHistoryEvent.ABOUT_TO_EXECUTE) {
			performingChange = true;
		} else if (event.getEventType() == OperationHistoryEvent.DONE
				|| event.getEventType() == OperationHistoryEvent.OPERATION_NOT_OK) {
			performingChange = false;
		}
	}

}

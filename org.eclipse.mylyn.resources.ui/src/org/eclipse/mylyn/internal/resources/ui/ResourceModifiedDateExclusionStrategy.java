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

package org.eclipse.mylyn.internal.resources.ui;

import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextCore;

/**
 * @author Shawn Minto
 */
public class ResourceModifiedDateExclusionStrategy extends AbstractContextListener implements
		IResourceExclusionStrategy, IPropertyChangeListener {

	private transient Date lastActivatedDate = null;

	private boolean isEnabled = false;

	public void dispose() {
		ContextCore.getContextManager().removeListener(this);
		ResourcesUiBridgePlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
	}

	public void init() {
		ResourcesUiBridgePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
		isEnabled = ResourcesUiBridgePlugin.getDefault().getPreferenceStore().getBoolean(
				ResourcesUiPreferenceInitializer.PREF_MODIFIED_DATE_EXCLUSIONS);
		ContextCore.getContextManager().addListener(this);
		if (ContextCore.getContextManager().isContextActive()) {
			lastActivatedDate = new Date();
		}
	}

	public void update() {
		// ignore
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public boolean isExcluded(IResource resource) {
		return (isEnabled() && resource instanceof IFile && !wasModifiedAfter(resource, lastActivatedDate));
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

	@Override
	public void contextChanged(ContextChangeEvent event) {
		switch (event.getEventKind()) {
		case ACTIVATED:
			lastActivatedDate = new Date();
			break;
		case DEACTIVATED:
			lastActivatedDate = null;
			break;
		}
	}

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

}

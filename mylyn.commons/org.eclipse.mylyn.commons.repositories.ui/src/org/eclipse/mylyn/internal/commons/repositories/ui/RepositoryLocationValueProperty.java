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

package org.eclipse.mylyn.internal.commons.repositories.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.IDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.IProperty;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.core.databinding.property.NativePropertyListener;
import org.eclipse.core.databinding.property.value.SimpleValueProperty;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;

/**
 * @author Steffen Pingel
 */
public class RepositoryLocationValueProperty extends SimpleValueProperty {

	private class PrivatePropertyChangeListener extends NativePropertyListener implements PropertyChangeListener {

		public PrivatePropertyChangeListener(IProperty property, ISimplePropertyListener listener) {
			super(property, listener);
		}

		@Override
		protected void doAddTo(Object source) {
			((RepositoryLocation) source).addPropertyChangeListener(this);
		}

		@Override
		protected void doRemoveFrom(Object source) {
			((RepositoryLocation) source).removePropertyChangeListener(this);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName() == null || key.equals(evt.getPropertyName())) {
				Object oldValue = evt.getOldValue();
				Object newValue = evt.getNewValue();
				IDiff diff;
				if (evt.getPropertyName() == null || oldValue == null || newValue == null) {
					diff = null;
				} else {
					diff = Diffs.createValueDiff(oldValue, newValue);
				}
				fireChange(evt.getSource(), diff);
			}
		}

	}

	private final String key;

	private final String defaultValue;

	public RepositoryLocationValueProperty(String key, String defaultValue) {
		this.key = key;
		this.defaultValue = defaultValue;
	}

	@Override
	public Object getValueType() {
		return String.class;
	}

	@Override
	protected Object doGetValue(Object source) {
//		if ("uri".equals(key)) {
//			URI uri = ((RepositoryLocation) source).getUri();
//			return (uri != null) ? uri.toString() : uri;
//		}
		String value = ((RepositoryLocation) source).getProperty(key);
		return value != null ? value : defaultValue;
	}

	@Override
	protected void doSetValue(Object source, Object value) {
//		if ("uri".equals(key)) {
//			try {
//				((RepositoryLocation) source).setUri(new URI((String) value));
//			} catch (URISyntaxException e) {
//				// ignore
//			}
//		} else {
		((RepositoryLocation) source).setProperty(key, value != null ? value.toString() : null);
//		}
	}

	@Override
	public INativePropertyListener adaptListener(final ISimplePropertyListener listener) {
		return new PrivatePropertyChangeListener(this, listener);
	}

}

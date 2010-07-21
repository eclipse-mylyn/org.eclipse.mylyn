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

package org.eclipse.mylyn.internal.commons.ui.repositories;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.IDiff;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.IProperty;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.core.databinding.property.NativePropertyListener;
import org.eclipse.core.databinding.property.value.SimpleValueProperty;
import org.eclipse.mylyn.commons.repositories.RepositoryLocation;

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
			((RepositoryLocation) source).addChangeListener(this);
		}

		@Override
		protected void doRemoveFrom(Object source) {
			((RepositoryLocation) source).removeChangeListener(this);
		}

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

	public RepositoryLocationValueProperty(String key) {
		this.key = key;
	}

	public Object getValueType() {
		return String.class;
	}

	@Override
	protected Object doGetValue(Object source) {
//		if ("uri".equals(key)) {
//			URI uri = ((RepositoryLocation) source).getUri();
//			return (uri != null) ? uri.toString() : uri;
//		}
		return ((RepositoryLocation) source).getProperty(key);
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
		((RepositoryLocation) source).setProperty(key, (value != null) ? value.toString() : null);
//		}
	}

	@Override
	public INativePropertyListener adaptListener(final ISimplePropertyListener listener) {
		return new PrivatePropertyChangeListener(this, listener);
	}

}

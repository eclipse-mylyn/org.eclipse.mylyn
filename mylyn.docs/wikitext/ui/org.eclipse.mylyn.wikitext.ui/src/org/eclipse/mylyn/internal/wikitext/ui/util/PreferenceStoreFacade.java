/*******************************************************************************
 * Copyright (c) 2007, 2021 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.util;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * A preference store that wraps a delegate, typically used to alter preferences based on some external influences (such as control focus).
 *
 * @author David Green
 */
public abstract class PreferenceStoreFacade implements IPreferenceStore {

	private final ListenerList<IPropertyChangeListener> listeners = new ListenerList<>(ListenerList.IDENTITY);

	private IPropertyChangeListener forwardingListener;

	protected IPreferenceStore delegate;

	protected PreferenceStoreFacade(IPreferenceStore preferenceStore) {
		delegate = preferenceStore;
	}

	@Override
	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		listeners.add(listener);
		if (forwardingListener == null) {
			forwardingListener = event -> firePropertyChangeEvent(event.getProperty(), event.getOldValue(),
					event.getNewValue());
			delegate.addPropertyChangeListener(forwardingListener);
		}
	}

	@Override
	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		listeners.remove(listener);
		if (listeners.isEmpty()) {
			if (forwardingListener != null) {
				delegate.removePropertyChangeListener(forwardingListener);
				forwardingListener = null;
			}
		}
	}

	@Override
	public void firePropertyChangeEvent(String name, Object oldValue, Object newValue) {
		if (listeners.isEmpty()) {
			return;
		}
		// always get the new value again in case the get methods have been overridden
		if (newValue instanceof Boolean) {
			newValue = getBoolean(name);
		} else if (newValue instanceof Integer) {
			newValue = getInt(name);
		} else if (newValue instanceof Long) {
			newValue = getLong(name);
		} else if (newValue instanceof Double) {
			newValue = getDouble(name);
		} else if (newValue instanceof Float) {
			newValue = getFloat(name);
		} else if (newValue instanceof String) {
			newValue = getString(name);
		}
		if (newValue == oldValue || newValue != null && newValue.equals(oldValue)) {
			return;
		}
		PropertyChangeEvent event = new PropertyChangeEvent(this, name, oldValue, newValue);
		for (IPropertyChangeListener listener : listeners) {
			listener.propertyChange(event);
		}
	}

	@Override
	public boolean contains(String name) {
		return delegate.contains(name);
	}

	@Override
	public boolean getBoolean(String name) {
		return delegate.getBoolean(name);
	}

	@Override
	public boolean getDefaultBoolean(String name) {
		return delegate.getDefaultBoolean(name);
	}

	@Override
	public double getDefaultDouble(String name) {
		return delegate.getDefaultDouble(name);
	}

	@Override
	public float getDefaultFloat(String name) {
		return delegate.getDefaultFloat(name);
	}

	@Override
	public int getDefaultInt(String name) {
		return delegate.getDefaultInt(name);
	}

	@Override
	public long getDefaultLong(String name) {
		return delegate.getDefaultLong(name);
	}

	@Override
	public String getDefaultString(String name) {
		return delegate.getDefaultString(name);
	}

	@Override
	public double getDouble(String name) {
		return delegate.getDouble(name);
	}

	@Override
	public float getFloat(String name) {
		return delegate.getFloat(name);
	}

	@Override
	public int getInt(String name) {
		return delegate.getInt(name);
	}

	@Override
	public long getLong(String name) {
		return delegate.getLong(name);
	}

	@Override
	public String getString(String name) {
		return delegate.getString(name);
	}

	@Override
	public boolean isDefault(String name) {
		return delegate.isDefault(name);
	}

	@Override
	public boolean needsSaving() {
		return delegate.needsSaving();
	}

	@Override
	public void putValue(String name, String value) {
		delegate.putValue(name, value);
	}

	@Override
	public void setDefault(String name, boolean value) {
		delegate.setDefault(name, value);
	}

	@Override
	public void setDefault(String name, double value) {
		delegate.setDefault(name, value);
	}

	@Override
	public void setDefault(String name, float value) {
		delegate.setDefault(name, value);
	}

	@Override
	public void setDefault(String name, int value) {
		delegate.setDefault(name, value);
	}

	@Override
	public void setDefault(String name, long value) {
		delegate.setDefault(name, value);
	}

	@Override
	public void setDefault(String name, String defaultObject) {
		delegate.setDefault(name, defaultObject);
	}

	@Override
	public void setToDefault(String name) {
		delegate.setToDefault(name);
	}

	@Override
	public void setValue(String name, boolean value) {
		delegate.setValue(name, value);
	}

	@Override
	public void setValue(String name, double value) {
		delegate.setValue(name, value);
	}

	@Override
	public void setValue(String name, float value) {
		delegate.setValue(name, value);
	}

	@Override
	public void setValue(String name, int value) {
		delegate.setValue(name, value);
	}

	@Override
	public void setValue(String name, long value) {
		delegate.setValue(name, value);
	}

	@Override
	public void setValue(String name, String value) {
		delegate.setValue(name, value);
	}

}

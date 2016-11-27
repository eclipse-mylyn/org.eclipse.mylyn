/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
 * A preference store that wraps a delegate, typically used to alter preferences based on some external influences (such
 * as control focus).
 * 
 * @author David Green
 */
public abstract class PreferenceStoreFacade implements IPreferenceStore {

	private final ListenerList listeners = new ListenerList(ListenerList.IDENTITY);

	private IPropertyChangeListener forwardingListener;

	protected IPreferenceStore delegate;

	protected PreferenceStoreFacade(IPreferenceStore preferenceStore) {
		delegate = preferenceStore;
	}

	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		listeners.add(listener);
		if (forwardingListener == null) {
			forwardingListener = new IPropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent event) {
					firePropertyChangeEvent(event.getProperty(), event.getOldValue(), event.getNewValue());
				}
			};
			delegate.addPropertyChangeListener(forwardingListener);
		}
	}

	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		listeners.remove(listener);
		if (listeners.isEmpty()) {
			if (forwardingListener != null) {
				delegate.removePropertyChangeListener(forwardingListener);
				forwardingListener = null;
			}
		}
	}

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
		if (newValue == oldValue || (newValue != null && newValue.equals(oldValue))) {
			return;
		}
		PropertyChangeEvent event = new PropertyChangeEvent(this, name, oldValue, newValue);
		for (Object o : listeners.getListeners()) {
			IPropertyChangeListener listener = (IPropertyChangeListener) o;
			listener.propertyChange(event);
		}
	}

	public boolean contains(String name) {
		return delegate.contains(name);
	}

	public boolean getBoolean(String name) {
		return delegate.getBoolean(name);
	}

	public boolean getDefaultBoolean(String name) {
		return delegate.getDefaultBoolean(name);
	}

	public double getDefaultDouble(String name) {
		return delegate.getDefaultDouble(name);
	}

	public float getDefaultFloat(String name) {
		return delegate.getDefaultFloat(name);
	}

	public int getDefaultInt(String name) {
		return delegate.getDefaultInt(name);
	}

	public long getDefaultLong(String name) {
		return delegate.getDefaultLong(name);
	}

	public String getDefaultString(String name) {
		return delegate.getDefaultString(name);
	}

	public double getDouble(String name) {
		return delegate.getDouble(name);
	}

	public float getFloat(String name) {
		return delegate.getFloat(name);
	}

	public int getInt(String name) {
		return delegate.getInt(name);
	}

	public long getLong(String name) {
		return delegate.getLong(name);
	}

	public String getString(String name) {
		return delegate.getString(name);
	}

	public boolean isDefault(String name) {
		return delegate.isDefault(name);
	}

	public boolean needsSaving() {
		return delegate.needsSaving();
	}

	public void putValue(String name, String value) {
		delegate.putValue(name, value);
	}

	public void setDefault(String name, boolean value) {
		delegate.setDefault(name, value);
	}

	public void setDefault(String name, double value) {
		delegate.setDefault(name, value);
	}

	public void setDefault(String name, float value) {
		delegate.setDefault(name, value);
	}

	public void setDefault(String name, int value) {
		delegate.setDefault(name, value);
	}

	public void setDefault(String name, long value) {
		delegate.setDefault(name, value);
	}

	public void setDefault(String name, String defaultObject) {
		delegate.setDefault(name, defaultObject);
	}

	public void setToDefault(String name) {
		delegate.setToDefault(name);
	}

	public void setValue(String name, boolean value) {
		delegate.setValue(name, value);
	}

	public void setValue(String name, double value) {
		delegate.setValue(name, value);
	}

	public void setValue(String name, float value) {
		delegate.setValue(name, value);
	}

	public void setValue(String name, int value) {
		delegate.setValue(name, value);
	}

	public void setValue(String name, long value) {
		delegate.setValue(name, value);
	}

	public void setValue(String name, String value) {
		delegate.setValue(name, value);
	}

}

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
package org.eclipse.mylyn.internal.discovery.core.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author David Green
 */
public class DiscoveryConnector extends ConnectorDescriptor {
	private AbstractDiscoverySource source;

	private DiscoveryCategory category;

	private boolean selected;

	private Boolean available;

	private DiscoveryCertification certification;

	private final PropertyChangeSupport changeSupport;

	public DiscoveryConnector() {
		changeSupport = new PropertyChangeSupport(this);
	}

	public DiscoveryCategory getCategory() {
		return category;
	}

	public void setCategory(DiscoveryCategory category) {
		this.category = category;
	}

	public AbstractDiscoverySource getSource() {
		return source;
	}

	public void setSource(AbstractDiscoverySource source) {
		this.source = source;
	}

	public DiscoveryCertification getCertification() {
		return certification;
	}

	public void setCertification(DiscoveryCertification certification) {
		this.certification = certification;
	}

	/**
	 * support selection
	 * 
	 * @return true if the item is selected, otherwise false
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * support selection
	 * 
	 * @param selected
	 *            true if the item is selected, otherwise false
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * indicate if this connector is available
	 * 
	 * @return true if available, false if not, or null if availability is unknown
	 */
	public Boolean getAvailable() {
		return available;
	}

	/**
	 * indicate if this connector is available
	 * 
	 * @param available
	 *            true if available, false if not, or null if availability is unknown
	 */
	public void setAvailable(Boolean available) {
		if (available != this.available || (available != null && !available.equals(this.available))) {
			Boolean previous = this.available;
			this.available = available;
			changeSupport.firePropertyChange("available", previous, this.available); //$NON-NLS-1$
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}
}

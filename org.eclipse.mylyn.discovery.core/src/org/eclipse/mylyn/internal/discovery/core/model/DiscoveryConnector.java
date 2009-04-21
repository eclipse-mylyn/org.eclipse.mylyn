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

/**
 * 
 * @author David Green
 */
public class DiscoveryConnector extends ConnectorDescriptor {
	private AbstractDiscoverySource source;
	private DiscoveryCategory category;
	private boolean selected;

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
	 * @param selected true if the item is selected, otherwise false
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}

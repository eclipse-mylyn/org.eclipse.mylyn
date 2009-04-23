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
 * a category of connectors, which is a way of organizing connectors in top-level groups.
 * 
 * @author David Green
 */
public class ConnectorCategory {

	protected String id;

	protected String name;

	protected String description;

	protected Icon icon;

	public ConnectorCategory() {
	}

	/**
	 * an id that uniquely identifies the category
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * the name of the category, as it is displayed in the ui.
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * A description of the category
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public void validate() throws ValidationException {
		if (id == null || id.length() == 0) {
			throw new ValidationException("Must specify connectorCategory/@id");
		}
		if (name == null || name.length() == 0) {
			throw new ValidationException("Must specify connectorCategory/@name");
		}
		if (icon != null) {
			icon.validate();
		}
	}
}

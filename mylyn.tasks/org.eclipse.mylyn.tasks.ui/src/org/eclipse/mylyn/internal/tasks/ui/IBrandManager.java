/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Collection;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.graphics.Image;

public interface IBrandManager {

	public Image getBrandingIcon(TaskRepository repository);

	public ImageDescriptor getOverlayIcon(TaskRepository repository);

	public ImageDescriptor getOverlayIcon(ITask task);

	/**
	 * Returns the branding icon for the given connector kind and brand. Returns the default icon for the repository
	 * type if the brand is <code>null</code> or is unknown to the connector.
	 */
	public Image getBrandingIcon(String connectorKind, @Nullable String brand);

	/**
	 * Returns the overlay icon for the given connector kind and brand. Returns the default icon for the repository type
	 * if the brand is <code>null</code> or is unknown to the connector.
	 */
	public ImageDescriptor getOverlayIcon(String connectorKind, @Nullable String brand);

	/**
	 * Returns the connector label for the given connector and brand. Returns the default label for the connector if the
	 * brand is <code>null</code> or is unknown to the connector.
	 */
	public String getConnectorLabel(AbstractRepositoryConnector connector, @Nullable String brand);

	/**
	 * Returns the brands known to the given connector.
	 */
	public Collection<String> getBrands(String connectorKind);

	public Image getDefaultBrandingIcon(String connectorKind);

	public ImageDescriptor getDefaultOverlayIcon(String connectorKind);

}
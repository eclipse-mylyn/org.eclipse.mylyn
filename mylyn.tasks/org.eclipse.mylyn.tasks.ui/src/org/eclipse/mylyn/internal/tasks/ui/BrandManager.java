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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.graphics.Image;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

public class BrandManager implements IBrandManager {

	private final Multimap<String, String> brands = LinkedHashMultimap.create();

	private final Table<String, String, Image> brandingIcons = HashBasedTable.create();

	private final Table<String, String, ImageDescriptor> overlayIcons = HashBasedTable.create();

	private final Table<String, String, String> connectorLabels = HashBasedTable.create();

	private final Map<String, Image> defaultBrandingIcons = new HashMap<>();

	private final Map<String, ImageDescriptor> defaultOverlayIcons = new HashMap<>();

	public void addBrandingIcon(String repositoryType, String brand, Image icon) {
		brands.put(repositoryType, brand);
		brandingIcons.put(repositoryType, brand, icon);
	}

	public void addOverlayIcon(String repositoryType, String brand, ImageDescriptor icon) {
		brands.put(repositoryType, brand);
		overlayIcons.put(repositoryType, brand, icon);
	}

	public void addConnectorLabel(String repositoryType, String brand, String label) {
		brands.put(repositoryType, brand);
		connectorLabels.put(repositoryType, brand, label);
	}

	public void addDefaultBrandingIcon(String repositoryType, Image icon) {
		defaultBrandingIcons.put(repositoryType, icon);
	}

	public void addDefaultOverlayIcon(String repositoryType, ImageDescriptor icon) {
		defaultOverlayIcons.put(repositoryType, icon);
	}

	@Override
	public Image getBrandingIcon(TaskRepository repository) {
		return getBrandingIcon(repository.getConnectorKind(),
				repository.getProperty(ITasksCoreConstants.PROPERTY_BRAND_ID));
	}

	@Override
	public ImageDescriptor getOverlayIcon(TaskRepository repository) {
		return getOverlayIcon(repository.getConnectorKind(),
				repository.getProperty(ITasksCoreConstants.PROPERTY_BRAND_ID));
	}

	@Override
	public ImageDescriptor getOverlayIcon(ITask task) {
		TaskRepository repository = getRepositoryManager().getRepository(task.getConnectorKind(),
				task.getRepositoryUrl());
		if (repository != null) {
			return getOverlayIcon(repository);
		}
		return getDefaultOverlayIcon(task.getConnectorKind());
	}

	@Override
	public Image getBrandingIcon(String repositoryType, @Nullable String brand) {
		Image icon = brandingIcons.get(repositoryType, brand);
		if (icon != null) {
			return icon;
		}
		return getDefaultBrandingIcon(repositoryType);
	}

	@Override
	public ImageDescriptor getOverlayIcon(String repositoryType, @Nullable String brand) {
		ImageDescriptor icon = overlayIcons.get(repositoryType, brand);
		if (icon != null) {
			return icon;
		}
		return getDefaultOverlayIcon(repositoryType);
	}

	@Override
	public String getConnectorLabel(AbstractRepositoryConnector connector, @Nullable String brand) {
		String brandLabel = connectorLabels.get(connector.getConnectorKind(), brand);
		if (brandLabel != null) {
			return brandLabel;
		}
		return connector.getLabel();
	}

	@Override
	public Collection<String> getBrands(String connectorKind) {
		return brands.get(connectorKind);
	}

	@Override
	public Image getDefaultBrandingIcon(String repositoryType) {
		return defaultBrandingIcons.get(repositoryType);
	}

	@Override
	public ImageDescriptor getDefaultOverlayIcon(String repositoryType) {
		return defaultOverlayIcons.get(repositoryType);
	}

	protected IRepositoryManager getRepositoryManager() {
		return TasksUi.getRepositoryManager();
	}
}

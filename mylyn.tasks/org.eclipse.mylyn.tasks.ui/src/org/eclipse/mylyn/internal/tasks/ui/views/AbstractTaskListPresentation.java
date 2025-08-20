/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPluginContribution;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public abstract class AbstractTaskListPresentation implements IPluginContribution {

	private String pluginId;

	private final String id;

	private String name;

	private ImageDescriptor imageDescriptor;

	private boolean primary = false;

	private final Map<TaskListView, AbstractTaskListContentProvider> contentProviders = new HashMap<>();

	public AbstractTaskListPresentation(String id) {
		this.id = id;
	}

	public AbstractTaskListContentProvider getContentProvider(TaskListView taskListView) {
		AbstractTaskListContentProvider contentProvider = contentProviders.get(taskListView);
		if (contentProvider == null) {
			contentProvider = createContentProvider(taskListView);
			contentProviders.put(taskListView, contentProvider);
		}
		return contentProvider;
	}

	/**
	 * Creates a new instance of a content provider for a particular instance of the Task List TODO: change view parameter to be the viewer
	 */
	protected abstract AbstractTaskListContentProvider createContentProvider(TaskListView taskListView);

	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setImageDescriptor(ImageDescriptor imageDescriptor) {
		this.imageDescriptor = imageDescriptor;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public String getId() {
		return id;
	}

	@Override
	public final String getLocalId() {
		return getId();
	}

	@Override
	public final String getPluginId() {
		return pluginId;
	}

	public final void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

}

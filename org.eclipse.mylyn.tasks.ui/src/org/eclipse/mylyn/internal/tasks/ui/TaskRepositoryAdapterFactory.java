/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IActionFilter;

/**
 * Adapter factory for adapting TaskRepository to org.eclipse.ui.IActionFilter
 * 
 * @author Eugene Kuleshov
 */
public class TaskRepositoryAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("unchecked")
	private static final Class[] ADAPTER_TYPES = new Class[] { IActionFilter.class };

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return ADAPTER_TYPES;
	}

	public Object getAdapter(final Object adaptable, @SuppressWarnings("unchecked")
	Class adapterType) {
		if (adaptable instanceof TaskRepository) {
			return new IActionFilter() {
				public boolean testAttribute(Object target, String name, String value) {
					TaskRepository repository = (TaskRepository) target;
					if ("supportQuery".equals(name)) {
						AbstractRepositoryConnectorUi connector = TasksUiPlugin.getConnectorUi(repository.getConnectorKind());
						return null != connector.getQueryWizard(repository, null);
					} else if ("supportNewTask".equals(name)) {
						AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
								.getRepositoryConnector(repository.getConnectorKind());
						return connector.canCreateNewTask(repository);
					} else if ("hasRepository".equals(name)) {
						return !repository.getConnectorKind().equals(LocalRepositoryConnector.REPOSITORY_KIND);
					}
					return false;
				}
			};
		}
		return null;
	}

}

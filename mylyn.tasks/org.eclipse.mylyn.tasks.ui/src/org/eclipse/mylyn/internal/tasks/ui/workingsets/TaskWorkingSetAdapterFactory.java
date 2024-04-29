/*******************************************************************************
 * Copyright (c) 2004, 2009 Eugene Kuleshov and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.workingsets;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/**
 * Adapter factory used to adapt AbstractTaskContainer to IPersistableElement
 *
 * @author Eugene Kuleshov
 */
public class TaskWorkingSetAdapterFactory implements IAdapterFactory {

	private static final String TASK_ELEMENT_FACTORY_ID = "org.eclipse.mylyn.tasks.ui.workingSets.elementFactory"; //$NON-NLS-1$

	@SuppressWarnings("rawtypes")
	private static final Class[] ADAPTER_TYPES = { IPersistableElement.class };

	@Override
	public Class<?>[] getAdapterList() {
		return ADAPTER_TYPES;
	}

	@Override
	public <T> T getAdapter(final Object adaptableObject, Class<T> adapterType) {
		if (adapterType == IPersistableElement.class && adaptableObject instanceof AbstractTaskContainer) {
			return adapterType.cast(new IPersistableElement() {
				@Override
				public void saveState(IMemento memento) {
					IRepositoryElement container = (IRepositoryElement) adaptableObject;
					memento.putString(TaskWorkingSetElementFactory.HANDLE_TASK, container.getHandleIdentifier());
				}

				@Override
				public String getFactoryId() {
					return TASK_ELEMENT_FACTORY_ID;
				}
			});
		} else if (adapterType == IPersistableElement.class && adaptableObject instanceof IProject) {
			return adapterType.cast(new IPersistableElement() {
				@Override
				public void saveState(IMemento memento) {
					IProject project = (IProject) adaptableObject;
					memento.putString(TaskWorkingSetElementFactory.HANDLE_PROJECT, project.getName());
				}

				@Override
				public String getFactoryId() {
					return TASK_ELEMENT_FACTORY_ID;
				}
			});
		}
		return null;
	}
}

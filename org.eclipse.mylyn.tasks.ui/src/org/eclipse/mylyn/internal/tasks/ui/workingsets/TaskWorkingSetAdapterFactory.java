/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.workingsets;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/**
 * Adapter factory used to adapt AbstractTaskContainer to IPersistableElement
 * 
 * @author Eugene Kuleshov
 */
public class TaskWorkingSetAdapterFactory implements IAdapterFactory {

	private static final String TASK_ELEMENT_FACTORY_ID = "org.eclipse.mylyn.tasks.ui.workingSets.elementFactory";

	@SuppressWarnings("unchecked")
	private static final Class[] ADAPTER_TYPES = new Class[] { IPersistableElement.class };

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return ADAPTER_TYPES;
	}

	public Object getAdapter(final Object adaptableObject, @SuppressWarnings("unchecked") Class adapterType) {
		if (adapterType == IPersistableElement.class && adaptableObject instanceof AbstractTaskContainer) {
			return new IPersistableElement() {
				public void saveState(IMemento memento) {
					AbstractTaskContainer container = (AbstractTaskContainer) adaptableObject;
					memento.putString(TaskWorkingSetElementFactory.HANDLE_TASK, container.getHandleIdentifier());
				}

				public String getFactoryId() {
					return TASK_ELEMENT_FACTORY_ID;
				}
			};
		} else if (adapterType == IPersistableElement.class && adaptableObject instanceof IProject) {
			return new IPersistableElement() {
				public void saveState(IMemento memento) {
					IProject project = (IProject) adaptableObject;
					memento.putString(TaskWorkingSetElementFactory.HANDLE_PROJECT, project.getName());
				}

				public String getFactoryId() {
					return TASK_ELEMENT_FACTORY_ID;
				}
			};
		}
		return null;
	}
}

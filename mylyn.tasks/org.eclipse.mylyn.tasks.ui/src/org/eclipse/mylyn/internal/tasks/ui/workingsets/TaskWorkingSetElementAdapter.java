/*******************************************************************************
 * Copyright (c) 2004, 2008 Eugene Kuleshov and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.workingsets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetElementAdapter;

/**
 * @author Eugene Kuleshov
 * @author Mik Kersten
 */
public class TaskWorkingSetElementAdapter implements IWorkingSetElementAdapter {

	public IAdaptable[] adaptElements(IWorkingSet workingSet, IAdaptable[] elements) {
		for (IAdaptable adaptable : elements) {
			if (!(adaptable instanceof AbstractTaskContainer)) {
				return selectContainers(elements);
			}
		}
		return elements;
	}

	private IAdaptable[] selectContainers(IAdaptable[] elements) {
		List<IAdaptable> containers = new ArrayList<IAdaptable>(elements.length);
		for (IAdaptable adaptable : elements) {
			if (adaptable instanceof AbstractTaskContainer) {
				containers.add(adaptable);
			} else if (adaptable instanceof IProject) {
				containers.add(adaptable);
			}
		}
		return containers.toArray(new IAdaptable[containers.size()]);
	}

	public void dispose() {
		// ignore
	}
}
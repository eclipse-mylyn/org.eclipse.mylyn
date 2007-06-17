/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.workingsets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetElementAdapter;

/**
 * @author Eugene Kuleshov
 */
public class TaskWorkingSetElementAdapter implements IWorkingSetElementAdapter {

	public IAdaptable[] adaptElements(IWorkingSet workingSet, IAdaptable[] elements) {
		for (int i = 0; i < elements.length; i++) {
			IAdaptable adaptable = elements[i];
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
			}
		}
		return (IAdaptable[]) containers.toArray(new IAdaptable[containers.size()]);
	}

	public void dispose() {
		// ignore
	}

}
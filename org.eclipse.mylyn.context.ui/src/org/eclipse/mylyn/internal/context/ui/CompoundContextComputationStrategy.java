/*******************************************************************************
 * Copyright (c) 2011, 2013 Tasktop Technologies.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.context.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.mylyn.context.core.ContextComputationStrategy;
import org.eclipse.mylyn.context.core.IInteractionContext;

/**
 * A compound context computation strategy, that uses delegates to do the work of computing a context.
 * 
 * @author David Green
 * @since 3.6
 */
public class CompoundContextComputationStrategy extends ContextComputationStrategy {

	private List<ContextComputationStrategy> delegates;

	@Override
	public List<Object> computeContext(IInteractionContext context, IAdaptable input, IProgressMonitor monitor) {
		if (delegates == null || delegates.isEmpty()) {
			return Collections.emptyList();
		}
		List<Object> objects = new ArrayList<Object>();

		SubMonitor progress = SubMonitor.convert(monitor);
		int workPerDelegate = 1000;
		progress.beginTask(Messages.CompoundContextComputationStrategy_Computing_Context_Task_Label,
				delegates.size() * workPerDelegate);
		try {
			for (ContextComputationStrategy delegate : delegates) {
				if (progress.isCanceled()) {
					break;
				}
				objects.addAll(delegate.computeContext(context, input, progress.newChild(workPerDelegate)));
			}
		} finally {
			progress.done();
		}

		return objects;
	}

	public List<ContextComputationStrategy> getDelegates() {
		return delegates;
	}

	public void setDelegates(List<ContextComputationStrategy> delegates) {
		this.delegates = delegates;
	}

}

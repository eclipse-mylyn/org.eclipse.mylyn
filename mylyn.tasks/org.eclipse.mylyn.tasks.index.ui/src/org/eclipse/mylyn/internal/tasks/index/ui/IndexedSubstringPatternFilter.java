/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies.
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
package org.eclipse.mylyn.internal.tasks.index.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.commons.workbench.SubstringPatternFilter;
import org.eclipse.mylyn.internal.tasks.index.core.TaskListIndex;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * A pattern filter that uses a {@link TaskListIndex} to support ITask matching.
 *
 * @author David Green
 */
public class IndexedSubstringPatternFilter extends SubstringPatternFilter {

	private final TaskListIndex index;

	private String patternString;

	public IndexedSubstringPatternFilter(TaskListIndex index) {
		this.index = index;
	}

	@Override
	public void setPattern(String patternString) {
		if (patternString != null) {
			patternString = patternString.trim();
		}
		this.patternString = patternString;
		super.setPattern(patternString);
	}

	@Override
	protected boolean isLeafMatch(Viewer viewer, Object element) {
		if (patternString != null && patternString.length() > 0) {
			if (element instanceof ITask task) {
				if (index.matches(task, patternString)) {
					return true;
				} else {
					// fall through so that we get non-indexed matching semantics
				}
			}
		}
		return super.isLeafMatch(viewer, element);
	}
}

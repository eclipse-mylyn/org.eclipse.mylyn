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

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.mylyn.internal.tasks.core.UncategorizedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnsubmittedTaskContainer;

/**
 * @author Mik Kersten
 */
public class TaskArchiveFilter extends AbstractTaskListFilter {

	@Override
	public boolean select(Object parent, Object element) {
		if (element instanceof UnmatchedTaskContainer) {
			if (((UnmatchedTaskContainer) element).isEmpty()) {
				return false;
			}
		} else if (element instanceof UnsubmittedTaskContainer) {
			if (((UnsubmittedTaskContainer) element).isEmpty()) {
				return false;
			}
		} else if (element instanceof UncategorizedTaskContainer) {
			if (((UncategorizedTaskContainer) element).isEmpty()) {
				return false;
			}
		}
		return true;
	}

}

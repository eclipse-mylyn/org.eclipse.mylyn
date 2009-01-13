/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

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
		}
		if (element instanceof UnsubmittedTaskContainer) {
			if (((UnsubmittedTaskContainer) element).isEmpty()) {
				return false;
			}
		}
		return true;
	}
}

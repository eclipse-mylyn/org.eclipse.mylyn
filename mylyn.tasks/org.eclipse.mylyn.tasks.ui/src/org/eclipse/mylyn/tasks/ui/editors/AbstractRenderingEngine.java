/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Xiaoyang Guan - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Extend to provide HTML preview for ticket description and comments
 *
 * @author Xiaoyang Guan
 * @since 2.1
 */
// TODO 4.0 move to core?
public abstract class AbstractRenderingEngine {

	/**
	 * generate HTML preview page for <code>text</code>
	 */
	public abstract String renderAsHtml(TaskRepository repository, String text, IProgressMonitor monitor)
			throws CoreException;
}

/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.ui;

import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * A factory for creating instances of reusable UI components.
 *
 * @author Steffen Pingel
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @since 3.1
 */
public interface ITasksUiFactory {

	/**
	 * Returns a content assist processor for references to tasks.
	 *
	 * @since 3.1
	 */
	IContentAssistProcessor createTaskContentAssistProcessor(TaskRepository repository);

	/**
	 * Returns a content proposal provider for repository users.
	 *
	 * @since 3.1
	 * @see #createPersonContentProposalLabelProvider(TaskRepository)
	 */
	IContentProposalProvider createPersonContentProposalProvider(TaskRepository repository);

	/**
	 * Returns a label provider for repository users content proposals.
	 *
	 * @since 3.1
	 * @see #createPersonContentProposalProvider(TaskRepository)
	 */
	ILabelProvider createPersonContentProposalLabelProvider(TaskRepository repository);

}

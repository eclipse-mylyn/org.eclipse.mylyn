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
 * @since 3.1
 */
public interface ITasksUiFactory {

	/**
	 * Returns a content assist processor for references to tasks.
	 * 
	 * @since 3.1
	 */
	public abstract IContentAssistProcessor createTaskContentAssistProcessor(TaskRepository repository);

	/**
	 * Returns a content proposal provider for repository users.
	 * 
	 * @since 3.1
	 * @see #createPersonContentProposalLabelProvider(TaskRepository)
	 */
	public abstract IContentProposalProvider createPersonContentProposalProvider(TaskRepository repository);

	/**
	 * Returns a label provider for repository users content proposals.
	 * 
	 * @since 3.1
	 * @see #createPersonContentProposalProvider(TaskRepository)
	 */
	public abstract ILabelProvider createPersonContentProposalLabelProvider(TaskRepository repository);

}

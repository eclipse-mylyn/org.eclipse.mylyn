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
 *     David Green - fix for bug 263418
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryCompletionProcessor;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.ITasksUiFactory;

/**
 * @author Steffen Pingel
 * @author David Green
 */
public class TasksUiFactory implements ITasksUiFactory {

	@Override
	public IContentProposalProvider createPersonContentProposalProvider(TaskRepository repository) {
		return new PersonProposalProvider(repository.getRepositoryUrl(), repository.getConnectorKind());
	}

	@Override
	public ILabelProvider createPersonContentProposalLabelProvider(TaskRepository repository) {
		return new PersonProposalLabelProvider();
	}

	@Override
	public IContentAssistProcessor createTaskContentAssistProcessor(TaskRepository repository) {
		return new RepositoryCompletionProcessor(repository);
	}

}

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

import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.ITasksUiFactory;

/**
 * @author Steffen Pingel
 */
public class TasksUiFactory implements ITasksUiFactory {

	public IContentProposalProvider createPersonContentProposalProvider(TaskRepository repository) {
		return new PersonProposalProvider(repository.getRepositoryUrl(), repository.getConnectorKind());
	}

	public ILabelProvider createPersonContentProposalLabelProvider(TaskRepository repository) {
		return new PersonProposalLabelProvider();
	}

	public IContentAssistProcessor createTaskContentAssistProcessor(TaskRepository repository) {
		// ignore
		return null;
	}

}

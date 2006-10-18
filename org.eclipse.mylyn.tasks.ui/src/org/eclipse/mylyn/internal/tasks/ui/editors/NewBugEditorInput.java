/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.editors;

import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * The <code>IEditorInput</code> implementation for <code>NewBugEditor</code>.
 * @author Rob Elves (modifications)
 */
public class NewBugEditorInput extends AbstractBugEditorInput {

	public NewBugEditorInput(TaskRepository repository, RepositoryTaskData taskData) {
		super(repository, taskData);		
	}

	public String getName() {
		return "NEW ["+repository.getUrl()+"]";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof NewBugEditorInput) {
			NewBugEditorInput input = (NewBugEditorInput) o;
			return input.getRepositoryTaskData().equals(this.getRepositoryTaskData());
		}
		return false;
	}

}

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

import java.net.Proxy;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * Abstract base implementation of an <code>IEditorInput</code> for a subclass
 * of <code>AbstractRepositoryTaskEditor</code>.
 * @author Rob Elves (modifications)
 */
public abstract class AbstractBugEditorInput implements IEditorInput {

	protected String toolTipText = "";

	protected Proxy proxySettings;

	protected TaskRepository repository;

	protected RepositoryTaskData repositoryTaskData;
	
	protected AbstractBugEditorInput(TaskRepository repository, RepositoryTaskData taskData) {
		this.repositoryTaskData = taskData;		
		this.repository = repository;
		this.proxySettings = TasksUiPlugin.getDefault().getProxySettings();
	}
	
	/**
	 * Sets the tool tip text for this editor input.
	 * 
	 * @param str
	 *            The new tool tip text.
	 */
	protected void setToolTipText(String str) {
		// 03-20-03 Allows editor to store title (once it is known)
		toolTipText = str;
	}

	public boolean exists() {
		return true;
	}

	public RepositoryTaskData getRepositoryTaskData() {
		return repositoryTaskData;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return toolTipText;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	/**
	 * @return <code>true</code> if the argument is an editor input on the
	 *         same bug.
	 */
	@Override
	public abstract boolean equals(Object o);

	public Proxy getProxySettings() {
		return proxySettings;
	}

	public TaskRepository getRepository() {
		return repository;
	}
}

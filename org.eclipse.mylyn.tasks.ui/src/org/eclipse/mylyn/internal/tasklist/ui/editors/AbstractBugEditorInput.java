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

package org.eclipse.mylar.internal.tasklist.ui.editors;

import java.net.Proxy;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.internal.tasklist.RepositoryTaskData;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * Abstract base implementation of an <code>IEditorInput</code> for a subclass
 * of <code>AbstractRepositoryTaskEditor</code>.
 */
public abstract class AbstractBugEditorInput implements IEditorInput {

	protected String toolTipText = "";

	protected Proxy proxySettings;
	
	protected AbstractBugEditorInput() {
		this.proxySettings = MylarTaskListPlugin.getDefault().getProxySettings();
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

	public abstract RepositoryTaskData getRepositoryTaskData();

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


	public abstract TaskRepository getRepository();
}

/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.editors;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IPluginContribution;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * @since 3.0
 * @author Steffen Pingel
 */
public abstract class AbstractTaskEditorPageFactory implements IPluginContribution {

	public static final int PRIORITY_ADDITIONS = 100;

	public static final int PRIORITY_CONTEXT = 20;

	public static final int PRIORITY_PLANNING = 10;

	public static final int PRIORITY_TASK = 30;

	private String id;

	private String pluginId;

	public abstract boolean canCreatePageFor(TaskEditorInput input);

	public abstract IFormPage createPage(TaskEditor parentEditor);

	public String[] getConflictingIds(TaskEditorInput input) {
		return null;
	}

	public String getId() {
		return id;
	}

	// TODO EDITOR life cycle of image?
	public abstract Image getPageImage();

	public abstract String getPageText();

	public int getPriority() {
		return PRIORITY_ADDITIONS;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 3.2
	 */
	public final String getLocalId() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 3.2
	 * @see #setPluginId(String)
	 */
	public final String getPluginId() {
		return pluginId;
	}

	/**
	 * @since 3.2
	 * @see #getPluginId()
	 */
	public final void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	/**
	 * Clients should override to provide an image for <code>page</code>. Invokes {@link #getPageImage()} for backwards
	 * compatibility.
	 * 
	 * @param editor
	 *            the task editor instance
	 * @param page
	 *            the page that uses the image
	 * @return an image
	 * @since 3.10
	 */
	public Image getPageImage(TaskEditor editor, IFormPage page) {
		return getPageImage();
	}

	/**
	 * Clients should override to provide a label for <code>page</code>. Invokes {@link #getPageText()} for backwards
	 * compatibility.
	 * 
	 * @param editor
	 *            the task editor instance
	 * @param page
	 *            the page that uses the label
	 * @return a label
	 * @since 3.10
	 */
	public String getPageText(TaskEditor editor, IFormPage page) {
		return getPageText();
	}

}

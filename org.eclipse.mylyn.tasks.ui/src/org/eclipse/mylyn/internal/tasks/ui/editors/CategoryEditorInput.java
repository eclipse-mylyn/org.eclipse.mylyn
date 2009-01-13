/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ken Sueda - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Ken Sueda
 */
public class CategoryEditorInput implements IEditorInput {

	private final TaskCategory category;

	public CategoryEditorInput(TaskCategory cat) {
		this.category = cat;
	}

	public boolean exists() {
		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return Messages.CategoryEditorInput_Category_Editor;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return Messages.CategoryEditorInput_Category_Editor;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public String getCategoryName() {
		return category.getSummary();
	}

	public String getUrl() {
		return category.getUrl();
	}

	public void setCategoryName(String description) {
		TasksUiPlugin.getTaskList().renameContainer(category, description);
//		category.setDescription(summary);
	}

	public void setUrl(String url) {
		category.setUrl(url);
		TasksUiPlugin.getTaskList().notifyElementChanged(category);
	}

}

/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.tasklist.ITaskListCategory;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Ken Sueda
 */
public class CategoryEditorInput implements IEditorInput {

	private ITaskListCategory category;
	
	public CategoryEditorInput(ITaskListCategory cat) {
		this.category = cat;		
	}
	public boolean exists() {
		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return "Category Editor";
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return "Category Editor";
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public String getCategoryName() {
		return category.getDescription(false);
	}
	
	public void setCategoryName(String description) {
		category.setDescription(description);		
	}
}

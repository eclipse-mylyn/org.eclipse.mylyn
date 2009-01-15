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

package org.eclipse.mylyn.tasks.ui.editors;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

/**
 * @author Steffen Pingel
 * @since 3.1
 */
public class TaskFormPage extends FormPage {

	public TaskFormPage(String id, String title) {
		super(id, title);
	}

	public TaskFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	protected void fillToolBar(IToolBarManager toolBarManager) {
	}

}

/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.editors;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.IManagedForm;
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

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		managedForm.getToolkit().setBorderStyle(SWT.NULL);
		EditorUtil.initializeScrollbars(managedForm.getForm());
	}

	/**
	 * Invoked when the task opened in the editor is opened while the editor was already open or if a synchronization
	 * completes.
	 * <p>
	 * Clients may override.
	 * 
	 * @since 3.4
	 */
	protected void refresh() {
		// ignore
	}

}

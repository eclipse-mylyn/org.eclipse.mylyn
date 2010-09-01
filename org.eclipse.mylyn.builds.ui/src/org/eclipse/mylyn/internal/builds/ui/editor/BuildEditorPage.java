/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.mylyn.tasks.ui.editors.TaskFormPage;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Steffen Pingel
 */
public abstract class BuildEditorPage extends TaskFormPage {

	private boolean reflow;

	private final DataBindingContext dataBindingContext = new DataBindingContext();

	public BuildEditorPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	public BuildEditorPage(String id, String title) {
		super(id, title);
	}

	@Override
	public BuildEditorInput getEditorInput() {
		return (BuildEditorInput) super.getEditorInput();
	}

	public DataBindingContext getDataBindingContext() {
		return dataBindingContext;
	}

	public boolean isReflow() {
		return reflow;
	}

	/**
	 * Force a re-layout of entire form.
	 */
	public void reflow() {
		if (reflow) {
			ScrolledForm form = getManagedForm().getForm();
			try {
				form.setRedraw(false);
				// help the layout managers: ensure that the form width always matches
				// the parent client area width.
				Rectangle parentClientArea = form.getParent().getClientArea();
				Point formSize = form.getSize();
				if (formSize.x != parentClientArea.width) {
					ScrollBar verticalBar = form.getVerticalBar();
					int verticalBarWidth = verticalBar != null ? verticalBar.getSize().x : 15;
					form.setSize(parentClientArea.width - verticalBarWidth, formSize.y);
				}

				form.layout(true, false);
				form.reflow(true);
			} finally {
				form.setRedraw(true);
			}
		}
	}

	public void setReflow(boolean reflow) {
		this.reflow = reflow;
	}

}

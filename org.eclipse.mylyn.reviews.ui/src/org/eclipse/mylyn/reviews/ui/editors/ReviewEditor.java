/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/*
 * @author Kilian Matt
 */
public class ReviewEditor extends EditorPart {

	public static final String ID = "org.eclipse.mylyn.reviews.ui.editors.ReviewEditor"; //$NON-NLS-1$

	@Override
	public void dispose() {
		super.dispose();
	}

	/**
	 * The dirty flag if the model has been changed
	 */
	private boolean dirty = false;

	/**
	 * The managed form for this editor
	 */

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName());

	}

	@Override
	public void createPartControl(Composite parent) {
		new EditorSupport((ReviewTaskEditorInput) getEditorInput(),
				new NewReviewSubmitHandler()).createPartControl(parent);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		setDirty(false);
	}

	@Override
	public void doSaveAs() {
		// not allowed
	}

	@Override
	public boolean isSaveAsAllowed() {
		// not allowed
		return false;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		if (dirty == true && !isDirty()) {
			this.dirty = true;
			firePropertyChange(IEditorPart.PROP_DIRTY);
		} else if (dirty == false && isDirty()) {
			this.dirty = false;
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}

	@Override
	public void setFocus() {
	}
}

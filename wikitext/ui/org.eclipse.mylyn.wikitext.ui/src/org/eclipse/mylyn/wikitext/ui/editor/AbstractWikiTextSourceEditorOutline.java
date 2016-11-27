/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.ui.editor;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * An abstract base class for all editor outlines to be used with {@link WikiTextSourceEditor}.
 * 
 * @author David Green
 * @since 1.3
 */
public abstract class AbstractWikiTextSourceEditorOutline extends ContentOutlinePage {

	private IEditorPart editor;

	private final IPropertyListener editorPropertyListener = new IPropertyListener() {
		public void propertyChanged(Object source, int propId) {
			editorPropertyChanged(source, propId);
		}
	};

	public void setEditor(IEditorPart editor) {
		if (this.editor != null) {
			this.editor.removePropertyListener(editorPropertyListener);
		}
		this.editor = editor;
		if (this.editor != null) {
			this.editor.addPropertyListener(editorPropertyListener);
		}
	}

	public IEditorPart getEditor() {
		return editor;
	}

	/**
	 * Listen to changes on the editor. Overriding methods should call super.editorPropertyChanaged.
	 */
	protected void editorPropertyChanged(Object source, int propId) {
		// nothing to do
	}

	@Override
	public void dispose() {
		setEditor(null);
		super.dispose();
	}
}

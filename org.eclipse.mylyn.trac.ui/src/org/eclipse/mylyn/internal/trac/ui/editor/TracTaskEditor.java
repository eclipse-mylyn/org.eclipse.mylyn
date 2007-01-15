/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.ui.editor;

import org.eclipse.mylar.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * @author Steffen Pingel
 */
public class TracTaskEditor extends AbstractRepositoryTaskEditor {

	public TracTaskEditor(FormEditor editor) {
		super(editor);
	}

	@Override
	protected void validateInput() {
	}

}

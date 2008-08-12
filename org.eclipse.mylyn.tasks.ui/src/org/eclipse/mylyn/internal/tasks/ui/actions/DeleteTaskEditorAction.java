/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.internal.WorkbenchImages;

/**
 * @author Mik Kersten
 */
public class DeleteTaskEditorAction extends DeleteAction {

	public DeleteTaskEditorAction() {
		super();
		setHoverImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		setImageDescriptor(CommonImages.REMOVE);
	}
}

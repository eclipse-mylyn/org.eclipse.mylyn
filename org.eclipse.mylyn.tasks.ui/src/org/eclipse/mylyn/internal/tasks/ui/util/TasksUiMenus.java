/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;

/**
 * @author Steffen Pingel
 */
public class TasksUiMenus {

	public static void fillTaskAttachmentMenu(IMenuManager manager) {
		manager.add(new Separator("group.open"));
		manager.add(new Separator("group.save"));
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

}

/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.context.ui.actions;

import java.util.Iterator;

import org.eclipse.mylar.internal.tasks.ui.TaskListImages;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * 
 * @author Mik Kersten
 */
public class RemoveFromContextAction extends BaseSelectionListenerAction {

	public static final String ID = "org.eclipse.mylar.context.ui.actions.delete";

	public RemoveFromContextAction() {
		super("Remove from Context");
		setId(ID);
		setImageDescriptor(TaskListImages.REMOVE);
	}

	@Override
	public void run() {
		for (Iterator<?> iterator = super.getStructuredSelection().iterator(); iterator.hasNext();) {
			Object selection = iterator.next();
			System.err.println(">>> " + selection); 
		}
 	}
	
}

/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.java.ui.actions;

import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.JavaUIMessages;
import org.eclipse.jdt.internal.ui.actions.OpenTypeAction;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

public class MylarOpenTypeAction extends OpenTypeAction implements IWorkbenchWindowActionDelegate {
	
	public MylarOpenTypeAction() {
		super();
		setText(JavaUIMessages.OpenTypeAction_label); 
		setDescription(JavaUIMessages.OpenTypeAction_description); 
		setToolTipText(JavaUIMessages.OpenTypeAction_tooltip); 
		setImageDescriptor(JavaPluginImages.DESC_TOOL_OPENTYPE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.OPEN_TYPE_ACTION);
	}
	
	public void run() {
		super.run();
	}
}
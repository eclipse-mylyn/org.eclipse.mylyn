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

package org.eclipse.mylar.internal.ide.ui;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.mylar.internal.context.ui.MylarImages;
import org.eclipse.mylar.internal.ide.team.MylarActiveChangeSet;
import org.eclipse.mylar.internal.tasklist.ui.TaskListColorsAndFonts;

/**
 * @author Mik Kersten
 */
public class MylarChangeSetDecorator implements ILightweightLabelDecorator {

	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof MylarActiveChangeSet) {
			decoration.addOverlay(MylarImages.MYLAR_OVERLAY, IDecoration.BOTTOM_RIGHT);
			MylarActiveChangeSet changeSet = (MylarActiveChangeSet)element;
			if (changeSet.getTask().isActive()) {    
				decoration.setFont(TaskListColorsAndFonts.BOLD);
			}
		}
	}

	public void addListener(ILabelProviderListener listener) {
		// ignore
	}

	public void dispose() {
		// ignore
	}

	public boolean isLabelProperty(Object element, String property) {
		// ignore
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// ignore
	}


}

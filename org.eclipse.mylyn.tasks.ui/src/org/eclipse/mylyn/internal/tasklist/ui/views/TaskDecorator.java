/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist.ui.views;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.mylar.internal.tasklist.AbstractQueryHit;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */

public class TaskDecorator implements ILabelDecorator {

	public Image decorateImage(Image image, Object element) {
		// ignore
		return null;
	}

	public String decorateText(String text, Object element) {
		if (element instanceof AbstractQueryHit) {
			AbstractQueryHit hit = (AbstractQueryHit)element;
			if (hit.getCorrespondingTask() != null) {
				return hit.getCorrespondingTask().getKind() + ": " + text;
			}
		}
		return null;
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

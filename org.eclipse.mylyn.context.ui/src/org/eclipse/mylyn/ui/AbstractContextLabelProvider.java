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

package org.eclipse.mylar.ui;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.mylar.core.IMylarRelation;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.swt.graphics.Image;

/**
 * TODO: this delegation idiom is breaking, refactor
 * 
 * @author Mik Kersten
 */
public abstract class AbstractContextLabelProvider implements ILabelProvider {

	public Image getImage(Object object) {
		if (object instanceof IMylarRelation) {
			return getImage((IMylarRelation) object);
		} else if (object instanceof IMylarElement) {
			return getImage((IMylarElement) object);
		} else {
			return getImageForObject(object);
		}
	}

	public String getText(Object object) {
		if (object instanceof IMylarRelation) {
			return getText((IMylarRelation) object);
		} else if (object instanceof IMylarElement) {
			return getText((IMylarElement) object);
		} else {
			return getTextForObject(object);
		}
	}

	protected abstract Image getImage(IMylarElement node);

	protected abstract Image getImage(IMylarRelation edge);

	protected abstract Image getImageForObject(Object object);

	protected abstract String getText(IMylarElement node);

	protected abstract String getTextForObject(Object object);

	protected abstract String getText(IMylarRelation edge);

	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

}

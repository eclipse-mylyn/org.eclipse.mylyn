/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.IInteractionRelation;
import org.eclipse.swt.graphics.Image;

/**
 * TODO: this delegation idiom is breaking, refactor
 * 
 * @author Mik Kersten
 */
public abstract class AbstractContextLabelProvider implements ILabelProvider {

	public Image getImage(Object object) {
		if (object instanceof IInteractionRelation) {
			return getImage((IInteractionRelation) object);
		} else if (object instanceof IInteractionElement) {
			return getImage((IInteractionElement) object);
		} else {
			return getImageForObject(object);
		}
	}

	public String getText(Object object) {
		if (object instanceof IInteractionRelation) {
			return getText((IInteractionRelation) object);
		} else if (object instanceof IInteractionElement) {
			return getText((IInteractionElement) object);
		} else {
			return getTextForObject(object);
		}
	}

	protected abstract Image getImage(IInteractionElement node);

	protected abstract Image getImage(IInteractionRelation edge);

	protected abstract Image getImageForObject(Object object);

	protected abstract String getText(IInteractionElement node);

	protected abstract String getTextForObject(Object object);

	protected abstract String getText(IInteractionRelation edge);

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

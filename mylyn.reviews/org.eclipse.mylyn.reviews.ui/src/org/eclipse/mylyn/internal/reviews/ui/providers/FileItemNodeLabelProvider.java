/*******************************************************************************
 * Copyright (c) 2013, 2014, Ericsson AB and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Sebastien Dubois (Ericsson) - Created for Mylyn Reviews
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.providers;

import org.eclipse.compare.ICompareInputLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.mylyn.internal.reviews.ui.compare.FileItemNode;
import org.eclipse.mylyn.internal.reviews.ui.compare.FileRevisionTypedElement;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

/**
 * Custom Label Provider for Review Compare Editor input
 * 
 * @author Sebastien Dubois
 */
public class FileItemNodeLabelProvider implements ICompareInputLabelProvider {

	@Override
	public Image getImage(Object element) {
		return null; //Not used
	}

	@Override
	public String getText(Object element) {
		return null; //Not used
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		//Not supported for now
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false; //Not supported for now
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		//Not supported for now
	}

	@Override
	public String getAncestorLabel(Object input) {
		return null; //Not supported
	}

	@Override
	public Image getAncestorImage(Object input) {
		return null; //Not supported
	}

	@Override
	public String getLeftLabel(Object input) {
		if (((FileItemNode) input).getLeft() instanceof FileRevisionTypedElement) {
			return NLS.bind("{0}: {1}", ((FileItemNode) input).getFileItem().getBase().getDescription(), //$NON-NLS-1$
					((FileItemNode) input).getFileItem().getName());
		}
		return NLS.bind("[{0}: {1}]", ((FileItemNode) input).getFileItem().getBase().getDescription(), //$NON-NLS-1$
				((FileItemNode) input).getFileItem().getName());
	}

	@Override
	public Image getLeftImage(Object input) {
		if (((FileItemNode) input).getLeft() instanceof FileRevisionTypedElement) {
			return ((FileItemNode) input).getImage();
		} else {
			return null;
		}
	}

	@Override
	public String getRightLabel(Object input) {
		if (((FileItemNode) input).getRight() instanceof FileRevisionTypedElement) {
			return NLS.bind("{0}: {1}", ((FileItemNode) input).getFileItem().getTarget().getDescription(), //$NON-NLS-1$
					((FileItemNode) input).getFileItem().getName());
		}
		return NLS.bind("[{0}: {1}]", ((FileItemNode) input).getFileItem().getTarget().getDescription(), //$NON-NLS-1$
				((FileItemNode) input).getFileItem().getName());
	}

	@Override
	public Image getRightImage(Object input) {
		if (((FileItemNode) input).getRight() instanceof FileRevisionTypedElement) {
			return ((FileItemNode) input).getImage();
		} else {
			return null;
		}
	}
}

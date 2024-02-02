/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     GitHub - fix for bug 352259
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.mylyn.builds.core.IArtifact;
import org.eclipse.mylyn.commons.workbench.CommonImageManger;
import org.eclipse.mylyn.internal.builds.ui.editor.ArtifactsPart.ArtifactFolder;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.internal.WorkbenchImages;

/**
 * @author Steffen Pingel
 * @author Kevin Sawicki
 */
public class ArtifactsLabelProvider extends LabelProvider implements IStyledLabelProvider {

	final Styler NO_STYLE = new Styler() {
		@Override
		public void applyStyles(TextStyle textStyle) {
		}
	};

	private final CommonImageManger imageManager;

	public ArtifactsLabelProvider() {
		imageManager = new CommonImageManger();
	}

	@Override
	public void dispose() {
		imageManager.dispose();
		super.dispose();
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof ArtifactFolder) {
			return WorkbenchImages.getImage(ISharedImages.IMG_OBJ_FOLDER);
		} else if (element instanceof IArtifact) {
			return imageManager.getFileImage(((IArtifact) element).getName());
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof IArtifact) {
			return ((IArtifact) element).getName();
		}
		if (element instanceof ArtifactFolder) {
			return ((ArtifactFolder) element).getName();
		}
		return super.getText(element);
	}

	@Override
	public StyledString getStyledText(Object element) {
		String text = getText(element);
		if (text != null) {
			StyledString styledString = new StyledString(text);
			return styledString;
		}
		return new StyledString();
	}

}

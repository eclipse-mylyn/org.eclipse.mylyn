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
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.mylyn.builds.core.IChange;
import org.eclipse.mylyn.builds.core.IChangeArtifact;
import org.eclipse.mylyn.builds.core.IUser;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.workbench.CommonImageManger;
import org.eclipse.mylyn.internal.builds.ui.BuildImages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;

/**
 * @author Steffen Pingel
 */
public class ChangesLabelProvider extends LabelProvider implements IStyledLabelProvider {

	final Styler NO_STYLE = new Styler() {
		@Override
		public void applyStyles(TextStyle textStyle) {
		}
	};

	private final CommonImageManger imageManager;

	public ChangesLabelProvider() {
		this.imageManager = new CommonImageManger();
	}

	@Override
	public void dispose() {
		imageManager.dispose();
		super.dispose();
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof IChange) {
			return CommonImages.getImage(BuildImages.CHANGE_SET);
		} else if (element instanceof IChangeArtifact) {
			IChangeArtifact changeArtifact = (IChangeArtifact) element;
			return imageManager.getFileImage(changeArtifact.getFile());
		}
		return null;
	}

	public StyledString getStyledText(Object element) {
		String text = getText(element);
		if (text != null) {
			StyledString styledString = new StyledString(text);
			if (element instanceof IChange) {
				IUser author = ((IChange) element).getAuthor();
				if (author != null && author.getId() != null) {
					styledString.append("  " + author.getId(), StyledString.DECORATIONS_STYLER);
				}
			} else if (element instanceof IChangeArtifact) {
				IChangeArtifact artifact = (IChangeArtifact) element;
				StringBuilder sb = new StringBuilder();
				if (artifact.getRevision() != null) {
					sb.append("  " + artifact.getRevision());
				}
				styledString.append(sb.toString(), StyledString.DECORATIONS_STYLER);
			}
			return styledString;
		}
		return new StyledString();
	}

	@Override
	public String getText(Object element) {
		if (element instanceof IChange) {
			return trim(((IChange) element).getMessage());
		}
		if (element instanceof IChangeArtifact) {
			return ((IChangeArtifact) element).getFile();
		}
		return super.getText(element);
	}

	private String trim(String message) {
		if (message == null) {
			return null;
		}
		int i = message.indexOf("\n"); //$NON-NLS-1$
		if (i != -1) {
			return message.substring(0, i);
		}
		return message;
	}

}

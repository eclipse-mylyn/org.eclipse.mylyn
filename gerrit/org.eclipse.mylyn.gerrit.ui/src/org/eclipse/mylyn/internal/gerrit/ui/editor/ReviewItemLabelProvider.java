/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.editor;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.internal.WorkbenchImages;

/**
 * @author Steffen Pingel
 */
public class ReviewItemLabelProvider extends LabelProvider implements IStyledLabelProvider {

	final Styler NO_STYLE = new Styler() {
		@Override
		public void applyStyles(TextStyle textStyle) {
		}
	};

	public ReviewItemLabelProvider() {
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof IReviewItem) {
			return WorkbenchImages.getImage(ISharedImages.IMG_OBJ_FILE);
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof IReviewItem) {
			return ((IReviewItem) element).getName();
		}
		return super.getText(element);
	}

	public StyledString getStyledText(Object element) {
		String text = getText(element);
		if (text != null) {
			StyledString styledString = new StyledString(text);
			if (element instanceof IFileItem) {
				IFileItem fileItem = (IFileItem) element;
				if (fileItem.getBase() != null && fileItem.getTarget() != null) {
					int i = fileItem.getTopics().size();
					i += fileItem.getBase().getTopics().size();
					i += fileItem.getTarget().getTopics().size();
					if (i > 0) {
						styledString.append(NLS.bind("  [{0} comments]", i), StyledString.DECORATIONS_STYLER);
					}
				}
			}
			return styledString;
		}
		return new StyledString();
	}

}

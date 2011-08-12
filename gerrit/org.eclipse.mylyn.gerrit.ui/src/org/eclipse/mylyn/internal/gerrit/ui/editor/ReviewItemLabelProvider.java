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

import java.util.List;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImageManger;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;

/**
 * @author Steffen Pingel
 */
public class ReviewItemLabelProvider extends LabelProvider implements IStyledLabelProvider {

	final Styler NO_STYLE = new Styler() {
		@Override
		public void applyStyles(TextStyle textStyle) {
		}
	};

	private final CommonImageManger imageManager;

	public ReviewItemLabelProvider() {
		imageManager = new CommonImageManger();
	}

	@Override
	public void dispose() {
		super.dispose();
		imageManager.dispose();
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof IReviewItem) {
			IReviewItem item = (IReviewItem) element;
			return imageManager.getFileImage(item.getName());
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
					Stats stats = new Stats();
					count(stats, fileItem.getTopics());
					count(stats, fileItem.getBase().getTopics());
					count(stats, fileItem.getTarget().getTopics());
					if (stats.comments > 0 && stats.drafts > 0) {
						styledString.append(NLS.bind("  [{0} comments, {1} drafts]", stats.comments, stats.drafts),
								StyledString.DECORATIONS_STYLER);
					} else if (stats.comments > 0) {
						styledString.append(NLS.bind("  [{0} comments]", stats.comments),
								StyledString.DECORATIONS_STYLER);
					} else if (stats.drafts > 0) {
						styledString.append(NLS.bind("  [{0} drafts]", stats.drafts), StyledString.DECORATIONS_STYLER);
					}
				}
			}
			return styledString;
		}
		return new StyledString();
	}

	private class Stats {
		int drafts;

		int comments;
	}

	private void count(Stats stats, List<ITopic> topics) {
		for (ITopic topic : topics) {
			if (topic.isDraft()) {
				stats.drafts++;
			} else {
				stats.comments++;
			}
		}
	}

}

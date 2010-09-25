/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.view;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.mylyn.builds.core.BuildState;
import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.internal.core.BuildServer;
import org.eclipse.mylyn.internal.builds.ui.BuildImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonFonts;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;

/**
 * @author Steffen Pingel
 */
public class BuildLabelProvider extends LabelProvider implements IStyledLabelProvider, IFontProvider {

	final Styler NO_STYLE = new Styler() {
		@Override
		public void applyStyles(TextStyle textStyle) {
		}
	};

	private final boolean decorateOperations;

	public BuildLabelProvider(boolean decorateOperations) {
		this.decorateOperations = decorateOperations;
	}

	public BuildLabelProvider() {
		this(false);
	}

	public Font getFont(Object element) {
		if (!decorateOperations) {
			return null;
		}

		if (element instanceof IBuildElement) {
			if (((IBuildElement) element).getOperations().size() > 0) {
				return CommonFonts.ITALIC;
			}
		}
		return null;
	}

	@Override
	public Image getImage(Object element) {
		ImageDescriptor descriptor = null;
		ImageDescriptor bottomLeftDecoration = null;
		ImageDescriptor bottomRightDecoration = null;

		// bottom left decoration
		if (element instanceof IBuildElement) {
			bottomLeftDecoration = getBottomLeftDecoration((IBuildElement) element);
		}

		// main image
		if (element instanceof IBuildServer) {
			if (((IBuildServer) element).getLocation().isOffline()) {
				descriptor = BuildImages.SERVER_DISABLED;
			} else {
				descriptor = BuildImages.SERVER;
			}
		} else if (element instanceof IBuildPlan) {
			descriptor = getImageDescriptor(((IBuildPlan) element).getStatus());
			bottomRightDecoration = getBottomRightDecoration(((IBuildPlan) element).getState());
		} else if (element instanceof IBuild) {
			descriptor = BuildLabelProvider.getImageDescriptor(((IBuild) element).getStatus());
			bottomRightDecoration = getBottomRightDecoration(((IBuild) element).getState());
		}

		if (descriptor != null) {
			if (bottomRightDecoration != null || bottomLeftDecoration != null) {
				descriptor = new DecorationOverlayIcon(CommonImages.getImage(descriptor), new ImageDescriptor[] { null,
						null, bottomLeftDecoration, bottomRightDecoration });
			}
			return CommonImages.getImage(descriptor);
		}
		return null;
	}

	public static ImageDescriptor getImageDescriptor(BuildStatus status) {
		if (status == BuildStatus.SUCCESS) {
			return BuildImages.STATUS_PASSED;
		} else if (status == BuildStatus.UNSTABLE) {
			return BuildImages.STATUS_UNSTABLE;
		} else if (status == BuildStatus.FAILED) {
			return BuildImages.STATUS_FAILED;
		} else if (status == BuildStatus.DISABLED || status == BuildStatus.ABORTED) {
			return BuildImages.STATUS_DISABLED;
		}
		return CommonImages.QUESTION;
	}

	private ImageDescriptor getBottomLeftDecoration(IBuildElement element) {
		if (element.getElementStatus() != null) {
			return CommonImages.OVERLAY_WARNING;
		}
		return null;
	}

	private ImageDescriptor getBottomRightDecoration(BuildState state) {
		if (state == BuildState.RUNNING) {
			return BuildImages.DECORATION_RUNNING;
		}
		return null;
	}

	public StyledString getStyledText(Object element) {
		String text = getText(element);
		if (text != null) {
			StyledString styledString = new StyledString(text);
			if (element instanceof IBuildServer) {
				styledString.append(" [" + ((BuildServer) element).getLocation().getUrl() + "]",
						StyledString.DECORATIONS_STYLER);
			}
			return styledString;
		}
		return new StyledString();
	}

	@Override
	public String getText(Object element) {
		if (element instanceof IBuildElement) {
			return ((IBuildElement) element).getLabel();
		}
		return null;
	}

}

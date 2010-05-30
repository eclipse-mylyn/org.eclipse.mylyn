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
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.mylyn.builds.core.BuildState;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.internal.builds.core.BuildPlan;
import org.eclipse.mylyn.internal.builds.ui.BuildImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;

/**
 * @author Steffen Pingel
 */
public class BuildLabelProvider extends LabelProvider implements IStyledLabelProvider {

	final Styler NO_STYLE = new Styler() {
		@Override
		public void applyStyles(TextStyle textStyle) {
		}
	};

	@Override
	public Image getImage(Object element) {
		if (element instanceof IBuildPlan) {
			ImageDescriptor descriptor = getImageDescriptor((IBuildPlan) element);
			ImageDescriptor decoration = getDecoration((IBuildPlan) element);
			if (decoration != null) {
				return CommonImages.getImageWithOverlay(descriptor, decoration, false, false);
			}
			return CommonImages.getImage(descriptor);
		}
		if (element instanceof IBuildServer) {
			return CommonImages.getImage(BuildImages.SERVER);
		}
		return null;
	}

	private ImageDescriptor getImageDescriptor(IBuildPlan element) {
		if (element.getHealth() >= 0 && element.getHealth() < 20) {
			return BuildImages.HEALTH_00;
		} else if (element.getHealth() >= 20 && element.getHealth() < 40) {
			return BuildImages.HEALTH_20;
		} else if (element.getHealth() >= 40 && element.getHealth() < 60) {
			return BuildImages.HEALTH_40;
		} else if (element.getHealth() >= 60 && element.getHealth() < 80) {
			return BuildImages.HEALTH_60;
		} else if (element.getHealth() >= 80) {
			return BuildImages.HEALTH_60;
		}
		return CommonImages.FILE_PLAIN;
	}

	private ImageDescriptor getDecoration(IBuildPlan element) {
		if (((BuildPlan) element).getState() == BuildState.RUNNING) {
			return BuildImages.DECORATION_RUNNING;
		}
		return null;
	}

	public StyledString getStyledText(Object element) {
		StyledString styledString = new StyledString(getText(element), NO_STYLE);
		return styledString;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof IBuildElement) {
			return ((IBuildElement) element).getName();
		}
		return null;
	}

}

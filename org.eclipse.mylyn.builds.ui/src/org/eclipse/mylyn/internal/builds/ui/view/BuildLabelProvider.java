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

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.internal.builds.core.BuildPlan;
import org.eclipse.mylyn.internal.builds.core.BuildState;
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
		if (element instanceof BuildPlan) {
			if (((BuildPlan) element).getState() == BuildState.RUNNING) {
				return CommonImages.getImageWithOverlay(BuildImages.STATUS_PASSED, BuildImages.DECORATION_RUNNING,
						false, false);
			}
		}
		if (element instanceof IBuildPlan) {
			return CommonImages.getImage(BuildImages.STATUS_PASSED);
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

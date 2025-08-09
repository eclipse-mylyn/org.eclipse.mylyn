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

package org.eclipse.mylyn.internal.builds.ui.view;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.builds.ui.BuildImages;
import org.eclipse.swt.graphics.Image;

/**
 * @author Steffen Pingel
 */
public class BuildSummaryLabelProvider extends ColumnLabelProvider {

	@Override
	public Image getImage(Object element) {
		if (element instanceof IBuildPlan) {
			ImageDescriptor descriptor = getImageDescriptor((IBuildPlan) element);
			if (descriptor != null) {
				return CommonImages.getImage(descriptor);
			}
		}
		return null;
	}

	private ImageDescriptor getImageDescriptor(IBuildPlan element) {
		int health = element.getHealth();
		return getHealthImageDescriptor(health);
	}

	public static ImageDescriptor getHealthImageDescriptor(int health) {
		if (health >= 0 && health <= 20) {
			return BuildImages.HEALTH_00;
		} else if (health > 20 && health <= 40) {
			return BuildImages.HEALTH_20;
		} else if (health > 40 && health <= 60) {
			return BuildImages.HEALTH_40;
		} else if (health > 60 && health <= 80) {
			return BuildImages.HEALTH_60;
		} else if (health > 80) {
			return BuildImages.HEALTH_80;
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof IBuildPlan) {
			return ((IBuildPlan) element).getSummary();
		}
		if (element instanceof IBuild) {
			return ((IBuild) element).getSummary();
		}
		return null;
	}

}

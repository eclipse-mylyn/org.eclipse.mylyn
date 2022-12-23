/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
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

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.commons.core.DateUtil;

/**
 * @author Steffen Pingel
 */
public class RelativeBuildTimeLabelProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof IBuildPlan) {
			IBuildPlan plan = (IBuildPlan) element;
			if (plan.getLastBuild() != null) {
				return getText(plan.getLastBuild());
			}
		}
		if (element instanceof IBuild) {
			return getText((IBuild) element);
		}
		return ""; //$NON-NLS-1$
	}

	protected String getText(IBuild build) {
		long timestamp = build.getTimestamp();
		if (timestamp != 0) {
			if (System.currentTimeMillis() - timestamp < 60 * 1000) {
				return "< 1 min ago";
			} else {
				return DateUtil.getRelative(timestamp);
			}
		}
		return null;
	}

}

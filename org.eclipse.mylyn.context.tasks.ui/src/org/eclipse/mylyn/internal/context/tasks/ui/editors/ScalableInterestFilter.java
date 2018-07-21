/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.tasks.ui.editors;

import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.InterestFilter;

/**
 * @author Mik Kersten
 */
public class ScalableInterestFilter extends InterestFilter {

	private double threshold = 0;

	public ScalableInterestFilter() {
	}

	public ScalableInterestFilter(IInteractionContext context) {
		super(context);
	}

	@Override
	protected boolean isInteresting(IInteractionElement element) {
		if (element.getInterest().getEvents().isEmpty()) {
			return false;
		} else {
			return element.getInterest().getValue() >= threshold;
		}
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

}

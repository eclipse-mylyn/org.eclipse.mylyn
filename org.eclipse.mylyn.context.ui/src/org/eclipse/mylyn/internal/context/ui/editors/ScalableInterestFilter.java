/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.editors;

import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.InterestFilter;

/**
 * @author Mik Kersten
 */
/**
 * @author Mik Kersten
 */
public class ScalableInterestFilter extends InterestFilter {

	private float threshold = 0;

	@Override
	protected boolean isInteresting(IInteractionElement element) {
		if (element.getInterest().getValue() == 0) {
			// TOD: parametrize default value
			return false;
		} else {
			return element.getInterest().getValue() > threshold;
		}
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

}

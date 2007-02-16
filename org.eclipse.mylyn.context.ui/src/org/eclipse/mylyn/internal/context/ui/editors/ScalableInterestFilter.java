/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.context.ui.editors;

import org.eclipse.mylar.context.core.IMylarElement;
import org.eclipse.mylar.context.ui.InterestFilter;

/**
 * @author Mik Kersten
 */
public class ScalableInterestFilter extends InterestFilter {

	@Override
	protected boolean isInteresting(IMylarElement element) {
		return element.getInterest().getValue() != 0;
	}

}

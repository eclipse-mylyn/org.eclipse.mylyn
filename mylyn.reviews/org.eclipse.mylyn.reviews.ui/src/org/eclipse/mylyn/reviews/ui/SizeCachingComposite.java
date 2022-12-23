/*******************************************************************************
 * Copyright (c) 2009, 2012 Atlassian and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Atlassian - initial API and implementation
 ******************************************************************************/
package org.eclipse.mylyn.reviews.ui;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

/**
 * A composite that can cache its size for faster layout computation
 * 
 * @author Shawn Minto
 */
public class SizeCachingComposite extends Composite {

	private Point cachedSize = null;

	public SizeCachingComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		if (cachedSize == null) {
			cachedSize = super.computeSize(wHint, hHint, changed);
		}
		return cachedSize;
	}

	public void clearCache() {
		cachedSize = null;
	}
}
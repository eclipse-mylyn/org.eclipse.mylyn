/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.splitter;

/**
 * a splitting strategy that causes no splits.
 *
 * @author David Green
 * @since 3.0
 */
public class NoSplittingStrategy extends SplittingStrategy {

	@Override
	public String getSplitTarget() {
		return null;
	}

	@Override
	public void heading(int level, String id, String label) {
	}

	@Override
	public boolean isSplit() {
		return false;
	}

}

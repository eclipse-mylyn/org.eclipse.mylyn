/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.core.parser.builder;

/**
 * a splitting strategy that causes no splits.
 * 
 * @author David Green
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

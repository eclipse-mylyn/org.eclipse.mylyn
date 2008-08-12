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
 * 
 * @author David Green
 */
public class DefaultSplittingStrategy extends SplittingStrategy {

	private int headingCount;

	private int firstHeadingSplit = 2;

	private int headingLevel;

	private int splitLevel = 1;

	private String id;

	@Override
	public void heading(int level, String id) {
		if (level <= 0) {
			return;
		}
		this.id = id;
		this.headingLevel = level;
		++headingCount;
	}

	@Override
	public String getSplitTarget() {
		if (id != null) {
			return id + ".html";
		}
		return "h" + headingLevel + "p" + headingCount + ".html";
	}

	@Override
	public boolean isSplit() {
		return headingCount >= firstHeadingSplit && splitLevel >= headingLevel;
	}

	public int getFirstHeadingSplit() {
		return firstHeadingSplit;
	}

	public void setFirstHeadingSplit(int firstHeadingSplit) {
		this.firstHeadingSplit = firstHeadingSplit;
	}

	public int getSplitLevel() {
		return splitLevel;
	}

	public void setSplitLevel(int splitLevel) {
		this.splitLevel = splitLevel;
	}
}

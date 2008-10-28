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

import java.util.HashSet;
import java.util.Set;

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

	private String label;

	private String target;

	private final Set<String> targets = new HashSet<String>();

	@Override
	public void heading(int level, String id, String label) {
		if (level <= 0) {
			return;
		}
		this.label = label;
		this.id = id;
		this.headingLevel = level;
		++headingCount;
		if (isSplit()) {
			target = computeSplitTarget();
		}
	}

	@Override
	public String getSplitTarget() {
		return target;
	}

	protected String computeSplitTarget() {
		String candidate = null;
		if (candidate == null) {
			if (label != null && label.length() > 0) {
				candidate = label.replaceAll("[^a-zA-Z0-9]+", "-"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (candidate == null || candidate.length() == 0) {
				if (id != null) {
					candidate = id;
				} else {
					candidate = "h" + headingLevel + "p" + headingCount; //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		String computedTarget = candidate;
		int seed = 1;
		while (!targets.add(computedTarget)) {
			computedTarget = candidate + (++seed);
		}
		return computedTarget + ".html"; //$NON-NLS-1$
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

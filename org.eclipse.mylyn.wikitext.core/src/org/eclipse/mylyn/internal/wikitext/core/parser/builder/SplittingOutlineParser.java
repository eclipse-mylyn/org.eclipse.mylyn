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

import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineParser;

/**
 * 
 * @author David Green
 */
public class SplittingOutlineParser extends OutlineParser {
	
	private SplittingStrategy splittingStrategy;
	
	
	@Override
	protected OutlineItem createOutlineItem(OutlineItem current, int level,
			String id, int offset, int length, String label) {
		if (splittingStrategy == null) { 
			throw new IllegalStateException();
		}
		splittingStrategy.heading(level, id);
		SplitOutlineItem outlineItem = new SplitOutlineItem(current,level,id,offset,length,label);
		if (splittingStrategy.isSplit()) {
			outlineItem.setSplitTarget(splittingStrategy.getSplitTarget());
		}
		return outlineItem;
	}

	public SplittingStrategy getSplittingStrategy() {
		return splittingStrategy;
	}

	public void setSplittingStrategy(SplittingStrategy splittingStrategy) {
		this.splittingStrategy = splittingStrategy;
	}
	
	@Override
	public SplitOutlineItem parse(String markup) {
		SplitOutlineItem rootItem = (SplitOutlineItem) super.parse(markup);
		if (!rootItem.getChildren().isEmpty()) {
			SplitOutlineItem firstChild = (SplitOutlineItem) rootItem.getChildren().get(0);
			if (firstChild.getSplitTarget() == null || firstChild.getSplitTarget().equals(rootItem.getSplitTarget())) {
				rootItem.setLabel(firstChild.getLabel());
			} else {
				rootItem.setLabel("");
			}
		}
		return rootItem;
	}
}

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;

/**
 * 
 * @author David Green
 */
public class SplitOutlineItem extends OutlineItem {

	private String splitTarget;
	private Map<String, SplitOutlineItem> outlineItemById;
	private List<SplitOutlineItem> pages;

	public SplitOutlineItem(OutlineItem parent, int level, String id,
			int offset, int length, String label) {
		super(parent, level, id, offset, length, label);
	}

	public void setSplitTarget(String splitTarget) {
		this.splitTarget = splitTarget;
	}

	public String getSplitTarget() {
		if (splitTarget == null && getParent() != null) {
			return getParent().getSplitTarget();
		}
		return splitTarget;
	}
	
	public List<SplitOutlineItem> getPageOrder() {
		if (getParent() != null) {
			return getParent().getPageOrder();
		}
		if (pages == null) {
			final Set<String> pageTargets = new HashSet<String>(); 
			pages = new ArrayList<SplitOutlineItem>();
			accept(new Visitor() {
				public boolean visit(OutlineItem item) {
					SplitOutlineItem split = (SplitOutlineItem)item;
					if (pageTargets.add(split.getSplitTarget())) {
						pages.add(split);
					}
					return true;
				}
			});
		}
		return pages;
	}
	
	@Override
	public SplitOutlineItem getParent() {
		return (SplitOutlineItem) super.getParent();
	}
	
	/**
	 * get the outline item for a given id
	 * 
	 * @param id the id for which the outline item should be returned
	 * 
	 * @return the outline item, or null if the given id is unknown
	 */
	public SplitOutlineItem getOutlineItemById(String id) {
		if (getParent() != null) {
			return getParent().getOutlineItemById(id);
		}
		if (outlineItemById == null) {
			final Map<String,SplitOutlineItem> splitTargetById = new HashMap<String, SplitOutlineItem>();
			this.accept(new Visitor() {
				public boolean visit(OutlineItem item) {
					if (item.getId() != null) {
						if (splitTargetById.containsKey(item.getId())) {
							throw new IllegalStateException(String.format("Duplicate id '%s'",item.getId()));
						}
						splitTargetById.put(item.getId(), (SplitOutlineItem)item);
					}
					return true;
				}
			});
			this.outlineItemById = splitTargetById;
		}
		return outlineItemById.get(id);
	}
}

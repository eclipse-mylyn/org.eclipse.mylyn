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
package org.eclipse.mylyn.internal.wikitext.ui.editor.syntax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 *
 * @author David Green
 */
public class Segments<T extends Segment<?>> {
	private List<T> list;


	@SuppressWarnings("unchecked")
	public void add(T t) {
		if (list == null) {
			list = new ArrayList<T>();
		} else if (list.size() > 0) {
			Segment previousSegment = list.get(list.size()-1);
			final int tOffset = t.getOffset();
			if (previousSegment.getOffset() > tOffset) {
				throw new IllegalArgumentException();
			}
			if (previousSegment.getEndOffset() > tOffset) {
				int newLength = tOffset-previousSegment.getOffset();
				previousSegment.setLength(newLength);
			}
		}
		list.add(t);
	}

	public void remove(T t) {
		list.remove(t);
	}

	public List<T> asList() {
		if (list==null) {
			return Collections.emptyList();
		}
		return list;
	}

	public boolean isEmpty() {
		return list==null || list.isEmpty();
	}

	public int size() {
		return list==null?0:list.size();
	}

}

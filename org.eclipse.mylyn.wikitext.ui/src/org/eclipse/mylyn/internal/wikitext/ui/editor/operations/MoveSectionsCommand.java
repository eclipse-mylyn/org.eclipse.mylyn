/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.editor.operations;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.swt.widgets.Text;

/**
 * A command that moves sections denoted by headings within a document.
 * 
 * @author David Green
 */
public class MoveSectionsCommand extends AbstractDocumentCommand {

	public class OffsetComparator implements Comparator<OutlineItem> {
		public int compare(OutlineItem o1, OutlineItem o2) {
			if (o1 == o2) {
				return 0;
			}
			if (o1.getOffset() < o2.getOffset()) {
				return -1;
			} else if (o1.getOffset() > o2.getOffset()) {
				return 1;
			}
			return 0;
		}
	}

	private final OutlineItem target;

	private final List<OutlineItem> items;

	private final InsertLocation location;

	public MoveSectionsCommand(OutlineItem target, List<OutlineItem> items, InsertLocation location) {
		if (target == null || items == null || location == null) {
			throw new IllegalArgumentException();
		}
		if (items.isEmpty()) {
			throw new IllegalArgumentException();
		}
		this.target = target;
		this.items = items;
		this.location = location;
		validate();
	}

	private void validate() {
		// target must not be contained by items to be moved
		for (int x = 0; x < items.size(); ++x) {
			OutlineItem item = items.get(x);
			if (item.contains(target)) {
				setProblemText(Messages.getString("MoveSectionsCommand.0")); //$NON-NLS-1$
			}
		}
	}

	private static class Section {
		int offset;

		int length;

		public Section(int offset, int length) {
			this.offset = offset;
			this.length = length;
		}

	}

	@Override
	protected void doCommand(IDocument document) throws BadLocationException {
		// prune items to relocate if they are contained within another item that is being
		// relocated.
		for (int x = 0; x < items.size(); ++x) {
			OutlineItem item = items.get(x);

			for (int y = 0; y < x; ++y) {
				OutlineItem previousItem = items.get(y);
				if (previousItem.contains(item)) {
					items.remove(x);
					--x;
					break;
				} else if (item.contains(previousItem)) {
					items.remove(y);
					--x;
					--y;
				}
			}
		}
		if (items.size() > 1) {
			Collections.sort(items, new OffsetComparator());
		}
		final Section[] sections = new Section[items.size()];
		for (int x = 0; x < items.size(); ++x) {
			OutlineItem item = items.get(x);
			sections[x] = new Section(item.getOffset(), item.getSectionLength());
		}
		int insertLocation = target.getOffset();
		if (location == InsertLocation.WITHIN) {
			List<OutlineItem> children = target.getChildren();
			if (children.isEmpty()) {
				insertLocation += target.getLength();
				// insert at the first position found after the header text and any following newlines
				while (insertLocation < document.getLength()) {
					char c = document.getChar(insertLocation);
					if (c == '\r' || c == '\n') {
						++insertLocation;
					} else {
						break;
					}
				}
			} else {
				// In this case we handle within as before the first child.  Even though this may
				// be somewhat non-intuitive, it actually makes sense.  Without this we would see
				// text content after the target heading but before the first child be magically appended
				// to the end of the dropped items -- which in almost all cases is undesirable.
				insertLocation = children.get(0).getOffset();
			}
		} else if (location == InsertLocation.AFTER) {
			// insert afte the section denoted by the heading
			insertLocation += target.getSectionLength();
		}

		String twoNewlines = Text.DELIMITER + Text.DELIMITER;

		for (int x = 0; x < sections.length; ++x) {
			Section section = sections[x];

			String text = document.get(section.offset, section.length);
			document.replace(section.offset, section.length, ""); //$NON-NLS-1$

			// whitespace hack: if the section we're moving doesn't end with a blank line
			// then we add one.
			int adjustmentFactor = 0;
			if (!text.endsWith(twoNewlines)) {
				text += twoNewlines;
				adjustmentFactor = twoNewlines.length();
				section.length += adjustmentFactor;
			}

			if (section.offset < insertLocation) {
				insertLocation -= section.length;
			}
			final int insertedLocation = insertLocation;
			document.replace(insertedLocation, 0, text);
			insertLocation += section.length;

			// any sections that come before the insert location must be shifted since
			// we cut text from there.  Note that sections that follow the insert location need
			// not be adjusted because we first cut and then pasted
			for (int y = x + 1; y < sections.length; ++y) {
				Section section2 = sections[y];

				if (section2.offset < insertedLocation) {
					section2.offset -= section.length;
				} else if (adjustmentFactor > 0) {
					section2.offset += adjustmentFactor;
				}
			}
		}
	}

}

/*******************************************************************************
 * Copyright (c) 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.mylyn.wikitext.core.parser.Locator;

/**
 * This class tracks which link definitions are used, unused, and missing.
 * 
 * @author Stefan Seelmann
 */
public class LinkDefinitionUsageTracker {

	private final Locator locator;

	private final LinkDefinitionParser linkDefinitionParser;

	private final Set<String> usedLinkDefinitionIds;

	private final List<Position> missingLinkDefinitionPositions;

	public LinkDefinitionUsageTracker(Locator locator, LinkDefinitionParser linkDefinitionParser) {
		this.locator = locator;
		this.linkDefinitionParser = linkDefinitionParser;
		usedLinkDefinitionIds = new HashSet<String>();
		missingLinkDefinitionPositions = new ArrayList<LinkDefinitionUsageTracker.Position>();
	}

	public void linkDefinitionRequested(String id) {
		// ignore
		LinkDefinition linkDefinition = linkDefinitionParser.getLinkDefinition(id);
		if (linkDefinition == null) {
			int offset = locator.getDocumentOffset();
			int length = locator.getLineSegmentEndOffset() - locator.getLineCharacterOffset();
			Position position = new Position(id, offset, length);
			missingLinkDefinitionPositions.add(position);
		} else {
			usedLinkDefinitionIds.add(id.toLowerCase());
		}
	}

	public List<Position> getUnusedLinkDefinitionPositions() {
		List<Position> usedLinkDefinitionPositions = new ArrayList<Position>();
		Map<String, LinkDefinition> linkDefinitions = linkDefinitionParser.getLinkDefinitions();
		for (Entry<String, LinkDefinition> entry : linkDefinitions.entrySet()) {
			String id = entry.getKey();
			if (!usedLinkDefinitionIds.contains(id.toLowerCase())) {
				LinkDefinition linkDefinition = entry.getValue();
				Position position = new Position(linkDefinition.getId(), linkDefinition.getOffset(),
						linkDefinition.getLength());
				usedLinkDefinitionPositions.add(position);
			}
		}
		return usedLinkDefinitionPositions;
	}

	public List<Position> getMissingLinkDefinitionPositions() {
		return missingLinkDefinitionPositions;
	}

	public class Position {
		private final String id;

		int offset;

		int length;

		public Position(String id, int offset, int length) {
			this.id = id;
			this.offset = offset;
			this.length = length;
		}

		public String getId() {
			return id;
		}

		public int getOffset() {
			return offset;
		}

		public int getLength() {
			return length;
		}
	}

}

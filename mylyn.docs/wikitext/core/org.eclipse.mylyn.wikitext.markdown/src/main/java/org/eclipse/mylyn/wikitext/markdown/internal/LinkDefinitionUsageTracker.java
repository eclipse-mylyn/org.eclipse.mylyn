/*******************************************************************************
 * Copyright (c) 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.markdown.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.mylyn.wikitext.parser.Locator;

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
		usedLinkDefinitionIds = new HashSet<>();
		missingLinkDefinitionPositions = new ArrayList<>();
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
		List<Position> usedLinkDefinitionPositions = new ArrayList<>();
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

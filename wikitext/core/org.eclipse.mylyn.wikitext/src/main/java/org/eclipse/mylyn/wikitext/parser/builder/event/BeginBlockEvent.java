/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.wikitext.parser.builder.event;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;

/**
 * An {@link DocumentBuilderEvent} corresponding to {@link DocumentBuilder#beginBlock(BlockType, Attributes)}.
 *
 * @author david.green
 * @since 3.0
 */
public class BeginBlockEvent extends DocumentBuilderEvent {

	private final BlockType type;

	private final Attributes attributes;

	public BeginBlockEvent(BlockType type, Attributes attributes) {
		this.type = checkNotNull(type, "Must provide a blockType"); //$NON-NLS-1$
		this.attributes = checkNotNull(attributes, "Must provide attributes").clone(); //$NON-NLS-1$
	}

	@Override
	public void invoke(DocumentBuilder builder) {
		builder.beginBlock(type, attributes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof BeginBlockEvent)) {
			return false;
		}
		BeginBlockEvent other = (BeginBlockEvent) obj;
		return type == other.type;
	}

	@Override
	public String toString() {
		return String.format("beginBlock(%s)", type); //$NON-NLS-1$
	}

}

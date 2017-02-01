/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.parser.builder.event;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

import com.google.common.base.Objects;

/**
 * An {@link DocumentBuilderEvent} corresponding to {@link DocumentBuilder#beginBlock(BlockType, Attributes)}.
 * 
 * @author david.green
 * @since 2.0
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
		return Objects.hashCode(type);
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

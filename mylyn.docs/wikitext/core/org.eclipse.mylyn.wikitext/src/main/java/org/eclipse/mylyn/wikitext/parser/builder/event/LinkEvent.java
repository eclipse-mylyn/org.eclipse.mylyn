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

import java.util.Objects;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;

/**
 * An {@link DocumentBuilderEvent} corresponding to {@link DocumentBuilder#link(Attributes, String, String)}.
 *
 * @author david.green
 * @since 3.0
 */
public class LinkEvent extends DocumentBuilderEvent {

	private final String hrefOrHashName;

	private final String text;

	private final Attributes attributes;

	public LinkEvent(Attributes attributes, String hrefOrHashName, String text) {
		this.attributes = Objects.requireNonNull(attributes, "Must provide attributes").clone(); //$NON-NLS-1$
		this.hrefOrHashName = Objects.requireNonNull(hrefOrHashName, "Must provide hrefOrHashName"); //$NON-NLS-1$
		this.text = Objects.requireNonNull(text, "Must provide text"); //$NON-NLS-1$
	}

	@Override
	public void invoke(DocumentBuilder builder) {
		builder.link(attributes, hrefOrHashName, text);
	}

	@Override
	public int hashCode() {
		return Objects.hash(hrefOrHashName, text);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof LinkEvent)) {
			return false;
		}
		LinkEvent other = (LinkEvent) obj;
		return Objects.equals(hrefOrHashName, other.hrefOrHashName) && Objects.equals(text, other.text);
	}

	@Override
	public String toString() {
		return String.format("link(%s,\"%s\")", hrefOrHashName, text); //$NON-NLS-1$
	}
}

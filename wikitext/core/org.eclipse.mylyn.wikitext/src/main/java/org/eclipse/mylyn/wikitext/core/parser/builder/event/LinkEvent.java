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

import com.google.common.base.Objects;

/**
 * An {@link DocumentBuilderEvent} corresponding to {@link DocumentBuilder#link(Attributes, String, String)}.
 * 
 * @author david.green
 * @since 2.0
 */
public class LinkEvent extends DocumentBuilderEvent {

	private final String hrefOrHashName;

	private final String text;

	private final Attributes attributes;

	public LinkEvent(Attributes attributes, String hrefOrHashName, String text) {
		this.attributes = checkNotNull(attributes, "Must provide attributes").clone(); //$NON-NLS-1$
		this.hrefOrHashName = checkNotNull(hrefOrHashName, "Must provide hrefOrHashName"); //$NON-NLS-1$
		this.text = checkNotNull(text, "Must provide text"); //$NON-NLS-1$
	}

	@Override
	public void invoke(DocumentBuilder builder) {
		builder.link(attributes, hrefOrHashName, text);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(hrefOrHashName, text);
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
		return Objects.equal(hrefOrHashName, other.hrefOrHashName) && Objects.equal(text, other.text);
	}

	@Override
	public String toString() {
		return String.format("link(%s,\"%s\")", hrefOrHashName, text); //$NON-NLS-1$
	}
}

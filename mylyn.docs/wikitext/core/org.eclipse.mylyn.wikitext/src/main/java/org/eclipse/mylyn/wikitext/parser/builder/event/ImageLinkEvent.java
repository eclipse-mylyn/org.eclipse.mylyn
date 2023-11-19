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
 * An {@link DocumentBuilderEvent} corresponding to
 * {@link DocumentBuilder#imageLink(Attributes, Attributes, String, String)}.
 *
 * @author david.green
 * @since 3.0
 */
public class ImageLinkEvent extends DocumentBuilderEvent {

	private final Attributes linkAttributes;

	private final String href;

	private final Attributes imageAttributes;

	private final String imageUrl;

	public ImageLinkEvent(Attributes linkAttributes, Attributes imageAttributes, String href, String imageUrl) {
		this.linkAttributes = Objects.requireNonNull(linkAttributes, "Must provide linkAttributes").clone(); //$NON-NLS-1$
		this.imageAttributes = Objects.requireNonNull(imageAttributes, "Must provide imageAttributes").clone(); //$NON-NLS-1$
		this.href = Objects.requireNonNull(href, "Must provide link href"); //$NON-NLS-1$
		this.imageUrl = Objects.requireNonNull(imageUrl, "Must provide imageUrl"); //$NON-NLS-1$
	}

	@Override
	public void invoke(DocumentBuilder builder) {
		builder.imageLink(linkAttributes, imageAttributes, href, imageUrl);
	}

	@Override
	public int hashCode() {
		return Objects.hash(href, imageUrl);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof ImageLinkEvent)) {
			return false;
		}
		ImageLinkEvent other = (ImageLinkEvent) obj;
		return Objects.equals(href, other.href) && Objects.equals(imageUrl, other.imageUrl);
	}

	@Override
	public String toString() {
		return String.format("imageLink(%s,%s)", href, imageUrl); //$NON-NLS-1$
	}

}

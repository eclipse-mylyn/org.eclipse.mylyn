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
 * An {@link DocumentBuilderEvent} corresponding to
 * {@link DocumentBuilder#imageLink(Attributes, Attributes, String, String)}.
 * 
 * @author david.green
 * @since 2.0
 */
public class ImageLinkEvent extends DocumentBuilderEvent {

	private final Attributes linkAttributes;

	private final String href;

	private final Attributes imageAttributes;

	private final String imageUrl;

	public ImageLinkEvent(Attributes linkAttributes, Attributes imageAttributes, String href, String imageUrl) {
		this.linkAttributes = checkNotNull(linkAttributes, "Must provide linkAttributes").clone(); //$NON-NLS-1$
		this.imageAttributes = checkNotNull(imageAttributes, "Must provide imageAttributes").clone(); //$NON-NLS-1$
		this.href = checkNotNull(href, "Must provide link href"); //$NON-NLS-1$
		this.imageUrl = checkNotNull(imageUrl, "Must provide imageUrl"); //$NON-NLS-1$
	}

	@Override
	public void invoke(DocumentBuilder builder) {
		builder.imageLink(linkAttributes, imageAttributes, href, imageUrl);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(href, imageUrl);
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
		return Objects.equal(href, other.href) && Objects.equal(imageUrl, other.imageUrl);
	}

	@Override
	public String toString() {
		return String.format("imageLink(%s,%s)", href, imageUrl); //$NON-NLS-1$
	}

}

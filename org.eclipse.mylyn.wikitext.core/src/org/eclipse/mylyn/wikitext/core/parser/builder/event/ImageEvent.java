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
 * An {@link DocumentBuilderEvent} corresponding to {@link DocumentBuilder#image(Attributes, String)}.
 * 
 * @author david.green
 * @since 2.0
 */
public class ImageEvent extends DocumentBuilderEvent {

	private final String url;

	private final Attributes attributes;

	public ImageEvent(Attributes attributes, String url) {
		this.url = checkNotNull(url, "Must provide an url"); //$NON-NLS-1$
		this.attributes = checkNotNull(attributes, "Must provide attributes").clone(); //$NON-NLS-1$
	}

	@Override
	public void invoke(DocumentBuilder builder) {
		builder.image(attributes, url);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(url);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof ImageEvent)) {
			return false;
		}
		return Objects.equal(url, ((ImageEvent) obj).url);
	}

	@Override
	public String toString() {
		return String.format("image(%s)", url); //$NON-NLS-1$
	}
}

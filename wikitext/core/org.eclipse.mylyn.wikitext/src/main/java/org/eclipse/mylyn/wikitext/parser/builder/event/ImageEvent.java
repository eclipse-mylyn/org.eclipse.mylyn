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

/**
 * An {@link DocumentBuilderEvent} corresponding to {@link DocumentBuilder#image(Attributes, String)}.
 *
 * @author david.green
 * @since 3.0
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
		return Objects.hash(url);
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
		return Objects.equals(url, ((ImageEvent) obj).url);
	}

	@Override
	public String toString() {
		return String.format("image(%s)", url); //$NON-NLS-1$
	}
}

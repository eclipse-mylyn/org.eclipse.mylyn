/*******************************************************************************
 * Copyright (c) 2013, 2022 Tasktop Technologies and others.
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

import static java.text.MessageFormat.format;

import java.util.List;
import java.util.Objects;

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.EventDocumentBuilder;

/**
 * Captures the result of a {@link EventDocumentBuilder} as a series of {@link DocumentBuilderEvent events}.
 *
 * @author david.green
 * @see EventDocumentBuilder
 * @noextend This class is not intended to be subclassed by clients.
 * @since 3.0
 */
public class DocumentBuilderEvents {
	private final List<DocumentBuilderEvent> events;

	public DocumentBuilderEvents(List<DocumentBuilderEvent> events) {
		this.events = List.copyOf(Objects.requireNonNull(events, "Must provide events")); //$NON-NLS-1$
	}

	public List<DocumentBuilderEvent> getEvents() {
		return events;
	}

	/**
	 * Invokes the {@link #getEvents() events} on the given {@code builder}.
	 *
	 * @param builder
	 *            the builder
	 */
	public void applyTo(DocumentBuilder builder) {
		Objects.requireNonNull(builder, "Must provide a builder"); //$NON-NLS-1$
		for (DocumentBuilderEvent event : events) {
			event.invoke(builder);
		}
	}

	@Override
	public String toString() {
		return format("{0} [events={1}]", events); //$NON-NLS-1$
	}
}

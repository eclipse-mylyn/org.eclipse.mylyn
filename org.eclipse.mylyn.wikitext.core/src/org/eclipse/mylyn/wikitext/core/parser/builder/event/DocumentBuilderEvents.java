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

import java.util.List;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.EventDocumentBuilder;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

/**
 * Captures the result of a {@link EventDocumentBuilder} as a series of {@link DocumentBuilderEvent events}.
 * 
 * @author david.green
 * @since 2.0
 * @see EventDocumentBuilder
 * @noextend This class is not intended to be subclassed by clients.
 */
public class DocumentBuilderEvents {
	private final List<DocumentBuilderEvent> events;

	public DocumentBuilderEvents(List<DocumentBuilderEvent> events) {
		this.events = ImmutableList.copyOf(checkNotNull(events, "Must provide events")); //$NON-NLS-1$
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
		checkNotNull(builder, "Must provide a builder"); //$NON-NLS-1$
		for (DocumentBuilderEvent event : events) {
			event.invoke(builder);
		}
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("events", events).toString(); //$NON-NLS-1$
	}
}

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

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.EventDocumentBuilder;

/**
 * Represents an invocation of a {@link DocumentBuilder} captured as an event which can be {@link #invoke(DocumentBuilder) applied}.
 *
 * @author david.green
 * @see EventDocumentBuilder
 * @see DocumentBuilderEvents
 * @noextend This class is not intended to be subclassed by clients.
 * @since 3.0
 */
public abstract class DocumentBuilderEvent {

	/**
	 * Invokes the event on the given {@code builder}.
	 *
	 * @param builder
	 *            the builder
	 */
	public abstract void invoke(DocumentBuilder builder);

}

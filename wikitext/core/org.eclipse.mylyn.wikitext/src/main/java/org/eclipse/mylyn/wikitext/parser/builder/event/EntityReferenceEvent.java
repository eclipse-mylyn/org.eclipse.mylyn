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

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;

/**
 * An {@link DocumentBuilderEvent} corresponding to {@link DocumentBuilder#entityReference(String)}.
 *
 * @author david.green
 * @since 3.0
 */
public class EntityReferenceEvent extends DocumentBuilderEvent {

	private final String entity;

	public EntityReferenceEvent(String entity) {
		this.entity = Objects.requireNonNull(entity, "Must provide entity"); //$NON-NLS-1$
	}

	@Override
	public void invoke(DocumentBuilder builder) {
		builder.entityReference(entity);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(entity);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof EntityReferenceEvent)) {
			return false;
		}
		return Objects.equals(entity, ((EntityReferenceEvent) obj).entity);
	}

	@Override
	public String toString() {
		return String.format("entityReference(\"%s\")", entity); //$NON-NLS-1$
	}
}

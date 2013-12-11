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

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

import com.google.common.base.Objects;

/**
 * An {@link DocumentBuilderEvent} corresponding to {@link DocumentBuilder#entityReference(String)}.
 * 
 * @author david.green
 * @since 2.0
 */
public class EntityReferenceEvent extends DocumentBuilderEvent {

	private final String entity;

	public EntityReferenceEvent(String entity) {
		this.entity = checkNotNull(entity, "Must provide entity"); //$NON-NLS-1$
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
		return Objects.equal(entity, ((EntityReferenceEvent) obj).entity);
	}

	@Override
	public String toString() {
		return String.format("entityReference(\"%s\")", entity); //$NON-NLS-1$
	}
}

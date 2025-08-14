/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

import org.eclipse.mylyn.context.core.IDegreeOfInterest;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.IInteractionRelation;

/**
 * TODO: make immutable?
 *
 * @author Mik Kersten
 */
public class InteractionContextRelation implements IInteractionRelation {

	private final DegreeOfInterest interest;

	private final String structureKind;

	private final String relationshipHandle;

	private final IInteractionElement source;

	private final IInteractionElement target;

	public InteractionContextRelation(String kind, String edgeKind, IInteractionElement source,
			IInteractionElement target, InteractionContext context) {
		interest = new DegreeOfInterest(context, context.getScaling());
		structureKind = kind;
		relationshipHandle = edgeKind;
		this.target = target;
		this.source = source;
	}

	@Override
	public IInteractionElement getTarget() {
		return target;
	}

	@Override
	public IDegreeOfInterest getInterest() {
		return interest;
	}

	@Override
	public String toString() {
		return "(rel: " + relationshipHandle + ", source: " + source.getHandleIdentifier() + ", target: " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ target.getHandleIdentifier() + ")"; //$NON-NLS-1$
	}

	@Override
	public String getLabel() {
		return toString();
	}

	@Override
	public String getRelationshipHandle() {
		return relationshipHandle;
	}

	@Override
	public String getContentType() {
		return structureKind;
	}

	@Override
	public IInteractionElement getSource() {
		return source;
	}
}

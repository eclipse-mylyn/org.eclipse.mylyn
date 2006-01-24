/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.core.internal;

import org.eclipse.mylar.core.IDegreeOfInterest;
import org.eclipse.mylar.core.IMylarRelation;
import org.eclipse.mylar.core.IMylarElement;

/**
 * TODO: make immutable?
 * 
 * @author Mik Kersten
 */
public class MylarContextRelation implements IMylarRelation {

	private DegreeOfInterest interest;

	private String structureKind;

	private String relationshipHandle;

	private IMylarElement source;

	private IMylarElement target;

	public MylarContextRelation(String kind, String edgeKind, IMylarElement source, IMylarElement target,
			MylarContext context) {
		interest = new DegreeOfInterest(context, MylarContextManager.getScalingFactors());
		this.structureKind = kind;
		this.relationshipHandle = edgeKind;
		this.target = target;
		this.source = source;
	}

	public IMylarElement getTarget() {
		return target;
	}

	public IDegreeOfInterest getInterest() {
		return interest;
	}

	@Override
	public String toString() {
		return "(rel: " + relationshipHandle + ", source: " + source.getHandleIdentifier() + ", target: "
				+ target.getHandleIdentifier() + ")";
	}

	public String getLabel() {
		return toString();
	}

	public String getRelationshipHandle() {
		return relationshipHandle;
	}

	public String getContentType() {
		return structureKind;
	}

	public IMylarElement getSource() {
		return source;
	}
}

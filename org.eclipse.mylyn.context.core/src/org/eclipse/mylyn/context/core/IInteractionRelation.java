/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.context.core;

/**
 * Virtual proxy for a relation between two elements in the context model.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public interface IInteractionRelation extends IInteractionObject {

	public abstract String getLabel();

	public abstract String getRelationshipHandle();

	public abstract IInteractionElement getTarget();

	public abstract IInteractionElement getSource();

}

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

package org.eclipse.mylyn.context.core;

import java.util.Collection;

import org.eclipse.mylyn.internal.context.core.InteractionContextRelation;

/**
 * Virtual proxy for a structured element in the contet model.
 * 
 * @author Mik Kersten
 * @since	2.0
 */
public interface IInteractionElement extends IInteractionObject {

	public abstract String getHandleIdentifier();

	public abstract void setHandleIdentifier(String handle);

	public abstract IInteractionContext getContext();

	public abstract Collection<InteractionContextRelation> getRelations();

	public abstract InteractionContextRelation getRelation(String targetHandle);

	public abstract void clearRelations();
}
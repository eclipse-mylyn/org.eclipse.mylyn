/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.core;

import java.util.Collection;

import org.eclipse.mylyn.internal.context.core.InteractionContextRelation;

/**
 * Virtual proxy for a structured element in the contet model.
 * 
 * @author Mik Kersten
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IInteractionElement extends IInteractionObject {

	String getHandleIdentifier();

	void setHandleIdentifier(String handle);

	IInteractionContext getContext();

	Collection<InteractionContextRelation> getRelations();

	/**
	 * @since 3.0
	 */
	IInteractionRelation getRelation(String targetHandle);

	void clearRelations();
}

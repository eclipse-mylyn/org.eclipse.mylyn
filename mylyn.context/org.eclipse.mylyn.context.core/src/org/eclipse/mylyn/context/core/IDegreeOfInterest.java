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

import java.util.List;

import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * Abstraction for capturing the interest level of elements and relations based on interaction events.
 * 
 * @author Mik Kersten
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IDegreeOfInterest {

	boolean isPropagated();

	boolean isPredicted();

	boolean isLandmark();

	boolean isInteresting();

	float getEncodedValue();

	float getDecayValue();

	float getValue();

	List<InteractionEvent> getEvents();
}

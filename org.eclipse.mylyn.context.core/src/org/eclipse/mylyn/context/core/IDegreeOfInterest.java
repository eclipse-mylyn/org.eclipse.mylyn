/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.context.core;

import java.util.List;

import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * Abstraction for capturing the interest level of elements and relations based on interaction events.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public interface IDegreeOfInterest {

	public abstract boolean isPropagated();

	public boolean isPredicted();

	public abstract boolean isLandmark();

	public abstract boolean isInteresting();

	public abstract float getEncodedValue();

	public float getDecayValue();

	public abstract float getValue();

	public abstract List<InteractionEvent> getEvents();
}

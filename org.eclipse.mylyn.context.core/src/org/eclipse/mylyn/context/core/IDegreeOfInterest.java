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

import java.util.List;

import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
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

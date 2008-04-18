/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.context.core;

import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 * @since 3.0
 */
public interface IInteractionContextScaling {

	public abstract float get(InteractionEvent.Kind kind);

	public abstract float getDecay();

	public abstract float getInteresting();

	public abstract float getLandmark();

	public abstract float getForcedLandmark();

	@Deprecated
	public abstract int getMaxNumInterestingErrors();

	@Deprecated
	public abstract float getErrorInterest();

}
/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.monitor.core;

/**
 * Notified of interaction events and the logging lifecycle.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public interface IInteractionEventListener {

	public abstract void interactionObserved(InteractionEvent event);

	public abstract void startMonitoring();

	public abstract void stopMonitoring();
}

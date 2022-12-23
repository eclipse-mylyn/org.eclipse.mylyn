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

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

package org.eclipse.mylyn.internal.monitor.core.collection;

import java.util.Set;

import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * A usage scanner will see all events for a user before any consumers
 * 
 * @author Gail Murphy
 */
public interface IUsageScanner {

	public void scanEvent(InteractionEvent event, int userId);

	public boolean accept(int userId);

	public Set<Integer> acceptedUsers();

}

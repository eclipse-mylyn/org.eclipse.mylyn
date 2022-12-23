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

import java.util.Comparator;

import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * Comparator of InteractionEvents
 * 
 * @author Gail Murphy
 */
public class InteractionEventComparator implements Comparator<InteractionEvent> {

	public int compare(InteractionEvent arg0, InteractionEvent arg1) {
		if (arg0.equals(arg1)) {
			return 0;
		}
		if (arg0.getDate().before(arg1.getDate())) {
			return -1;
		}
		return 1;
	}
}

/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.actions;

/**
 * @author Mik Kersten
 */
// TODO 3.9 rename to RemoveFromContextAction?
public class InterestDecrementAction extends AbstractInterestManipulationAction {

	@Override
	protected boolean isIncrement() {
		return false;
	}
}

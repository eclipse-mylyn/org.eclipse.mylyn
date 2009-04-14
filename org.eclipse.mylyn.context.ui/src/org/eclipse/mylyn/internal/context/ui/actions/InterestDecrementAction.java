/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.actions;

/**
 * @author Mik Kersten
 */
// TODO 3.2 rename to RemoveFromContextAction?
public class InterestDecrementAction extends AbstractInterestManipulationAction {

	@Override
	protected boolean isIncrement() {
		return false;
	}
}

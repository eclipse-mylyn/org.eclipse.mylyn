/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import org.eclipse.mylyn.commons.workbench.SubstringPatternFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * The default implementation of a search handler
 * 
 * @author David Green
 * @see SubstringPatternFilter
 */
public class DefaultSearchHandler extends AbstractSearchHandler {

	@Override
	public PatternFilter createFilter() {
		return new SubstringPatternFilter();
	}

	@Override
	public Composite createSearchComposite(Composite container) {
		// no additional controls
		return null;
	}

	@Override
	public void dispose() {
		// nothing to do
	}

}

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

package org.eclipse.mylyn.internal.tasks.bugs;

import org.eclipse.mylyn.internal.provisional.tasks.bugs.IProvider;

/**
 * @author Steffen Pingel
 */
public class SupportProvider extends AbstractSupportElement implements IProvider {

	SupportCategory category;

	public SupportProvider() {
	}

	public SupportCategory getCategory() {
		return category;
	}

	public void setCategory(SupportCategory category) {
		this.category = category;
	}

}

/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.externalization;

import org.xml.sax.helpers.AttributesImpl;

public class AttributesWrapper {

	private final AttributesImpl attributes;

	public AttributesWrapper() {
		this.attributes = new AttributesImpl();
	}

	public void addAttribute(String key, String value) {
		if (key != null && value != null) {
			attributes.addAttribute("", key, key, "", value); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public AttributesImpl getAttributes() {
		return attributes;
	}

}

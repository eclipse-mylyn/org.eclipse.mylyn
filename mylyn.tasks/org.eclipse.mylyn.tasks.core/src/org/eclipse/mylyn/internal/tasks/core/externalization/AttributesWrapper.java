/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
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

/*******************************************************************************
 * Copyright (c) 2011, 2024 Tom Seidel, Remus Software and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *
 * Contributors:
 *     Tom Seidel - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/
package org.eclipse.mylyn.htmltext.configuration;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author Tom Seidel <tom.seidel@remus-software.org>
 * @since 0.9
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class ConfigurationElement {

	private String ckConfigElement;
	protected Object value;

	protected ConfigurationElement(String ckConfigElement, Object value) {
		this.ckConfigElement = ckConfigElement;
		this.value = value;
	}

	public String getCkConfigElement() {
		return ckConfigElement;
	}

	protected abstract Object doGetDefaultValue();

	public final Object getDefaultValue() {
		return doGetDefaultValue();
	}

	public String toQuery() {
		StringBuilder sb = new StringBuilder();
		if (value != null && !value.equals(getDefaultValue())) {
			String valueForEditor = URLEncoder.encode(getValueForEditor(), StandardCharsets.UTF_8);
			if (valueForEditor != null) {
				sb.append(ckConfigElement).append("=").append(valueForEditor); //$NON-NLS-1$
			}
		}
		return sb.toString();
	}

	public String getValueForEditor() {
		return this.value == null ? null : value.toString();
	}

}

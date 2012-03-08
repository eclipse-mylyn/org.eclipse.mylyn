/*******************************************************************************
 * Copyright (c) 2011 Tom Seidel, Remus Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *
 * Contributors:
 *     Tom Seidel - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.htmltext.configuration;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
		StringBuffer sb = new StringBuffer();
		if (value != null && !value.equals(getDefaultValue())) {
			String valueForEditor;
			try {
				valueForEditor = URLEncoder
						.encode(getValueForEditor(), "UTF-8");
				if (valueForEditor != null) {
					sb.append(ckConfigElement).append("=")
							.append(valueForEditor);
				}
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException(e);
			}
		}
		return sb.toString();
	}

	public String getValueForEditor() {
		return this.value == null ? null : value.toString();
	}

}

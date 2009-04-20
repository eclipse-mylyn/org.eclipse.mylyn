/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.osgi.util.NLS;

/**
 * A resource bundle based on {@link NLS}.
 * 
 * @author dgreen
 */
public class NlsResourceBundle extends ResourceBundle {

	private final Class<? extends NLS> nlsClass;

	public NlsResourceBundle(Class<? extends NLS> nlsClass) {
		this.nlsClass = nlsClass;
	}

	@Override
	public Enumeration<String> getKeys() {
		Set<String> keys = new HashSet<String>();
		for (Field field : nlsClass.getFields()) {
			if (field.getType() == String.class) {
				if (Modifier.isStatic(field.getModifiers())) {
					keys.add(field.getName());
				}
			}
		}
		return Collections.enumeration(keys);
	}

	@Override
	protected Object handleGetObject(String key) {
		try {
			Field field = nlsClass.getField(key);
			field.setAccessible(true);
			return field.get(null);
		} catch (Exception e) {
			return null;
		}
	}

}

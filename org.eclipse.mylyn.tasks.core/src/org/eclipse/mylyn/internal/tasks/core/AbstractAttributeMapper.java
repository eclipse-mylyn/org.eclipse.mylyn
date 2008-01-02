/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractAttributeMapper {

	private final AbstractAttributeFactory attributeFactory;

	public AbstractAttributeMapper(AbstractAttributeFactory attributeFactory) {
		this.attributeFactory = attributeFactory;
	}

	public String mapToRepositoryKey(String key) {
		return attributeFactory.mapCommonAttributeKey(key);
	}

	public Date getDateValue(RepositoryTaskAttribute attribute) {
		String dateString = attribute.getValue();
		try {
			if (dateString != null && dateString.length() > 0) {
				return new Date(Long.parseLong(dateString));
			}
		} catch (NumberFormatException e) {
			// ignore
		}
		return null;
	}

	public void setDateValue(RepositoryTaskAttribute attribute, Date date) {
		attribute.setValue(Long.toString(date.getTime()));
	}

	public abstract Map<String, String> getOptions(RepositoryTaskAttribute attribute);

	public void setValue(RepositoryTaskAttribute attribute, String value) {
		attribute.setValue(value);
	}

	public void setValues(RepositoryTaskAttribute attribute, String[] values) {
		attribute.setValues(Arrays.asList(values));
	}

	public String[] getValues(RepositoryTaskAttribute attribute) {
		return attribute.getValues().toArray(new String[0]);
	}

}

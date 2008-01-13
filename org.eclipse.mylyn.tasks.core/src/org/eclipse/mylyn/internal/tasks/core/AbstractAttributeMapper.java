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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;

/**
 * @author Steffen Pingel
 */
// TODO EDITOR return null if attribute value invalid for primitive types? 
public abstract class AbstractAttributeMapper {

	private final AbstractAttributeFactory attributeFactory;

	public AbstractAttributeMapper(AbstractAttributeFactory attributeFactory) {
		this.attributeFactory = attributeFactory;
	}

	public String mapToRepositoryKey(String key) {
		return attributeFactory.mapCommonAttributeKey(key);
	}

	public boolean getBooleanValue(RepositoryTaskAttribute attribute) {
		String booleanString = attribute.getValue();
		if (booleanString != null && booleanString.length() > 0) {
			return Boolean.parseBoolean(booleanString);
		}
		return false;
	}

	public void setBooleanValue(RepositoryTaskAttribute attribute, Boolean value) {
		attribute.setValue(Boolean.toString(value));
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

	/**
	 * Returns labelByValue.
	 */
	public Map<String, String> getOptions(RepositoryTaskAttribute attribute) {
		List<String> options = attribute.getOptions();
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (String option : options) {
			map.put(option, option);
		}
		return map;
	}

	public void setValue(RepositoryTaskAttribute attribute, String value) {
		attribute.setValue(value);
	}

	public void setValues(RepositoryTaskAttribute attribute, String[] values) {
		attribute.setValues(Arrays.asList(values));
	}

	public String[] getValues(RepositoryTaskAttribute attribute) {
		return attribute.getValues().toArray(new String[0]);
	}

	public String getValue(RepositoryTaskAttribute taskAttribute) {
		return taskAttribute.getValue();
	}

	public String getLabel(RepositoryTaskAttribute taskAttribute) {
		return taskAttribute.getName();
	}

	public String getValueLabel(RepositoryTaskAttribute taskAttribute) {
		StringBuilder sb = new StringBuilder();
		String sep = "";
		for (String key : taskAttribute.getValues()) {
			sb.append(sep).append(key);
			sep = ", ";
		}
		return sb.toString();
	}

	public String[] getValueLabels(RepositoryTaskAttribute taskAttribute) {
		return taskAttribute.getValues().toArray(new String[0]);
	}

}

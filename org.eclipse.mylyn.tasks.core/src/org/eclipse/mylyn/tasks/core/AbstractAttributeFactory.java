/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.io.Serializable;
import java.util.Date;

/**
 * Extend to provide mapping between repository task attribute IDs and native repository IDs.
 * 
 * @author Rob Elves
 * @since 2.0
 */
@SuppressWarnings("serial")
public abstract class AbstractAttributeFactory implements Serializable {

	public RepositoryTaskAttribute createAttribute(String key) {
		String mapped = mapCommonAttributeKey(key);
		RepositoryTaskAttribute attribute = new RepositoryTaskAttribute(mapped, getName(mapped), isHidden(mapped));
		attribute.setReadOnly(isReadOnly(mapped));
		return attribute;
	}

	public abstract String mapCommonAttributeKey(String key);

	public abstract String getName(String key);

	public abstract boolean isReadOnly(String key);

	public abstract boolean isHidden(String key);

	/**
	 * @return null if date cannot be parsed
	 */
	public abstract Date getDateForAttributeType(String attributeKey, String dateString);
}

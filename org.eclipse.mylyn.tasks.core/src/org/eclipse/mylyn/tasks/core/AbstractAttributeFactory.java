/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.io.Serializable;
import java.util.Date;


/**
 * @author Rob Elves
 */
public abstract class AbstractAttributeFactory implements Serializable {

	public RepositoryTaskAttribute createAttribute(String key) {
		String mapped = mapCommonAttributeKey(key);
		RepositoryTaskAttribute attribute = new RepositoryTaskAttribute(mapped, getName(mapped), getIsHidden(mapped));
		attribute.setReadOnly(isReadOnly(mapped));
		return attribute;
	}
	
	public abstract String mapCommonAttributeKey(String key);
	
	public abstract boolean getIsHidden(String key);

	public abstract String getName(String key);
		
	public abstract boolean isReadOnly(String key);
	
	/**
	 * @return null if date cannot be parsed
	 */
	public abstract Date getDateForAttributeType(String attributeKey, String dateString);
}

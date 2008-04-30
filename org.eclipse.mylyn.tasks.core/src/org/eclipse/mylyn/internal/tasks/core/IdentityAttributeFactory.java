/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.util.Date;

import org.eclipse.mylyn.tasks.core.AbstractAttributeFactory;

/**
 * @since 3.0
 * @author Steffen Pingel
 */
@Deprecated
public class IdentityAttributeFactory extends AbstractAttributeFactory {

	private static final IdentityAttributeFactory INSTANCE = new IdentityAttributeFactory();

	private static final long serialVersionUID = 1L;

	public static AbstractAttributeFactory getInstance() {
		return INSTANCE;
	}

	private IdentityAttributeFactory() {
	}

	@Override
	public Date getDateForAttributeType(String attributeKey, String dateString) {
		return null;
	}

	@Override
	public String getName(String key) {
		return null;
	}

	@Override
	public boolean isHidden(String key) {
		return false;
	}

	@Override
	public boolean isReadOnly(String key) {
		return false;
	}

	@Override
	public String mapCommonAttributeKey(String key) {
		return key;
	}

}

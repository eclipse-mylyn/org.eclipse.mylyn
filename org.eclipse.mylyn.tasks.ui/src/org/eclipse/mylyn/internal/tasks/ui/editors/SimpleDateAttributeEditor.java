/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.Date;

import org.eclipse.mylyn.monitor.core.DateUtil;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;

class SimpleDateAttributeEditor extends TextAttributeEditor {

	static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";

	public SimpleDateAttributeEditor(AttributeManager manager, RepositoryTaskAttribute taskAttribute) {
		super(manager, taskAttribute);
		setDecorationEnabled(false);
	}

	@Override
	public String getValue() {
		Date date = getAttributeMapper().getDateValue(getTaskAttribute());
		if (date != null) {
			return DateUtil.getFormattedDate(date, DATE_FORMAT);
		}
		return "";
	}

	@Override
	protected boolean isReadOnly() {
		return true;
	}

	@Override
	public void setValue(String text) {
		throw new UnsupportedOperationException();
	}

}
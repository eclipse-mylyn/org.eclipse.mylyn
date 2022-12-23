/*******************************************************************************
 * Copyright (c) 2011 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.search;

import java.io.Serializable;

public class ChartExpression implements Serializable {

	private static final long serialVersionUID = 6211236042329085161L;

	private int fieldName;

	private int operation;

	private String value;

	public ChartExpression(int fieldName, int operation, String value) {
		super();
		this.fieldName = fieldName;
		this.operation = operation;
		this.value = value;
	}

	public int getFieldName() {
		return fieldName;
	}

	public void setFieldName(int fieldName) {
		this.fieldName = fieldName;
	}

	public int getOperation() {
		return operation;
	}

	public void setOperation(int operation) {
		this.operation = operation;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}

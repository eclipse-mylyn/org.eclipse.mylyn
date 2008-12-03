/*******************************************************************************
* Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a search criterion. Each criterion is applied to a field such as milestone or priority. It has a compare
 * operator and a list of values. The compare mode is <code>OR</code> for the operators <code>contains</code>,
 * <code>starts with</code>, <code>ends with</code> and <code>is</code>. The compare mode is <code>AND</code> for all
 * other (negated) operators.
 * 
 * @author Steffen Pingel
 */
public class TracSearchFilter {

	public enum CompareOperator {
		CONTAINS("~"), CONTAINS_NOT("!~"), BEGINS_WITH("^"), NOT_BEGINS_WITH("!^"), ENDS_WITH("$"), NOT_ENDS_WITH("!$"), IS( //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
				""), IS_NOT("!"); //$NON-NLS-1$ //$NON-NLS-2$

		public static CompareOperator fromUrl(String value) {
			for (CompareOperator operator : values()) {
				if (operator != IS && operator != IS_NOT && value.startsWith(operator.queryValue)) {
					return operator;
				}
			}
			if (value.startsWith(IS_NOT.queryValue)) {
				return IS_NOT;
			}
			return IS;
		}

		/** The string that represent the operator in a Trac query. */
		private String queryValue;

		CompareOperator(String queryValue) {
			this.queryValue = queryValue;
		}

		public String getQueryValue() {
			return queryValue;
		}

		@Override
		public String toString() {
			switch (this) {
			case CONTAINS:
				return "contains"; //$NON-NLS-1$
			case CONTAINS_NOT:
				return "does not contain"; //$NON-NLS-1$
			case BEGINS_WITH:
				return "begins with"; //$NON-NLS-1$
			case NOT_BEGINS_WITH:
				return "does not begin with"; //$NON-NLS-1$
			case ENDS_WITH:
				return "ends with"; //$NON-NLS-1$
			case NOT_ENDS_WITH:
				return "does not end with"; //$NON-NLS-1$
			case IS_NOT:
				return "is not"; //$NON-NLS-1$
			default:
				return "is"; //$NON-NLS-1$
			}
		}

	}

	private final String fieldName;

	private CompareOperator operator;

	private final List<String> values = new ArrayList<String>();

	public TracSearchFilter(String fieldName) {
		this.fieldName = fieldName;
	}

	public void addValue(String value) {
		values.add(value);
	}

	public String getFieldName() {
		return fieldName;
	}

	public CompareOperator getOperator() {
		return operator;
	}

	public List<String> getValues() {
		return values;
	}

	public void setOperator(CompareOperator operator) {
		this.operator = operator;
	}

}

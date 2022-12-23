/**
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.reviews.core.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Requirement Status</b></em>', and
 * utility methods for working with them. <!-- end-user-doc -->
 * 
 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getRequirementStatus()
 * @generated
 */
public enum RequirementStatus implements InternalRequirementStatus {
	/**
	 * The '<em><b>Unknown</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #UNKNOWN_VALUE
	 * @generated
	 * @ordered
	 */
	UNKNOWN(0, "Unknown", "UNKNOWN"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Satisfied</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #SATISFIED_VALUE
	 * @generated
	 * @ordered
	 */
	SATISFIED(1, "Satisfied", "SATISFIED"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Optional</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #OPTIONAL_VALUE
	 * @generated
	 * @ordered
	 */
	OPTIONAL(2, "Optional", "OPTIONAL"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Closed</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #CLOSED_VALUE
	 * @generated
	 * @ordered
	 */
	CLOSED(3, "Closed", "CLOSED"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Not Satisfied</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #NOT_SATISFIED_VALUE
	 * @generated
	 * @ordered
	 */
	NOT_SATISFIED(10, "NotSatisfied", "NOT_SATISFIED"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Rejected</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #REJECTED_VALUE
	 * @generated
	 * @ordered
	 */
	REJECTED(11, "Rejected", "REJECTED"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Error</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #ERROR_VALUE
	 * @generated
	 * @ordered
	 */
	ERROR(100, "Error", "ERROR"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Unknown</b></em>' literal value. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Unknown</b></em>' literal object isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @see #UNKNOWN
	 * @generated
	 * @ordered
	 */
	public static final int UNKNOWN_VALUE = 0;

	/**
	 * The '<em><b>Satisfied</b></em>' literal value. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Satisfied</b></em>' literal object isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @see #SATISFIED
	 * @generated
	 * @ordered
	 */
	public static final int SATISFIED_VALUE = 1;

	/**
	 * The '<em><b>Optional</b></em>' literal value. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Optional</b></em>' literal object isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @see #OPTIONAL
	 * @generated
	 * @ordered
	 */
	public static final int OPTIONAL_VALUE = 2;

	/**
	 * The '<em><b>Closed</b></em>' literal value. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Closed</b></em>' literal object isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @see #CLOSED
	 * @generated
	 * @ordered
	 */
	public static final int CLOSED_VALUE = 3;

	/**
	 * The '<em><b>Not Satisfied</b></em>' literal value. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Not Satisfied</b></em>' literal object isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @see #NOT_SATISFIED
	 * @generated
	 * @ordered
	 */
	public static final int NOT_SATISFIED_VALUE = 10;

	/**
	 * The '<em><b>Rejected</b></em>' literal value. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Rejected</b></em>' literal object isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @see #REJECTED
	 * @generated
	 * @ordered
	 */
	public static final int REJECTED_VALUE = 11;

	/**
	 * The '<em><b>Error</b></em>' literal value. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Error</b></em>' literal object isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @see #ERROR
	 * @generated
	 * @ordered
	 */
	public static final int ERROR_VALUE = 100;

	/**
	 * An array of all the '<em><b>Requirement Status</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 */
	private static final RequirementStatus[] VALUES_ARRAY = new RequirementStatus[] { UNKNOWN, SATISFIED, OPTIONAL,
			CLOSED, NOT_SATISFIED, REJECTED, ERROR, };

	/**
	 * A public read-only list of all the '<em><b>Requirement Status</b></em>' enumerators. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	public static final List<RequirementStatus> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Requirement Status</b></em>' literal with the specified literal value. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static RequirementStatus get(String literal) {
		for (RequirementStatus result : VALUES_ARRAY) {
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Requirement Status</b></em>' literal with the specified name. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	public static RequirementStatus getByName(String name) {
		for (RequirementStatus result : VALUES_ARRAY) {
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Requirement Status</b></em>' literal with the specified integer value. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static RequirementStatus get(int value) {
		switch (value) {
		case UNKNOWN_VALUE:
			return UNKNOWN;
		case SATISFIED_VALUE:
			return SATISFIED;
		case OPTIONAL_VALUE:
			return OPTIONAL;
		case CLOSED_VALUE:
			return CLOSED;
		case NOT_SATISFIED_VALUE:
			return NOT_SATISFIED;
		case REJECTED_VALUE:
			return REJECTED;
		case ERROR_VALUE:
			return ERROR;
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private final int value;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private final String name;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private final String literal;

	/**
	 * Only this class can construct instances. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private RequirementStatus(int value, String name, String literal) {
		this.value = value;
		this.name = name;
		this.literal = literal;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public int getValue() {
		return value;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getLiteral() {
		return literal;
	}

	/**
	 * Returns the literal value of the enumerator, which is its string representation. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String toString() {
		return literal;
	}

} //RequirementStatus

/**
 * A private implementation interface used to hide the inheritance from Enumerator. <!-- begin-user-doc --> <!--
 * end-user-doc -->
 * 
 * @generated
 */
interface InternalRequirementStatus extends org.eclipse.emf.common.util.Enumerator {
	// Empty
}

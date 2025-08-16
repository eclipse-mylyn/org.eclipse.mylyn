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
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Review Status</b></em>', and utility methods for
 * working with them. <!-- end-user-doc -->
 *
 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewStatus()
 * @generated
 */
public enum ReviewStatus implements InternalReviewStatus {
	/**
	 * The '<em><b>New</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #NEW_VALUE
	 * @generated
	 * @ordered
	 */
	NEW(0, "New", "NEW"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Submitted</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #SUBMITTED_VALUE
	 * @generated
	 * @ordered
	 */
	SUBMITTED(1, "Submitted", "SUBMITTED"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Merged</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #MERGED_VALUE
	 * @generated
	 * @ordered
	 */
	MERGED(2, "Merged", "MERGED"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Abandoned</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #ABANDONED_VALUE
	 * @generated
	 * @ordered
	 */
	ABANDONED(3, "Abandoned", "ABANDONED"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Draft</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #DRAFT_VALUE
	 * @generated
	 * @ordered
	 */
	DRAFT(4, "Draft", "DRAFT"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>New</b></em>' literal value. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>New</b></em>' literal object isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @see #NEW
	 * @generated
	 * @ordered
	 */
	public static final int NEW_VALUE = 0;

	/**
	 * The '<em><b>Submitted</b></em>' literal value. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Submitted</b></em>' literal object isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @see #SUBMITTED
	 * @generated
	 * @ordered
	 */
	public static final int SUBMITTED_VALUE = 1;

	/**
	 * The '<em><b>Merged</b></em>' literal value. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Merged</b></em>' literal object isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @see #MERGED
	 * @generated
	 * @ordered
	 */
	public static final int MERGED_VALUE = 2;

	/**
	 * The '<em><b>Abandoned</b></em>' literal value. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Abandoned</b></em>' literal object isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @see #ABANDONED
	 * @generated
	 * @ordered
	 */
	public static final int ABANDONED_VALUE = 3;

	/**
	 * The '<em><b>Draft</b></em>' literal value. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Draft</b></em>' literal object isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @see #DRAFT
	 * @generated
	 * @ordered
	 */
	public static final int DRAFT_VALUE = 4;

	/**
	 * An array of all the '<em><b>Review Status</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	private static final ReviewStatus[] VALUES_ARRAY = { NEW, SUBMITTED, MERGED, ABANDONED, DRAFT, };

	/**
	 * A public read-only list of all the '<em><b>Review Status</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public static final List<ReviewStatus> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Review Status</b></em>' literal with the specified literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public static ReviewStatus get(String literal) {
		for (ReviewStatus result : VALUES_ARRAY) {
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Review Status</b></em>' literal with the specified name. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public static ReviewStatus getByName(String name) {
		for (ReviewStatus result : VALUES_ARRAY) {
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Review Status</b></em>' literal with the specified integer value. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public static ReviewStatus get(int value) {
		switch (value) {
			case NEW_VALUE:
				return NEW;
			case SUBMITTED_VALUE:
				return SUBMITTED;
			case MERGED_VALUE:
				return MERGED;
			case ABANDONED_VALUE:
				return ABANDONED;
			case DRAFT_VALUE:
				return DRAFT;
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
	ReviewStatus(int value, String name, String literal) {
		this.value = value;
		this.name = name;
		this.literal = literal;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public int getValue() {
		return value;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String getLiteral() {
		return literal;
	}

	/**
	 * Returns the literal value of the enumerator, which is its string representation. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String toString() {
		return literal;
	}

} //ReviewStatus

/**
 * A private implementation interface used to hide the inheritance from Enumerator. <!-- begin-user-doc --> <!-- end-user-doc -->
 *
 * @generated
 */
interface InternalReviewStatus extends org.eclipse.emf.common.util.Enumerator {
	// Empty
}

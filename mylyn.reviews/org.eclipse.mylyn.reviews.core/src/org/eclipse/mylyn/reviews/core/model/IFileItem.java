/**
 * Copyright (c) 2011, 2013 Tasktop Technologies and others.
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

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>File Item</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IFileItem#getBase <em>Base</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IFileItem#getTarget <em>Target</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IFileItem#getSet <em>Set</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IFileItem extends IReviewItem {
	/**
	 * Returns the value of the '<em><b>Base</b></em>' containment reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Base</em>' reference isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Base</em>' containment reference.
	 * @see #setBase(IFileVersion)
	 * @generated
	 */
	IFileVersion getBase();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IFileItem#getBase <em>Base</em>}' containment
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Base</em>' containment reference.
	 * @see #getBase()
	 * @generated
	 */
	void setBase(IFileVersion value);

	/**
	 * Returns the value of the '<em><b>Target</b></em>' containment reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Target</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Target</em>' containment reference.
	 * @see #setTarget(IFileVersion)
	 * @generated
	 */
	IFileVersion getTarget();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IFileItem#getTarget <em>Target</em>}'
	 * containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Target</em>' containment reference.
	 * @see #getTarget()
	 * @generated
	 */
	void setTarget(IFileVersion value);

	/**
	 * Returns the value of the '<em><b>Set</b></em>' container reference. It is bidirectional and its opposite is '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getItems <em>Items</em>}'. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Set</em>' container reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Set</em>' container reference.
	 * @see #setSet(IReviewItemSet)
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getItems
	 * @generated
	 */
	IReviewItemSet getSet();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IFileItem#getSet <em>Set</em>}' container
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Set</em>' container reference.
	 * @see #getSet()
	 * @generated
	 */
	void setSet(IReviewItemSet value);

} // IFileItem

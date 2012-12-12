/**
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
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
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IFileItem extends IReviewItem {
	/**
	 * Returns the value of the '<em><b>Base</b></em>' reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Base</em>' reference isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Base</em>' reference.
	 * @see #setBase(IFileRevision)
	 * @generated
	 */
	IFileRevision getBase();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IFileItem#getBase <em>Base</em>}' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Base</em>' reference.
	 * @see #getBase()
	 * @generated
	 */
	void setBase(IFileRevision value);

	/**
	 * Returns the value of the '<em><b>Target</b></em>' reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Target</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Target</em>' reference.
	 * @see #setTarget(IFileRevision)
	 * @generated
	 */
	IFileRevision getTarget();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IFileItem#getTarget <em>Target</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Target</em>' reference.
	 * @see #getTarget()
	 * @generated
	 */
	void setTarget(IFileRevision value);

} // IFileItem

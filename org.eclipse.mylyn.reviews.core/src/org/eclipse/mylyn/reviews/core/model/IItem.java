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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Item</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IItem#getAddedBy <em>Added By</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IItem#getReview <em>Review</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IItem extends IReviewComponent {
	/**
	 * Returns the value of the '<em><b>Added By</b></em>' reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Added By</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Added By</em>' reference.
	 * @see #setAddedBy(IUser)
	 * @generated
	 */
	IUser getAddedBy();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IItem#getAddedBy <em>Added By</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Added By</em>' reference.
	 * @see #getAddedBy()
	 * @generated
	 */
	void setAddedBy(IUser value);

	/**
	 * Returns the value of the '<em><b>Review</b></em>' reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Review</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Review</em>' reference.
	 * @see #setReview(IReview)
	 * @generated
	 */
	IReview getReview();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IItem#getReview <em>Review</em>}' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Review</em>' reference.
	 * @see #getReview()
	 * @generated
	 */
	void setReview(IReview value);

} // IItem

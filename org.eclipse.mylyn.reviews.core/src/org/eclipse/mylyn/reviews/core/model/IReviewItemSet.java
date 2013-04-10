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

import java.util.List;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Review Item Set</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getItems <em>Items</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getRevision <em>Revision</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getParentReview <em>Parent Review</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IReviewItemSet extends IReviewItem, IDated {
	/**
	 * Returns the value of the '<em><b>Items</b></em>' containment reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.IFileItem}. It is bidirectional and its opposite is '
	 * {@link org.eclipse.mylyn.reviews.core.model.IFileItem#getSet <em>Set</em>}'. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Items</em>' containment reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Items</em>' containment reference list.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileItem#getSet
	 * @generated
	 */
	List<IFileItem> getItems();

	/**
	 * Returns the value of the '<em><b>Revision</b></em>' attribute. The default value is <code>""</code>. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Revision</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Revision</em>' attribute.
	 * @see #setRevision(String)
	 * @generated
	 */
	String getRevision();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getRevision <em>Revision</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Revision</em>' attribute.
	 * @see #getRevision()
	 * @generated
	 */
	void setRevision(String value);

	/**
	 * Returns the value of the '<em><b>Parent Review</b></em>' container reference. It is bidirectional and its
	 * opposite is '{@link org.eclipse.mylyn.reviews.core.model.IReview#getSets <em>Sets</em>}'. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent Review</em>' container reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Parent Review</em>' container reference.
	 * @see #setParentReview(IReview)
	 * @see org.eclipse.mylyn.reviews.core.model.IReview#getSets
	 * @generated
	 */
	IReview getParentReview();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getParentReview
	 * <em>Parent Review</em>}' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Parent Review</em>' container reference.
	 * @see #getParentReview()
	 * @generated
	 */
	void setParentReview(IReview value);

} // IReviewItemSet

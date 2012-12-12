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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Review Group</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReviewGroup#getReviews <em>Reviews</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReviewGroup#getReviewGroupTask <em>Review Group Task</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReviewGroup#getDescription <em>Description</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IReviewGroup extends IReviewComponent {
	/**
	 * Returns the value of the '<em><b>Reviews</b></em>' containment reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.IReview}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Reviews</em>' containment reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Reviews</em>' containment reference list.
	 * @generated
	 */
	List<IReview> getReviews();

	/**
	 * Returns the value of the '<em><b>Review Group Task</b></em>' containment reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Review Group Task</em>' containment reference isn't clear, there really should be more
	 * of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Review Group Task</em>' containment reference.
	 * @see #setReviewGroupTask(ITaskReference)
	 * @generated
	 */
	ITaskReference getReviewGroupTask();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IReviewGroup#getReviewGroupTask
	 * <em>Review Group Task</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Review Group Task</em>' containment reference.
	 * @see #getReviewGroupTask()
	 * @generated
	 */
	void setReviewGroupTask(ITaskReference value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Description</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IReviewGroup#getDescription
	 * <em>Description</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

} // IReviewGroup

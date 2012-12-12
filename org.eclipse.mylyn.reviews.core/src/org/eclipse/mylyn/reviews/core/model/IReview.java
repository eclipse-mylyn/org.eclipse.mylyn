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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Review</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReview#getItems <em>Items</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReview#getReviewTask <em>Review Task</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReview#getState <em>State</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReview#getId <em>Id</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReview#getOwner <em>Owner</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IReview extends ITopicContainer, IDated {
	/**
	 * Returns the value of the '<em><b>Items</b></em>' reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.IReviewItem}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Items</em>' reference list isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Items</em>' reference list.
	 * @generated
	 */
	List<IReviewItem> getItems();

	/**
	 * Returns the value of the '<em><b>Review Task</b></em>' containment reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Review Task</em>' containment reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Review Task</em>' containment reference.
	 * @see #setReviewTask(ITaskReference)
	 * @generated
	 */
	ITaskReference getReviewTask();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IReview#getReviewTask <em>Review Task</em>}'
	 * containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Review Task</em>' containment reference.
	 * @see #getReviewTask()
	 * @generated
	 */
	void setReviewTask(ITaskReference value);

	/**
	 * Returns the value of the '<em><b>State</b></em>' containment reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>State</em>' containment reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>State</em>' containment reference.
	 * @see #setState(IReviewState)
	 * @generated
	 */
	IReviewState getState();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IReview#getState <em>State</em>}' containment
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>State</em>' containment reference.
	 * @see #getState()
	 * @generated
	 */
	void setState(IReviewState value);

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IReview#getId <em>Id</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Owner</b></em>' reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Owner</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Owner</em>' reference.
	 * @see #setOwner(IUser)
	 * @generated
	 */
	IUser getOwner();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IReview#getOwner <em>Owner</em>}' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Owner</em>' reference.
	 * @see #getOwner()
	 * @generated
	 */
	void setOwner(IUser value);

} // IReview

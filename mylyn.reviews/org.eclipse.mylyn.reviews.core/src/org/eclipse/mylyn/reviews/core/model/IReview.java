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

import java.util.List;
import java.util.Map;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Review</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReview#getSets <em>Sets</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReview#getRepository <em>Repository</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReview#getParents <em>Parents</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReview#getChildren <em>Children</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReview#getReviewerApprovals <em>Reviewer Approvals</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IReview#getRequirements <em>Requirements</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IReview extends ICommentContainer, IChange {
	/**
	 * Returns the value of the '<em><b>Sets</b></em>' containment reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet}. It is bidirectional and its opposite is '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getParentReview <em>Parent Review</em>}'. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sets</em>' containment reference list isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Sets</em>' containment reference list.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getParentReview
	 * @generated
	 */
	List<IReviewItemSet> getSets();

	/**
	 * Returns the value of the '<em><b>Repository</b></em>' container reference. It is bidirectional and its opposite is
	 * '{@link org.eclipse.mylyn.reviews.core.model.IRepository#getReviews <em>Reviews</em>}'. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Repository</em>' container reference isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Repository</em>' container reference.
	 * @see #setRepository(IRepository)
	 * @see org.eclipse.mylyn.reviews.core.model.IRepository#getReviews
	 * @generated
	 */
	IRepository getRepository();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.IReview#getRepository <em>Repository</em>}' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Repository</em>' container reference.
	 * @see #getRepository()
	 * @generated
	 */
	void setRepository(IRepository value);

	/**
	 * Returns the value of the '<em><b>Parents</b></em>' containment reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.IChange}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parents</em>' containment reference list isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Parents</em>' containment reference list.
	 * @generated
	 */
	List<IChange> getParents();

	/**
	 * Returns the value of the '<em><b>Children</b></em>' containment reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.IChange}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Children</em>' containment reference list isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Children</em>' containment reference list.
	 * @generated
	 */
	List<IChange> getChildren();

	/**
	 * Returns the value of the '<em><b>Reviewer Approvals</b></em>' map. The key is of type
	 * {@link org.eclipse.mylyn.reviews.core.model.IUser}, and the value is of type
	 * {@link org.eclipse.mylyn.reviews.core.model.IReviewerEntry}, <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Reviewer Approvals</em>' map isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Reviewer Approvals</em>' map.
	 * @generated
	 */
	Map<IUser, IReviewerEntry> getReviewerApprovals();

	/**
	 * Returns the value of the '<em><b>Requirements</b></em>' map. The key is of type
	 * {@link org.eclipse.mylyn.reviews.core.model.IApprovalType}, and the value is of type
	 * {@link org.eclipse.mylyn.reviews.core.model.IRequirementEntry}, <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Requirements</em>' map isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Requirements</em>' map.
	 * @generated
	 */
	Map<IApprovalType, IRequirementEntry> getRequirements();

} // IReview

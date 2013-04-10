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
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * 
 * @generated
 */
public interface IReviewsFactory {
	/**
	 * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	IReviewsFactory INSTANCE = org.eclipse.mylyn.reviews.internal.core.model.ReviewsFactory.eINSTANCE;

	/**
	 * Returns a new object of class '<em>Review</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Review</em>'.
	 * @generated
	 */
	IReview createReview();

	/**
	 * Returns a new object of class '<em>Comment</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Comment</em>'.
	 * @generated
	 */
	IComment createComment();

	/**
	 * Returns a new object of class '<em>Review Item</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Review Item</em>'.
	 * @generated
	 */
	IReviewItem createReviewItem();

	/**
	 * Returns a new object of class '<em>User</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>User</em>'.
	 * @generated
	 */
	IUser createUser();

	/**
	 * Returns a new object of class '<em>Review Group</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Review Group</em>'.
	 * @generated
	 */
	IReviewGroup createReviewGroup();

	/**
	 * Returns a new object of class '<em>Repository</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Repository</em>'.
	 * @generated
	 */
	IRepository createRepository();

	/**
	 * Returns a new object of class '<em>Topic</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Topic</em>'.
	 * @generated
	 */
	ITopic createTopic();

	/**
	 * Returns a new object of class '<em>Review Component</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Review Component</em>'.
	 * @generated
	 */
	IReviewComponent createReviewComponent();

	/**
	 * Returns a new object of class '<em>File Item</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>File Item</em>'.
	 * @generated
	 */
	IFileItem createFileItem();

	/**
	 * Returns a new object of class '<em>Review Item Set</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Review Item Set</em>'.
	 * @generated
	 */
	IReviewItemSet createReviewItemSet();

	/**
	 * Returns a new object of class '<em>Line Location</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Line Location</em>'.
	 * @generated
	 */
	ILineLocation createLineLocation();

	/**
	 * Returns a new object of class '<em>Line Range</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Line Range</em>'.
	 * @generated
	 */
	ILineRange createLineRange();

	/**
	 * Returns a new object of class '<em>File Revision</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>File Revision</em>'.
	 * @generated
	 */
	IFileRevision createFileRevision();

	/**
	 * Returns a new object of class '<em>Reviewer Entry</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Reviewer Entry</em>'.
	 * @generated
	 */
	IReviewerEntry createReviewerEntry();

	/**
	 * Returns a new object of class '<em>Requirement Entry</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Requirement Entry</em>'.
	 * @generated
	 */
	IRequirementEntry createRequirementEntry();

	/**
	 * Returns a new object of class '<em>Requirement Review State</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Requirement Review State</em>'.
	 * @generated
	 */
	IRequirementReviewState createRequirementReviewState();

	/**
	 * Returns a new object of class '<em>Simple Review State</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Simple Review State</em>'.
	 * @generated
	 */
	ISimpleReviewState createSimpleReviewState();

	/**
	 * Returns a new object of class '<em>Approval Type</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Approval Type</em>'.
	 * @generated
	 */
	IApprovalType createApprovalType();

	/**
	 * Returns a new object of class '<em>Change</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Change</em>'.
	 * @generated
	 */
	IChange createChange();

} //IReviewsFactory

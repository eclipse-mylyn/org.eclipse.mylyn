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
package org.eclipse.mylyn.reviews.internal.core.model;

import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.mylyn.reviews.core.model.*;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class ReviewsFactory extends EFactoryImpl implements IReviewsFactory {
	/**
	 * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static final ReviewsFactory eINSTANCE = init();

	/**
	 * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static ReviewsFactory init() {
		try {
			ReviewsFactory theReviewsFactory = (ReviewsFactory) EPackage.Registry.INSTANCE.getEFactory(ReviewsPackage.eNS_URI);
			if (theReviewsFactory != null) {
				return theReviewsFactory;
			}
		} catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new ReviewsFactory();
	}

	/**
	 * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ReviewsFactory() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
		case ReviewsPackage.CHANGE:
			return createChange();
		case ReviewsPackage.REVIEW:
			return createReview();
		case ReviewsPackage.COMMENT:
			return createComment();
		case ReviewsPackage.REVIEW_ITEM:
			return createReviewItem();
		case ReviewsPackage.USER:
			return createUser();
		case ReviewsPackage.REVIEW_GROUP:
			return createReviewGroup();
		case ReviewsPackage.REPOSITORY:
			return createRepository();
		case ReviewsPackage.TOPIC:
			return createTopic();
		case ReviewsPackage.REVIEW_COMPONENT:
			return createReviewComponent();
		case ReviewsPackage.FILE_ITEM:
			return createFileItem();
		case ReviewsPackage.REVIEW_ITEM_SET:
			return createReviewItemSet();
		case ReviewsPackage.LINE_LOCATION:
			return createLineLocation();
		case ReviewsPackage.LINE_RANGE:
			return createLineRange();
		case ReviewsPackage.FILE_VERSION:
			return createFileVersion();
		case ReviewsPackage.APPROVAL_TYPE:
			return createApprovalType();
		case ReviewsPackage.USER_APPROVALS_MAP:
			return (EObject) createUserApprovalsMap();
		case ReviewsPackage.REVIEWER_ENTRY:
			return createReviewerEntry();
		case ReviewsPackage.APPROVAL_VALUE_MAP:
			return (EObject) createApprovalValueMap();
		case ReviewsPackage.REQUIREMENT_ENTRY:
			return createRequirementEntry();
		case ReviewsPackage.REVIEW_REQUIREMENTS_MAP:
			return (EObject) createReviewRequirementsMap();
		case ReviewsPackage.REQUIREMENT_REVIEW_STATE:
			return createRequirementReviewState();
		case ReviewsPackage.SIMPLE_REVIEW_STATE:
			return createSimpleReviewState();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
		case ReviewsPackage.REQUIREMENT_STATUS:
			return createRequirementStatusFromString(eDataType, initialValue);
		default:
			throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
		case ReviewsPackage.REQUIREMENT_STATUS:
			return convertRequirementStatusToString(eDataType, instanceValue);
		default:
			throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IReview createReview() {
		Review review = new Review();
		return review;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IComment createComment() {
		Comment comment = new Comment();
		return comment;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IReviewItem createReviewItem() {
		ReviewItem reviewItem = new ReviewItem();
		return reviewItem;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IUser createUser() {
		User user = new User();
		return user;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IReviewGroup createReviewGroup() {
		ReviewGroup reviewGroup = new ReviewGroup();
		return reviewGroup;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IRepository createRepository() {
		Repository repository = new Repository();
		return repository;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ITopic createTopic() {
		Topic topic = new Topic();
		return topic;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IReviewComponent createReviewComponent() {
		ReviewComponent reviewComponent = new ReviewComponent();
		return reviewComponent;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IFileItem createFileItem() {
		FileItem fileItem = new FileItem();
		return fileItem;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IReviewItemSet createReviewItemSet() {
		ReviewItemSet reviewItemSet = new ReviewItemSet();
		return reviewItemSet;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ILineLocation createLineLocation() {
		LineLocation lineLocation = new LineLocation();
		return lineLocation;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ILineRange createLineRange() {
		LineRange lineRange = new LineRange();
		return lineRange;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IFileVersion createFileVersion() {
		FileVersion fileVersion = new FileVersion();
		return fileVersion;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IReviewerEntry createReviewerEntry() {
		ReviewerEntry reviewerEntry = new ReviewerEntry();
		return reviewerEntry;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IApprovalType createApprovalType() {
		ApprovalType approvalType = new ApprovalType();
		return approvalType;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Map.Entry<IApprovalType, Integer> createApprovalValueMap() {
		ApprovalValueMap approvalValueMap = new ApprovalValueMap();
		return approvalValueMap;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IRequirementEntry createRequirementEntry() {
		RequirementEntry requirementEntry = new RequirementEntry();
		return requirementEntry;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Map.Entry<IApprovalType, IRequirementEntry> createReviewRequirementsMap() {
		ReviewRequirementsMap reviewRequirementsMap = new ReviewRequirementsMap();
		return reviewRequirementsMap;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IRequirementReviewState createRequirementReviewState() {
		RequirementReviewState requirementReviewState = new RequirementReviewState();
		return requirementReviewState;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ISimpleReviewState createSimpleReviewState() {
		SimpleReviewState simpleReviewState = new SimpleReviewState();
		return simpleReviewState;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public RequirementStatus createRequirementStatusFromString(EDataType eDataType, String initialValue) {
		RequirementStatus result = RequirementStatus.get(initialValue);
		if (result == null)
			throw new IllegalArgumentException(
					"The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return result;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String convertRequirementStatusToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Map.Entry<IUser, IReviewerEntry> createUserApprovalsMap() {
		UserApprovalsMap userApprovalsMap = new UserApprovalsMap();
		return userApprovalsMap;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IChange createChange() {
		Change change = new Change();
		return change;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ReviewsPackage getReviewsPackage() {
		return (ReviewsPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static ReviewsPackage getPackage() {
		return ReviewsPackage.eINSTANCE;
	}

} //ReviewsFactory

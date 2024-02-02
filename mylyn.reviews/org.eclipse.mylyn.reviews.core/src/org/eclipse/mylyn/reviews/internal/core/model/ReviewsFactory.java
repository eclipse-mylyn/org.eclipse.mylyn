/**
 * Copyright (c) 2011, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.reviews.internal.core.model;

import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.mylyn.reviews.core.model.IApprovalType;
import org.eclipse.mylyn.reviews.core.model.IChange;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.ICommit;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileVersion;
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ILineRange;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IRequirementEntry;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IReviewerEntry;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.core.model.RequirementStatus;
import org.eclipse.mylyn.reviews.core.model.ReviewStatus;

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
			ReviewsFactory theReviewsFactory = (ReviewsFactory) EPackage.Registry.INSTANCE
					.getEFactory(ReviewsPackage.eNS_URI);
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
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		return switch (eClass.getClassifierID()) {
			case ReviewsPackage.CHANGE -> createChange();
			case ReviewsPackage.REVIEW -> createReview();
			case ReviewsPackage.COMMENT -> createComment();
			case ReviewsPackage.USER -> createUser();
			case ReviewsPackage.REPOSITORY -> createRepository();
			case ReviewsPackage.FILE_ITEM -> createFileItem();
			case ReviewsPackage.REVIEW_ITEM_SET -> createReviewItemSet();
			case ReviewsPackage.LINE_LOCATION -> createLineLocation();
			case ReviewsPackage.LINE_RANGE -> createLineRange();
			case ReviewsPackage.FILE_VERSION -> createFileVersion();
			case ReviewsPackage.APPROVAL_TYPE -> createApprovalType();
			case ReviewsPackage.USER_APPROVALS_MAP -> (EObject) createUserApprovalsMap();
			case ReviewsPackage.REVIEWER_ENTRY -> createReviewerEntry();
			case ReviewsPackage.APPROVAL_VALUE_MAP -> (EObject) createApprovalValueMap();
			case ReviewsPackage.REQUIREMENT_ENTRY -> createRequirementEntry();
			case ReviewsPackage.REVIEW_REQUIREMENTS_MAP -> (EObject) createReviewRequirementsMap();
			case ReviewsPackage.COMMIT -> createCommit();
			default -> throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		};
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		return switch (eDataType.getClassifierID()) {
			case ReviewsPackage.REQUIREMENT_STATUS -> createRequirementStatusFromString(eDataType, initialValue);
			case ReviewsPackage.REVIEW_STATUS -> createReviewStatusFromString(eDataType, initialValue);
			default -> throw new IllegalArgumentException(
					"The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		};
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		return switch (eDataType.getClassifierID()) {
			case ReviewsPackage.REQUIREMENT_STATUS -> convertRequirementStatusToString(eDataType, instanceValue);
			case ReviewsPackage.REVIEW_STATUS -> convertReviewStatusToString(eDataType, instanceValue);
			default -> throw new IllegalArgumentException(
					"The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		};
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public IReview createReview() {
		Review review = new Review();
		return review;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public IComment createComment() {
		Comment comment = new Comment();
		return comment;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public IUser createUser() {
		User user = new User();
		return user;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public IRepository createRepository() {
		Repository repository = new Repository();
		return repository;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public IFileItem createFileItem() {
		FileItem fileItem = new FileItem();
		return fileItem;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public IReviewItemSet createReviewItemSet() {
		ReviewItemSet reviewItemSet = new ReviewItemSet();
		return reviewItemSet;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public ILineLocation createLineLocation() {
		LineLocation lineLocation = new LineLocation();
		return lineLocation;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public ILineRange createLineRange() {
		LineRange lineRange = new LineRange();
		return lineRange;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public IFileVersion createFileVersion() {
		FileVersion fileVersion = new FileVersion();
		return fileVersion;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public IReviewerEntry createReviewerEntry() {
		ReviewerEntry reviewerEntry = new ReviewerEntry();
		return reviewerEntry;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
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
	@Override
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
	@Override
	public ICommit createCommit() {
		Commit commit = new Commit();
		return commit;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public RequirementStatus createRequirementStatusFromString(EDataType eDataType, String initialValue) {
		RequirementStatus result = RequirementStatus.get(initialValue);
		if (result == null) {
			throw new IllegalArgumentException(
					"The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
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
	public ReviewStatus createReviewStatusFromString(EDataType eDataType, String initialValue) {
		ReviewStatus result = ReviewStatus.get(initialValue);
		if (result == null) {
			throw new IllegalArgumentException(
					"The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return result;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String convertReviewStatusToString(EDataType eDataType, Object instanceValue) {
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
	@Override
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

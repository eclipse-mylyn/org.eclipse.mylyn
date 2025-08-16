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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.mylyn.reviews.core.model.IApprovalType;
import org.eclipse.mylyn.reviews.core.model.IChange;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.ICommentContainer;
import org.eclipse.mylyn.reviews.core.model.ICommit;
import org.eclipse.mylyn.reviews.core.model.IDated;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileVersion;
import org.eclipse.mylyn.reviews.core.model.IIndexed;
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ILineRange;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IRequirementEntry;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IReviewerEntry;
import org.eclipse.mylyn.reviews.core.model.IUser;

/**
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides an adapter <code>createXXX</code> method for each class of
 * the model. <!-- end-user-doc -->
 *
 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage
 * @generated
 */
public class ReviewsAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected static ReviewsPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public ReviewsAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = ReviewsPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object. <!-- begin-user-doc --> This implementation returns
	 * <code>true</code> if the object is either the model's package or is an instance object of the model. <!-- end-user-doc -->
	 *
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject) object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected ReviewsSwitch<Adapter> modelSwitch = new ReviewsSwitch<>() {
		@Override
		public Adapter caseCommentContainer(ICommentContainer object) {
			return createCommentContainerAdapter();
		}

		@Override
		public Adapter caseChange(IChange object) {
			return createChangeAdapter();
		}

		@Override
		public Adapter caseReview(IReview object) {
			return createReviewAdapter();
		}

		@Override
		public Adapter caseComment(IComment object) {
			return createCommentAdapter();
		}

		@Override
		public Adapter caseReviewItem(IReviewItem object) {
			return createReviewItemAdapter();
		}

		@Override
		public Adapter caseLocation(ILocation object) {
			return createLocationAdapter();
		}

		@Override
		public Adapter caseUser(IUser object) {
			return createUserAdapter();
		}

		@Override
		public Adapter caseRepository(IRepository object) {
			return createRepositoryAdapter();
		}

		@Override
		public Adapter caseFileItem(IFileItem object) {
			return createFileItemAdapter();
		}

		@Override
		public Adapter caseReviewItemSet(IReviewItemSet object) {
			return createReviewItemSetAdapter();
		}

		@Override
		public Adapter caseLineLocation(ILineLocation object) {
			return createLineLocationAdapter();
		}

		@Override
		public Adapter caseLineRange(ILineRange object) {
			return createLineRangeAdapter();
		}

		@Override
		public Adapter caseFileVersion(IFileVersion object) {
			return createFileVersionAdapter();
		}

		@Override
		public Adapter caseIndexed(IIndexed object) {
			return createIndexedAdapter();
		}

		@Override
		public Adapter caseDated(IDated object) {
			return createDatedAdapter();
		}

		@Override
		public Adapter caseApprovalType(IApprovalType object) {
			return createApprovalTypeAdapter();
		}

		@Override
		public Adapter caseUserApprovalsMap(Map.Entry<IUser, IReviewerEntry> object) {
			return createUserApprovalsMapAdapter();
		}

		@Override
		public Adapter caseReviewerEntry(IReviewerEntry object) {
			return createReviewerEntryAdapter();
		}

		@Override
		public Adapter caseApprovalValueMap(Map.Entry<IApprovalType, Integer> object) {
			return createApprovalValueMapAdapter();
		}

		@Override
		public Adapter caseRequirementEntry(IRequirementEntry object) {
			return createRequirementEntryAdapter();
		}

		@Override
		public Adapter caseReviewRequirementsMap(Map.Entry<IApprovalType, IRequirementEntry> object) {
			return createReviewRequirementsMapAdapter();
		}

		@Override
		public Adapter caseCommit(ICommit object) {
			return createCommitAdapter();
		}

		@Override
		public Adapter defaultCase(EObject object) {
			return createEObjectAdapter();
		}
	};

	/**
	 * Creates an adapter for the <code>target</code>. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @param target
	 *            the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject) target);
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.ICommentContainer <em>Comment
	 * Container</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
	 * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.ICommentContainer
	 * @generated
	 */
	public Adapter createCommentContainerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IReview <em>Review</em>}'. <!--
	 * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when
	 * inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IReview
	 * @generated
	 */
	public Adapter createReviewAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IComment <em>Comment</em>}'. <!--
	 * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when
	 * inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IComment
	 * @generated
	 */
	public Adapter createCommentAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IReviewItem <em>Review Item</em>}'. <!--
	 * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when
	 * inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItem
	 * @generated
	 */
	public Adapter createReviewItemAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.ILocation <em>Location</em>}'. <!--
	 * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when
	 * inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.ILocation
	 * @generated
	 */
	public Adapter createLocationAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IUser <em>User</em>}'. <!-- begin-user-doc
	 * --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will
	 * catch all the cases anyway. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IUser
	 * @generated
	 */
	public Adapter createUserAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IRepository <em>Repository</em>}'. <!--
	 * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when
	 * inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IRepository
	 * @generated
	 */
	public Adapter createRepositoryAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IFileItem <em>File Item</em>}'. <!--
	 * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when
	 * inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileItem
	 * @generated
	 */
	public Adapter createFileItemAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet <em>Review Item Set</em>}'.
	 * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case
	 * when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItemSet
	 * @generated
	 */
	public Adapter createReviewItemSetAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.ILineLocation <em>Line Location</em>}'.
	 * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case
	 * when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.ILineLocation
	 * @generated
	 */
	public Adapter createLineLocationAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.ILineRange <em>Line Range</em>}'. <!--
	 * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when
	 * inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.ILineRange
	 * @generated
	 */
	public Adapter createLineRangeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IFileVersion <em>File Version</em>}'. <!--
	 * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when
	 * inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileVersion
	 * @generated
	 */
	public Adapter createFileVersionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IIndexed <em>Indexed</em>}'. <!--
	 * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when
	 * inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IIndexed
	 * @generated
	 */
	public Adapter createIndexedAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IDated <em>Dated</em>} '. <!--
	 * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when
	 * inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IDated
	 * @generated
	 */
	public Adapter createDatedAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IReviewerEntry <em>Reviewer Entry</em>}'.
	 * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case
	 * when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewerEntry
	 * @generated
	 */
	public Adapter createReviewerEntryAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IApprovalType <em>Approval Type</em>}'.
	 * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case
	 * when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IApprovalType
	 * @generated
	 */
	public Adapter createApprovalTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>Approval Value Map</em>}'. <!-- begin-user-doc --> This
	 * default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all
	 * the cases anyway. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see java.util.Map.Entry
	 * @generated
	 */
	public Adapter createApprovalValueMapAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IRequirementEntry <em>Requirement
	 * Entry</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
	 * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IRequirementEntry
	 * @generated
	 */
	public Adapter createRequirementEntryAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>Review Requirements Map</em>}'. <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch
	 * all the cases anyway. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see java.util.Map.Entry
	 * @generated
	 */
	public Adapter createReviewRequirementsMapAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.ICommit <em>Commit</em>}'. <!--
	 * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when
	 * inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.ICommit
	 * @generated
	 */
	public Adapter createCommitAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>User Approvals Map</em>}'. <!-- begin-user-doc --> This
	 * default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all
	 * the cases anyway. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see java.util.Map.Entry
	 * @generated
	 */
	public Adapter createUserApprovalsMapAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IChange <em>Change</em>}'. <!--
	 * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when
	 * inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IChange
	 * @generated
	 */
	public Adapter createChangeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case. <!-- begin-user-doc --> This default implementation returns null. <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //ReviewsAdapterFactory

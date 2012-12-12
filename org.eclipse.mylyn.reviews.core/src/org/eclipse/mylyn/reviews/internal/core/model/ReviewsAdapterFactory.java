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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.mylyn.reviews.core.model.*;

/**
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides an adapter <code>createXXX</code>
 * method for each class of the model. <!-- end-user-doc -->
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
	 * Returns whether this factory is applicable for the type of the object. <!-- begin-user-doc --> This
	 * implementation returns <code>true</code> if the object is either the model's package or is an instance object of
	 * the model. <!-- end-user-doc -->
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
	protected ReviewsSwitch<Adapter> modelSwitch = new ReviewsSwitch<Adapter>() {
		@Override
		public Adapter caseTopicContainer(ITopicContainer object) {
			return createTopicContainerAdapter();
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
		public Adapter caseTaskReference(ITaskReference object) {
			return createTaskReferenceAdapter();
		}

		@Override
		public Adapter caseReviewState(IReviewState object) {
			return createReviewStateAdapter();
		}

		@Override
		public Adapter caseReviewGroup(IReviewGroup object) {
			return createReviewGroupAdapter();
		}

		@Override
		public Adapter caseCommentType(ICommentType object) {
			return createCommentTypeAdapter();
		}

		@Override
		public Adapter caseTopic(ITopic object) {
			return createTopicAdapter();
		}

		@Override
		public Adapter caseReviewComponent(IReviewComponent object) {
			return createReviewComponentAdapter();
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
		public Adapter caseFileRevision(IFileRevision object) {
			return createFileRevisionAdapter();
		}

		@Override
		public Adapter caseModelVersioning(IModelVersioning object) {
			return createModelVersioningAdapter();
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
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.ITopicContainer
	 * <em>Topic Container</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
	 * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
	 * end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.ITopicContainer
	 * @generated
	 */
	public Adapter createTopicContainerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IReview
	 * <em>Review</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
	 * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IReview
	 * @generated
	 */
	public Adapter createReviewAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IComment
	 * <em>Comment</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
	 * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IComment
	 * @generated
	 */
	public Adapter createCommentAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IReviewItem
	 * <em>Review Item</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
	 * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc
	 * -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItem
	 * @generated
	 */
	public Adapter createReviewItemAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.ILocation
	 * <em>Location</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
	 * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc
	 * -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.ILocation
	 * @generated
	 */
	public Adapter createLocationAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IUser <em>User</em>}'.
	 * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful
	 * to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IUser
	 * @generated
	 */
	public Adapter createUserAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.ITaskReference
	 * <em>Task Reference</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
	 * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc
	 * -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.ITaskReference
	 * @generated
	 */
	public Adapter createTaskReferenceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IReviewState
	 * <em>Review State</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
	 * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc
	 * -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewState
	 * @generated
	 */
	public Adapter createReviewStateAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IReviewGroup
	 * <em>Review Group</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
	 * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc
	 * -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewGroup
	 * @generated
	 */
	public Adapter createReviewGroupAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.ICommentType
	 * <em>Comment Type</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
	 * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc
	 * -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.ICommentType
	 * @generated
	 */
	public Adapter createCommentTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.ITopic <em>Topic</em>}
	 * '. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's
	 * useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.ITopic
	 * @generated
	 */
	public Adapter createTopicAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IReviewComponent
	 * <em>Review Component</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
	 * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
	 * end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewComponent
	 * @generated
	 */
	public Adapter createReviewComponentAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IFileItem
	 * <em>File Item</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
	 * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc
	 * -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileItem
	 * @generated
	 */
	public Adapter createFileItemAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet
	 * <em>Review Item Set</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
	 * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
	 * end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItemSet
	 * @generated
	 */
	public Adapter createReviewItemSetAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.ILineLocation
	 * <em>Line Location</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
	 * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc
	 * -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.ILineLocation
	 * @generated
	 */
	public Adapter createLineLocationAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.ILineRange
	 * <em>Line Range</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
	 * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc
	 * -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.ILineRange
	 * @generated
	 */
	public Adapter createLineRangeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IFileRevision
	 * <em>File Revision</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
	 * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc
	 * -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileRevision
	 * @generated
	 */
	public Adapter createFileRevisionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IModelVersioning
	 * <em>Model Versioning</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
	 * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
	 * end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IModelVersioning
	 * @generated
	 */
	public Adapter createModelVersioningAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IIndexed
	 * <em>Indexed</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
	 * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IIndexed
	 * @generated
	 */
	public Adapter createIndexedAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.mylyn.reviews.core.model.IDated <em>Dated</em>}
	 * '. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's
	 * useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @see org.eclipse.mylyn.reviews.core.model.IDated
	 * @generated
	 */
	public Adapter createDatedAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case. <!-- begin-user-doc --> This default implementation returns null.
	 * <!-- end-user-doc -->
	 * 
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //ReviewsAdapterFactory

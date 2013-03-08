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

import org.eclipse.emf.ecore.EClass;
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
		case ReviewsPackage.REVIEW:
			return createReview();
		case ReviewsPackage.COMMENT:
			return createComment();
		case ReviewsPackage.REVIEW_ITEM:
			return createReviewItem();
		case ReviewsPackage.USER:
			return createUser();
		case ReviewsPackage.TASK_REFERENCE:
			return createTaskReference();
		case ReviewsPackage.REVIEW_GROUP:
			return createReviewGroup();
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
		case ReviewsPackage.FILE_REVISION:
			return createFileRevision();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
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
	public ITaskReference createTaskReference() {
		TaskReference taskReference = new TaskReference();
		return taskReference;
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
	public IFileRevision createFileRevision() {
		FileRevision fileRevision = new FileRevision();
		return fileRevision;
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

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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.ICommentType;
import org.eclipse.mylyn.reviews.core.model.IDated;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileRevision;
import org.eclipse.mylyn.reviews.core.model.IIndexed;
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ILineRange;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.IModelVersioning;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewComponent;
import org.eclipse.mylyn.reviews.core.model.IReviewGroup;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IReviewState;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.model.ITaskReference;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.mylyn.reviews.core.model.ITopicContainer;
import org.eclipse.mylyn.reviews.core.model.IUser;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * 
 * @see org.eclipse.mylyn.reviews.core.model.IReviewsFactory
 * @generated
 */
public class ReviewsPackage extends EPackageImpl {
	/**
	 * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static final String eNAME = "reviews"; //$NON-NLS-1$

	/**
	 * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static final String eNS_URI = "http://eclipse.org/mylyn/reviews/core/1.0"; //$NON-NLS-1$

	/**
	 * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static final String eNS_PREFIX = "reviews"; //$NON-NLS-1$

	/**
	 * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static final ReviewsPackage eINSTANCE = org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage.init();

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewComponent
	 * <em>Review Component</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewComponent
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewComponent()
	 * @generated
	 */
	public static final int REVIEW_COMPONENT = 11;

	/**
	 * The feature id for the '<em><b>Enabled</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_COMPONENT__ENABLED = 0;

	/**
	 * The number of structural features of the '<em>Review Component</em>' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_COMPONENT_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.TopicContainer
	 * <em>Topic Container</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.TopicContainer
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getTopicContainer()
	 * @generated
	 */
	public static final int TOPIC_CONTAINER = 0;

	/**
	 * The feature id for the '<em><b>Enabled</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC_CONTAINER__ENABLED = REVIEW_COMPONENT__ENABLED;

	/**
	 * The feature id for the '<em><b>All Comments</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC_CONTAINER__ALL_COMMENTS = REVIEW_COMPONENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Topics</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC_CONTAINER__TOPICS = REVIEW_COMPONENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Direct Topics</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC_CONTAINER__DIRECT_TOPICS = REVIEW_COMPONENT_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Topic Container</em>' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC_CONTAINER_FEATURE_COUNT = REVIEW_COMPONENT_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Review <em>Review</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.Review
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReview()
	 * @generated
	 */
	public static final int REVIEW = 1;

	/**
	 * The feature id for the '<em><b>Enabled</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__ENABLED = TOPIC_CONTAINER__ENABLED;

	/**
	 * The feature id for the '<em><b>All Comments</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__ALL_COMMENTS = TOPIC_CONTAINER__ALL_COMMENTS;

	/**
	 * The feature id for the '<em><b>Topics</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__TOPICS = TOPIC_CONTAINER__TOPICS;

	/**
	 * The feature id for the '<em><b>Direct Topics</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__DIRECT_TOPICS = TOPIC_CONTAINER__DIRECT_TOPICS;

	/**
	 * The feature id for the '<em><b>Creation Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__CREATION_DATE = TOPIC_CONTAINER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Modification Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__MODIFICATION_DATE = TOPIC_CONTAINER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Items</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__ITEMS = TOPIC_CONTAINER_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Review Task</b></em>' containment reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__REVIEW_TASK = TOPIC_CONTAINER_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>State</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__STATE = TOPIC_CONTAINER_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__ID = TOPIC_CONTAINER_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Owner</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__OWNER = TOPIC_CONTAINER_FEATURE_COUNT + 6;

	/**
	 * The number of structural features of the '<em>Review</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_FEATURE_COUNT = TOPIC_CONTAINER_FEATURE_COUNT + 7;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Comment <em>Comment</em>}'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.Comment
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getComment()
	 * @generated
	 */
	public static final int COMMENT = 2;

	/**
	 * The feature id for the '<em><b>Enabled</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__ENABLED = REVIEW_COMPONENT__ENABLED;

	/**
	 * The feature id for the '<em><b>Index</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__INDEX = REVIEW_COMPONENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Creation Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__CREATION_DATE = REVIEW_COMPONENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Modification Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__MODIFICATION_DATE = REVIEW_COMPONENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Author</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__AUTHOR = REVIEW_COMPONENT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Type</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__TYPE = REVIEW_COMPONENT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__DESCRIPTION = REVIEW_COMPONENT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__ID = REVIEW_COMPONENT_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Replies</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__REPLIES = REVIEW_COMPONENT_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Draft</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__DRAFT = REVIEW_COMPONENT_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Parent Topic</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__PARENT_TOPIC = REVIEW_COMPONENT_FEATURE_COUNT + 9;

	/**
	 * The number of structural features of the '<em>Comment</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT_FEATURE_COUNT = REVIEW_COMPONENT_FEATURE_COUNT + 10;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItem <em>Review Item</em>}
	 * ' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewItem
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewItem()
	 * @generated
	 */
	public static final int REVIEW_ITEM = 3;

	/**
	 * The feature id for the '<em><b>Enabled</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__ENABLED = TOPIC_CONTAINER__ENABLED;

	/**
	 * The feature id for the '<em><b>All Comments</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__ALL_COMMENTS = TOPIC_CONTAINER__ALL_COMMENTS;

	/**
	 * The feature id for the '<em><b>Topics</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__TOPICS = TOPIC_CONTAINER__TOPICS;

	/**
	 * The feature id for the '<em><b>Direct Topics</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__DIRECT_TOPICS = TOPIC_CONTAINER__DIRECT_TOPICS;

	/**
	 * The feature id for the '<em><b>Added By</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__ADDED_BY = TOPIC_CONTAINER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Review</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__REVIEW = TOPIC_CONTAINER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__NAME = TOPIC_CONTAINER_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__ID = TOPIC_CONTAINER_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Review Item</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_FEATURE_COUNT = TOPIC_CONTAINER_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.core.model.IIndexed <em>Indexed</em>}' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.core.model.IIndexed
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getIndexed()
	 * @generated
	 */
	public static final int INDEXED = 18;

	/**
	 * The feature id for the '<em><b>Index</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int INDEXED__INDEX = 0;

	/**
	 * The number of structural features of the '<em>Indexed</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int INDEXED_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Location <em>Location</em>}'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.Location
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getLocation()
	 * @generated
	 */
	public static final int LOCATION = 4;

	/**
	 * The feature id for the '<em><b>Index</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int LOCATION__INDEX = INDEXED__INDEX;

	/**
	 * The number of structural features of the '<em>Location</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int LOCATION_FEATURE_COUNT = INDEXED_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.User <em>User</em>}' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.User
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getUser()
	 * @generated
	 */
	public static final int USER = 5;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int USER__ID = 0;

	/**
	 * The feature id for the '<em><b>Email</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int USER__EMAIL = 1;

	/**
	 * The feature id for the '<em><b>Display Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int USER__DISPLAY_NAME = 2;

	/**
	 * The number of structural features of the '<em>User</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int USER_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.TaskReference
	 * <em>Task Reference</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.TaskReference
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getTaskReference()
	 * @generated
	 */
	public static final int TASK_REFERENCE = 6;

	/**
	 * The feature id for the '<em><b>Enabled</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TASK_REFERENCE__ENABLED = REVIEW_COMPONENT__ENABLED;

	/**
	 * The feature id for the '<em><b>Task Id</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TASK_REFERENCE__TASK_ID = REVIEW_COMPONENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Repository URL</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TASK_REFERENCE__REPOSITORY_URL = REVIEW_COMPONENT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Task Reference</em>' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TASK_REFERENCE_FEATURE_COUNT = REVIEW_COMPONENT_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewState
	 * <em>Review State</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewState
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewState()
	 * @generated
	 */
	public static final int REVIEW_STATE = 7;

	/**
	 * The feature id for the '<em><b>Enabled</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_STATE__ENABLED = REVIEW_COMPONENT__ENABLED;

	/**
	 * The number of structural features of the '<em>Review State</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_STATE_FEATURE_COUNT = REVIEW_COMPONENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewGroup
	 * <em>Review Group</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewGroup
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewGroup()
	 * @generated
	 */
	public static final int REVIEW_GROUP = 8;

	/**
	 * The feature id for the '<em><b>Enabled</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_GROUP__ENABLED = REVIEW_COMPONENT__ENABLED;

	/**
	 * The feature id for the '<em><b>Reviews</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_GROUP__REVIEWS = REVIEW_COMPONENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Review Group Task</b></em>' containment reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_GROUP__REVIEW_GROUP_TASK = REVIEW_COMPONENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_GROUP__DESCRIPTION = REVIEW_COMPONENT_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Review Group</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_GROUP_FEATURE_COUNT = REVIEW_COMPONENT_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.CommentType
	 * <em>Comment Type</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.CommentType
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getCommentType()
	 * @generated
	 */
	public static final int COMMENT_TYPE = 9;

	/**
	 * The number of structural features of the '<em>Comment Type</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT_TYPE_FEATURE_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Topic <em>Topic</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.Topic
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getTopic()
	 * @generated
	 */
	public static final int TOPIC = 10;

	/**
	 * The feature id for the '<em><b>Enabled</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__ENABLED = COMMENT__ENABLED;

	/**
	 * The feature id for the '<em><b>Index</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__INDEX = COMMENT__INDEX;

	/**
	 * The feature id for the '<em><b>Creation Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__CREATION_DATE = COMMENT__CREATION_DATE;

	/**
	 * The feature id for the '<em><b>Modification Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__MODIFICATION_DATE = COMMENT__MODIFICATION_DATE;

	/**
	 * The feature id for the '<em><b>Author</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__AUTHOR = COMMENT__AUTHOR;

	/**
	 * The feature id for the '<em><b>Type</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__TYPE = COMMENT__TYPE;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__DESCRIPTION = COMMENT__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__ID = COMMENT__ID;

	/**
	 * The feature id for the '<em><b>Replies</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__REPLIES = COMMENT__REPLIES;

	/**
	 * The feature id for the '<em><b>Draft</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__DRAFT = COMMENT__DRAFT;

	/**
	 * The feature id for the '<em><b>Parent Topic</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__PARENT_TOPIC = COMMENT__PARENT_TOPIC;

	/**
	 * The feature id for the '<em><b>Task</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__TASK = COMMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Locations</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__LOCATIONS = COMMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Comments</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__COMMENTS = COMMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Review</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__REVIEW = COMMENT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Title</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__TITLE = COMMENT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Item</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__ITEM = COMMENT_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>Topic</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC_FEATURE_COUNT = COMMENT_FEATURE_COUNT + 6;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.FileItem <em>File Item</em>}'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.FileItem
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getFileItem()
	 * @generated
	 */
	public static final int FILE_ITEM = 12;

	/**
	 * The feature id for the '<em><b>Enabled</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_ITEM__ENABLED = REVIEW_ITEM__ENABLED;

	/**
	 * The feature id for the '<em><b>All Comments</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_ITEM__ALL_COMMENTS = REVIEW_ITEM__ALL_COMMENTS;

	/**
	 * The feature id for the '<em><b>Topics</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_ITEM__TOPICS = REVIEW_ITEM__TOPICS;

	/**
	 * The feature id for the '<em><b>Direct Topics</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_ITEM__DIRECT_TOPICS = REVIEW_ITEM__DIRECT_TOPICS;

	/**
	 * The feature id for the '<em><b>Added By</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_ITEM__ADDED_BY = REVIEW_ITEM__ADDED_BY;

	/**
	 * The feature id for the '<em><b>Review</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_ITEM__REVIEW = REVIEW_ITEM__REVIEW;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_ITEM__NAME = REVIEW_ITEM__NAME;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_ITEM__ID = REVIEW_ITEM__ID;

	/**
	 * The feature id for the '<em><b>Base</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_ITEM__BASE = REVIEW_ITEM_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_ITEM__TARGET = REVIEW_ITEM_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>File Item</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_ITEM_FEATURE_COUNT = REVIEW_ITEM_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItemSet
	 * <em>Review Item Set</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewItemSet
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewItemSet()
	 * @generated
	 */
	public static final int REVIEW_ITEM_SET = 13;

	/**
	 * The feature id for the '<em><b>Enabled</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__ENABLED = REVIEW_ITEM__ENABLED;

	/**
	 * The feature id for the '<em><b>All Comments</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__ALL_COMMENTS = REVIEW_ITEM__ALL_COMMENTS;

	/**
	 * The feature id for the '<em><b>Topics</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__TOPICS = REVIEW_ITEM__TOPICS;

	/**
	 * The feature id for the '<em><b>Direct Topics</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__DIRECT_TOPICS = REVIEW_ITEM__DIRECT_TOPICS;

	/**
	 * The feature id for the '<em><b>Added By</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__ADDED_BY = REVIEW_ITEM__ADDED_BY;

	/**
	 * The feature id for the '<em><b>Review</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__REVIEW = REVIEW_ITEM__REVIEW;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__NAME = REVIEW_ITEM__NAME;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__ID = REVIEW_ITEM__ID;

	/**
	 * The feature id for the '<em><b>Creation Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__CREATION_DATE = REVIEW_ITEM_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Modification Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__MODIFICATION_DATE = REVIEW_ITEM_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Items</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__ITEMS = REVIEW_ITEM_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Revision</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__REVISION = REVIEW_ITEM_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Review Item Set</em>' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET_FEATURE_COUNT = REVIEW_ITEM_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.LineLocation
	 * <em>Line Location</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.LineLocation
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getLineLocation()
	 * @generated
	 */
	public static final int LINE_LOCATION = 14;

	/**
	 * The feature id for the '<em><b>Index</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int LINE_LOCATION__INDEX = LOCATION__INDEX;

	/**
	 * The feature id for the '<em><b>Ranges</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int LINE_LOCATION__RANGES = LOCATION_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Range Min</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int LINE_LOCATION__RANGE_MIN = LOCATION_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Range Max</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int LINE_LOCATION__RANGE_MAX = LOCATION_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Line Location</em>' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int LINE_LOCATION_FEATURE_COUNT = LOCATION_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.LineRange <em>Line Range</em>}'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.LineRange
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getLineRange()
	 * @generated
	 */
	public static final int LINE_RANGE = 15;

	/**
	 * The feature id for the '<em><b>Start</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int LINE_RANGE__START = 0;

	/**
	 * The feature id for the '<em><b>End</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int LINE_RANGE__END = 1;

	/**
	 * The number of structural features of the '<em>Line Range</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int LINE_RANGE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.FileRevision
	 * <em>File Revision</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.FileRevision
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getFileRevision()
	 * @generated
	 */
	public static final int FILE_REVISION = 16;

	/**
	 * The feature id for the '<em><b>Enabled</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_REVISION__ENABLED = REVIEW_ITEM__ENABLED;

	/**
	 * The feature id for the '<em><b>All Comments</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_REVISION__ALL_COMMENTS = REVIEW_ITEM__ALL_COMMENTS;

	/**
	 * The feature id for the '<em><b>Topics</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_REVISION__TOPICS = REVIEW_ITEM__TOPICS;

	/**
	 * The feature id for the '<em><b>Direct Topics</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_REVISION__DIRECT_TOPICS = REVIEW_ITEM__DIRECT_TOPICS;

	/**
	 * The feature id for the '<em><b>Added By</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_REVISION__ADDED_BY = REVIEW_ITEM__ADDED_BY;

	/**
	 * The feature id for the '<em><b>Review</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_REVISION__REVIEW = REVIEW_ITEM__REVIEW;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_REVISION__NAME = REVIEW_ITEM__NAME;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_REVISION__ID = REVIEW_ITEM__ID;

	/**
	 * The feature id for the '<em><b>Path</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_REVISION__PATH = REVIEW_ITEM_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Revision</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_REVISION__REVISION = REVIEW_ITEM_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Content</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_REVISION__CONTENT = REVIEW_ITEM_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>File</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_REVISION__FILE = REVIEW_ITEM_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>File Revision</em>' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_REVISION_FEATURE_COUNT = REVIEW_ITEM_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.core.model.IModelVersioning
	 * <em>Model Versioning</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.core.model.IModelVersioning
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getModelVersioning()
	 * @generated
	 */
	public static final int MODEL_VERSIONING = 17;

	/**
	 * The feature id for the '<em><b>Fragment Version</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int MODEL_VERSIONING__FRAGMENT_VERSION = 0;

	/**
	 * The number of structural features of the '<em>Model Versioning</em>' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int MODEL_VERSIONING_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.core.model.IDated <em>Dated</em>}' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.core.model.IDated
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getDated()
	 * @generated
	 */
	public static final int DATED = 19;

	/**
	 * The feature id for the '<em><b>Creation Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int DATED__CREATION_DATE = 0;

	/**
	 * The feature id for the '<em><b>Modification Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int DATED__MODIFICATION_DATE = 1;

	/**
	 * The number of structural features of the '<em>Dated</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int DATED_FEATURE_COUNT = 2;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass topicContainerEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass reviewEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass commentEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass reviewItemEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass locationEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass userEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass taskReferenceEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass reviewStateEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass reviewGroupEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass commentTypeEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass topicEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass reviewComponentEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass fileItemEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass reviewItemSetEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass lineLocationEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass lineRangeEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass fileRevisionEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass modelVersioningEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass indexedEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass datedEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
	 * EPackage.Registry} by the package package URI value.
	 * <p>
	 * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
	 * performs initialization of the package, or returns the registered package, if one already exists. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ReviewsPackage() {
		super(eNS_URI, ((EFactory) IReviewsFactory.INSTANCE));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * <p>
	 * This method is used to initialize {@link ReviewsPackage#eINSTANCE} when that field is accessed. Clients should
	 * not invoke it directly. Instead, they should simply access that field to obtain the package. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static ReviewsPackage init() {
		if (isInited)
			return (ReviewsPackage) EPackage.Registry.INSTANCE.getEPackage(ReviewsPackage.eNS_URI);

		// Obtain or create and register package
		ReviewsPackage theReviewsPackage = (ReviewsPackage) (EPackage.Registry.INSTANCE.get(eNS_URI) instanceof ReviewsPackage
				? EPackage.Registry.INSTANCE.get(eNS_URI)
				: new ReviewsPackage());

		isInited = true;

		// Create package meta-data objects
		theReviewsPackage.createPackageContents();

		// Initialize created meta-data
		theReviewsPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theReviewsPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(ReviewsPackage.eNS_URI, theReviewsPackage);
		return theReviewsPackage;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.ITopicContainer
	 * <em>Topic Container</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Topic Container</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ITopicContainer
	 * @generated
	 */
	public EClass getTopicContainer() {
		return topicContainerEClass;
	}

	/**
	 * Returns the meta object for the reference list '
	 * {@link org.eclipse.mylyn.reviews.core.model.ITopicContainer#getAllComments <em>All Comments</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference list '<em>All Comments</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ITopicContainer#getAllComments()
	 * @see #getTopicContainer()
	 * @generated
	 */
	public EReference getTopicContainer_AllComments() {
		return (EReference) topicContainerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the reference list '
	 * {@link org.eclipse.mylyn.reviews.core.model.ITopicContainer#getTopics <em>Topics</em>}'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference list '<em>Topics</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ITopicContainer#getTopics()
	 * @see #getTopicContainer()
	 * @generated
	 */
	public EReference getTopicContainer_Topics() {
		return (EReference) topicContainerEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the reference list '
	 * {@link org.eclipse.mylyn.reviews.core.model.ITopicContainer#getDirectTopics <em>Direct Topics</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference list '<em>Direct Topics</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ITopicContainer#getDirectTopics()
	 * @see #getTopicContainer()
	 * @generated
	 */
	public EReference getTopicContainer_DirectTopics() {
		return (EReference) topicContainerEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IReview <em>Review</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Review</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReview
	 * @generated
	 */
	public EClass getReview() {
		return reviewEClass;
	}

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.mylyn.reviews.core.model.IReview#getItems
	 * <em>Items</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference list '<em>Items</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReview#getItems()
	 * @see #getReview()
	 * @generated
	 */
	public EReference getReview_Items() {
		return (EReference) reviewEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the containment reference '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReview#getReviewTask <em>Review Task</em>}'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference '<em>Review Task</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReview#getReviewTask()
	 * @see #getReview()
	 * @generated
	 */
	public EReference getReview_ReviewTask() {
		return (EReference) reviewEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the containment reference '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReview#getState <em>State</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the meta object for the containment reference '<em>State</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReview#getState()
	 * @see #getReview()
	 * @generated
	 */
	public EReference getReview_State() {
		return (EReference) reviewEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IReview#getId <em>Id</em>}
	 * '. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReview#getId()
	 * @see #getReview()
	 * @generated
	 */
	public EAttribute getReview_Id() {
		return (EAttribute) reviewEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.reviews.core.model.IReview#getOwner
	 * <em>Owner</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Owner</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReview#getOwner()
	 * @see #getReview()
	 * @generated
	 */
	public EReference getReview_Owner() {
		return (EReference) reviewEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IComment <em>Comment</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Comment</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IComment
	 * @generated
	 */
	public EClass getComment() {
		return commentEClass;
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.reviews.core.model.IComment#getAuthor
	 * <em>Author</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Author</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IComment#getAuthor()
	 * @see #getComment()
	 * @generated
	 */
	public EReference getComment_Author() {
		return (EReference) commentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the containment reference '
	 * {@link org.eclipse.mylyn.reviews.core.model.IComment#getType <em>Type</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the meta object for the containment reference '<em>Type</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IComment#getType()
	 * @see #getComment()
	 * @generated
	 */
	public EReference getComment_Type() {
		return (EReference) commentEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IComment#getDescription
	 * <em>Description</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IComment#getDescription()
	 * @see #getComment()
	 * @generated
	 */
	public EAttribute getComment_Description() {
		return (EAttribute) commentEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IComment#getId
	 * <em>Id</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IComment#getId()
	 * @see #getComment()
	 * @generated
	 */
	public EAttribute getComment_Id() {
		return (EAttribute) commentEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.reviews.core.model.IComment#getReplies <em>Replies</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Replies</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IComment#getReplies()
	 * @see #getComment()
	 * @generated
	 */
	public EReference getComment_Replies() {
		return (EReference) commentEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IComment#isDraft
	 * <em>Draft</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Draft</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IComment#isDraft()
	 * @see #getComment()
	 * @generated
	 */
	public EAttribute getComment_Draft() {
		return (EAttribute) commentEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.reviews.core.model.IComment#getParentTopic
	 * <em>Parent Topic</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Parent Topic</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IComment#getParentTopic()
	 * @see #getComment()
	 * @generated
	 */
	public EReference getComment_ParentTopic() {
		return (EReference) commentEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IReviewItem <em>Review Item</em>}
	 * '. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Review Item</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItem
	 * @generated
	 */
	public EClass getReviewItem() {
		return reviewItemEClass;
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.reviews.core.model.IReviewItem#getAddedBy
	 * <em>Added By</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Added By</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItem#getAddedBy()
	 * @see #getReviewItem()
	 * @generated
	 */
	public EReference getReviewItem_AddedBy() {
		return (EReference) reviewItemEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.reviews.core.model.IReviewItem#getReview
	 * <em>Review</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Review</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItem#getReview()
	 * @see #getReviewItem()
	 * @generated
	 */
	public EReference getReviewItem_Review() {
		return (EReference) reviewItemEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IReviewItem#getName
	 * <em>Name</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItem#getName()
	 * @see #getReviewItem()
	 * @generated
	 */
	public EAttribute getReviewItem_Name() {
		return (EAttribute) reviewItemEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IReviewItem#getId
	 * <em>Id</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItem#getId()
	 * @see #getReviewItem()
	 * @generated
	 */
	public EAttribute getReviewItem_Id() {
		return (EAttribute) reviewItemEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.ILocation <em>Location</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Location</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ILocation
	 * @generated
	 */
	public EClass getLocation() {
		return locationEClass;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IUser <em>User</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>User</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IUser
	 * @generated
	 */
	public EClass getUser() {
		return userEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IUser#getId <em>Id</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IUser#getId()
	 * @see #getUser()
	 * @generated
	 */
	public EAttribute getUser_Id() {
		return (EAttribute) userEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IUser#getEmail
	 * <em>Email</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Email</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IUser#getEmail()
	 * @see #getUser()
	 * @generated
	 */
	public EAttribute getUser_Email() {
		return (EAttribute) userEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IUser#getDisplayName
	 * <em>Display Name</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Display Name</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IUser#getDisplayName()
	 * @see #getUser()
	 * @generated
	 */
	public EAttribute getUser_DisplayName() {
		return (EAttribute) userEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.ITaskReference
	 * <em>Task Reference</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Task Reference</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ITaskReference
	 * @generated
	 */
	public EClass getTaskReference() {
		return taskReferenceEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.ITaskReference#getTaskId
	 * <em>Task Id</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Task Id</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ITaskReference#getTaskId()
	 * @see #getTaskReference()
	 * @generated
	 */
	public EAttribute getTaskReference_TaskId() {
		return (EAttribute) taskReferenceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.mylyn.reviews.core.model.ITaskReference#getRepositoryURL <em>Repository URL</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Repository URL</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ITaskReference#getRepositoryURL()
	 * @see #getTaskReference()
	 * @generated
	 */
	public EAttribute getTaskReference_RepositoryURL() {
		return (EAttribute) taskReferenceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IReviewState
	 * <em>Review State</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Review State</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewState
	 * @generated
	 */
	public EClass getReviewState() {
		return reviewStateEClass;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IReviewGroup
	 * <em>Review Group</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Review Group</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewGroup
	 * @generated
	 */
	public EClass getReviewGroup() {
		return reviewGroupEClass;
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReviewGroup#getReviews <em>Reviews</em>}'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Reviews</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewGroup#getReviews()
	 * @see #getReviewGroup()
	 * @generated
	 */
	public EReference getReviewGroup_Reviews() {
		return (EReference) reviewGroupEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the containment reference '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReviewGroup#getReviewGroupTask <em>Review Group Task</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference '<em>Review Group Task</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewGroup#getReviewGroupTask()
	 * @see #getReviewGroup()
	 * @generated
	 */
	public EReference getReviewGroup_ReviewGroupTask() {
		return (EReference) reviewGroupEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReviewGroup#getDescription <em>Description</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewGroup#getDescription()
	 * @see #getReviewGroup()
	 * @generated
	 */
	public EAttribute getReviewGroup_Description() {
		return (EAttribute) reviewGroupEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.ICommentType
	 * <em>Comment Type</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Comment Type</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ICommentType
	 * @generated
	 */
	public EClass getCommentType() {
		return commentTypeEClass;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.ITopic <em>Topic</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Topic</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ITopic
	 * @generated
	 */
	public EClass getTopic() {
		return topicEClass;
	}

	/**
	 * Returns the meta object for the containment reference '
	 * {@link org.eclipse.mylyn.reviews.core.model.ITopic#getTask <em>Task</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the meta object for the containment reference '<em>Task</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ITopic#getTask()
	 * @see #getTopic()
	 * @generated
	 */
	public EReference getTopic_Task() {
		return (EReference) topicEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.reviews.core.model.ITopic#getLocations <em>Locations</em>}'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Locations</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ITopic#getLocations()
	 * @see #getTopic()
	 * @generated
	 */
	public EReference getTopic_Locations() {
		return (EReference) topicEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.mylyn.reviews.core.model.ITopic#getComments
	 * <em>Comments</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference list '<em>Comments</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ITopic#getComments()
	 * @see #getTopic()
	 * @generated
	 */
	public EReference getTopic_Comments() {
		return (EReference) topicEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.reviews.core.model.ITopic#getReview
	 * <em>Review</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Review</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ITopic#getReview()
	 * @see #getTopic()
	 * @generated
	 */
	public EReference getTopic_Review() {
		return (EReference) topicEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.ITopic#getTitle
	 * <em>Title</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Title</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ITopic#getTitle()
	 * @see #getTopic()
	 * @generated
	 */
	public EAttribute getTopic_Title() {
		return (EAttribute) topicEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.reviews.core.model.ITopic#getItem
	 * <em>Item</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Item</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ITopic#getItem()
	 * @see #getTopic()
	 * @generated
	 */
	public EReference getTopic_Item() {
		return (EReference) topicEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IReviewComponent
	 * <em>Review Component</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Review Component</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewComponent
	 * @generated
	 */
	public EClass getReviewComponent() {
		return reviewComponentEClass;
	}

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReviewComponent#isEnabled <em>Enabled</em>}'. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Enabled</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewComponent#isEnabled()
	 * @see #getReviewComponent()
	 * @generated
	 */
	public EAttribute getReviewComponent_Enabled() {
		return (EAttribute) reviewComponentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IFileItem <em>File Item</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>File Item</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileItem
	 * @generated
	 */
	public EClass getFileItem() {
		return fileItemEClass;
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.reviews.core.model.IFileItem#getBase
	 * <em>Base</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Base</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileItem#getBase()
	 * @see #getFileItem()
	 * @generated
	 */
	public EReference getFileItem_Base() {
		return (EReference) fileItemEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.reviews.core.model.IFileItem#getTarget
	 * <em>Target</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Target</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileItem#getTarget()
	 * @see #getFileItem()
	 * @generated
	 */
	public EReference getFileItem_Target() {
		return (EReference) fileItemEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet
	 * <em>Review Item Set</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Review Item Set</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItemSet
	 * @generated
	 */
	public EClass getReviewItemSet() {
		return reviewItemSetEClass;
	}

	/**
	 * Returns the meta object for the reference list '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getItems <em>Items</em>}'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference list '<em>Items</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getItems()
	 * @see #getReviewItemSet()
	 * @generated
	 */
	public EReference getReviewItemSet_Items() {
		return (EReference) reviewItemSetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getRevision <em>Revision</em>}'. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Revision</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getRevision()
	 * @see #getReviewItemSet()
	 * @generated
	 */
	public EAttribute getReviewItemSet_Revision() {
		return (EAttribute) reviewItemSetEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.ILineLocation
	 * <em>Line Location</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Line Location</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ILineLocation
	 * @generated
	 */
	public EClass getLineLocation() {
		return lineLocationEClass;
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.reviews.core.model.ILineLocation#getRanges <em>Ranges</em>}'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Ranges</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ILineLocation#getRanges()
	 * @see #getLineLocation()
	 * @generated
	 */
	public EReference getLineLocation_Ranges() {
		return (EReference) lineLocationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.ILineLocation#getRangeMin
	 * <em>Range Min</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Range Min</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ILineLocation#getRangeMin()
	 * @see #getLineLocation()
	 * @generated
	 */
	public EAttribute getLineLocation_RangeMin() {
		return (EAttribute) lineLocationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.ILineLocation#getRangeMax
	 * <em>Range Max</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Range Max</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ILineLocation#getRangeMax()
	 * @see #getLineLocation()
	 * @generated
	 */
	public EAttribute getLineLocation_RangeMax() {
		return (EAttribute) lineLocationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.ILineRange <em>Line Range</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Line Range</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ILineRange
	 * @generated
	 */
	public EClass getLineRange() {
		return lineRangeEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.ILineRange#getStart
	 * <em>Start</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Start</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ILineRange#getStart()
	 * @see #getLineRange()
	 * @generated
	 */
	public EAttribute getLineRange_Start() {
		return (EAttribute) lineRangeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.ILineRange#getEnd
	 * <em>End</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>End</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ILineRange#getEnd()
	 * @see #getLineRange()
	 * @generated
	 */
	public EAttribute getLineRange_End() {
		return (EAttribute) lineRangeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IFileRevision
	 * <em>File Revision</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>File Revision</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileRevision
	 * @generated
	 */
	public EClass getFileRevision() {
		return fileRevisionEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IFileRevision#getPath
	 * <em>Path</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Path</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileRevision#getPath()
	 * @see #getFileRevision()
	 * @generated
	 */
	public EAttribute getFileRevision_Path() {
		return (EAttribute) fileRevisionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IFileRevision#getRevision
	 * <em>Revision</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Revision</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileRevision#getRevision()
	 * @see #getFileRevision()
	 * @generated
	 */
	public EAttribute getFileRevision_Revision() {
		return (EAttribute) fileRevisionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IFileRevision#getContent
	 * <em>Content</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Content</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileRevision#getContent()
	 * @see #getFileRevision()
	 * @generated
	 */
	public EAttribute getFileRevision_Content() {
		return (EAttribute) fileRevisionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.reviews.core.model.IFileRevision#getFile
	 * <em>File</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>File</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileRevision#getFile()
	 * @see #getFileRevision()
	 * @generated
	 */
	public EReference getFileRevision_File() {
		return (EReference) fileRevisionEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IModelVersioning
	 * <em>Model Versioning</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Model Versioning</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IModelVersioning
	 * @generated
	 */
	public EClass getModelVersioning() {
		return modelVersioningEClass;
	}

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.mylyn.reviews.core.model.IModelVersioning#getFragmentVersion <em>Fragment Version</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Fragment Version</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IModelVersioning#getFragmentVersion()
	 * @see #getModelVersioning()
	 * @generated
	 */
	public EAttribute getModelVersioning_FragmentVersion() {
		return (EAttribute) modelVersioningEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IIndexed <em>Indexed</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Indexed</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IIndexed
	 * @generated
	 */
	public EClass getIndexed() {
		return indexedEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IIndexed#getIndex
	 * <em>Index</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Index</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IIndexed#getIndex()
	 * @see #getIndexed()
	 * @generated
	 */
	public EAttribute getIndexed_Index() {
		return (EAttribute) indexedEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IDated <em>Dated</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Dated</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IDated
	 * @generated
	 */
	public EClass getDated() {
		return datedEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IDated#getCreationDate
	 * <em>Creation Date</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Creation Date</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IDated#getCreationDate()
	 * @see #getDated()
	 * @generated
	 */
	public EAttribute getDated_CreationDate() {
		return (EAttribute) datedEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.mylyn.reviews.core.model.IDated#getModificationDate <em>Modification Date</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Modification Date</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IDated#getModificationDate()
	 * @see #getDated()
	 * @generated
	 */
	public EAttribute getDated_ModificationDate() {
		return (EAttribute) datedEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	public IReviewsFactory getReviewsFactory() {
		return (IReviewsFactory) getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package. This method is guarded to have no affect on any invocation but
	 * its first. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated)
			return;
		isCreated = true;

		// Create classes and their features
		topicContainerEClass = createEClass(TOPIC_CONTAINER);
		createEReference(topicContainerEClass, TOPIC_CONTAINER__ALL_COMMENTS);
		createEReference(topicContainerEClass, TOPIC_CONTAINER__TOPICS);
		createEReference(topicContainerEClass, TOPIC_CONTAINER__DIRECT_TOPICS);

		reviewEClass = createEClass(REVIEW);
		createEReference(reviewEClass, REVIEW__ITEMS);
		createEReference(reviewEClass, REVIEW__REVIEW_TASK);
		createEReference(reviewEClass, REVIEW__STATE);
		createEAttribute(reviewEClass, REVIEW__ID);
		createEReference(reviewEClass, REVIEW__OWNER);

		commentEClass = createEClass(COMMENT);
		createEReference(commentEClass, COMMENT__AUTHOR);
		createEReference(commentEClass, COMMENT__TYPE);
		createEAttribute(commentEClass, COMMENT__DESCRIPTION);
		createEAttribute(commentEClass, COMMENT__ID);
		createEReference(commentEClass, COMMENT__REPLIES);
		createEAttribute(commentEClass, COMMENT__DRAFT);
		createEReference(commentEClass, COMMENT__PARENT_TOPIC);

		reviewItemEClass = createEClass(REVIEW_ITEM);
		createEReference(reviewItemEClass, REVIEW_ITEM__ADDED_BY);
		createEReference(reviewItemEClass, REVIEW_ITEM__REVIEW);
		createEAttribute(reviewItemEClass, REVIEW_ITEM__NAME);
		createEAttribute(reviewItemEClass, REVIEW_ITEM__ID);

		locationEClass = createEClass(LOCATION);

		userEClass = createEClass(USER);
		createEAttribute(userEClass, USER__ID);
		createEAttribute(userEClass, USER__EMAIL);
		createEAttribute(userEClass, USER__DISPLAY_NAME);

		taskReferenceEClass = createEClass(TASK_REFERENCE);
		createEAttribute(taskReferenceEClass, TASK_REFERENCE__TASK_ID);
		createEAttribute(taskReferenceEClass, TASK_REFERENCE__REPOSITORY_URL);

		reviewStateEClass = createEClass(REVIEW_STATE);

		reviewGroupEClass = createEClass(REVIEW_GROUP);
		createEReference(reviewGroupEClass, REVIEW_GROUP__REVIEWS);
		createEReference(reviewGroupEClass, REVIEW_GROUP__REVIEW_GROUP_TASK);
		createEAttribute(reviewGroupEClass, REVIEW_GROUP__DESCRIPTION);

		commentTypeEClass = createEClass(COMMENT_TYPE);

		topicEClass = createEClass(TOPIC);
		createEReference(topicEClass, TOPIC__TASK);
		createEReference(topicEClass, TOPIC__LOCATIONS);
		createEReference(topicEClass, TOPIC__COMMENTS);
		createEReference(topicEClass, TOPIC__REVIEW);
		createEAttribute(topicEClass, TOPIC__TITLE);
		createEReference(topicEClass, TOPIC__ITEM);

		reviewComponentEClass = createEClass(REVIEW_COMPONENT);
		createEAttribute(reviewComponentEClass, REVIEW_COMPONENT__ENABLED);

		fileItemEClass = createEClass(FILE_ITEM);
		createEReference(fileItemEClass, FILE_ITEM__BASE);
		createEReference(fileItemEClass, FILE_ITEM__TARGET);

		reviewItemSetEClass = createEClass(REVIEW_ITEM_SET);
		createEReference(reviewItemSetEClass, REVIEW_ITEM_SET__ITEMS);
		createEAttribute(reviewItemSetEClass, REVIEW_ITEM_SET__REVISION);

		lineLocationEClass = createEClass(LINE_LOCATION);
		createEReference(lineLocationEClass, LINE_LOCATION__RANGES);
		createEAttribute(lineLocationEClass, LINE_LOCATION__RANGE_MIN);
		createEAttribute(lineLocationEClass, LINE_LOCATION__RANGE_MAX);

		lineRangeEClass = createEClass(LINE_RANGE);
		createEAttribute(lineRangeEClass, LINE_RANGE__START);
		createEAttribute(lineRangeEClass, LINE_RANGE__END);

		fileRevisionEClass = createEClass(FILE_REVISION);
		createEAttribute(fileRevisionEClass, FILE_REVISION__PATH);
		createEAttribute(fileRevisionEClass, FILE_REVISION__REVISION);
		createEAttribute(fileRevisionEClass, FILE_REVISION__CONTENT);
		createEReference(fileRevisionEClass, FILE_REVISION__FILE);

		modelVersioningEClass = createEClass(MODEL_VERSIONING);
		createEAttribute(modelVersioningEClass, MODEL_VERSIONING__FRAGMENT_VERSION);

		indexedEClass = createEClass(INDEXED);
		createEAttribute(indexedEClass, INDEXED__INDEX);

		datedEClass = createEClass(DATED);
		createEAttribute(datedEClass, DATED__CREATION_DATE);
		createEAttribute(datedEClass, DATED__MODIFICATION_DATE);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model. This method is guarded to have no affect on any
	 * invocation but its first. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized)
			return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		topicContainerEClass.getESuperTypes().add(this.getReviewComponent());
		reviewEClass.getESuperTypes().add(this.getTopicContainer());
		reviewEClass.getESuperTypes().add(this.getDated());
		commentEClass.getESuperTypes().add(this.getReviewComponent());
		commentEClass.getESuperTypes().add(this.getIndexed());
		commentEClass.getESuperTypes().add(this.getDated());
		reviewItemEClass.getESuperTypes().add(this.getTopicContainer());
		locationEClass.getESuperTypes().add(this.getIndexed());
		taskReferenceEClass.getESuperTypes().add(this.getReviewComponent());
		reviewStateEClass.getESuperTypes().add(this.getReviewComponent());
		reviewGroupEClass.getESuperTypes().add(this.getReviewComponent());
		topicEClass.getESuperTypes().add(this.getComment());
		fileItemEClass.getESuperTypes().add(this.getReviewItem());
		reviewItemSetEClass.getESuperTypes().add(this.getReviewItem());
		reviewItemSetEClass.getESuperTypes().add(this.getDated());
		lineLocationEClass.getESuperTypes().add(this.getLocation());
		fileRevisionEClass.getESuperTypes().add(this.getReviewItem());

		// Initialize classes and features; add operations and parameters
		initEClass(topicContainerEClass, ITopicContainer.class,
				"TopicContainer", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getTopicContainer_AllComments(),
				this.getComment(),
				null,
				"allComments", null, 0, -1, ITopicContainer.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getTopicContainer_Topics(),
				this.getTopic(),
				null,
				"topics", null, 0, -1, ITopicContainer.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getTopicContainer_DirectTopics(),
				this.getTopic(),
				this.getTopic_Item(),
				"directTopics", null, 0, -1, ITopicContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		EOperation op = addEOperation(topicContainerEClass, this.getTopic(),
				"createTopicComment", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
		addEParameter(op, this.getLocation(), "initalLocation", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
		addEParameter(op, ecorePackage.getEString(), "commentText", 1, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

		initEClass(reviewEClass, IReview.class, "Review", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getReview_Items(),
				this.getReviewItem(),
				null,
				"items", null, 0, -1, IReview.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getReview_ReviewTask(),
				this.getTaskReference(),
				null,
				"reviewTask", null, 0, 1, IReview.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getReview_State(),
				this.getReviewState(),
				null,
				"state", null, 1, 1, IReview.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getReview_Id(),
				ecorePackage.getEString(),
				"id", null, 0, 1, IReview.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getReview_Owner(),
				this.getUser(),
				null,
				"owner", null, 0, 1, IReview.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(commentEClass, IComment.class, "Comment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getComment_Author(),
				this.getUser(),
				null,
				"author", null, 1, 1, IComment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getComment_Type(),
				this.getCommentType(),
				null,
				"type", null, 1, 1, IComment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getComment_Description(),
				ecorePackage.getEString(),
				"description", null, 0, 1, IComment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getComment_Id(),
				ecorePackage.getEString(),
				"id", null, 0, 1, IComment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getComment_Replies(),
				this.getComment(),
				null,
				"replies", null, 0, -1, IComment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getComment_Draft(),
				ecorePackage.getEBoolean(),
				"draft", null, 0, 1, IComment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getComment_ParentTopic(),
				this.getTopic(),
				this.getTopic_Comments(),
				"parentTopic", null, 0, 1, IComment.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(reviewItemEClass, IReviewItem.class,
				"ReviewItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getReviewItem_AddedBy(),
				this.getUser(),
				null,
				"addedBy", null, 1, 1, IReviewItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getReviewItem_Review(),
				this.getReview(),
				null,
				"review", null, 1, 1, IReviewItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getReviewItem_Name(),
				ecorePackage.getEString(),
				"name", null, 0, 1, IReviewItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getReviewItem_Id(),
				ecorePackage.getEString(),
				"id", null, 0, 1, IReviewItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(locationEClass, ILocation.class, "Location", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(userEClass, IUser.class, "User", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getUser_Id(),
				ecorePackage.getEString(),
				"id", null, 0, 1, IUser.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getUser_Email(),
				ecorePackage.getEString(),
				"email", null, 0, 1, IUser.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getUser_DisplayName(),
				ecorePackage.getEString(),
				"displayName", null, 0, 1, IUser.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(taskReferenceEClass, ITaskReference.class,
				"TaskReference", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getTaskReference_TaskId(),
				ecorePackage.getEString(),
				"taskId", null, 0, 1, ITaskReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getTaskReference_RepositoryURL(),
				ecorePackage.getEString(),
				"repositoryURL", null, 0, 1, ITaskReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(reviewStateEClass, IReviewState.class,
				"ReviewState", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(reviewGroupEClass, IReviewGroup.class,
				"ReviewGroup", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getReviewGroup_Reviews(),
				this.getReview(),
				null,
				"reviews", null, 0, -1, IReviewGroup.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getReviewGroup_ReviewGroupTask(),
				this.getTaskReference(),
				null,
				"reviewGroupTask", null, 0, 1, IReviewGroup.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getReviewGroup_Description(),
				ecorePackage.getEString(),
				"description", null, 0, 1, IReviewGroup.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(commentTypeEClass, ICommentType.class,
				"CommentType", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(topicEClass, ITopic.class, "Topic", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getTopic_Task(),
				this.getTaskReference(),
				null,
				"task", null, 0, 1, ITopic.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getTopic_Locations(),
				this.getLocation(),
				null,
				"locations", null, 0, -1, ITopic.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getTopic_Comments(),
				this.getComment(),
				this.getComment_ParentTopic(),
				"comments", null, 0, -1, ITopic.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getTopic_Review(),
				this.getReview(),
				null,
				"review", null, 1, 1, ITopic.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getTopic_Title(),
				ecorePackage.getEString(),
				"title", null, 0, 1, ITopic.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getTopic_Item(),
				this.getTopicContainer(),
				this.getTopicContainer_DirectTopics(),
				"item", null, 0, 1, ITopic.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(reviewComponentEClass, IReviewComponent.class,
				"ReviewComponent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getReviewComponent_Enabled(),
				ecorePackage.getEBoolean(),
				"enabled", "true", 0, 1, IReviewComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$

		initEClass(fileItemEClass, IFileItem.class,
				"FileItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getFileItem_Base(),
				this.getFileRevision(),
				null,
				"base", null, 0, 1, IFileItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getFileItem_Target(),
				this.getFileRevision(),
				null,
				"target", null, 0, 1, IFileItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(reviewItemSetEClass, IReviewItemSet.class,
				"ReviewItemSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getReviewItemSet_Items(),
				this.getReviewItem(),
				null,
				"items", null, 0, -1, IReviewItemSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getReviewItemSet_Revision(),
				ecorePackage.getEString(),
				"revision", "", 0, 1, IReviewItemSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$

		initEClass(lineLocationEClass, ILineLocation.class,
				"LineLocation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getLineLocation_Ranges(),
				this.getLineRange(),
				null,
				"ranges", null, 0, -1, ILineLocation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getLineLocation_RangeMin(),
				ecorePackage.getEInt(),
				"rangeMin", null, 1, 1, ILineLocation.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getLineLocation_RangeMax(),
				ecorePackage.getEInt(),
				"rangeMax", null, 1, 1, ILineLocation.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(lineRangeEClass, ILineRange.class,
				"LineRange", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getLineRange_Start(),
				ecorePackage.getEInt(),
				"start", null, 0, 1, ILineRange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getLineRange_End(),
				ecorePackage.getEInt(),
				"end", null, 0, 1, ILineRange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(fileRevisionEClass, IFileRevision.class,
				"FileRevision", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getFileRevision_Path(),
				ecorePackage.getEString(),
				"path", null, 0, 1, IFileRevision.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getFileRevision_Revision(),
				ecorePackage.getEString(),
				"revision", null, 0, 1, IFileRevision.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getFileRevision_Content(),
				ecorePackage.getEString(),
				"content", null, 0, 1, IFileRevision.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getFileRevision_File(),
				this.getFileItem(),
				null,
				"file", null, 0, 1, IFileRevision.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(modelVersioningEClass, IModelVersioning.class,
				"ModelVersioning", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getModelVersioning_FragmentVersion(),
				ecorePackage.getEString(),
				"fragmentVersion", "1.0.0", 0, 1, IModelVersioning.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$

		initEClass(indexedEClass, IIndexed.class, "Indexed", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getIndexed_Index(),
				ecorePackage.getELong(),
				"index", null, 1, 1, IIndexed.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(datedEClass, IDated.class, "Dated", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getDated_CreationDate(),
				ecorePackage.getEDate(),
				"creationDate", null, 0, 1, IDated.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getDated_ModificationDate(),
				ecorePackage.getEDate(),
				"modificationDate", null, 0, 1, IDated.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http:///org/eclipse/emf/ecore/util/ExtendedMetaData
		createExtendedMetaDataAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void createExtendedMetaDataAnnotations() {
		String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData"; //$NON-NLS-1$		
		addAnnotation(getReviewComponent_Enabled(), source, new String[] { "namespace", "", //$NON-NLS-1$ //$NON-NLS-2$
				"wildcards", "", //$NON-NLS-1$ //$NON-NLS-2$
				"name", "" //$NON-NLS-1$ //$NON-NLS-2$
		});
	}

	/**
	 * <!-- begin-user-doc --> Defines literals for the meta objects that represent
	 * <ul>
	 * <li>each class,</li>
	 * <li>each feature of each class,</li>
	 * <li>each enum,</li>
	 * <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.TopicContainer
		 * <em>Topic Container</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.TopicContainer
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getTopicContainer()
		 * @generated
		 */
		public static final EClass TOPIC_CONTAINER = eINSTANCE.getTopicContainer();

		/**
		 * The meta object literal for the '<em><b>All Comments</b></em>' reference list feature. <!-- begin-user-doc
		 * --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference TOPIC_CONTAINER__ALL_COMMENTS = eINSTANCE.getTopicContainer_AllComments();

		/**
		 * The meta object literal for the '<em><b>Topics</b></em>' reference list feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference TOPIC_CONTAINER__TOPICS = eINSTANCE.getTopicContainer_Topics();

		/**
		 * The meta object literal for the '<em><b>Direct Topics</b></em>' reference list feature. <!-- begin-user-doc
		 * --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference TOPIC_CONTAINER__DIRECT_TOPICS = eINSTANCE.getTopicContainer_DirectTopics();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Review <em>Review</em>}
		 * ' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.Review
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReview()
		 * @generated
		 */
		public static final EClass REVIEW = eINSTANCE.getReview();

		/**
		 * The meta object literal for the '<em><b>Items</b></em>' reference list feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REVIEW__ITEMS = eINSTANCE.getReview_Items();

		/**
		 * The meta object literal for the '<em><b>Review Task</b></em>' containment reference feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REVIEW__REVIEW_TASK = eINSTANCE.getReview_ReviewTask();

		/**
		 * The meta object literal for the '<em><b>State</b></em>' containment reference feature. <!-- begin-user-doc
		 * --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REVIEW__STATE = eINSTANCE.getReview_State();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute REVIEW__ID = eINSTANCE.getReview_Id();

		/**
		 * The meta object literal for the '<em><b>Owner</b></em>' reference feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REVIEW__OWNER = eINSTANCE.getReview_Owner();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Comment
		 * <em>Comment</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.Comment
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getComment()
		 * @generated
		 */
		public static final EClass COMMENT = eINSTANCE.getComment();

		/**
		 * The meta object literal for the '<em><b>Author</b></em>' reference feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference COMMENT__AUTHOR = eINSTANCE.getComment_Author();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' containment reference feature. <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference COMMENT__TYPE = eINSTANCE.getComment_Type();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute COMMENT__DESCRIPTION = eINSTANCE.getComment_Description();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute COMMENT__ID = eINSTANCE.getComment_Id();

		/**
		 * The meta object literal for the '<em><b>Replies</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference COMMENT__REPLIES = eINSTANCE.getComment_Replies();

		/**
		 * The meta object literal for the '<em><b>Draft</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute COMMENT__DRAFT = eINSTANCE.getComment_Draft();

		/**
		 * The meta object literal for the '<em><b>Parent Topic</b></em>' reference feature. <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference COMMENT__PARENT_TOPIC = eINSTANCE.getComment_ParentTopic();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItem
		 * <em>Review Item</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewItem
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewItem()
		 * @generated
		 */
		public static final EClass REVIEW_ITEM = eINSTANCE.getReviewItem();

		/**
		 * The meta object literal for the '<em><b>Added By</b></em>' reference feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REVIEW_ITEM__ADDED_BY = eINSTANCE.getReviewItem_AddedBy();

		/**
		 * The meta object literal for the '<em><b>Review</b></em>' reference feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REVIEW_ITEM__REVIEW = eINSTANCE.getReviewItem_Review();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute REVIEW_ITEM__NAME = eINSTANCE.getReviewItem_Name();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute REVIEW_ITEM__ID = eINSTANCE.getReviewItem_Id();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Location
		 * <em>Location</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.Location
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getLocation()
		 * @generated
		 */
		public static final EClass LOCATION = eINSTANCE.getLocation();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.User <em>User</em>}'
		 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.User
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getUser()
		 * @generated
		 */
		public static final EClass USER = eINSTANCE.getUser();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute USER__ID = eINSTANCE.getUser_Id();

		/**
		 * The meta object literal for the '<em><b>Email</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute USER__EMAIL = eINSTANCE.getUser_Email();

		/**
		 * The meta object literal for the '<em><b>Display Name</b></em>' attribute feature. <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute USER__DISPLAY_NAME = eINSTANCE.getUser_DisplayName();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.TaskReference
		 * <em>Task Reference</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.TaskReference
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getTaskReference()
		 * @generated
		 */
		public static final EClass TASK_REFERENCE = eINSTANCE.getTaskReference();

		/**
		 * The meta object literal for the '<em><b>Task Id</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute TASK_REFERENCE__TASK_ID = eINSTANCE.getTaskReference_TaskId();

		/**
		 * The meta object literal for the '<em><b>Repository URL</b></em>' attribute feature. <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute TASK_REFERENCE__REPOSITORY_URL = eINSTANCE.getTaskReference_RepositoryURL();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewState
		 * <em>Review State</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewState
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewState()
		 * @generated
		 */
		public static final EClass REVIEW_STATE = eINSTANCE.getReviewState();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewGroup
		 * <em>Review Group</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewGroup
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewGroup()
		 * @generated
		 */
		public static final EClass REVIEW_GROUP = eINSTANCE.getReviewGroup();

		/**
		 * The meta object literal for the '<em><b>Reviews</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REVIEW_GROUP__REVIEWS = eINSTANCE.getReviewGroup_Reviews();

		/**
		 * The meta object literal for the '<em><b>Review Group Task</b></em>' containment reference feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REVIEW_GROUP__REVIEW_GROUP_TASK = eINSTANCE.getReviewGroup_ReviewGroupTask();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute REVIEW_GROUP__DESCRIPTION = eINSTANCE.getReviewGroup_Description();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.CommentType
		 * <em>Comment Type</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.CommentType
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getCommentType()
		 * @generated
		 */
		public static final EClass COMMENT_TYPE = eINSTANCE.getCommentType();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Topic <em>Topic</em>}'
		 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.Topic
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getTopic()
		 * @generated
		 */
		public static final EClass TOPIC = eINSTANCE.getTopic();

		/**
		 * The meta object literal for the '<em><b>Task</b></em>' containment reference feature. <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference TOPIC__TASK = eINSTANCE.getTopic_Task();

		/**
		 * The meta object literal for the '<em><b>Locations</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference TOPIC__LOCATIONS = eINSTANCE.getTopic_Locations();

		/**
		 * The meta object literal for the '<em><b>Comments</b></em>' reference list feature. <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference TOPIC__COMMENTS = eINSTANCE.getTopic_Comments();

		/**
		 * The meta object literal for the '<em><b>Review</b></em>' reference feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference TOPIC__REVIEW = eINSTANCE.getTopic_Review();

		/**
		 * The meta object literal for the '<em><b>Title</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute TOPIC__TITLE = eINSTANCE.getTopic_Title();

		/**
		 * The meta object literal for the '<em><b>Item</b></em>' reference feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference TOPIC__ITEM = eINSTANCE.getTopic_Item();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewComponent
		 * <em>Review Component</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewComponent
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewComponent()
		 * @generated
		 */
		public static final EClass REVIEW_COMPONENT = eINSTANCE.getReviewComponent();

		/**
		 * The meta object literal for the '<em><b>Enabled</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute REVIEW_COMPONENT__ENABLED = eINSTANCE.getReviewComponent_Enabled();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.FileItem
		 * <em>File Item</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.FileItem
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getFileItem()
		 * @generated
		 */
		public static final EClass FILE_ITEM = eINSTANCE.getFileItem();

		/**
		 * The meta object literal for the '<em><b>Base</b></em>' reference feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference FILE_ITEM__BASE = eINSTANCE.getFileItem_Base();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' reference feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference FILE_ITEM__TARGET = eINSTANCE.getFileItem_Target();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItemSet
		 * <em>Review Item Set</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewItemSet
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewItemSet()
		 * @generated
		 */
		public static final EClass REVIEW_ITEM_SET = eINSTANCE.getReviewItemSet();

		/**
		 * The meta object literal for the '<em><b>Items</b></em>' reference list feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REVIEW_ITEM_SET__ITEMS = eINSTANCE.getReviewItemSet_Items();

		/**
		 * The meta object literal for the '<em><b>Revision</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute REVIEW_ITEM_SET__REVISION = eINSTANCE.getReviewItemSet_Revision();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.LineLocation
		 * <em>Line Location</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.LineLocation
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getLineLocation()
		 * @generated
		 */
		public static final EClass LINE_LOCATION = eINSTANCE.getLineLocation();

		/**
		 * The meta object literal for the '<em><b>Ranges</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference LINE_LOCATION__RANGES = eINSTANCE.getLineLocation_Ranges();

		/**
		 * The meta object literal for the '<em><b>Range Min</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute LINE_LOCATION__RANGE_MIN = eINSTANCE.getLineLocation_RangeMin();

		/**
		 * The meta object literal for the '<em><b>Range Max</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute LINE_LOCATION__RANGE_MAX = eINSTANCE.getLineLocation_RangeMax();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.LineRange
		 * <em>Line Range</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.LineRange
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getLineRange()
		 * @generated
		 */
		public static final EClass LINE_RANGE = eINSTANCE.getLineRange();

		/**
		 * The meta object literal for the '<em><b>Start</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute LINE_RANGE__START = eINSTANCE.getLineRange_Start();

		/**
		 * The meta object literal for the '<em><b>End</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute LINE_RANGE__END = eINSTANCE.getLineRange_End();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.FileRevision
		 * <em>File Revision</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.FileRevision
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getFileRevision()
		 * @generated
		 */
		public static final EClass FILE_REVISION = eINSTANCE.getFileRevision();

		/**
		 * The meta object literal for the '<em><b>Path</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute FILE_REVISION__PATH = eINSTANCE.getFileRevision_Path();

		/**
		 * The meta object literal for the '<em><b>Revision</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute FILE_REVISION__REVISION = eINSTANCE.getFileRevision_Revision();

		/**
		 * The meta object literal for the '<em><b>Content</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute FILE_REVISION__CONTENT = eINSTANCE.getFileRevision_Content();

		/**
		 * The meta object literal for the '<em><b>File</b></em>' reference feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference FILE_REVISION__FILE = eINSTANCE.getFileRevision_File();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.core.model.IModelVersioning
		 * <em>Model Versioning</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.core.model.IModelVersioning
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getModelVersioning()
		 * @generated
		 */
		public static final EClass MODEL_VERSIONING = eINSTANCE.getModelVersioning();

		/**
		 * The meta object literal for the '<em><b>Fragment Version</b></em>' attribute feature. <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute MODEL_VERSIONING__FRAGMENT_VERSION = eINSTANCE.getModelVersioning_FragmentVersion();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.core.model.IIndexed <em>Indexed</em>}'
		 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.core.model.IIndexed
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getIndexed()
		 * @generated
		 */
		public static final EClass INDEXED = eINSTANCE.getIndexed();

		/**
		 * The meta object literal for the '<em><b>Index</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute INDEXED__INDEX = eINSTANCE.getIndexed_Index();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.core.model.IDated <em>Dated</em>}' class.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.core.model.IDated
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getDated()
		 * @generated
		 */
		public static final EClass DATED = eINSTANCE.getDated();

		/**
		 * The meta object literal for the '<em><b>Creation Date</b></em>' attribute feature. <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute DATED__CREATION_DATE = eINSTANCE.getDated_CreationDate();

		/**
		 * The meta object literal for the '<em><b>Modification Date</b></em>' attribute feature. <!-- begin-user-doc
		 * --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute DATED__MODIFICATION_DATE = eINSTANCE.getDated_ModificationDate();

	}

} //ReviewsPackage

/**
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.reviews.internal.core.model;

import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.mylyn.reviews.core.model.IApprovalType;
import org.eclipse.mylyn.reviews.core.model.IChange;
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
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IRequirementEntry;
import org.eclipse.mylyn.reviews.core.model.IRequirementReviewState;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewComponent;
import org.eclipse.mylyn.reviews.core.model.IReviewGroup;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IReviewState;
import org.eclipse.mylyn.reviews.core.model.IReviewerEntry;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.model.ISimpleReviewState;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.mylyn.reviews.core.model.ITopicContainer;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.core.model.RequirementStatus;

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
	public static final int REVIEW_COMPONENT = 12;

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
	 * The feature id for the '<em><b>Direct Topics</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
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
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.core.model.IDated <em>Dated</em>}' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.core.model.IDated
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getDated()
	 * @generated
	 */
	public static final int DATED = 20;

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
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Change <em>Change</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.Change
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getChange()
	 * @generated
	 */
	public static final int CHANGE = 1;

	/**
	 * The feature id for the '<em><b>Creation Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE__CREATION_DATE = DATED__CREATION_DATE;

	/**
	 * The feature id for the '<em><b>Modification Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE__MODIFICATION_DATE = DATED__MODIFICATION_DATE;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE__ID = DATED_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE__KEY = DATED_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Subject</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE__SUBJECT = DATED_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Message</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE__MESSAGE = DATED_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Owner</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE__OWNER = DATED_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>State</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE__STATE = DATED_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>Change</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int CHANGE_FEATURE_COUNT = DATED_FEATURE_COUNT + 6;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Review <em>Review</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.Review
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReview()
	 * @generated
	 */
	public static final int REVIEW = 2;

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
	 * The feature id for the '<em><b>Direct Topics</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
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
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__ID = TOPIC_CONTAINER_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__KEY = TOPIC_CONTAINER_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Subject</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__SUBJECT = TOPIC_CONTAINER_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Message</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__MESSAGE = TOPIC_CONTAINER_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Owner</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__OWNER = TOPIC_CONTAINER_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>State</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__STATE = TOPIC_CONTAINER_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Sets</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__SETS = TOPIC_CONTAINER_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Group</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__GROUP = TOPIC_CONTAINER_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Parents</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__PARENTS = TOPIC_CONTAINER_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Children</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__CHILDREN = TOPIC_CONTAINER_FEATURE_COUNT + 11;

	/**
	 * The feature id for the '<em><b>Reviewer Approvals</b></em>' map. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__REVIEWER_APPROVALS = TOPIC_CONTAINER_FEATURE_COUNT + 12;

	/**
	 * The feature id for the '<em><b>Requirements</b></em>' map. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__REQUIREMENTS = TOPIC_CONTAINER_FEATURE_COUNT + 13;

	/**
	 * The number of structural features of the '<em>Review</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_FEATURE_COUNT = TOPIC_CONTAINER_FEATURE_COUNT + 14;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Comment <em>Comment</em>}'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.Comment
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getComment()
	 * @generated
	 */
	public static final int COMMENT = 3;

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
	 * The feature id for the '<em><b>Parent Topic</b></em>' container reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
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
	public static final int REVIEW_ITEM = 4;

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
	 * The feature id for the '<em><b>Direct Topics</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
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
	 * The feature id for the '<em><b>Committed By</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__COMMITTED_BY = TOPIC_CONTAINER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Review</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__REVIEW = TOPIC_CONTAINER_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__NAME = TOPIC_CONTAINER_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__ID = TOPIC_CONTAINER_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__REFERENCE = TOPIC_CONTAINER_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>Review Item</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_FEATURE_COUNT = TOPIC_CONTAINER_FEATURE_COUNT + 6;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.core.model.IIndexed <em>Indexed</em>}' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.core.model.IIndexed
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getIndexed()
	 * @generated
	 */
	public static final int INDEXED = 19;

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
	public static final int LOCATION = 5;

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
	public static final int USER = 6;

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
	 * The feature id for the '<em><b>Descriptor</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_STATE__DESCRIPTOR = REVIEW_COMPONENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Review State</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_STATE_FEATURE_COUNT = REVIEW_COMPONENT_FEATURE_COUNT + 1;

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
	 * The feature id for the '<em><b>Users</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_GROUP__USERS = REVIEW_COMPONENT_FEATURE_COUNT + 1;

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
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Repository <em>Repository</em>}'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.Repository
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getRepository()
	 * @generated
	 */
	public static final int REPOSITORY = 9;

	/**
	 * The feature id for the '<em><b>Enabled</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REPOSITORY__ENABLED = REVIEW_GROUP__ENABLED;

	/**
	 * The feature id for the '<em><b>Reviews</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REPOSITORY__REVIEWS = REVIEW_GROUP__REVIEWS;

	/**
	 * The feature id for the '<em><b>Users</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REPOSITORY__USERS = REVIEW_GROUP__USERS;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REPOSITORY__DESCRIPTION = REVIEW_GROUP__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Approval Types</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REPOSITORY__APPROVAL_TYPES = REVIEW_GROUP_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Review States</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REPOSITORY__REVIEW_STATES = REVIEW_GROUP_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Repository</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REPOSITORY_FEATURE_COUNT = REVIEW_GROUP_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.CommentType
	 * <em>Comment Type</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.CommentType
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getCommentType()
	 * @generated
	 */
	public static final int COMMENT_TYPE = 10;

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
	public static final int TOPIC = 11;

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
	 * The feature id for the '<em><b>Parent Topic</b></em>' container reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__PARENT_TOPIC = COMMENT__PARENT_TOPIC;

	/**
	 * The feature id for the '<em><b>Locations</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__LOCATIONS = COMMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Comments</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__COMMENTS = COMMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Review</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__REVIEW = COMMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Title</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__TITLE = COMMENT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Item</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC__ITEM = COMMENT_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Topic</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int TOPIC_FEATURE_COUNT = COMMENT_FEATURE_COUNT + 5;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.FileItem <em>File Item</em>}'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.FileItem
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getFileItem()
	 * @generated
	 */
	public static final int FILE_ITEM = 13;

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
	 * The feature id for the '<em><b>Direct Topics</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
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
	 * The feature id for the '<em><b>Committed By</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_ITEM__COMMITTED_BY = REVIEW_ITEM__COMMITTED_BY;

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
	 * The feature id for the '<em><b>Reference</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_ITEM__REFERENCE = REVIEW_ITEM__REFERENCE;

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
	 * The feature id for the '<em><b>Set</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_ITEM__SET = REVIEW_ITEM_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>File Item</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_ITEM_FEATURE_COUNT = REVIEW_ITEM_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItemSet
	 * <em>Review Item Set</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewItemSet
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewItemSet()
	 * @generated
	 */
	public static final int REVIEW_ITEM_SET = 14;

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
	 * The feature id for the '<em><b>Direct Topics</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
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
	 * The feature id for the '<em><b>Committed By</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__COMMITTED_BY = REVIEW_ITEM__COMMITTED_BY;

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
	 * The feature id for the '<em><b>Reference</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__REFERENCE = REVIEW_ITEM__REFERENCE;

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
	 * The feature id for the '<em><b>Items</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
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
	 * The feature id for the '<em><b>Parent Review</b></em>' container reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__PARENT_REVIEW = REVIEW_ITEM_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Review Item Set</em>' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET_FEATURE_COUNT = REVIEW_ITEM_FEATURE_COUNT + 5;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.LineLocation
	 * <em>Line Location</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.LineLocation
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getLineLocation()
	 * @generated
	 */
	public static final int LINE_LOCATION = 15;

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
	public static final int LINE_RANGE = 16;

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
	public static final int FILE_REVISION = 17;

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
	 * The feature id for the '<em><b>Direct Topics</b></em>' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
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
	 * The feature id for the '<em><b>Committed By</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_REVISION__COMMITTED_BY = REVIEW_ITEM__COMMITTED_BY;

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
	 * The feature id for the '<em><b>Reference</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int FILE_REVISION__REFERENCE = REVIEW_ITEM__REFERENCE;

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
	public static final int MODEL_VERSIONING = 18;

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
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ApprovalType
	 * <em>Approval Type</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ApprovalType
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getApprovalType()
	 * @generated
	 */
	public static final int APPROVAL_TYPE = 21;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int APPROVAL_TYPE__KEY = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int APPROVAL_TYPE__NAME = 1;

	/**
	 * The number of structural features of the '<em>Approval Type</em>' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int APPROVAL_TYPE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.UserApprovalsMap
	 * <em>User Approvals Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.UserApprovalsMap
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getUserApprovalsMap()
	 * @generated
	 */
	public static final int USER_APPROVALS_MAP = 22;

	/**
	 * The feature id for the '<em><b>Key</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int USER_APPROVALS_MAP__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int USER_APPROVALS_MAP__VALUE = 1;

	/**
	 * The number of structural features of the '<em>User Approvals Map</em>' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int USER_APPROVALS_MAP_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewerEntry
	 * <em>Reviewer Entry</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewerEntry
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewerEntry()
	 * @generated
	 */
	public static final int REVIEWER_ENTRY = 23;

	/**
	 * The feature id for the '<em><b>Approvals</b></em>' map. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEWER_ENTRY__APPROVALS = 0;

	/**
	 * The number of structural features of the '<em>Reviewer Entry</em>' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEWER_ENTRY_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ApprovalValueMap
	 * <em>Approval Value Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ApprovalValueMap
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getApprovalValueMap()
	 * @generated
	 */
	public static final int APPROVAL_VALUE_MAP = 24;

	/**
	 * The feature id for the '<em><b>Key</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int APPROVAL_VALUE_MAP__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int APPROVAL_VALUE_MAP__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Approval Value Map</em>' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int APPROVAL_VALUE_MAP_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.RequirementEntry
	 * <em>Requirement Entry</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.RequirementEntry
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getRequirementEntry()
	 * @generated
	 */
	public static final int REQUIREMENT_ENTRY = 25;

	/**
	 * The feature id for the '<em><b>Status</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REQUIREMENT_ENTRY__STATUS = 0;

	/**
	 * The feature id for the '<em><b>By</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REQUIREMENT_ENTRY__BY = 1;

	/**
	 * The number of structural features of the '<em>Requirement Entry</em>' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REQUIREMENT_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewRequirementsMap
	 * <em>Review Requirements Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewRequirementsMap
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewRequirementsMap()
	 * @generated
	 */
	public static final int REVIEW_REQUIREMENTS_MAP = 26;

	/**
	 * The feature id for the '<em><b>Key</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_REQUIREMENTS_MAP__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_REQUIREMENTS_MAP__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Review Requirements Map</em>' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_REQUIREMENTS_MAP_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.RequirementReviewState
	 * <em>Requirement Review State</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.RequirementReviewState
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getRequirementReviewState()
	 * @generated
	 */
	public static final int REQUIREMENT_REVIEW_STATE = 27;

	/**
	 * The feature id for the '<em><b>Enabled</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REQUIREMENT_REVIEW_STATE__ENABLED = REVIEW_STATE__ENABLED;

	/**
	 * The feature id for the '<em><b>Descriptor</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REQUIREMENT_REVIEW_STATE__DESCRIPTOR = REVIEW_STATE__DESCRIPTOR;

	/**
	 * The feature id for the '<em><b>Status</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REQUIREMENT_REVIEW_STATE__STATUS = REVIEW_STATE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Requirement Review State</em>' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int REQUIREMENT_REVIEW_STATE_FEATURE_COUNT = REVIEW_STATE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.SimpleReviewState
	 * <em>Simple Review State</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.internal.core.model.SimpleReviewState
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getSimpleReviewState()
	 * @generated
	 */
	public static final int SIMPLE_REVIEW_STATE = 28;

	/**
	 * The feature id for the '<em><b>Enabled</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int SIMPLE_REVIEW_STATE__ENABLED = REVIEW_STATE__ENABLED;

	/**
	 * The feature id for the '<em><b>Descriptor</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int SIMPLE_REVIEW_STATE__DESCRIPTOR = REVIEW_STATE__DESCRIPTOR;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int SIMPLE_REVIEW_STATE__NAME = REVIEW_STATE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Simple Review State</em>' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	public static final int SIMPLE_REVIEW_STATE_FEATURE_COUNT = REVIEW_STATE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.core.model.RequirementStatus
	 * <em>Requirement Status</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.mylyn.reviews.core.model.RequirementStatus
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getRequirementStatus()
	 * @generated
	 */
	public static final int REQUIREMENT_STATUS = 29;

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
	private EClass changeEClass = null;

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
	private EClass repositoryEClass = null;

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
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass approvalTypeEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass userApprovalsMapEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass reviewerEntryEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass approvalValueMapEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass requirementEntryEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass reviewRequirementsMapEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass requirementReviewStateEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass simpleReviewStateEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EEnum requirementStatusEEnum = null;

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
		if (isInited) {
			return (ReviewsPackage) EPackage.Registry.INSTANCE.getEPackage(ReviewsPackage.eNS_URI);
		}

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
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.reviews.core.model.ITopicContainer#getDirectTopics <em>Direct Topics</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Direct Topics</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ITopicContainer#getDirectTopics()
	 * @see #getTopicContainer()
	 * @generated
	 */
	public EReference getTopicContainer_DirectTopics() {
		return (EReference) topicContainerEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IChange <em>Change</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Change</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IChange
	 * @generated
	 */
	public EClass getChange() {
		return changeEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IChange#getId <em>Id</em>}
	 * '. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IChange#getId()
	 * @see #getChange()
	 * @generated
	 */
	public EAttribute getChange_Id() {
		return (EAttribute) changeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IChange#getKey
	 * <em>Key</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IChange#getKey()
	 * @see #getChange()
	 * @generated
	 */
	public EAttribute getChange_Key() {
		return (EAttribute) changeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IChange#getSubject
	 * <em>Subject</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Subject</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IChange#getSubject()
	 * @see #getChange()
	 * @generated
	 */
	public EAttribute getChange_Subject() {
		return (EAttribute) changeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IChange#getMessage
	 * <em>Message</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Message</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IChange#getMessage()
	 * @see #getChange()
	 * @generated
	 */
	public EAttribute getChange_Message() {
		return (EAttribute) changeEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.reviews.core.model.IChange#getOwner
	 * <em>Owner</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Owner</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IChange#getOwner()
	 * @see #getChange()
	 * @generated
	 */
	public EReference getChange_Owner() {
		return (EReference) changeEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * Returns the meta object for the containment reference '
	 * {@link org.eclipse.mylyn.reviews.core.model.IChange#getState <em>State</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the meta object for the containment reference '<em>State</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IChange#getState()
	 * @see #getChange()
	 * @generated
	 */
	public EReference getChange_State() {
		return (EReference) changeEClass.getEStructuralFeatures().get(5);
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
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReview#getSets <em>Sets</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Sets</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReview#getSets()
	 * @see #getReview()
	 * @generated
	 */
	public EReference getReview_Sets() {
		return (EReference) reviewEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the container reference '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReview#getGroup <em>Group</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the meta object for the container reference '<em>Group</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReview#getGroup()
	 * @see #getReview()
	 * @generated
	 */
	public EReference getReview_Group() {
		return (EReference) reviewEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReview#getParents <em>Parents</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Parents</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReview#getParents()
	 * @see #getReview()
	 * @generated
	 */
	public EReference getReview_Parents() {
		return (EReference) reviewEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReview#getChildren <em>Children</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Children</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReview#getChildren()
	 * @see #getReview()
	 * @generated
	 */
	public EReference getReview_Children() {
		return (EReference) reviewEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for the map '{@link org.eclipse.mylyn.reviews.core.model.IReview#getReviewerApprovals
	 * <em>Reviewer Approvals</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the map '<em>Reviewer Approvals</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReview#getReviewerApprovals()
	 * @see #getReview()
	 * @generated
	 */
	public EReference getReview_ReviewerApprovals() {
		return (EReference) reviewEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * Returns the meta object for the map '{@link org.eclipse.mylyn.reviews.core.model.IReview#getRequirements
	 * <em>Requirements</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the map '<em>Requirements</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReview#getRequirements()
	 * @see #getReview()
	 * @generated
	 */
	public EReference getReview_Requirements() {
		return (EReference) reviewEClass.getEStructuralFeatures().get(5);
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
	 * Returns the meta object for the container reference '
	 * {@link org.eclipse.mylyn.reviews.core.model.IComment#getParentTopic <em>Parent Topic</em>}'. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the container reference '<em>Parent Topic</em>'.
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
	 * Returns the meta object for the reference '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReviewItem#getCommittedBy <em>Committed By</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Committed By</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItem#getCommittedBy()
	 * @see #getReviewItem()
	 * @generated
	 */
	public EReference getReviewItem_CommittedBy() {
		return (EReference) reviewItemEClass.getEStructuralFeatures().get(1);
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
		return (EReference) reviewItemEClass.getEStructuralFeatures().get(2);
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
		return (EAttribute) reviewItemEClass.getEStructuralFeatures().get(3);
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
		return (EAttribute) reviewItemEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IReviewItem#getReference
	 * <em>Reference</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Reference</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItem#getReference()
	 * @see #getReviewItem()
	 * @generated
	 */
	public EAttribute getReviewItem_Reference() {
		return (EAttribute) reviewItemEClass.getEStructuralFeatures().get(5);
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
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReviewState#getDescriptor <em>Descriptor</em>}'. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Descriptor</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewState#getDescriptor()
	 * @see #getReviewState()
	 * @generated
	 */
	public EAttribute getReviewState_Descriptor() {
		return (EAttribute) reviewStateEClass.getEStructuralFeatures().get(0);
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
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReviewGroup#getUsers <em>Users</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Users</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewGroup#getUsers()
	 * @see #getReviewGroup()
	 * @generated
	 */
	public EReference getReviewGroup_Users() {
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
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IRepository <em>Repository</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Repository</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IRepository
	 * @generated
	 */
	public EClass getRepository() {
		return repositoryEClass;
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.reviews.core.model.IRepository#getApprovalTypes <em>Approval Types</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Approval Types</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IRepository#getApprovalTypes()
	 * @see #getRepository()
	 * @generated
	 */
	public EReference getRepository_ApprovalTypes() {
		return (EReference) repositoryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.reviews.core.model.IRepository#getReviewStates <em>Review States</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Review States</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IRepository#getReviewStates()
	 * @see #getRepository()
	 * @generated
	 */
	public EReference getRepository_ReviewStates() {
		return (EReference) repositoryEClass.getEStructuralFeatures().get(1);
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
		return (EReference) topicEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.reviews.core.model.ITopic#getComments <em>Comments</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Comments</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ITopic#getComments()
	 * @see #getTopic()
	 * @generated
	 */
	public EReference getTopic_Comments() {
		return (EReference) topicEClass.getEStructuralFeatures().get(1);
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
		return (EReference) topicEClass.getEStructuralFeatures().get(2);
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
		return (EAttribute) topicEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.mylyn.reviews.core.model.ITopic#getItem
	 * <em>Item</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the container reference '<em>Item</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ITopic#getItem()
	 * @see #getTopic()
	 * @generated
	 */
	public EReference getTopic_Item() {
		return (EReference) topicEClass.getEStructuralFeatures().get(4);
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
	 * Returns the meta object for the container reference '
	 * {@link org.eclipse.mylyn.reviews.core.model.IFileItem#getSet <em>Set</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the meta object for the container reference '<em>Set</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileItem#getSet()
	 * @see #getFileItem()
	 * @generated
	 */
	public EReference getFileItem_Set() {
		return (EReference) fileItemEClass.getEStructuralFeatures().get(2);
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
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getItems <em>Items</em>}'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '<em>Items</em>'.
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
	 * Returns the meta object for the container reference '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getParentReview <em>Parent Review</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the container reference '<em>Parent Review</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getParentReview()
	 * @see #getReviewItemSet()
	 * @generated
	 */
	public EReference getReviewItemSet_ParentReview() {
		return (EReference) reviewItemSetEClass.getEStructuralFeatures().get(2);
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
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IApprovalType
	 * <em>Approval Type</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Approval Type</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IApprovalType
	 * @generated
	 */
	public EClass getApprovalType() {
		return approvalTypeEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IApprovalType#getKey
	 * <em>Key</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IApprovalType#getKey()
	 * @see #getApprovalType()
	 * @generated
	 */
	public EAttribute getApprovalType_Key() {
		return (EAttribute) approvalTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IApprovalType#getName
	 * <em>Name</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IApprovalType#getName()
	 * @see #getApprovalType()
	 * @generated
	 */
	public EAttribute getApprovalType_Name() {
		return (EAttribute) approvalTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>User Approvals Map</em>}'. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>User Approvals Map</em>'.
	 * @see java.util.Map.Entry
	 * @generated
	 */
	public EClass getUserApprovalsMap() {
		return userApprovalsMapEClass;
	}

	/**
	 * Returns the meta object for the reference '{@link java.util.Map.Entry <em>Key</em>}'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getUserApprovalsMap()
	 * @generated
	 */
	public EReference getUserApprovalsMap_Key() {
		return (EReference) userApprovalsMapEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the reference '{@link java.util.Map.Entry <em>Value</em>}'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getUserApprovalsMap()
	 * @generated
	 */
	public EReference getUserApprovalsMap_Value() {
		return (EReference) userApprovalsMapEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IReviewerEntry
	 * <em>Reviewer Entry</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Reviewer Entry</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewerEntry
	 * @generated
	 */
	public EClass getReviewerEntry() {
		return reviewerEntryEClass;
	}

	/**
	 * Returns the meta object for the map '{@link org.eclipse.mylyn.reviews.core.model.IReviewerEntry#getApprovals
	 * <em>Approvals</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the map '<em>Approvals</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewerEntry#getApprovals()
	 * @see #getReviewerEntry()
	 * @generated
	 */
	public EReference getReviewerEntry_Approvals() {
		return (EReference) reviewerEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Approval Value Map</em>}'. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Approval Value Map</em>'.
	 * @see java.util.Map.Entry
	 * @generated
	 */
	public EClass getApprovalValueMap() {
		return approvalValueMapEClass;
	}

	/**
	 * Returns the meta object for the reference '{@link java.util.Map.Entry <em>Key</em>}'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getApprovalValueMap()
	 * @generated
	 */
	public EReference getApprovalValueMap_Key() {
		return (EReference) approvalValueMapEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getApprovalValueMap()
	 * @generated
	 */
	public EAttribute getApprovalValueMap_Value() {
		return (EAttribute) approvalValueMapEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IRequirementEntry
	 * <em>Requirement Entry</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Requirement Entry</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IRequirementEntry
	 * @generated
	 */
	public EClass getRequirementEntry() {
		return requirementEntryEClass;
	}

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.mylyn.reviews.core.model.IRequirementEntry#getStatus <em>Status</em>}'. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Status</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IRequirementEntry#getStatus()
	 * @see #getRequirementEntry()
	 * @generated
	 */
	public EAttribute getRequirementEntry_Status() {
		return (EAttribute) requirementEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.reviews.core.model.IRequirementEntry#getBy
	 * <em>By</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>By</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IRequirementEntry#getBy()
	 * @see #getRequirementEntry()
	 * @generated
	 */
	public EReference getRequirementEntry_By() {
		return (EReference) requirementEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Review Requirements Map</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Review Requirements Map</em>'.
	 * @see java.util.Map.Entry
	 * @generated
	 */
	public EClass getReviewRequirementsMap() {
		return reviewRequirementsMapEClass;
	}

	/**
	 * Returns the meta object for the reference '{@link java.util.Map.Entry <em>Key</em>}'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getReviewRequirementsMap()
	 * @generated
	 */
	public EReference getReviewRequirementsMap_Key() {
		return (EReference) reviewRequirementsMapEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the reference '{@link java.util.Map.Entry <em>Value</em>}'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getReviewRequirementsMap()
	 * @generated
	 */
	public EReference getReviewRequirementsMap_Value() {
		return (EReference) reviewRequirementsMapEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IRequirementReviewState
	 * <em>Requirement Review State</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Requirement Review State</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IRequirementReviewState
	 * @generated
	 */
	public EClass getRequirementReviewState() {
		return requirementReviewStateEClass;
	}

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.mylyn.reviews.core.model.IRequirementReviewState#getStatus <em>Status</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Status</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IRequirementReviewState#getStatus()
	 * @see #getRequirementReviewState()
	 * @generated
	 */
	public EAttribute getRequirementReviewState_Status() {
		return (EAttribute) requirementReviewStateEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.ISimpleReviewState
	 * <em>Simple Review State</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Simple Review State</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ISimpleReviewState
	 * @generated
	 */
	public EClass getSimpleReviewState() {
		return simpleReviewStateEClass;
	}

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.mylyn.reviews.core.model.ISimpleReviewState#getName <em>Name</em>}'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ISimpleReviewState#getName()
	 * @see #getSimpleReviewState()
	 * @generated
	 */
	public EAttribute getSimpleReviewState_Name() {
		return (EAttribute) simpleReviewStateEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for enum '{@link org.eclipse.mylyn.reviews.core.model.RequirementStatus
	 * <em>Requirement Status</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for enum '<em>Requirement Status</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.RequirementStatus
	 * @generated
	 */
	public EEnum getRequirementStatus() {
		return requirementStatusEEnum;
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
		if (isCreated) {
			return;
		}
		isCreated = true;

		// Create classes and their features
		topicContainerEClass = createEClass(TOPIC_CONTAINER);
		createEReference(topicContainerEClass, TOPIC_CONTAINER__ALL_COMMENTS);
		createEReference(topicContainerEClass, TOPIC_CONTAINER__TOPICS);
		createEReference(topicContainerEClass, TOPIC_CONTAINER__DIRECT_TOPICS);

		changeEClass = createEClass(CHANGE);
		createEAttribute(changeEClass, CHANGE__ID);
		createEAttribute(changeEClass, CHANGE__KEY);
		createEAttribute(changeEClass, CHANGE__SUBJECT);
		createEAttribute(changeEClass, CHANGE__MESSAGE);
		createEReference(changeEClass, CHANGE__OWNER);
		createEReference(changeEClass, CHANGE__STATE);

		reviewEClass = createEClass(REVIEW);
		createEReference(reviewEClass, REVIEW__SETS);
		createEReference(reviewEClass, REVIEW__GROUP);
		createEReference(reviewEClass, REVIEW__PARENTS);
		createEReference(reviewEClass, REVIEW__CHILDREN);
		createEReference(reviewEClass, REVIEW__REVIEWER_APPROVALS);
		createEReference(reviewEClass, REVIEW__REQUIREMENTS);

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
		createEReference(reviewItemEClass, REVIEW_ITEM__COMMITTED_BY);
		createEReference(reviewItemEClass, REVIEW_ITEM__REVIEW);
		createEAttribute(reviewItemEClass, REVIEW_ITEM__NAME);
		createEAttribute(reviewItemEClass, REVIEW_ITEM__ID);
		createEAttribute(reviewItemEClass, REVIEW_ITEM__REFERENCE);

		locationEClass = createEClass(LOCATION);

		userEClass = createEClass(USER);
		createEAttribute(userEClass, USER__ID);
		createEAttribute(userEClass, USER__EMAIL);
		createEAttribute(userEClass, USER__DISPLAY_NAME);

		reviewStateEClass = createEClass(REVIEW_STATE);
		createEAttribute(reviewStateEClass, REVIEW_STATE__DESCRIPTOR);

		reviewGroupEClass = createEClass(REVIEW_GROUP);
		createEReference(reviewGroupEClass, REVIEW_GROUP__REVIEWS);
		createEReference(reviewGroupEClass, REVIEW_GROUP__USERS);
		createEAttribute(reviewGroupEClass, REVIEW_GROUP__DESCRIPTION);

		repositoryEClass = createEClass(REPOSITORY);
		createEReference(repositoryEClass, REPOSITORY__APPROVAL_TYPES);
		createEReference(repositoryEClass, REPOSITORY__REVIEW_STATES);

		commentTypeEClass = createEClass(COMMENT_TYPE);

		topicEClass = createEClass(TOPIC);
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
		createEReference(fileItemEClass, FILE_ITEM__SET);

		reviewItemSetEClass = createEClass(REVIEW_ITEM_SET);
		createEReference(reviewItemSetEClass, REVIEW_ITEM_SET__ITEMS);
		createEAttribute(reviewItemSetEClass, REVIEW_ITEM_SET__REVISION);
		createEReference(reviewItemSetEClass, REVIEW_ITEM_SET__PARENT_REVIEW);

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

		approvalTypeEClass = createEClass(APPROVAL_TYPE);
		createEAttribute(approvalTypeEClass, APPROVAL_TYPE__KEY);
		createEAttribute(approvalTypeEClass, APPROVAL_TYPE__NAME);

		userApprovalsMapEClass = createEClass(USER_APPROVALS_MAP);
		createEReference(userApprovalsMapEClass, USER_APPROVALS_MAP__KEY);
		createEReference(userApprovalsMapEClass, USER_APPROVALS_MAP__VALUE);

		reviewerEntryEClass = createEClass(REVIEWER_ENTRY);
		createEReference(reviewerEntryEClass, REVIEWER_ENTRY__APPROVALS);

		approvalValueMapEClass = createEClass(APPROVAL_VALUE_MAP);
		createEReference(approvalValueMapEClass, APPROVAL_VALUE_MAP__KEY);
		createEAttribute(approvalValueMapEClass, APPROVAL_VALUE_MAP__VALUE);

		requirementEntryEClass = createEClass(REQUIREMENT_ENTRY);
		createEAttribute(requirementEntryEClass, REQUIREMENT_ENTRY__STATUS);
		createEReference(requirementEntryEClass, REQUIREMENT_ENTRY__BY);

		reviewRequirementsMapEClass = createEClass(REVIEW_REQUIREMENTS_MAP);
		createEReference(reviewRequirementsMapEClass, REVIEW_REQUIREMENTS_MAP__KEY);
		createEReference(reviewRequirementsMapEClass, REVIEW_REQUIREMENTS_MAP__VALUE);

		requirementReviewStateEClass = createEClass(REQUIREMENT_REVIEW_STATE);
		createEAttribute(requirementReviewStateEClass, REQUIREMENT_REVIEW_STATE__STATUS);

		simpleReviewStateEClass = createEClass(SIMPLE_REVIEW_STATE);
		createEAttribute(simpleReviewStateEClass, SIMPLE_REVIEW_STATE__NAME);

		// Create enums
		requirementStatusEEnum = createEEnum(REQUIREMENT_STATUS);
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
		if (isInitialized) {
			return;
		}
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		topicContainerEClass.getESuperTypes().add(this.getReviewComponent());
		changeEClass.getESuperTypes().add(this.getDated());
		reviewEClass.getESuperTypes().add(this.getTopicContainer());
		reviewEClass.getESuperTypes().add(this.getChange());
		commentEClass.getESuperTypes().add(this.getReviewComponent());
		commentEClass.getESuperTypes().add(this.getIndexed());
		commentEClass.getESuperTypes().add(this.getDated());
		reviewItemEClass.getESuperTypes().add(this.getTopicContainer());
		locationEClass.getESuperTypes().add(this.getIndexed());
		reviewStateEClass.getESuperTypes().add(this.getReviewComponent());
		reviewGroupEClass.getESuperTypes().add(this.getReviewComponent());
		repositoryEClass.getESuperTypes().add(this.getReviewGroup());
		topicEClass.getESuperTypes().add(this.getComment());
		fileItemEClass.getESuperTypes().add(this.getReviewItem());
		reviewItemSetEClass.getESuperTypes().add(this.getReviewItem());
		reviewItemSetEClass.getESuperTypes().add(this.getDated());
		lineLocationEClass.getESuperTypes().add(this.getLocation());
		fileRevisionEClass.getESuperTypes().add(this.getReviewItem());
		requirementReviewStateEClass.getESuperTypes().add(this.getReviewState());
		simpleReviewStateEClass.getESuperTypes().add(this.getReviewState());

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
				"directTopics", null, 0, -1, ITopicContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		EOperation op = addEOperation(topicContainerEClass, this.getTopic(),
				"createTopicComment", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
		addEParameter(op, this.getLocation(), "initalLocation", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
		addEParameter(op, ecorePackage.getEString(), "commentText", 1, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

		initEClass(changeEClass, IChange.class, "Change", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getChange_Id(),
				ecorePackage.getEString(),
				"id", null, 0, 1, IChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getChange_Key(),
				ecorePackage.getEString(),
				"key", null, 0, 1, IChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getChange_Subject(),
				ecorePackage.getEString(),
				"subject", null, 0, 1, IChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getChange_Message(),
				ecorePackage.getEString(),
				"message", null, 0, 1, IChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getChange_Owner(),
				this.getUser(),
				null,
				"owner", null, 0, 1, IChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getChange_State(),
				this.getReviewState(),
				null,
				"state", null, 1, 1, IChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(reviewEClass, IReview.class, "Review", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getReview_Sets(),
				this.getReviewItemSet(),
				this.getReviewItemSet_ParentReview(),
				"sets", null, 0, -1, IReview.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getReview_Group(),
				this.getReviewGroup(),
				this.getReviewGroup_Reviews(),
				"group", null, 1, 1, IReview.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getReview_Parents(),
				this.getChange(),
				null,
				"parents", null, 0, -1, IReview.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getReview_Children(),
				this.getChange(),
				null,
				"children", null, 0, -1, IReview.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getReview_ReviewerApprovals(),
				this.getUserApprovalsMap(),
				null,
				"reviewerApprovals", null, 0, -1, IReview.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getReview_Requirements(),
				this.getReviewRequirementsMap(),
				null,
				"requirements", null, 0, -1, IReview.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

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
				getReviewItem_CommittedBy(),
				this.getUser(),
				null,
				"committedBy", null, 1, 1, IReviewItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getReviewItem_Review(),
				this.getReview(),
				null,
				"review", null, 1, 1, IReviewItem.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getReviewItem_Name(),
				ecorePackage.getEString(),
				"name", null, 0, 1, IReviewItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getReviewItem_Id(),
				ecorePackage.getEString(),
				"id", null, 0, 1, IReviewItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getReviewItem_Reference(),
				ecorePackage.getEString(),
				"reference", null, 0, 1, IReviewItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

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

		initEClass(reviewStateEClass, IReviewState.class,
				"ReviewState", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getReviewState_Descriptor(),
				ecorePackage.getEString(),
				"descriptor", null, 1, 1, IReviewState.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(reviewGroupEClass, IReviewGroup.class,
				"ReviewGroup", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getReviewGroup_Reviews(),
				this.getReview(),
				this.getReview_Group(),
				"reviews", null, 0, -1, IReviewGroup.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		getReviewGroup_Reviews().getEKeys().add(this.getChange_Key());
		initEReference(
				getReviewGroup_Users(),
				this.getUser(),
				null,
				"users", null, 0, -1, IReviewGroup.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getReviewGroup_Description(),
				ecorePackage.getEString(),
				"description", null, 0, 1, IReviewGroup.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(repositoryEClass, IRepository.class,
				"Repository", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getRepository_ApprovalTypes(),
				this.getApprovalType(),
				null,
				"approvalTypes", null, 0, -1, IRepository.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		getRepository_ApprovalTypes().getEKeys().add(this.getApprovalType_Key());
		initEReference(
				getRepository_ReviewStates(),
				this.getReviewState(),
				null,
				"reviewStates", null, 0, -1, IRepository.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(commentTypeEClass, ICommentType.class,
				"CommentType", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(topicEClass, ITopic.class, "Topic", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getTopic_Locations(),
				this.getLocation(),
				null,
				"locations", null, 0, -1, ITopic.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getTopic_Comments(),
				this.getComment(),
				this.getComment_ParentTopic(),
				"comments", null, 0, -1, ITopic.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
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
				"enabled", "true", 0, 1, IReviewComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$

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
		initEReference(
				getFileItem_Set(),
				this.getReviewItemSet(),
				this.getReviewItemSet_Items(),
				"set", null, 0, 1, IFileItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(reviewItemSetEClass, IReviewItemSet.class,
				"ReviewItemSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getReviewItemSet_Items(),
				this.getFileItem(),
				this.getFileItem_Set(),
				"items", null, 0, -1, IReviewItemSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getReviewItemSet_Revision(),
				ecorePackage.getEString(),
				"revision", "", 0, 1, IReviewItemSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEReference(
				getReviewItemSet_ParentReview(),
				this.getReview(),
				this.getReview_Sets(),
				"parentReview", null, 1, 1, IReviewItemSet.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

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

		initEClass(approvalTypeEClass, IApprovalType.class,
				"ApprovalType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getApprovalType_Key(),
				ecorePackage.getEString(),
				"key", null, 1, 1, IApprovalType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getApprovalType_Name(),
				ecorePackage.getEString(),
				"name", null, 1, 1, IApprovalType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(userApprovalsMapEClass, Map.Entry.class,
				"UserApprovalsMap", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getUserApprovalsMap_Key(),
				this.getUser(),
				null,
				"key", null, 1, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getUserApprovalsMap_Value(),
				this.getReviewerEntry(),
				null,
				"value", null, 1, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(reviewerEntryEClass, IReviewerEntry.class,
				"ReviewerEntry", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getReviewerEntry_Approvals(),
				this.getApprovalValueMap(),
				null,
				"approvals", null, 0, -1, IReviewerEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(approvalValueMapEClass, Map.Entry.class,
				"ApprovalValueMap", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getApprovalValueMap_Key(),
				this.getApprovalType(),
				null,
				"key", null, 1, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getApprovalValueMap_Value(),
				ecorePackage.getEIntegerObject(),
				"value", "0", 1, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$

		initEClass(requirementEntryEClass, IRequirementEntry.class,
				"RequirementEntry", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getRequirementEntry_Status(),
				this.getRequirementStatus(),
				"status", null, 1, 1, IRequirementEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getRequirementEntry_By(),
				this.getUser(),
				null,
				"by", null, 0, 1, IRequirementEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(reviewRequirementsMapEClass, Map.Entry.class,
				"ReviewRequirementsMap", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(
				getReviewRequirementsMap_Key(),
				this.getApprovalType(),
				null,
				"key", null, 1, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(
				getReviewRequirementsMap_Value(),
				this.getRequirementEntry(),
				null,
				"value", null, 1, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(requirementReviewStateEClass, IRequirementReviewState.class,
				"RequirementReviewState", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getRequirementReviewState_Status(),
				this.getRequirementStatus(),
				"status", null, 1, 1, IRequirementReviewState.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(simpleReviewStateEClass, ISimpleReviewState.class,
				"SimpleReviewState", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getSimpleReviewState_Name(),
				ecorePackage.getEString(),
				"name", null, 1, 1, ISimpleReviewState.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		// Initialize enums and add enum literals
		initEEnum(requirementStatusEEnum, RequirementStatus.class, "RequirementStatus"); //$NON-NLS-1$
		addEEnumLiteral(requirementStatusEEnum, RequirementStatus.UNKNOWN);
		addEEnumLiteral(requirementStatusEEnum, RequirementStatus.SATISFIED);
		addEEnumLiteral(requirementStatusEEnum, RequirementStatus.OPTIONAL);
		addEEnumLiteral(requirementStatusEEnum, RequirementStatus.CLOSED);
		addEEnumLiteral(requirementStatusEEnum, RequirementStatus.NOT_SATISFIED);
		addEEnumLiteral(requirementStatusEEnum, RequirementStatus.REJECTED);
		addEEnumLiteral(requirementStatusEEnum, RequirementStatus.ERROR);

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
		 * The meta object literal for the '<em><b>Direct Topics</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference TOPIC_CONTAINER__DIRECT_TOPICS = eINSTANCE.getTopicContainer_DirectTopics();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Change <em>Change</em>}
		 * ' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.Change
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getChange()
		 * @generated
		 */
		public static final EClass CHANGE = eINSTANCE.getChange();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute CHANGE__ID = eINSTANCE.getChange_Id();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute CHANGE__KEY = eINSTANCE.getChange_Key();

		/**
		 * The meta object literal for the '<em><b>Subject</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute CHANGE__SUBJECT = eINSTANCE.getChange_Subject();

		/**
		 * The meta object literal for the '<em><b>Message</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute CHANGE__MESSAGE = eINSTANCE.getChange_Message();

		/**
		 * The meta object literal for the '<em><b>Owner</b></em>' reference feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference CHANGE__OWNER = eINSTANCE.getChange_Owner();

		/**
		 * The meta object literal for the '<em><b>State</b></em>' containment reference feature. <!-- begin-user-doc
		 * --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference CHANGE__STATE = eINSTANCE.getChange_State();

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
		 * The meta object literal for the '<em><b>Sets</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REVIEW__SETS = eINSTANCE.getReview_Sets();

		/**
		 * The meta object literal for the '<em><b>Group</b></em>' container reference feature. <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REVIEW__GROUP = eINSTANCE.getReview_Group();

		/**
		 * The meta object literal for the '<em><b>Parents</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REVIEW__PARENTS = eINSTANCE.getReview_Parents();

		/**
		 * The meta object literal for the '<em><b>Children</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REVIEW__CHILDREN = eINSTANCE.getReview_Children();

		/**
		 * The meta object literal for the '<em><b>Reviewer Approvals</b></em>' map feature. <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REVIEW__REVIEWER_APPROVALS = eINSTANCE.getReview_ReviewerApprovals();

		/**
		 * The meta object literal for the '<em><b>Requirements</b></em>' map feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REVIEW__REQUIREMENTS = eINSTANCE.getReview_Requirements();

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
		 * The meta object literal for the '<em><b>Parent Topic</b></em>' container reference feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
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
		 * The meta object literal for the '<em><b>Committed By</b></em>' reference feature. <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REVIEW_ITEM__COMMITTED_BY = eINSTANCE.getReviewItem_CommittedBy();

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
		 * The meta object literal for the '<em><b>Reference</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute REVIEW_ITEM__REFERENCE = eINSTANCE.getReviewItem_Reference();

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
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewState
		 * <em>Review State</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewState
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewState()
		 * @generated
		 */
		public static final EClass REVIEW_STATE = eINSTANCE.getReviewState();

		/**
		 * The meta object literal for the '<em><b>Descriptor</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute REVIEW_STATE__DESCRIPTOR = eINSTANCE.getReviewState_Descriptor();

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
		 * The meta object literal for the '<em><b>Users</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REVIEW_GROUP__USERS = eINSTANCE.getReviewGroup_Users();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute REVIEW_GROUP__DESCRIPTION = eINSTANCE.getReviewGroup_Description();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Repository
		 * <em>Repository</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.Repository
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getRepository()
		 * @generated
		 */
		public static final EClass REPOSITORY = eINSTANCE.getRepository();

		/**
		 * The meta object literal for the '<em><b>Approval Types</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REPOSITORY__APPROVAL_TYPES = eINSTANCE.getRepository_ApprovalTypes();

		/**
		 * The meta object literal for the '<em><b>Review States</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REPOSITORY__REVIEW_STATES = eINSTANCE.getRepository_ReviewStates();

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
		 * The meta object literal for the '<em><b>Locations</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference TOPIC__LOCATIONS = eINSTANCE.getTopic_Locations();

		/**
		 * The meta object literal for the '<em><b>Comments</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
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
		 * The meta object literal for the '<em><b>Item</b></em>' container reference feature. <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
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
		 * The meta object literal for the '<em><b>Set</b></em>' container reference feature. <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference FILE_ITEM__SET = eINSTANCE.getFileItem_Set();

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
		 * The meta object literal for the '<em><b>Items</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
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
		 * The meta object literal for the '<em><b>Parent Review</b></em>' container reference feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REVIEW_ITEM_SET__PARENT_REVIEW = eINSTANCE.getReviewItemSet_ParentReview();

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

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ApprovalType
		 * <em>Approval Type</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ApprovalType
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getApprovalType()
		 * @generated
		 */
		public static final EClass APPROVAL_TYPE = eINSTANCE.getApprovalType();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute APPROVAL_TYPE__KEY = eINSTANCE.getApprovalType_Key();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute APPROVAL_TYPE__NAME = eINSTANCE.getApprovalType_Name();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.UserApprovalsMap
		 * <em>User Approvals Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.UserApprovalsMap
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getUserApprovalsMap()
		 * @generated
		 */
		public static final EClass USER_APPROVALS_MAP = eINSTANCE.getUserApprovalsMap();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' reference feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference USER_APPROVALS_MAP__KEY = eINSTANCE.getUserApprovalsMap_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' reference feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference USER_APPROVALS_MAP__VALUE = eINSTANCE.getUserApprovalsMap_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewerEntry
		 * <em>Reviewer Entry</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewerEntry
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewerEntry()
		 * @generated
		 */
		public static final EClass REVIEWER_ENTRY = eINSTANCE.getReviewerEntry();

		/**
		 * The meta object literal for the '<em><b>Approvals</b></em>' map feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REVIEWER_ENTRY__APPROVALS = eINSTANCE.getReviewerEntry_Approvals();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ApprovalValueMap
		 * <em>Approval Value Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ApprovalValueMap
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getApprovalValueMap()
		 * @generated
		 */
		public static final EClass APPROVAL_VALUE_MAP = eINSTANCE.getApprovalValueMap();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' reference feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference APPROVAL_VALUE_MAP__KEY = eINSTANCE.getApprovalValueMap_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute APPROVAL_VALUE_MAP__VALUE = eINSTANCE.getApprovalValueMap_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.RequirementEntry
		 * <em>Requirement Entry</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.RequirementEntry
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getRequirementEntry()
		 * @generated
		 */
		public static final EClass REQUIREMENT_ENTRY = eINSTANCE.getRequirementEntry();

		/**
		 * The meta object literal for the '<em><b>Status</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute REQUIREMENT_ENTRY__STATUS = eINSTANCE.getRequirementEntry_Status();

		/**
		 * The meta object literal for the '<em><b>By</b></em>' reference feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REQUIREMENT_ENTRY__BY = eINSTANCE.getRequirementEntry_By();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewRequirementsMap
		 * <em>Review Requirements Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewRequirementsMap
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewRequirementsMap()
		 * @generated
		 */
		public static final EClass REVIEW_REQUIREMENTS_MAP = eINSTANCE.getReviewRequirementsMap();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' reference feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REVIEW_REQUIREMENTS_MAP__KEY = eINSTANCE.getReviewRequirementsMap_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' reference feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EReference REVIEW_REQUIREMENTS_MAP__VALUE = eINSTANCE.getReviewRequirementsMap_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.RequirementReviewState
		 * <em>Requirement Review State</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.RequirementReviewState
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getRequirementReviewState()
		 * @generated
		 */
		public static final EClass REQUIREMENT_REVIEW_STATE = eINSTANCE.getRequirementReviewState();

		/**
		 * The meta object literal for the '<em><b>Status</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute REQUIREMENT_REVIEW_STATE__STATUS = eINSTANCE.getRequirementReviewState_Status();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.SimpleReviewState
		 * <em>Simple Review State</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.internal.core.model.SimpleReviewState
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getSimpleReviewState()
		 * @generated
		 */
		public static final EClass SIMPLE_REVIEW_STATE = eINSTANCE.getSimpleReviewState();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		public static final EAttribute SIMPLE_REVIEW_STATE__NAME = eINSTANCE.getSimpleReviewState_Name();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.core.model.RequirementStatus
		 * <em>Requirement Status</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.eclipse.mylyn.reviews.core.model.RequirementStatus
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getRequirementStatus()
		 * @generated
		 */
		public static final EEnum REQUIREMENT_STATUS = eINSTANCE.getRequirementStatus();

	}

} //ReviewsPackage

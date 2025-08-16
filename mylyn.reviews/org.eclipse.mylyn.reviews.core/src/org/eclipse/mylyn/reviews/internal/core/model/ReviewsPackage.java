/**
 * Copyright (c) 2013, 2015 Tasktop Technologies and others.
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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
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
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.core.model.RequirementStatus;
import org.eclipse.mylyn.reviews.core.model.ReviewStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.team.core.history.IFileRevision;

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
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.CommentContainer <em>Comment Container</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.reviews.internal.core.model.CommentContainer
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getCommentContainer()
	 * @generated
	 */
	public static final int COMMENT_CONTAINER = 0;

	/**
	 * The feature id for the '<em><b>All Comments</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT_CONTAINER__ALL_COMMENTS = 0;

	/**
	 * The feature id for the '<em><b>Comments</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT_CONTAINER__COMMENTS = 1;

	/**
	 * The feature id for the '<em><b>All Drafts</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT_CONTAINER__ALL_DRAFTS = 2;

	/**
	 * The feature id for the '<em><b>Drafts</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT_CONTAINER__DRAFTS = 3;

	/**
	 * The number of structural features of the '<em>Comment Container</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT_CONTAINER_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.core.model.IDated <em>Dated</em>}' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.reviews.core.model.IDated
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getDated()
	 * @generated
	 */
	public static final int DATED = 14;

	/**
	 * The feature id for the '<em><b>Creation Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int DATED__CREATION_DATE = 0;

	/**
	 * The feature id for the '<em><b>Modification Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Change <em>Change</em>}' class. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
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
	 * The feature id for the '<em><b>Modification Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * The feature id for the '<em><b>State</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Review <em>Review</em>}' class. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.reviews.internal.core.model.Review
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReview()
	 * @generated
	 */
	public static final int REVIEW = 2;

	/**
	 * The feature id for the '<em><b>All Comments</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__ALL_COMMENTS = COMMENT_CONTAINER__ALL_COMMENTS;

	/**
	 * The feature id for the '<em><b>Comments</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__COMMENTS = COMMENT_CONTAINER__COMMENTS;

	/**
	 * The feature id for the '<em><b>All Drafts</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__ALL_DRAFTS = COMMENT_CONTAINER__ALL_DRAFTS;

	/**
	 * The feature id for the '<em><b>Drafts</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__DRAFTS = COMMENT_CONTAINER__DRAFTS;

	/**
	 * The feature id for the '<em><b>Creation Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__CREATION_DATE = COMMENT_CONTAINER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Modification Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__MODIFICATION_DATE = COMMENT_CONTAINER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__ID = COMMENT_CONTAINER_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__KEY = COMMENT_CONTAINER_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Subject</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__SUBJECT = COMMENT_CONTAINER_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Message</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__MESSAGE = COMMENT_CONTAINER_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Owner</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__OWNER = COMMENT_CONTAINER_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>State</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__STATE = COMMENT_CONTAINER_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Sets</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__SETS = COMMENT_CONTAINER_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Repository</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__REPOSITORY = COMMENT_CONTAINER_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Parents</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__PARENTS = COMMENT_CONTAINER_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Children</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__CHILDREN = COMMENT_CONTAINER_FEATURE_COUNT + 11;

	/**
	 * The feature id for the '<em><b>Reviewer Approvals</b></em>' map. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__REVIEWER_APPROVALS = COMMENT_CONTAINER_FEATURE_COUNT + 12;

	/**
	 * The feature id for the '<em><b>Requirements</b></em>' map. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW__REQUIREMENTS = COMMENT_CONTAINER_FEATURE_COUNT + 13;

	/**
	 * The number of structural features of the '<em>Review</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_FEATURE_COUNT = COMMENT_CONTAINER_FEATURE_COUNT + 14;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.core.model.IIndexed <em>Indexed</em>}' class. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.reviews.core.model.IIndexed
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getIndexed()
	 * @generated
	 */
	public static final int INDEXED = 13;

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
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Comment <em>Comment</em>}' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.reviews.internal.core.model.Comment
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getComment()
	 * @generated
	 */
	public static final int COMMENT = 3;

	/**
	 * The feature id for the '<em><b>Index</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__INDEX = INDEXED__INDEX;

	/**
	 * The feature id for the '<em><b>Creation Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__CREATION_DATE = INDEXED_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Modification Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__MODIFICATION_DATE = INDEXED_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Author</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__AUTHOR = INDEXED_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__DESCRIPTION = INDEXED_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__ID = INDEXED_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Replies</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__REPLIES = INDEXED_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Draft</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__DRAFT = INDEXED_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Locations</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__LOCATIONS = INDEXED_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Review</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__REVIEW = INDEXED_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Title</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__TITLE = INDEXED_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Item</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__ITEM = INDEXED_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Mine</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT__MINE = INDEXED_FEATURE_COUNT + 11;

	/**
	 * The number of structural features of the '<em>Comment</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int COMMENT_FEATURE_COUNT = INDEXED_FEATURE_COUNT + 12;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItem <em>Review Item</em>} ' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewItem
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewItem()
	 * @generated
	 */
	public static final int REVIEW_ITEM = 4;

	/**
	 * The feature id for the '<em><b>All Comments</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__ALL_COMMENTS = COMMENT_CONTAINER__ALL_COMMENTS;

	/**
	 * The feature id for the '<em><b>Comments</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__COMMENTS = COMMENT_CONTAINER__COMMENTS;

	/**
	 * The feature id for the '<em><b>All Drafts</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__ALL_DRAFTS = COMMENT_CONTAINER__ALL_DRAFTS;

	/**
	 * The feature id for the '<em><b>Drafts</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__DRAFTS = COMMENT_CONTAINER__DRAFTS;

	/**
	 * The feature id for the '<em><b>Added By</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__ADDED_BY = COMMENT_CONTAINER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Committed By</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__COMMITTED_BY = COMMENT_CONTAINER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Review</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__REVIEW = COMMENT_CONTAINER_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__NAME = COMMENT_CONTAINER_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__ID = COMMENT_CONTAINER_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM__REFERENCE = COMMENT_CONTAINER_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>Review Item</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_FEATURE_COUNT = COMMENT_CONTAINER_FEATURE_COUNT + 6;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Location <em>Location</em>}' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
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
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.User <em>User</em>}' class. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
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
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Repository <em>Repository</em>}' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.reviews.internal.core.model.Repository
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getRepository()
	 * @generated
	 */
	public static final int REPOSITORY = 7;

	/**
	 * The feature id for the '<em><b>Approval Types</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REPOSITORY__APPROVAL_TYPES = 0;

	/**
	 * The feature id for the '<em><b>Task Repository Url</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REPOSITORY__TASK_REPOSITORY_URL = 1;

	/**
	 * The feature id for the '<em><b>Task Connector Kind</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REPOSITORY__TASK_CONNECTOR_KIND = 2;

	/**
	 * The feature id for the '<em><b>Task Repository</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REPOSITORY__TASK_REPOSITORY = 3;

	/**
	 * The feature id for the '<em><b>Account</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REPOSITORY__ACCOUNT = 4;

	/**
	 * The feature id for the '<em><b>Reviews</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REPOSITORY__REVIEWS = 5;

	/**
	 * The feature id for the '<em><b>Users</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REPOSITORY__USERS = 6;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REPOSITORY__DESCRIPTION = 7;

	/**
	 * The number of structural features of the '<em>Repository</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REPOSITORY_FEATURE_COUNT = 8;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.FileItem <em>File Item</em>}' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.reviews.internal.core.model.FileItem
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getFileItem()
	 * @generated
	 */
	public static final int FILE_ITEM = 8;

	/**
	 * The feature id for the '<em><b>All Comments</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_ITEM__ALL_COMMENTS = REVIEW_ITEM__ALL_COMMENTS;

	/**
	 * The feature id for the '<em><b>Comments</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_ITEM__COMMENTS = REVIEW_ITEM__COMMENTS;

	/**
	 * The feature id for the '<em><b>All Drafts</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_ITEM__ALL_DRAFTS = REVIEW_ITEM__ALL_DRAFTS;

	/**
	 * The feature id for the '<em><b>Drafts</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_ITEM__DRAFTS = REVIEW_ITEM__DRAFTS;

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
	 * The feature id for the '<em><b>Base</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_ITEM__BASE = REVIEW_ITEM_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Target</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * The number of structural features of the '<em>File Item</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_ITEM_FEATURE_COUNT = REVIEW_ITEM_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItemSet <em>Review Item Set</em>}' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewItemSet
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewItemSet()
	 * @generated
	 */
	public static final int REVIEW_ITEM_SET = 9;

	/**
	 * The feature id for the '<em><b>All Comments</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__ALL_COMMENTS = REVIEW_ITEM__ALL_COMMENTS;

	/**
	 * The feature id for the '<em><b>Comments</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__COMMENTS = REVIEW_ITEM__COMMENTS;

	/**
	 * The feature id for the '<em><b>All Drafts</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__ALL_DRAFTS = REVIEW_ITEM__ALL_DRAFTS;

	/**
	 * The feature id for the '<em><b>Drafts</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__DRAFTS = REVIEW_ITEM__DRAFTS;

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
	 * The feature id for the '<em><b>Modification Date</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__MODIFICATION_DATE = REVIEW_ITEM_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Items</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * The feature id for the '<em><b>Parent Review</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__PARENT_REVIEW = REVIEW_ITEM_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Parent Commits</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__PARENT_COMMITS = REVIEW_ITEM_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>In Need Of Retrieval</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET__IN_NEED_OF_RETRIEVAL = REVIEW_ITEM_FEATURE_COUNT + 6;

	/**
	 * The number of structural features of the '<em>Review Item Set</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_ITEM_SET_FEATURE_COUNT = REVIEW_ITEM_FEATURE_COUNT + 7;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.LineLocation <em>Line Location</em>}' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.reviews.internal.core.model.LineLocation
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getLineLocation()
	 * @generated
	 */
	public static final int LINE_LOCATION = 10;

	/**
	 * The feature id for the '<em><b>Index</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int LINE_LOCATION__INDEX = LOCATION__INDEX;

	/**
	 * The feature id for the '<em><b>Ranges</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * The number of structural features of the '<em>Line Location</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int LINE_LOCATION_FEATURE_COUNT = LOCATION_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.LineRange <em>Line Range</em>}' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.reviews.internal.core.model.LineRange
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getLineRange()
	 * @generated
	 */
	public static final int LINE_RANGE = 11;

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
	 * The number of structural features of the '<em>Line Range</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int LINE_RANGE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.FileVersion <em>File Version</em>}' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.reviews.internal.core.model.FileVersion
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getFileVersion()
	 * @generated
	 */
	public static final int FILE_VERSION = 12;

	/**
	 * The feature id for the '<em><b>All Comments</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_VERSION__ALL_COMMENTS = REVIEW_ITEM__ALL_COMMENTS;

	/**
	 * The feature id for the '<em><b>Comments</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_VERSION__COMMENTS = REVIEW_ITEM__COMMENTS;

	/**
	 * The feature id for the '<em><b>All Drafts</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_VERSION__ALL_DRAFTS = REVIEW_ITEM__ALL_DRAFTS;

	/**
	 * The feature id for the '<em><b>Drafts</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_VERSION__DRAFTS = REVIEW_ITEM__DRAFTS;

	/**
	 * The feature id for the '<em><b>Added By</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_VERSION__ADDED_BY = REVIEW_ITEM__ADDED_BY;

	/**
	 * The feature id for the '<em><b>Committed By</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_VERSION__COMMITTED_BY = REVIEW_ITEM__COMMITTED_BY;

	/**
	 * The feature id for the '<em><b>Review</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_VERSION__REVIEW = REVIEW_ITEM__REVIEW;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_VERSION__NAME = REVIEW_ITEM__NAME;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_VERSION__ID = REVIEW_ITEM__ID;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_VERSION__REFERENCE = REVIEW_ITEM__REFERENCE;

	/**
	 * The feature id for the '<em><b>Path</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_VERSION__PATH = REVIEW_ITEM_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_VERSION__DESCRIPTION = REVIEW_ITEM_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Content</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_VERSION__CONTENT = REVIEW_ITEM_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>File</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_VERSION__FILE = REVIEW_ITEM_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>File Revision</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_VERSION__FILE_REVISION = REVIEW_ITEM_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Binary Content</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_VERSION__BINARY_CONTENT = REVIEW_ITEM_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>File Version</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int FILE_VERSION_FEATURE_COUNT = REVIEW_ITEM_FEATURE_COUNT + 6;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ApprovalType <em>Approval Type</em>}' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ApprovalType
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getApprovalType()
	 * @generated
	 */
	public static final int APPROVAL_TYPE = 15;

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
	 * The number of structural features of the '<em>Approval Type</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int APPROVAL_TYPE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.UserApprovalsMap <em>User Approvals Map</em>}'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.reviews.internal.core.model.UserApprovalsMap
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getUserApprovalsMap()
	 * @generated
	 */
	public static final int USER_APPROVALS_MAP = 16;

	/**
	 * The feature id for the '<em><b>Key</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int USER_APPROVALS_MAP__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int USER_APPROVALS_MAP__VALUE = 1;

	/**
	 * The number of structural features of the '<em>User Approvals Map</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int USER_APPROVALS_MAP_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewerEntry <em>Reviewer Entry</em>}' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewerEntry
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewerEntry()
	 * @generated
	 */
	public static final int REVIEWER_ENTRY = 17;

	/**
	 * The feature id for the '<em><b>Approvals</b></em>' map. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEWER_ENTRY__APPROVALS = 0;

	/**
	 * The number of structural features of the '<em>Reviewer Entry</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEWER_ENTRY_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ApprovalValueMap <em>Approval Value Map</em>}'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ApprovalValueMap
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getApprovalValueMap()
	 * @generated
	 */
	public static final int APPROVAL_VALUE_MAP = 18;

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
	 * The number of structural features of the '<em>Approval Value Map</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int APPROVAL_VALUE_MAP_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.RequirementEntry <em>Requirement Entry</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.reviews.internal.core.model.RequirementEntry
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getRequirementEntry()
	 * @generated
	 */
	public static final int REQUIREMENT_ENTRY = 19;

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
	 * The number of structural features of the '<em>Requirement Entry</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REQUIREMENT_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewRequirementsMap <em>Review Requirements
	 * Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewRequirementsMap
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewRequirementsMap()
	 * @generated
	 */
	public static final int REVIEW_REQUIREMENTS_MAP = 20;

	/**
	 * The feature id for the '<em><b>Key</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_REQUIREMENTS_MAP__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_REQUIREMENTS_MAP__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Review Requirements Map</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int REVIEW_REQUIREMENTS_MAP_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Commit <em>Commit</em>}' class. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.reviews.internal.core.model.Commit
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getCommit()
	 * @generated
	 */
	public static final int COMMIT = 21;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int COMMIT__ID = 0;

	/**
	 * The feature id for the '<em><b>Subject</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int COMMIT__SUBJECT = 1;

	/**
	 * The number of structural features of the '<em>Commit</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	public static final int COMMIT_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.core.model.RequirementStatus <em>Requirement Status</em>}' enum. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.reviews.core.model.RequirementStatus
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getRequirementStatus()
	 * @generated
	 */
	public static final int REQUIREMENT_STATUS = 22;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.core.model.ReviewStatus <em>Review Status</em>}' enum. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.reviews.core.model.ReviewStatus
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewStatus()
	 * @generated
	 */
	public static final int REVIEW_STATUS = 23;

	/**
	 * The meta object id for the '<em>IFile Revision</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.team.core.history.IFileRevision
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getIFileRevision()
	 * @generated
	 */
	public static final int IFILE_REVISION = 24;

	/**
	 * The meta object id for the '<em>Task Repository</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.tasks.core.TaskRepository
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getTaskRepository()
	 * @generated
	 */
	public static final int TASK_REPOSITORY = 25;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	private EClass commentContainerEClass = null;

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
	private EClass repositoryEClass = null;

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
	private EClass fileVersionEClass = null;

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
	private EClass commitEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	private EEnum requirementStatusEEnum = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	private EEnum reviewStatusEEnum = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	private EDataType iFileRevisionEDataType = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	private EDataType taskRepositoryEDataType = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by
	 * the package package URI value.
	 * <p>
	 * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also performs initialization
	 * of the package, or returns the registered package, if one already exists. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ReviewsPackage() {
		super(eNS_URI, (EFactory) IReviewsFactory.INSTANCE);
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
	 * This method is used to initialize {@link ReviewsPackage#eINSTANCE} when that field is accessed. Clients should not invoke it
	 * directly. Instead, they should simply access that field to obtain the package. <!-- begin-user-doc --> <!-- end-user-doc -->
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
		ReviewsPackage theReviewsPackage = (ReviewsPackage) (EPackage.Registry.INSTANCE.get(
				eNS_URI) instanceof ReviewsPackage ? EPackage.Registry.INSTANCE.get(eNS_URI) : new ReviewsPackage());

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
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.ICommentContainer <em>Comment Container</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Comment Container</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ICommentContainer
	 * @generated
	 */
	public EClass getCommentContainer() {
		return commentContainerEClass;
	}

	/**
	 * Returns the meta object for the reference list ' {@link org.eclipse.mylyn.reviews.core.model.ICommentContainer#getAllComments <em>All
	 * Comments</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the reference list '<em>All Comments</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ICommentContainer#getAllComments()
	 * @see #getCommentContainer()
	 * @generated
	 */
	public EReference getCommentContainer_AllComments() {
		return (EReference) commentContainerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.reviews.core.model.ICommentContainer#getComments <em>Comments</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @return the meta object for the containment reference list '<em>Comments</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ICommentContainer#getComments()
	 * @see #getCommentContainer()
	 * @generated
	 */
	public EReference getCommentContainer_Comments() {
		return (EReference) commentContainerEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the reference list ' {@link org.eclipse.mylyn.reviews.core.model.ICommentContainer#getAllDrafts <em>All
	 * Drafts</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the reference list '<em>All Drafts</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ICommentContainer#getAllDrafts()
	 * @see #getCommentContainer()
	 * @generated
	 */
	public EReference getCommentContainer_AllDrafts() {
		return (EReference) commentContainerEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the containment reference list ' {@link org.eclipse.mylyn.reviews.core.model.ICommentContainer#getDrafts
	 * <em>Drafts</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference list '<em>Drafts</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ICommentContainer#getDrafts()
	 * @see #getCommentContainer()
	 * @generated
	 */
	public EReference getCommentContainer_Drafts() {
		return (EReference) commentContainerEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IChange <em>Change</em>}'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Change</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IChange
	 * @generated
	 */
	public EClass getChange() {
		return changeEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IChange#getId <em>Id</em>} '. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IChange#getKey <em>Key</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IChange#getSubject <em>Subject</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IChange#getMessage <em>Message</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.reviews.core.model.IChange#getOwner <em>Owner</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IChange#getState <em>State</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>State</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IChange#getState()
	 * @see #getChange()
	 * @generated
	 */
	public EAttribute getChange_State() {
		return (EAttribute) changeEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IReview <em>Review</em>}'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Review</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReview
	 * @generated
	 */
	public EClass getReview() {
		return reviewEClass;
	}

	/**
	 * Returns the meta object for the containment reference list ' {@link org.eclipse.mylyn.reviews.core.model.IReview#getSets
	 * <em>Sets</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the container reference ' {@link org.eclipse.mylyn.reviews.core.model.IReview#getRepository
	 * <em>Repository</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the container reference '<em>Repository</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReview#getRepository()
	 * @see #getReview()
	 * @generated
	 */
	public EReference getReview_Repository() {
		return (EReference) reviewEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the containment reference list ' {@link org.eclipse.mylyn.reviews.core.model.IReview#getParents
	 * <em>Parents</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the containment reference list ' {@link org.eclipse.mylyn.reviews.core.model.IReview#getChildren
	 * <em>Children</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the map '{@link org.eclipse.mylyn.reviews.core.model.IReview#getReviewerApprovals <em>Reviewer
	 * Approvals</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the map '{@link org.eclipse.mylyn.reviews.core.model.IReview#getRequirements <em>Requirements</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IComment <em>Comment</em>}'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Comment</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IComment
	 * @generated
	 */
	public EClass getComment() {
		return commentEClass;
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.reviews.core.model.IComment#getAuthor <em>Author</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IComment#getDescription
	 * <em>Description</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IComment#getDescription()
	 * @see #getComment()
	 * @generated
	 */
	public EAttribute getComment_Description() {
		return (EAttribute) commentEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IComment#getId <em>Id</em> }'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IComment#getId()
	 * @see #getComment()
	 * @generated
	 */
	public EAttribute getComment_Id() {
		return (EAttribute) commentEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.mylyn.reviews.core.model.IComment#getReplies <em>Replies</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the reference list '<em>Replies</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IComment#getReplies()
	 * @see #getComment()
	 * @generated
	 */
	public EReference getComment_Replies() {
		return (EReference) commentEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IComment#isDraft <em>Draft</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Draft</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IComment#isDraft()
	 * @see #getComment()
	 * @generated
	 */
	public EAttribute getComment_Draft() {
		return (EAttribute) commentEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * Returns the meta object for the containment reference list ' {@link org.eclipse.mylyn.reviews.core.model.IComment#getLocations
	 * <em>Locations</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference list '<em>Locations</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IComment#getLocations()
	 * @see #getComment()
	 * @generated
	 */
	public EReference getComment_Locations() {
		return (EReference) commentEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.reviews.core.model.IComment#getReview <em>Review</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the reference '<em>Review</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IComment#getReview()
	 * @see #getComment()
	 * @generated
	 */
	public EReference getComment_Review() {
		return (EReference) commentEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IComment#getTitle <em>Title</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Title</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IComment#getTitle()
	 * @see #getComment()
	 * @generated
	 */
	public EAttribute getComment_Title() {
		return (EAttribute) commentEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.mylyn.reviews.core.model.IComment#getItem <em>Item</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the container reference '<em>Item</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IComment#getItem()
	 * @see #getComment()
	 * @generated
	 */
	public EReference getComment_Item() {
		return (EReference) commentEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IComment#isMine <em>Mine</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Mine</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IComment#isMine()
	 * @see #getComment()
	 * @generated
	 */
	public EAttribute getComment_Mine() {
		return (EAttribute) commentEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IReviewItem <em>Review Item</em>} '. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Review Item</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItem
	 * @generated
	 */
	public EClass getReviewItem() {
		return reviewItemEClass;
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.reviews.core.model.IReviewItem#getAddedBy <em>Added By</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the reference ' {@link org.eclipse.mylyn.reviews.core.model.IReviewItem#getCommittedBy <em>Committed
	 * By</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.reviews.core.model.IReviewItem#getReview <em>Review</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IReviewItem#getName <em>Name</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IReviewItem#getId <em>Id</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IReviewItem#getReference <em>Reference</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.ILocation <em>Location</em>}'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Location</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ILocation
	 * @generated
	 */
	public EClass getLocation() {
		return locationEClass;
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IUser <em>User</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @return the meta object for class '<em>User</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IUser
	 * @generated
	 */
	public EClass getUser() {
		return userEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IUser#getId <em>Id</em>}'. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
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
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IUser#getEmail <em>Email</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IUser#getDisplayName <em>Display Name</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IRepository <em>Repository</em>}'. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Repository</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IRepository
	 * @generated
	 */
	public EClass getRepository() {
		return repositoryEClass;
	}

	/**
	 * Returns the meta object for the containment reference list ' {@link org.eclipse.mylyn.reviews.core.model.IRepository#getApprovalTypes
	 * <em>Approval Types</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the attribute ' {@link org.eclipse.mylyn.reviews.core.model.IRepository#getTaskRepositoryUrl <em>Task
	 * Repository Url</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Task Repository Url</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IRepository#getTaskRepositoryUrl()
	 * @see #getRepository()
	 * @generated
	 */
	public EAttribute getRepository_TaskRepositoryUrl() {
		return (EAttribute) repositoryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute ' {@link org.eclipse.mylyn.reviews.core.model.IRepository#getTaskConnectorKind <em>Task
	 * Connector Kind</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Task Connector Kind</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IRepository#getTaskConnectorKind()
	 * @see #getRepository()
	 * @generated
	 */
	public EAttribute getRepository_TaskConnectorKind() {
		return (EAttribute) repositoryEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the attribute ' {@link org.eclipse.mylyn.reviews.core.model.IRepository#getTaskRepository <em>Task
	 * Repository</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Task Repository</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IRepository#getTaskRepository()
	 * @see #getRepository()
	 * @generated
	 */
	public EAttribute getRepository_TaskRepository() {
		return (EAttribute) repositoryEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.reviews.core.model.IRepository#getAccount <em>Account</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the reference '<em>Account</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IRepository#getAccount()
	 * @see #getRepository()
	 * @generated
	 */
	public EReference getRepository_Account() {
		return (EReference) repositoryEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * Returns the meta object for the containment reference list ' {@link org.eclipse.mylyn.reviews.core.model.IRepository#getReviews
	 * <em>Reviews</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference list '<em>Reviews</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IRepository#getReviews()
	 * @see #getRepository()
	 * @generated
	 */
	public EReference getRepository_Reviews() {
		return (EReference) repositoryEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * Returns the meta object for the containment reference list ' {@link org.eclipse.mylyn.reviews.core.model.IRepository#getUsers
	 * <em>Users</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference list '<em>Users</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IRepository#getUsers()
	 * @see #getRepository()
	 * @generated
	 */
	public EReference getRepository_Users() {
		return (EReference) repositoryEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * Returns the meta object for the attribute ' {@link org.eclipse.mylyn.reviews.core.model.IRepository#getDescription
	 * <em>Description</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IRepository#getDescription()
	 * @see #getRepository()
	 * @generated
	 */
	public EAttribute getRepository_Description() {
		return (EAttribute) repositoryEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IFileItem <em>File Item</em>}'. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>File Item</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileItem
	 * @generated
	 */
	public EClass getFileItem() {
		return fileItemEClass;
	}

	/**
	 * Returns the meta object for the containment reference ' {@link org.eclipse.mylyn.reviews.core.model.IFileItem#getBase
	 * <em>Base</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference '<em>Base</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileItem#getBase()
	 * @see #getFileItem()
	 * @generated
	 */
	public EReference getFileItem_Base() {
		return (EReference) fileItemEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the containment reference ' {@link org.eclipse.mylyn.reviews.core.model.IFileItem#getTarget
	 * <em>Target</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference '<em>Target</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileItem#getTarget()
	 * @see #getFileItem()
	 * @generated
	 */
	public EReference getFileItem_Target() {
		return (EReference) fileItemEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.mylyn.reviews.core.model.IFileItem#getSet <em>Set</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet <em>Review Item Set</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Review Item Set</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItemSet
	 * @generated
	 */
	public EClass getReviewItemSet() {
		return reviewItemSetEClass;
	}

	/**
	 * Returns the meta object for the containment reference list ' {@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getItems
	 * <em>Items</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getRevision
	 * <em>Revision</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the container reference ' {@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getParentReview
	 * <em>Parent Review</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getParentCommits <em>Parent Commits</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @return the meta object for the containment reference list '<em>Parent Commits</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItemSet#getParentCommits()
	 * @see #getReviewItemSet()
	 * @generated
	 */
	public EReference getReviewItemSet_ParentCommits() {
		return (EReference) reviewItemSetEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for the attribute ' {@link org.eclipse.mylyn.reviews.core.model.IReviewItemSet#isInNeedOfRetrieval <em>In
	 * Need Of Retrieval</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>In Need Of Retrieval</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewItemSet#isInNeedOfRetrieval()
	 * @see #getReviewItemSet()
	 * @generated
	 */
	public EAttribute getReviewItemSet_InNeedOfRetrieval() {
		return (EAttribute) reviewItemSetEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.ILineLocation <em>Line Location</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Line Location</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ILineLocation
	 * @generated
	 */
	public EClass getLineLocation() {
		return lineLocationEClass;
	}

	/**
	 * Returns the meta object for the containment reference list ' {@link org.eclipse.mylyn.reviews.core.model.ILineLocation#getRanges
	 * <em>Ranges</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.ILineLocation#getRangeMin <em>Range
	 * Min</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.ILineLocation#getRangeMax <em>Range
	 * Max</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.ILineRange <em>Line Range</em>}'. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Line Range</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ILineRange
	 * @generated
	 */
	public EClass getLineRange() {
		return lineRangeEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.ILineRange#getStart <em>Start</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.ILineRange#getEnd <em>End</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IFileVersion <em>File Version</em> }'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>File Version</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileVersion
	 * @generated
	 */
	public EClass getFileVersion() {
		return fileVersionEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IFileVersion#getPath <em>Path</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Path</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileVersion#getPath()
	 * @see #getFileVersion()
	 * @generated
	 */
	public EAttribute getFileVersion_Path() {
		return (EAttribute) fileVersionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute ' {@link org.eclipse.mylyn.reviews.core.model.IFileVersion#getDescription
	 * <em>Description</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileVersion#getDescription()
	 * @see #getFileVersion()
	 * @generated
	 */
	public EAttribute getFileVersion_Description() {
		return (EAttribute) fileVersionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IFileVersion#getContent <em>Content</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Content</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileVersion#getContent()
	 * @see #getFileVersion()
	 * @generated
	 */
	public EAttribute getFileVersion_Content() {
		return (EAttribute) fileVersionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.reviews.core.model.IFileVersion#getFile <em>File</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the reference '<em>File</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileVersion#getFile()
	 * @see #getFileVersion()
	 * @generated
	 */
	public EReference getFileVersion_File() {
		return (EReference) fileVersionEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * Returns the meta object for the attribute ' {@link org.eclipse.mylyn.reviews.core.model.IFileVersion#getFileRevision <em>File
	 * Revision</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>File Revision</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileVersion#getFileRevision()
	 * @see #getFileVersion()
	 * @generated
	 */
	public EAttribute getFileVersion_FileRevision() {
		return (EAttribute) fileVersionEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * Returns the meta object for the attribute ' {@link org.eclipse.mylyn.reviews.core.model.IFileVersion#getBinaryContent <em>Binary
	 * Content</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Binary Content</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IFileVersion#getBinaryContent()
	 * @see #getFileVersion()
	 * @generated
	 */
	public EAttribute getFileVersion_BinaryContent() {
		return (EAttribute) fileVersionEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IIndexed <em>Indexed</em>}'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Indexed</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IIndexed
	 * @generated
	 */
	public EClass getIndexed() {
		return indexedEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IIndexed#getIndex <em>Index</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IDated <em>Dated</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @return the meta object for class '<em>Dated</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IDated
	 * @generated
	 */
	public EClass getDated() {
		return datedEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IDated#getCreationDate <em>Creation
	 * Date</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the attribute ' {@link org.eclipse.mylyn.reviews.core.model.IDated#getModificationDate <em>Modification
	 * Date</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IApprovalType <em>Approval Type</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Approval Type</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IApprovalType
	 * @generated
	 */
	public EClass getApprovalType() {
		return approvalTypeEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IApprovalType#getKey <em>Key</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.IApprovalType#getName <em>Name</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>User Approvals Map</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @return the meta object for class '<em>User Approvals Map</em>'.
	 * @see java.util.Map.Entry
	 * @generated
	 */
	public EClass getUserApprovalsMap() {
		return userApprovalsMapEClass;
	}

	/**
	 * Returns the meta object for the reference '{@link java.util.Map.Entry <em>Key</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getUserApprovalsMap()
	 * @generated
	 */
	public EReference getUserApprovalsMap_Value() {
		return (EReference) userApprovalsMapEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IReviewerEntry <em>Reviewer Entry</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Reviewer Entry</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IReviewerEntry
	 * @generated
	 */
	public EClass getReviewerEntry() {
		return reviewerEntryEClass;
	}

	/**
	 * Returns the meta object for the map '{@link org.eclipse.mylyn.reviews.core.model.IReviewerEntry#getApprovals <em>Approvals</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Approval Value Map</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @return the meta object for class '<em>Approval Value Map</em>'.
	 * @see java.util.Map.Entry
	 * @generated
	 */
	public EClass getApprovalValueMap() {
		return approvalValueMapEClass;
	}

	/**
	 * Returns the meta object for the reference '{@link java.util.Map.Entry <em>Key</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.IRequirementEntry <em>Requirement Entry</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Requirement Entry</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.IRequirementEntry
	 * @generated
	 */
	public EClass getRequirementEntry() {
		return requirementEntryEClass;
	}

	/**
	 * Returns the meta object for the attribute ' {@link org.eclipse.mylyn.reviews.core.model.IRequirementEntry#getStatus
	 * <em>Status</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.reviews.core.model.IRequirementEntry#getBy <em>By</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Review Requirements Map</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @return the meta object for class '<em>Review Requirements Map</em>'.
	 * @see java.util.Map.Entry
	 * @generated
	 */
	public EClass getReviewRequirementsMap() {
		return reviewRequirementsMapEClass;
	}

	/**
	 * Returns the meta object for the reference '{@link java.util.Map.Entry <em>Key</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getReviewRequirementsMap()
	 * @generated
	 */
	public EReference getReviewRequirementsMap_Value() {
		return (EReference) reviewRequirementsMapEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.ICommit <em>Commit</em>}'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Commit</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ICommit
	 * @generated
	 */
	public EClass getCommit() {
		return commitEClass;
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.ICommit#getId <em>Id</em>} '. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ICommit#getId()
	 * @see #getCommit()
	 * @generated
	 */
	public EAttribute getCommit_Id() {
		return (EAttribute) commitEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.ICommit#getSubject <em>Subject</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Subject</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ICommit#getSubject()
	 * @see #getCommit()
	 * @generated
	 */
	public EAttribute getCommit_Subject() {
		return (EAttribute) commitEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * Returns the meta object for enum '{@link org.eclipse.mylyn.reviews.core.model.RequirementStatus <em>Requirement Status</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for enum '<em>Requirement Status</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.RequirementStatus
	 * @generated
	 */
	public EEnum getRequirementStatus() {
		return requirementStatusEEnum;
	}

	/**
	 * Returns the meta object for enum '{@link org.eclipse.mylyn.reviews.core.model.ReviewStatus <em>Review Status</em> }'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for enum '<em>Review Status</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.ReviewStatus
	 * @generated
	 */
	public EEnum getReviewStatus() {
		return reviewStatusEEnum;
	}

	/**
	 * Returns the meta object for data type '{@link org.eclipse.team.core.history.IFileRevision <em>IFile Revision</em> }'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for data type '<em>IFile Revision</em>'.
	 * @see org.eclipse.team.core.history.IFileRevision
	 * @generated
	 */
	public EDataType getIFileRevision() {
		return iFileRevisionEDataType;
	}

	/**
	 * Returns the meta object for data type '{@link org.eclipse.mylyn.tasks.core.TaskRepository <em>Task Repository</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for data type '<em>Task Repository</em>'.
	 * @see org.eclipse.mylyn.tasks.core.TaskRepository
	 * @generated
	 */
	public EDataType getTaskRepository() {
		return taskRepositoryEDataType;
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
	 * Creates the meta-model objects for the package. This method is guarded to have no affect on any invocation but its first. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) {
			return;
		}
		isCreated = true;

		// Create classes and their features
		commentContainerEClass = createEClass(COMMENT_CONTAINER);
		createEReference(commentContainerEClass, COMMENT_CONTAINER__ALL_COMMENTS);
		createEReference(commentContainerEClass, COMMENT_CONTAINER__COMMENTS);
		createEReference(commentContainerEClass, COMMENT_CONTAINER__ALL_DRAFTS);
		createEReference(commentContainerEClass, COMMENT_CONTAINER__DRAFTS);

		changeEClass = createEClass(CHANGE);
		createEAttribute(changeEClass, CHANGE__ID);
		createEAttribute(changeEClass, CHANGE__KEY);
		createEAttribute(changeEClass, CHANGE__SUBJECT);
		createEAttribute(changeEClass, CHANGE__MESSAGE);
		createEReference(changeEClass, CHANGE__OWNER);
		createEAttribute(changeEClass, CHANGE__STATE);

		reviewEClass = createEClass(REVIEW);
		createEReference(reviewEClass, REVIEW__SETS);
		createEReference(reviewEClass, REVIEW__REPOSITORY);
		createEReference(reviewEClass, REVIEW__PARENTS);
		createEReference(reviewEClass, REVIEW__CHILDREN);
		createEReference(reviewEClass, REVIEW__REVIEWER_APPROVALS);
		createEReference(reviewEClass, REVIEW__REQUIREMENTS);

		commentEClass = createEClass(COMMENT);
		createEReference(commentEClass, COMMENT__AUTHOR);
		createEAttribute(commentEClass, COMMENT__DESCRIPTION);
		createEAttribute(commentEClass, COMMENT__ID);
		createEReference(commentEClass, COMMENT__REPLIES);
		createEAttribute(commentEClass, COMMENT__DRAFT);
		createEReference(commentEClass, COMMENT__LOCATIONS);
		createEReference(commentEClass, COMMENT__REVIEW);
		createEAttribute(commentEClass, COMMENT__TITLE);
		createEReference(commentEClass, COMMENT__ITEM);
		createEAttribute(commentEClass, COMMENT__MINE);

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

		repositoryEClass = createEClass(REPOSITORY);
		createEReference(repositoryEClass, REPOSITORY__APPROVAL_TYPES);
		createEAttribute(repositoryEClass, REPOSITORY__TASK_REPOSITORY_URL);
		createEAttribute(repositoryEClass, REPOSITORY__TASK_CONNECTOR_KIND);
		createEAttribute(repositoryEClass, REPOSITORY__TASK_REPOSITORY);
		createEReference(repositoryEClass, REPOSITORY__ACCOUNT);
		createEReference(repositoryEClass, REPOSITORY__REVIEWS);
		createEReference(repositoryEClass, REPOSITORY__USERS);
		createEAttribute(repositoryEClass, REPOSITORY__DESCRIPTION);

		fileItemEClass = createEClass(FILE_ITEM);
		createEReference(fileItemEClass, FILE_ITEM__BASE);
		createEReference(fileItemEClass, FILE_ITEM__TARGET);
		createEReference(fileItemEClass, FILE_ITEM__SET);

		reviewItemSetEClass = createEClass(REVIEW_ITEM_SET);
		createEReference(reviewItemSetEClass, REVIEW_ITEM_SET__ITEMS);
		createEAttribute(reviewItemSetEClass, REVIEW_ITEM_SET__REVISION);
		createEReference(reviewItemSetEClass, REVIEW_ITEM_SET__PARENT_REVIEW);
		createEReference(reviewItemSetEClass, REVIEW_ITEM_SET__PARENT_COMMITS);
		createEAttribute(reviewItemSetEClass, REVIEW_ITEM_SET__IN_NEED_OF_RETRIEVAL);

		lineLocationEClass = createEClass(LINE_LOCATION);
		createEReference(lineLocationEClass, LINE_LOCATION__RANGES);
		createEAttribute(lineLocationEClass, LINE_LOCATION__RANGE_MIN);
		createEAttribute(lineLocationEClass, LINE_LOCATION__RANGE_MAX);

		lineRangeEClass = createEClass(LINE_RANGE);
		createEAttribute(lineRangeEClass, LINE_RANGE__START);
		createEAttribute(lineRangeEClass, LINE_RANGE__END);

		fileVersionEClass = createEClass(FILE_VERSION);
		createEAttribute(fileVersionEClass, FILE_VERSION__PATH);
		createEAttribute(fileVersionEClass, FILE_VERSION__DESCRIPTION);
		createEAttribute(fileVersionEClass, FILE_VERSION__CONTENT);
		createEReference(fileVersionEClass, FILE_VERSION__FILE);
		createEAttribute(fileVersionEClass, FILE_VERSION__FILE_REVISION);
		createEAttribute(fileVersionEClass, FILE_VERSION__BINARY_CONTENT);

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

		commitEClass = createEClass(COMMIT);
		createEAttribute(commitEClass, COMMIT__ID);
		createEAttribute(commitEClass, COMMIT__SUBJECT);

		// Create enums
		requirementStatusEEnum = createEEnum(REQUIREMENT_STATUS);
		reviewStatusEEnum = createEEnum(REVIEW_STATUS);

		// Create data types
		iFileRevisionEDataType = createEDataType(IFILE_REVISION);
		taskRepositoryEDataType = createEDataType(TASK_REPOSITORY);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model. This method is guarded to have no affect on any invocation but its
	 * first. <!-- begin-user-doc --> <!-- end-user-doc -->
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
		changeEClass.getESuperTypes().add(getDated());
		reviewEClass.getESuperTypes().add(getCommentContainer());
		reviewEClass.getESuperTypes().add(getChange());
		commentEClass.getESuperTypes().add(getIndexed());
		commentEClass.getESuperTypes().add(getDated());
		reviewItemEClass.getESuperTypes().add(getCommentContainer());
		locationEClass.getESuperTypes().add(getIndexed());
		fileItemEClass.getESuperTypes().add(getReviewItem());
		reviewItemSetEClass.getESuperTypes().add(getReviewItem());
		reviewItemSetEClass.getESuperTypes().add(getDated());
		lineLocationEClass.getESuperTypes().add(getLocation());
		fileVersionEClass.getESuperTypes().add(getReviewItem());

		// Initialize classes and features; add operations and parameters
		initEClass(commentContainerEClass, ICommentContainer.class, "CommentContainer", IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCommentContainer_AllComments(), getComment(), null, "allComments", null, 0, -1, //$NON-NLS-1$
				ICommentContainer.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getCommentContainer_Comments(), getComment(), getComment_Item(), "comments", null, 0, //$NON-NLS-1$
				-1, ICommentContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCommentContainer_AllDrafts(), getComment(), null, "allDrafts", null, 0, -1, //$NON-NLS-1$
				ICommentContainer.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getCommentContainer_Drafts(), getComment(), null, "drafts", null, 0, -1, //$NON-NLS-1$
				ICommentContainer.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		EOperation op = addEOperation(commentContainerEClass, getComment(), "createComment", 0, 1, IS_UNIQUE, //$NON-NLS-1$
				IS_ORDERED);
		addEParameter(op, getLocation(), "initalLocation", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$
		addEParameter(op, ecorePackage.getEString(), "commentText", 1, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

		initEClass(changeEClass, IChange.class, "Change", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getChange_Id(), ecorePackage.getEString(), "id", null, 0, 1, IChange.class, !IS_TRANSIENT, //$NON-NLS-1$
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChange_Key(), ecorePackage.getEString(), "key", null, 0, 1, IChange.class, !IS_TRANSIENT, //$NON-NLS-1$
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChange_Subject(), ecorePackage.getEString(), "subject", null, 0, 1, IChange.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChange_Message(), ecorePackage.getEString(), "message", null, 0, 1, IChange.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getChange_Owner(), getUser(), null, "owner", null, 0, 1, IChange.class, !IS_TRANSIENT, //$NON-NLS-1$
				!IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
				IS_ORDERED);
		initEAttribute(getChange_State(), getReviewStatus(), "state", null, 0, 1, IChange.class, !IS_TRANSIENT, //$NON-NLS-1$
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(reviewEClass, IReview.class, "Review", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(getReview_Sets(), getReviewItemSet(), getReviewItemSet_ParentReview(), "sets", null, 0, //$NON-NLS-1$
				-1, IReview.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getReview_Repository(), getRepository(), getRepository_Reviews(), "repository", null, //$NON-NLS-1$
				1, 1, IReview.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getReview_Parents(), getChange(), null, "parents", null, 0, -1, IReview.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEReference(getReview_Children(), getChange(), null, "children", null, 0, -1, IReview.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEReference(getReview_ReviewerApprovals(), getUserApprovalsMap(), null, "reviewerApprovals", null, 0, //$NON-NLS-1$
				-1, IReview.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getReview_Requirements(), getReviewRequirementsMap(), null, "requirements", null, 0, -1, //$NON-NLS-1$
				IReview.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(commentEClass, IComment.class, "Comment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(getComment_Author(), getUser(), null, "author", null, 1, 1, IComment.class, !IS_TRANSIENT, //$NON-NLS-1$
				!IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
				IS_ORDERED);
		initEAttribute(getComment_Description(), ecorePackage.getEString(), "description", null, 0, 1, IComment.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getComment_Id(), ecorePackage.getEString(), "id", null, 0, 1, IComment.class, !IS_TRANSIENT, //$NON-NLS-1$
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getComment_Replies(), getComment(), null, "replies", null, 0, -1, IComment.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getComment_Draft(), ecorePackage.getEBoolean(), "draft", null, 0, 1, IComment.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getComment_Locations(), getLocation(), null, "locations", null, 0, -1, IComment.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEReference(getComment_Review(), getReview(), null, "review", null, 1, 1, IComment.class, IS_TRANSIENT, //$NON-NLS-1$
				!IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED,
				IS_ORDERED);
		initEAttribute(getComment_Title(), ecorePackage.getEString(), "title", null, 0, 1, IComment.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getComment_Item(), getCommentContainer(), getCommentContainer_Comments(), "item", null, //$NON-NLS-1$
				0, 1, IComment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getComment_Mine(), ecorePackage.getEBoolean(), "mine", null, 1, 1, IComment.class, IS_TRANSIENT, //$NON-NLS-1$
				IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(reviewItemEClass, IReviewItem.class, "ReviewItem", IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getReviewItem_AddedBy(), getUser(), null, "addedBy", null, 1, 1, IReviewItem.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getReviewItem_CommittedBy(), getUser(), null, "committedBy", null, 1, 1, IReviewItem.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getReviewItem_Review(), getReview(), null, "review", null, 1, 1, IReviewItem.class, //$NON-NLS-1$
				IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getReviewItem_Name(), ecorePackage.getEString(), "name", null, 0, 1, IReviewItem.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getReviewItem_Id(), ecorePackage.getEString(), "id", null, 0, 1, IReviewItem.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getReviewItem_Reference(), ecorePackage.getEString(), "reference", null, 0, 1, IReviewItem.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(locationEClass, ILocation.class, "Location", IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
				IS_GENERATED_INSTANCE_CLASS);

		initEClass(userEClass, IUser.class, "User", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getUser_Id(), ecorePackage.getEString(), "id", null, 0, 1, IUser.class, !IS_TRANSIENT, //$NON-NLS-1$
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getUser_Email(), ecorePackage.getEString(), "email", null, 0, 1, IUser.class, !IS_TRANSIENT, //$NON-NLS-1$
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getUser_DisplayName(), ecorePackage.getEString(), "displayName", null, 0, 1, IUser.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(repositoryEClass, IRepository.class, "Repository", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getRepository_ApprovalTypes(), getApprovalType(), null, "approvalTypes", null, 0, -1, //$NON-NLS-1$
				IRepository.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		getRepository_ApprovalTypes().getEKeys().add(getApprovalType_Key());
		initEAttribute(getRepository_TaskRepositoryUrl(), ecorePackage.getEString(), "taskRepositoryUrl", null, 0, 1, //$NON-NLS-1$
				IRepository.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEAttribute(getRepository_TaskConnectorKind(), ecorePackage.getEString(), "taskConnectorKind", null, 0, 1, //$NON-NLS-1$
				IRepository.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEAttribute(getRepository_TaskRepository(), getTaskRepository(), "taskRepository", null, 0, 1, //$NON-NLS-1$
				IRepository.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEReference(getRepository_Account(), getUser(), null, "account", null, 1, 1, IRepository.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRepository_Reviews(), getReview(), getReview_Repository(), "reviews", null, 0, -1, //$NON-NLS-1$
				IRepository.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		getRepository_Reviews().getEKeys().add(getChange_Key());
		initEReference(getRepository_Users(), getUser(), null, "users", null, 0, -1, IRepository.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		getRepository_Users().getEKeys().add(getUser_Id());
		initEAttribute(getRepository_Description(), ecorePackage.getEString(), "description", null, 0, 1, //$NON-NLS-1$
				IRepository.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);

		initEClass(fileItemEClass, IFileItem.class, "FileItem", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getFileItem_Base(), getFileVersion(), null, "base", null, 0, 1, IFileItem.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEReference(getFileItem_Target(), getFileVersion(), null, "target", null, 0, 1, IFileItem.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEReference(getFileItem_Set(), getReviewItemSet(), getReviewItemSet_Items(), "set", null, 0, 1, //$NON-NLS-1$
				IFileItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(reviewItemSetEClass, IReviewItemSet.class, "ReviewItemSet", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getReviewItemSet_Items(), getFileItem(), getFileItem_Set(), "items", null, 0, -1, //$NON-NLS-1$
				IReviewItemSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getReviewItemSet_Revision(), ecorePackage.getEString(), "revision", "", 0, 1, //$NON-NLS-1$//$NON-NLS-2$
				IReviewItemSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEReference(getReviewItemSet_ParentReview(), getReview(), getReview_Sets(), "parentReview", null, //$NON-NLS-1$
				1, 1, IReviewItemSet.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getReviewItemSet_ParentCommits(), getCommit(), null, "parentCommits", null, 0, -1, //$NON-NLS-1$
				IReviewItemSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getReviewItemSet_InNeedOfRetrieval(), ecorePackage.getEBoolean(), "inNeedOfRetrieval", "false", //$NON-NLS-1$//$NON-NLS-2$
				0, 1, IReviewItemSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(lineLocationEClass, ILineLocation.class, "LineLocation", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getLineLocation_Ranges(), getLineRange(), null, "ranges", null, 0, -1, ILineLocation.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEAttribute(getLineLocation_RangeMin(), ecorePackage.getEInt(), "rangeMin", null, 1, 1, ILineLocation.class, //$NON-NLS-1$
				IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getLineLocation_RangeMax(), ecorePackage.getEInt(), "rangeMax", null, 1, 1, ILineLocation.class, //$NON-NLS-1$
				IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(lineRangeEClass, ILineRange.class, "LineRange", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getLineRange_Start(), ecorePackage.getEInt(), "start", null, 0, 1, ILineRange.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLineRange_End(), ecorePackage.getEInt(), "end", null, 0, 1, ILineRange.class, !IS_TRANSIENT, //$NON-NLS-1$
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(fileVersionEClass, IFileVersion.class, "FileVersion", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getFileVersion_Path(), ecorePackage.getEString(), "path", null, 0, 1, IFileVersion.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFileVersion_Description(), ecorePackage.getEString(), "description", null, 0, 1, //$NON-NLS-1$
				IFileVersion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEAttribute(getFileVersion_Content(), ecorePackage.getEString(), "content", null, 0, 1, IFileVersion.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getFileVersion_File(), getFileItem(), null, "file", null, 0, 1, IFileVersion.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFileVersion_FileRevision(), getIFileRevision(), "fileRevision", null, 0, 1, //$NON-NLS-1$
				IFileVersion.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEAttribute(getFileVersion_BinaryContent(), ecorePackage.getEByteArray(), "binaryContent", null, 0, 1, //$NON-NLS-1$
				IFileVersion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);

		initEClass(indexedEClass, IIndexed.class, "Indexed", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getIndexed_Index(), ecorePackage.getELong(), "index", null, 1, 1, IIndexed.class, IS_TRANSIENT, //$NON-NLS-1$
				IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(datedEClass, IDated.class, "Dated", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getDated_CreationDate(), ecorePackage.getEDate(), "creationDate", null, 0, 1, IDated.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDated_ModificationDate(), ecorePackage.getEDate(), "modificationDate", null, 0, 1, //$NON-NLS-1$
				IDated.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);

		initEClass(approvalTypeEClass, IApprovalType.class, "ApprovalType", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getApprovalType_Key(), ecorePackage.getEString(), "key", null, 1, 1, IApprovalType.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getApprovalType_Name(), ecorePackage.getEString(), "name", null, 1, 1, IApprovalType.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(userApprovalsMapEClass, Map.Entry.class, "UserApprovalsMap", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
				!IS_GENERATED_INSTANCE_CLASS);
		initEReference(getUserApprovalsMap_Key(), getUser(), null, "key", null, 1, 1, Map.Entry.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getUserApprovalsMap_Value(), getReviewerEntry(), null, "value", null, 1, 1, Map.Entry.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);

		initEClass(reviewerEntryEClass, IReviewerEntry.class, "ReviewerEntry", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getReviewerEntry_Approvals(), getApprovalValueMap(), null, "approvals", null, 0, -1, //$NON-NLS-1$
				IReviewerEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(approvalValueMapEClass, Map.Entry.class, "ApprovalValueMap", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
				!IS_GENERATED_INSTANCE_CLASS);
		initEReference(getApprovalValueMap_Key(), getApprovalType(), null, "key", null, 1, 1, Map.Entry.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getApprovalValueMap_Value(), ecorePackage.getEIntegerObject(), "value", "0", 1, 1, //$NON-NLS-1$//$NON-NLS-2$
				Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);

		initEClass(requirementEntryEClass, IRequirementEntry.class, "RequirementEntry", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getRequirementEntry_Status(), getRequirementStatus(), "status", null, 1, 1, //$NON-NLS-1$
				IRequirementEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEReference(getRequirementEntry_By(), getUser(), null, "by", null, 0, 1, IRequirementEntry.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(reviewRequirementsMapEClass, Map.Entry.class, "ReviewRequirementsMap", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
				!IS_GENERATED_INSTANCE_CLASS);
		initEReference(getReviewRequirementsMap_Key(), getApprovalType(), null, "key", null, 1, 1, Map.Entry.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getReviewRequirementsMap_Value(), getRequirementEntry(), null, "value", null, 1, 1, //$NON-NLS-1$
				Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(commitEClass, ICommit.class, "Commit", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getCommit_Id(), ecorePackage.getEString(), "id", null, 1, 1, ICommit.class, !IS_TRANSIENT, //$NON-NLS-1$
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCommit_Subject(), ecorePackage.getEString(), "subject", null, 0, 1, ICommit.class, //$NON-NLS-1$
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(requirementStatusEEnum, RequirementStatus.class, "RequirementStatus"); //$NON-NLS-1$
		addEEnumLiteral(requirementStatusEEnum, RequirementStatus.UNKNOWN);
		addEEnumLiteral(requirementStatusEEnum, RequirementStatus.SATISFIED);
		addEEnumLiteral(requirementStatusEEnum, RequirementStatus.OPTIONAL);
		addEEnumLiteral(requirementStatusEEnum, RequirementStatus.CLOSED);
		addEEnumLiteral(requirementStatusEEnum, RequirementStatus.NOT_SATISFIED);
		addEEnumLiteral(requirementStatusEEnum, RequirementStatus.REJECTED);
		addEEnumLiteral(requirementStatusEEnum, RequirementStatus.ERROR);

		initEEnum(reviewStatusEEnum, ReviewStatus.class, "ReviewStatus"); //$NON-NLS-1$
		addEEnumLiteral(reviewStatusEEnum, ReviewStatus.NEW);
		addEEnumLiteral(reviewStatusEEnum, ReviewStatus.SUBMITTED);
		addEEnumLiteral(reviewStatusEEnum, ReviewStatus.MERGED);
		addEEnumLiteral(reviewStatusEEnum, ReviewStatus.ABANDONED);
		addEEnumLiteral(reviewStatusEEnum, ReviewStatus.DRAFT);

		// Initialize data types
		initEDataType(iFileRevisionEDataType, IFileRevision.class, "IFileRevision", !IS_SERIALIZABLE, //$NON-NLS-1$
				!IS_GENERATED_INSTANCE_CLASS);
		initEDataType(taskRepositoryEDataType, TaskRepository.class, "TaskRepository", !IS_SERIALIZABLE, //$NON-NLS-1$
				!IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
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
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.CommentContainer <em>Comment
		 * Container</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.internal.core.model.CommentContainer
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getCommentContainer()
		 * @generated
		 */
		EClass COMMENT_CONTAINER = eINSTANCE.getCommentContainer();

		/**
		 * The meta object literal for the '<em><b>All Comments</b></em>' reference list feature. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 *
		 * @generated
		 */
		EReference COMMENT_CONTAINER__ALL_COMMENTS = eINSTANCE.getCommentContainer_AllComments();

		/**
		 * The meta object literal for the '<em><b>Comments</b></em>' containment reference list feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference COMMENT_CONTAINER__COMMENTS = eINSTANCE.getCommentContainer_Comments();

		/**
		 * The meta object literal for the '<em><b>All Drafts</b></em>' reference list feature. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 *
		 * @generated
		 */
		EReference COMMENT_CONTAINER__ALL_DRAFTS = eINSTANCE.getCommentContainer_AllDrafts();

		/**
		 * The meta object literal for the '<em><b>Drafts</b></em>' containment reference list feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference COMMENT_CONTAINER__DRAFTS = eINSTANCE.getCommentContainer_Drafts();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Change <em>Change</em>} ' class. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.internal.core.model.Change
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getChange()
		 * @generated
		 */
		EClass CHANGE = eINSTANCE.getChange();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute CHANGE__ID = eINSTANCE.getChange_Id();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute CHANGE__KEY = eINSTANCE.getChange_Key();

		/**
		 * The meta object literal for the '<em><b>Subject</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute CHANGE__SUBJECT = eINSTANCE.getChange_Subject();

		/**
		 * The meta object literal for the '<em><b>Message</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute CHANGE__MESSAGE = eINSTANCE.getChange_Message();

		/**
		 * The meta object literal for the '<em><b>Owner</b></em>' reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference CHANGE__OWNER = eINSTANCE.getChange_Owner();

		/**
		 * The meta object literal for the '<em><b>State</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute CHANGE__STATE = eINSTANCE.getChange_State();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Review <em>Review</em>} ' class. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.internal.core.model.Review
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReview()
		 * @generated
		 */
		EClass REVIEW = eINSTANCE.getReview();

		/**
		 * The meta object literal for the '<em><b>Sets</b></em>' containment reference list feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference REVIEW__SETS = eINSTANCE.getReview_Sets();

		/**
		 * The meta object literal for the '<em><b>Repository</b></em>' container reference feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference REVIEW__REPOSITORY = eINSTANCE.getReview_Repository();

		/**
		 * The meta object literal for the '<em><b>Parents</b></em>' containment reference list feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference REVIEW__PARENTS = eINSTANCE.getReview_Parents();

		/**
		 * The meta object literal for the '<em><b>Children</b></em>' containment reference list feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference REVIEW__CHILDREN = eINSTANCE.getReview_Children();

		/**
		 * The meta object literal for the '<em><b>Reviewer Approvals</b></em>' map feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference REVIEW__REVIEWER_APPROVALS = eINSTANCE.getReview_ReviewerApprovals();

		/**
		 * The meta object literal for the '<em><b>Requirements</b></em>' map feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference REVIEW__REQUIREMENTS = eINSTANCE.getReview_Requirements();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Comment <em>Comment</em>}' class. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.internal.core.model.Comment
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getComment()
		 * @generated
		 */
		EClass COMMENT = eINSTANCE.getComment();

		/**
		 * The meta object literal for the '<em><b>Author</b></em>' reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference COMMENT__AUTHOR = eINSTANCE.getComment_Author();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute COMMENT__DESCRIPTION = eINSTANCE.getComment_Description();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute COMMENT__ID = eINSTANCE.getComment_Id();

		/**
		 * The meta object literal for the '<em><b>Replies</b></em>' containment reference list feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference COMMENT__REPLIES = eINSTANCE.getComment_Replies();

		/**
		 * The meta object literal for the '<em><b>Draft</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute COMMENT__DRAFT = eINSTANCE.getComment_Draft();

		/**
		 * The meta object literal for the '<em><b>Locations</b></em>' containment reference list feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference COMMENT__LOCATIONS = eINSTANCE.getComment_Locations();

		/**
		 * The meta object literal for the '<em><b>Review</b></em>' reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference COMMENT__REVIEW = eINSTANCE.getComment_Review();

		/**
		 * The meta object literal for the '<em><b>Title</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute COMMENT__TITLE = eINSTANCE.getComment_Title();

		/**
		 * The meta object literal for the '<em><b>Item</b></em>' container reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference COMMENT__ITEM = eINSTANCE.getComment_Item();

		/**
		 * The meta object literal for the '<em><b>Mine</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute COMMENT__MINE = eINSTANCE.getComment_Mine();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItem <em>Review Item</em>}' class.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewItem
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewItem()
		 * @generated
		 */
		EClass REVIEW_ITEM = eINSTANCE.getReviewItem();

		/**
		 * The meta object literal for the '<em><b>Added By</b></em>' reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference REVIEW_ITEM__ADDED_BY = eINSTANCE.getReviewItem_AddedBy();

		/**
		 * The meta object literal for the '<em><b>Committed By</b></em>' reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference REVIEW_ITEM__COMMITTED_BY = eINSTANCE.getReviewItem_CommittedBy();

		/**
		 * The meta object literal for the '<em><b>Review</b></em>' reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference REVIEW_ITEM__REVIEW = eINSTANCE.getReviewItem_Review();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute REVIEW_ITEM__NAME = eINSTANCE.getReviewItem_Name();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute REVIEW_ITEM__ID = eINSTANCE.getReviewItem_Id();

		/**
		 * The meta object literal for the '<em><b>Reference</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute REVIEW_ITEM__REFERENCE = eINSTANCE.getReviewItem_Reference();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Location <em>Location</em>}' class. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.internal.core.model.Location
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getLocation()
		 * @generated
		 */
		EClass LOCATION = eINSTANCE.getLocation();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.User <em>User</em>}' class. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.internal.core.model.User
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getUser()
		 * @generated
		 */
		EClass USER = eINSTANCE.getUser();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute USER__ID = eINSTANCE.getUser_Id();

		/**
		 * The meta object literal for the '<em><b>Email</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute USER__EMAIL = eINSTANCE.getUser_Email();

		/**
		 * The meta object literal for the '<em><b>Display Name</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute USER__DISPLAY_NAME = eINSTANCE.getUser_DisplayName();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Repository <em>Repository</em>}' class.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.internal.core.model.Repository
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getRepository()
		 * @generated
		 */
		EClass REPOSITORY = eINSTANCE.getRepository();

		/**
		 * The meta object literal for the '<em><b>Approval Types</b></em>' containment reference list feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference REPOSITORY__APPROVAL_TYPES = eINSTANCE.getRepository_ApprovalTypes();

		/**
		 * The meta object literal for the '<em><b>Task Repository Url</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute REPOSITORY__TASK_REPOSITORY_URL = eINSTANCE.getRepository_TaskRepositoryUrl();

		/**
		 * The meta object literal for the '<em><b>Task Connector Kind</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute REPOSITORY__TASK_CONNECTOR_KIND = eINSTANCE.getRepository_TaskConnectorKind();

		/**
		 * The meta object literal for the '<em><b>Task Repository</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 *
		 * @generated
		 */
		EAttribute REPOSITORY__TASK_REPOSITORY = eINSTANCE.getRepository_TaskRepository();

		/**
		 * The meta object literal for the '<em><b>Account</b></em>' reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference REPOSITORY__ACCOUNT = eINSTANCE.getRepository_Account();

		/**
		 * The meta object literal for the '<em><b>Reviews</b></em>' containment reference list feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference REPOSITORY__REVIEWS = eINSTANCE.getRepository_Reviews();

		/**
		 * The meta object literal for the '<em><b>Users</b></em>' containment reference list feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference REPOSITORY__USERS = eINSTANCE.getRepository_Users();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute REPOSITORY__DESCRIPTION = eINSTANCE.getRepository_Description();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.FileItem <em>File Item</em>}' class. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.internal.core.model.FileItem
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getFileItem()
		 * @generated
		 */
		EClass FILE_ITEM = eINSTANCE.getFileItem();

		/**
		 * The meta object literal for the '<em><b>Base</b></em>' containment reference feature. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 *
		 * @generated
		 */
		EReference FILE_ITEM__BASE = eINSTANCE.getFileItem_Base();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' containment reference feature. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 *
		 * @generated
		 */
		EReference FILE_ITEM__TARGET = eINSTANCE.getFileItem_Target();

		/**
		 * The meta object literal for the '<em><b>Set</b></em>' container reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference FILE_ITEM__SET = eINSTANCE.getFileItem_Set();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewItemSet <em>Review Item Set</em>}'
		 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewItemSet
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewItemSet()
		 * @generated
		 */
		EClass REVIEW_ITEM_SET = eINSTANCE.getReviewItemSet();

		/**
		 * The meta object literal for the '<em><b>Items</b></em>' containment reference list feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference REVIEW_ITEM_SET__ITEMS = eINSTANCE.getReviewItemSet_Items();

		/**
		 * The meta object literal for the '<em><b>Revision</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute REVIEW_ITEM_SET__REVISION = eINSTANCE.getReviewItemSet_Revision();

		/**
		 * The meta object literal for the '<em><b>Parent Review</b></em>' container reference feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference REVIEW_ITEM_SET__PARENT_REVIEW = eINSTANCE.getReviewItemSet_ParentReview();

		/**
		 * The meta object literal for the '<em><b>Parent Commits</b></em>' containment reference list feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference REVIEW_ITEM_SET__PARENT_COMMITS = eINSTANCE.getReviewItemSet_ParentCommits();

		/**
		 * The meta object literal for the '<em><b>In Need Of Retrieval</b></em>' attribute feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute REVIEW_ITEM_SET__IN_NEED_OF_RETRIEVAL = eINSTANCE.getReviewItemSet_InNeedOfRetrieval();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.LineLocation <em>Line Location</em>}'
		 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.internal.core.model.LineLocation
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getLineLocation()
		 * @generated
		 */
		EClass LINE_LOCATION = eINSTANCE.getLineLocation();

		/**
		 * The meta object literal for the '<em><b>Ranges</b></em>' containment reference list feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference LINE_LOCATION__RANGES = eINSTANCE.getLineLocation_Ranges();

		/**
		 * The meta object literal for the '<em><b>Range Min</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute LINE_LOCATION__RANGE_MIN = eINSTANCE.getLineLocation_RangeMin();

		/**
		 * The meta object literal for the '<em><b>Range Max</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute LINE_LOCATION__RANGE_MAX = eINSTANCE.getLineLocation_RangeMax();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.LineRange <em>Line Range</em>}' class. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.internal.core.model.LineRange
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getLineRange()
		 * @generated
		 */
		EClass LINE_RANGE = eINSTANCE.getLineRange();

		/**
		 * The meta object literal for the '<em><b>Start</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute LINE_RANGE__START = eINSTANCE.getLineRange_Start();

		/**
		 * The meta object literal for the '<em><b>End</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute LINE_RANGE__END = eINSTANCE.getLineRange_End();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.FileVersion <em>File Version</em>}' class.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.internal.core.model.FileVersion
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getFileVersion()
		 * @generated
		 */
		EClass FILE_VERSION = eINSTANCE.getFileVersion();

		/**
		 * The meta object literal for the '<em><b>Path</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute FILE_VERSION__PATH = eINSTANCE.getFileVersion_Path();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute FILE_VERSION__DESCRIPTION = eINSTANCE.getFileVersion_Description();

		/**
		 * The meta object literal for the '<em><b>Content</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute FILE_VERSION__CONTENT = eINSTANCE.getFileVersion_Content();

		/**
		 * The meta object literal for the '<em><b>File</b></em>' reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference FILE_VERSION__FILE = eINSTANCE.getFileVersion_File();

		/**
		 * The meta object literal for the '<em><b>File Revision</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute FILE_VERSION__FILE_REVISION = eINSTANCE.getFileVersion_FileRevision();

		/**
		 * The meta object literal for the '<em><b>Binary Content</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute FILE_VERSION__BINARY_CONTENT = eINSTANCE.getFileVersion_BinaryContent();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.core.model.IIndexed <em>Indexed</em>}' class. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.core.model.IIndexed
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getIndexed()
		 * @generated
		 */
		EClass INDEXED = eINSTANCE.getIndexed();

		/**
		 * The meta object literal for the '<em><b>Index</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute INDEXED__INDEX = eINSTANCE.getIndexed_Index();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.core.model.IDated <em>Dated</em>}' class. <!-- begin-user-doc
		 * --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.core.model.IDated
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getDated()
		 * @generated
		 */
		EClass DATED = eINSTANCE.getDated();

		/**
		 * The meta object literal for the '<em><b>Creation Date</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute DATED__CREATION_DATE = eINSTANCE.getDated_CreationDate();

		/**
		 * The meta object literal for the '<em><b>Modification Date</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 *
		 * @generated
		 */
		EAttribute DATED__MODIFICATION_DATE = eINSTANCE.getDated_ModificationDate();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ApprovalType <em>Approval Type</em>}'
		 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ApprovalType
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getApprovalType()
		 * @generated
		 */
		EClass APPROVAL_TYPE = eINSTANCE.getApprovalType();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute APPROVAL_TYPE__KEY = eINSTANCE.getApprovalType_Key();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute APPROVAL_TYPE__NAME = eINSTANCE.getApprovalType_Name();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.UserApprovalsMap <em>User Approvals
		 * Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.internal.core.model.UserApprovalsMap
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getUserApprovalsMap()
		 * @generated
		 */
		EClass USER_APPROVALS_MAP = eINSTANCE.getUserApprovalsMap();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference USER_APPROVALS_MAP__KEY = eINSTANCE.getUserApprovalsMap_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 *
		 * @generated
		 */
		EReference USER_APPROVALS_MAP__VALUE = eINSTANCE.getUserApprovalsMap_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewerEntry <em>Reviewer Entry</em>}'
		 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewerEntry
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewerEntry()
		 * @generated
		 */
		EClass REVIEWER_ENTRY = eINSTANCE.getReviewerEntry();

		/**
		 * The meta object literal for the '<em><b>Approvals</b></em>' map feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference REVIEWER_ENTRY__APPROVALS = eINSTANCE.getReviewerEntry_Approvals();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ApprovalValueMap <em>Approval Value
		 * Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ApprovalValueMap
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getApprovalValueMap()
		 * @generated
		 */
		EClass APPROVAL_VALUE_MAP = eINSTANCE.getApprovalValueMap();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference APPROVAL_VALUE_MAP__KEY = eINSTANCE.getApprovalValueMap_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute APPROVAL_VALUE_MAP__VALUE = eINSTANCE.getApprovalValueMap_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.RequirementEntry <em>Requirement
		 * Entry</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.internal.core.model.RequirementEntry
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getRequirementEntry()
		 * @generated
		 */
		EClass REQUIREMENT_ENTRY = eINSTANCE.getRequirementEntry();

		/**
		 * The meta object literal for the '<em><b>Status</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute REQUIREMENT_ENTRY__STATUS = eINSTANCE.getRequirementEntry_Status();

		/**
		 * The meta object literal for the '<em><b>By</b></em>' reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference REQUIREMENT_ENTRY__BY = eINSTANCE.getRequirementEntry_By();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.ReviewRequirementsMap <em>Review
		 * Requirements Map</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewRequirementsMap
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewRequirementsMap()
		 * @generated
		 */
		EClass REVIEW_REQUIREMENTS_MAP = eINSTANCE.getReviewRequirementsMap();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference REVIEW_REQUIREMENTS_MAP__KEY = eINSTANCE.getReviewRequirementsMap_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 *
		 * @generated
		 */
		EReference REVIEW_REQUIREMENTS_MAP__VALUE = eINSTANCE.getReviewRequirementsMap_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.internal.core.model.Commit <em>Commit</em>} ' class. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.internal.core.model.Commit
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getCommit()
		 * @generated
		 */
		EClass COMMIT = eINSTANCE.getCommit();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute COMMIT__ID = eINSTANCE.getCommit_Id();

		/**
		 * The meta object literal for the '<em><b>Subject</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute COMMIT__SUBJECT = eINSTANCE.getCommit_Subject();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.core.model.RequirementStatus <em>Requirement Status</em>}'
		 * enum. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.core.model.RequirementStatus
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getRequirementStatus()
		 * @generated
		 */
		EEnum REQUIREMENT_STATUS = eINSTANCE.getRequirementStatus();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.core.model.ReviewStatus <em>Review Status</em>}' enum. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.reviews.core.model.ReviewStatus
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getReviewStatus()
		 * @generated
		 */
		EEnum REVIEW_STATUS = eINSTANCE.getReviewStatus();

		/**
		 * The meta object literal for the '<em>IFile Revision</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.team.core.history.IFileRevision
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getIFileRevision()
		 * @generated
		 */
		EDataType IFILE_REVISION = eINSTANCE.getIFileRevision();

		/**
		 * The meta object literal for the '<em>Task Repository</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.tasks.core.TaskRepository
		 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage#getTaskRepository()
		 * @generated
		 */
		EDataType TASK_REPOSITORY = eINSTANCE.getTaskRepository();

	}

} //ReviewsPackage

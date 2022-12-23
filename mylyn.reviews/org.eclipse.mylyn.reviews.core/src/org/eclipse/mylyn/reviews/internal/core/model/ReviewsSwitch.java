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

import java.util.List;

import java.util.Map;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.mylyn.reviews.core.model.*;

/**
 * <!-- begin-user-doc --> The <b>Switch</b> for the model's inheritance hierarchy. It supports the call
 * {@link #doSwitch(EObject) doSwitch(object)} to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object and proceeding up the inheritance hierarchy until a non-null result is
 * returned, which is the result of the switch. <!-- end-user-doc -->
 * 
 * @see org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage
 * @generated
 */
public class ReviewsSwitch<T> {
	/**
	 * The cached model package <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected static ReviewsPackage modelPackage;

	/**
	 * Creates an instance of the switch. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ReviewsSwitch() {
		if (modelPackage == null) {
			modelPackage = ReviewsPackage.eINSTANCE;
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that
	 * result. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	public T doSwitch(EObject theEObject) {
		return doSwitch(theEObject.eClass(), theEObject);
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that
	 * result. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(EClass theEClass, EObject theEObject) {
		if (theEClass.eContainer() == modelPackage) {
			return doSwitch(theEClass.getClassifierID(), theEObject);
		} else {
			List<EClass> eSuperTypes = theEClass.getESuperTypes();
			return eSuperTypes.isEmpty() ? defaultCase(theEObject) : doSwitch(eSuperTypes.get(0), theEObject);
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that
	 * result. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
		case ReviewsPackage.COMMENT_CONTAINER: {
			ICommentContainer commentContainer = (ICommentContainer) theEObject;
			T result = caseCommentContainer(commentContainer);
			if (result == null) {
				result = defaultCase(theEObject);
			}
			return result;
		}
		case ReviewsPackage.CHANGE: {
			IChange change = (IChange) theEObject;
			T result = caseChange(change);
			if (result == null) {
				result = caseDated(change);
			}
			if (result == null) {
				result = defaultCase(theEObject);
			}
			return result;
		}
		case ReviewsPackage.REVIEW: {
			IReview review = (IReview) theEObject;
			T result = caseReview(review);
			if (result == null) {
				result = caseCommentContainer(review);
			}
			if (result == null) {
				result = caseChange(review);
			}
			if (result == null) {
				result = caseDated(review);
			}
			if (result == null) {
				result = defaultCase(theEObject);
			}
			return result;
		}
		case ReviewsPackage.COMMENT: {
			IComment comment = (IComment) theEObject;
			T result = caseComment(comment);
			if (result == null) {
				result = caseIndexed(comment);
			}
			if (result == null) {
				result = caseDated(comment);
			}
			if (result == null) {
				result = defaultCase(theEObject);
			}
			return result;
		}
		case ReviewsPackage.REVIEW_ITEM: {
			IReviewItem reviewItem = (IReviewItem) theEObject;
			T result = caseReviewItem(reviewItem);
			if (result == null) {
				result = caseCommentContainer(reviewItem);
			}
			if (result == null) {
				result = defaultCase(theEObject);
			}
			return result;
		}
		case ReviewsPackage.LOCATION: {
			ILocation location = (ILocation) theEObject;
			T result = caseLocation(location);
			if (result == null) {
				result = caseIndexed(location);
			}
			if (result == null) {
				result = defaultCase(theEObject);
			}
			return result;
		}
		case ReviewsPackage.USER: {
			IUser user = (IUser) theEObject;
			T result = caseUser(user);
			if (result == null) {
				result = defaultCase(theEObject);
			}
			return result;
		}
		case ReviewsPackage.REPOSITORY: {
			IRepository repository = (IRepository) theEObject;
			T result = caseRepository(repository);
			if (result == null) {
				result = defaultCase(theEObject);
			}
			return result;
		}
		case ReviewsPackage.FILE_ITEM: {
			IFileItem fileItem = (IFileItem) theEObject;
			T result = caseFileItem(fileItem);
			if (result == null) {
				result = caseReviewItem(fileItem);
			}
			if (result == null) {
				result = caseCommentContainer(fileItem);
			}
			if (result == null) {
				result = defaultCase(theEObject);
			}
			return result;
		}
		case ReviewsPackage.REVIEW_ITEM_SET: {
			IReviewItemSet reviewItemSet = (IReviewItemSet) theEObject;
			T result = caseReviewItemSet(reviewItemSet);
			if (result == null) {
				result = caseReviewItem(reviewItemSet);
			}
			if (result == null) {
				result = caseDated(reviewItemSet);
			}
			if (result == null) {
				result = caseCommentContainer(reviewItemSet);
			}
			if (result == null) {
				result = defaultCase(theEObject);
			}
			return result;
		}
		case ReviewsPackage.LINE_LOCATION: {
			ILineLocation lineLocation = (ILineLocation) theEObject;
			T result = caseLineLocation(lineLocation);
			if (result == null) {
				result = caseLocation(lineLocation);
			}
			if (result == null) {
				result = caseIndexed(lineLocation);
			}
			if (result == null) {
				result = defaultCase(theEObject);
			}
			return result;
		}
		case ReviewsPackage.LINE_RANGE: {
			ILineRange lineRange = (ILineRange) theEObject;
			T result = caseLineRange(lineRange);
			if (result == null) {
				result = defaultCase(theEObject);
			}
			return result;
		}
		case ReviewsPackage.FILE_VERSION: {
			IFileVersion fileVersion = (IFileVersion) theEObject;
			T result = caseFileVersion(fileVersion);
			if (result == null) {
				result = caseReviewItem(fileVersion);
			}
			if (result == null) {
				result = caseCommentContainer(fileVersion);
			}
			if (result == null) {
				result = defaultCase(theEObject);
			}
			return result;
		}
		case ReviewsPackage.INDEXED: {
			IIndexed indexed = (IIndexed) theEObject;
			T result = caseIndexed(indexed);
			if (result == null) {
				result = defaultCase(theEObject);
			}
			return result;
		}
		case ReviewsPackage.DATED: {
			IDated dated = (IDated) theEObject;
			T result = caseDated(dated);
			if (result == null) {
				result = defaultCase(theEObject);
			}
			return result;
		}
		case ReviewsPackage.APPROVAL_TYPE: {
			IApprovalType approvalType = (IApprovalType) theEObject;
			T result = caseApprovalType(approvalType);
			if (result == null) {
				result = defaultCase(theEObject);
			}
			return result;
		}
		case ReviewsPackage.USER_APPROVALS_MAP: {
			@SuppressWarnings("unchecked")
			Map.Entry<IUser, IReviewerEntry> userApprovalsMap = (Map.Entry<IUser, IReviewerEntry>) theEObject;
			T result = caseUserApprovalsMap(userApprovalsMap);
			if (result == null) {
				result = defaultCase(theEObject);
			}
			return result;
		}
		case ReviewsPackage.REVIEWER_ENTRY: {
			IReviewerEntry reviewerEntry = (IReviewerEntry) theEObject;
			T result = caseReviewerEntry(reviewerEntry);
			if (result == null) {
				result = defaultCase(theEObject);
			}
			return result;
		}
		case ReviewsPackage.APPROVAL_VALUE_MAP: {
			@SuppressWarnings("unchecked")
			Map.Entry<IApprovalType, Integer> approvalValueMap = (Map.Entry<IApprovalType, Integer>) theEObject;
			T result = caseApprovalValueMap(approvalValueMap);
			if (result == null) {
				result = defaultCase(theEObject);
			}
			return result;
		}
		case ReviewsPackage.REQUIREMENT_ENTRY: {
			IRequirementEntry requirementEntry = (IRequirementEntry) theEObject;
			T result = caseRequirementEntry(requirementEntry);
			if (result == null) {
				result = defaultCase(theEObject);
			}
			return result;
		}
		case ReviewsPackage.REVIEW_REQUIREMENTS_MAP: {
			@SuppressWarnings("unchecked")
			Map.Entry<IApprovalType, IRequirementEntry> reviewRequirementsMap = (Map.Entry<IApprovalType, IRequirementEntry>) theEObject;
			T result = caseReviewRequirementsMap(reviewRequirementsMap);
			if (result == null) {
				result = defaultCase(theEObject);
			}
			return result;
		}
		case ReviewsPackage.COMMIT: {
			ICommit commit = (ICommit) theEObject;
			T result = caseCommit(commit);
			if (result == null) {
				result = defaultCase(theEObject);
			}
			return result;
		}
		default:
			return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Comment Container</em>'. <!-- begin-user-doc
	 * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc
	 * -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Comment Container</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCommentContainer(ICommentContainer object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Review</em>'. <!-- begin-user-doc --> This
	 * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Review</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseReview(IReview object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Comment</em>'. <!-- begin-user-doc --> This
	 * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Comment</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseComment(IComment object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Review Item</em>'. <!-- begin-user-doc -->
	 * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Review Item</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseReviewItem(IReviewItem object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Location</em>'. <!-- begin-user-doc --> This
	 * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Location</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseLocation(ILocation object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>User</em>'. <!-- begin-user-doc --> This
	 * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>User</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseUser(IUser object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Repository</em>'. <!-- begin-user-doc -->
	 * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Repository</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRepository(IRepository object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>File Item</em>'. <!-- begin-user-doc -->
	 * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>File Item</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFileItem(IFileItem object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Review Item Set</em>'. <!-- begin-user-doc
	 * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc
	 * -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Review Item Set</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseReviewItemSet(IReviewItemSet object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Line Location</em>'. <!-- begin-user-doc -->
	 * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Line Location</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseLineLocation(ILineLocation object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Line Range</em>'. <!-- begin-user-doc -->
	 * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Line Range</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseLineRange(ILineRange object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>File Version</em>'. <!-- begin-user-doc -->
	 * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>File Version</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFileVersion(IFileVersion object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Indexed</em>'. <!-- begin-user-doc --> This
	 * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Indexed</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIndexed(IIndexed object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Dated</em>'. <!-- begin-user-doc --> This
	 * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Dated</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDated(IDated object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Reviewer Entry</em>'. <!-- begin-user-doc
	 * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc
	 * -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Reviewer Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseReviewerEntry(IReviewerEntry object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Approval Type</em>'. <!-- begin-user-doc -->
	 * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Approval Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseApprovalType(IApprovalType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Approval Value Map</em>'. <!--
	 * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
	 * end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Approval Value Map</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseApprovalValueMap(Map.Entry<IApprovalType, Integer> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Requirement Entry</em>'. <!-- begin-user-doc
	 * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc
	 * -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Requirement Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRequirementEntry(IRequirementEntry object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Review Requirements Map</em>'. <!--
	 * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
	 * end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Review Requirements Map</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseReviewRequirementsMap(Map.Entry<IApprovalType, IRequirementEntry> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Commit</em>'. <!-- begin-user-doc --> This
	 * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Commit</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCommit(ICommit object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>User Approvals Map</em>'. <!--
	 * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
	 * end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>User Approvals Map</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseUserApprovalsMap(Map.Entry<IUser, IReviewerEntry> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Change</em>'. <!-- begin-user-doc --> This
	 * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Change</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseChange(IChange object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'. <!-- begin-user-doc --> This
	 * implementation returns null; returning a non-null result will terminate the switch, but this is the last case
	 * anyway. <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	public T defaultCase(EObject object) {
		return null;
	}

} //ReviewsSwitch

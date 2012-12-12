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
		case ReviewsPackage.TOPIC_CONTAINER: {
			ITopicContainer topicContainer = (ITopicContainer) theEObject;
			T result = caseTopicContainer(topicContainer);
			if (result == null)
				result = caseReviewComponent(topicContainer);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case ReviewsPackage.REVIEW: {
			IReview review = (IReview) theEObject;
			T result = caseReview(review);
			if (result == null)
				result = caseTopicContainer(review);
			if (result == null)
				result = caseDated(review);
			if (result == null)
				result = caseReviewComponent(review);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case ReviewsPackage.COMMENT: {
			IComment comment = (IComment) theEObject;
			T result = caseComment(comment);
			if (result == null)
				result = caseReviewComponent(comment);
			if (result == null)
				result = caseIndexed(comment);
			if (result == null)
				result = caseDated(comment);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case ReviewsPackage.REVIEW_ITEM: {
			IReviewItem reviewItem = (IReviewItem) theEObject;
			T result = caseReviewItem(reviewItem);
			if (result == null)
				result = caseTopicContainer(reviewItem);
			if (result == null)
				result = caseReviewComponent(reviewItem);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case ReviewsPackage.LOCATION: {
			ILocation location = (ILocation) theEObject;
			T result = caseLocation(location);
			if (result == null)
				result = caseIndexed(location);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case ReviewsPackage.USER: {
			IUser user = (IUser) theEObject;
			T result = caseUser(user);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case ReviewsPackage.TASK_REFERENCE: {
			ITaskReference taskReference = (ITaskReference) theEObject;
			T result = caseTaskReference(taskReference);
			if (result == null)
				result = caseReviewComponent(taskReference);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case ReviewsPackage.REVIEW_STATE: {
			IReviewState reviewState = (IReviewState) theEObject;
			T result = caseReviewState(reviewState);
			if (result == null)
				result = caseReviewComponent(reviewState);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case ReviewsPackage.REVIEW_GROUP: {
			IReviewGroup reviewGroup = (IReviewGroup) theEObject;
			T result = caseReviewGroup(reviewGroup);
			if (result == null)
				result = caseReviewComponent(reviewGroup);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case ReviewsPackage.COMMENT_TYPE: {
			ICommentType commentType = (ICommentType) theEObject;
			T result = caseCommentType(commentType);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case ReviewsPackage.TOPIC: {
			ITopic topic = (ITopic) theEObject;
			T result = caseTopic(topic);
			if (result == null)
				result = caseComment(topic);
			if (result == null)
				result = caseReviewComponent(topic);
			if (result == null)
				result = caseIndexed(topic);
			if (result == null)
				result = caseDated(topic);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case ReviewsPackage.REVIEW_COMPONENT: {
			IReviewComponent reviewComponent = (IReviewComponent) theEObject;
			T result = caseReviewComponent(reviewComponent);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case ReviewsPackage.FILE_ITEM: {
			IFileItem fileItem = (IFileItem) theEObject;
			T result = caseFileItem(fileItem);
			if (result == null)
				result = caseReviewItem(fileItem);
			if (result == null)
				result = caseTopicContainer(fileItem);
			if (result == null)
				result = caseReviewComponent(fileItem);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case ReviewsPackage.REVIEW_ITEM_SET: {
			IReviewItemSet reviewItemSet = (IReviewItemSet) theEObject;
			T result = caseReviewItemSet(reviewItemSet);
			if (result == null)
				result = caseReviewItem(reviewItemSet);
			if (result == null)
				result = caseDated(reviewItemSet);
			if (result == null)
				result = caseTopicContainer(reviewItemSet);
			if (result == null)
				result = caseReviewComponent(reviewItemSet);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case ReviewsPackage.LINE_LOCATION: {
			ILineLocation lineLocation = (ILineLocation) theEObject;
			T result = caseLineLocation(lineLocation);
			if (result == null)
				result = caseLocation(lineLocation);
			if (result == null)
				result = caseIndexed(lineLocation);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case ReviewsPackage.LINE_RANGE: {
			ILineRange lineRange = (ILineRange) theEObject;
			T result = caseLineRange(lineRange);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case ReviewsPackage.FILE_REVISION: {
			IFileRevision fileRevision = (IFileRevision) theEObject;
			T result = caseFileRevision(fileRevision);
			if (result == null)
				result = caseReviewItem(fileRevision);
			if (result == null)
				result = caseTopicContainer(fileRevision);
			if (result == null)
				result = caseReviewComponent(fileRevision);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case ReviewsPackage.MODEL_VERSIONING: {
			IModelVersioning modelVersioning = (IModelVersioning) theEObject;
			T result = caseModelVersioning(modelVersioning);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case ReviewsPackage.INDEXED: {
			IIndexed indexed = (IIndexed) theEObject;
			T result = caseIndexed(indexed);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case ReviewsPackage.DATED: {
			IDated dated = (IDated) theEObject;
			T result = caseDated(dated);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		default:
			return defaultCase(theEObject);
		}
	}

	/**
	 * <<<<<<< Upstream, based on origin/master ======= Returns the result of interpreting the object as an instance of
	 * '<em>Topic Container</em>'. <!-- begin-user-doc --> This implementation returns null; returning a non-null result
	 * will terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Topic Container</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTopicContainer(ITopicContainer object) {
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
	 * Returns the result of interpreting the object as an instance of '<em>Task Reference</em>'. <!-- begin-user-doc
	 * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc
	 * -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Task Reference</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTaskReference(ITaskReference object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Review State</em>'. <!-- begin-user-doc -->
	 * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Review State</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseReviewState(IReviewState object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Review Group</em>'. <!-- begin-user-doc -->
	 * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Review Group</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseReviewGroup(IReviewGroup object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Comment Type</em>'. <!-- begin-user-doc -->
	 * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Comment Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCommentType(ICommentType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Topic</em>'. <!-- begin-user-doc --> This
	 * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Topic</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTopic(ITopic object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Review Component</em>'. <!-- begin-user-doc
	 * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc
	 * -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Review Component</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseReviewComponent(IReviewComponent object) {
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
	 * Returns the result of interpreting the object as an instance of '<em>File Revision</em>'. <!-- begin-user-doc -->
	 * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>File Revision</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFileRevision(IFileRevision object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Model Versioning</em>'. <!-- begin-user-doc
	 * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc
	 * -->
	 * 
	 * @param object
	 *            the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Model Versioning</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseModelVersioning(IModelVersioning object) {
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

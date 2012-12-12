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
package org.eclipse.mylyn.reviews.core.model;

import java.util.List;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Topic</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.ITopic#getTask <em>Task</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.ITopic#getLocations <em>Locations</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.ITopic#getComments <em>Comments</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.ITopic#getReview <em>Review</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.ITopic#getTitle <em>Title</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.ITopic#getItem <em>Item</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface ITopic extends IComment {
	/**
	 * Returns the value of the '<em><b>Task</b></em>' containment reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Task</em>' containment reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Task</em>' containment reference.
	 * @see #setTask(ITaskReference)
	 * @generated
	 */
	ITaskReference getTask();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.ITopic#getTask <em>Task</em>}' containment
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Task</em>' containment reference.
	 * @see #getTask()
	 * @generated
	 */
	void setTask(ITaskReference value);

	/**
	 * Returns the value of the '<em><b>Locations</b></em>' containment reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.ILocation}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Locations</em>' containment reference list isn't clear, there really should be more of
	 * a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Locations</em>' containment reference list.
	 * @generated
	 */
	List<ILocation> getLocations();

	/**
	 * Returns the value of the '<em><b>Comments</b></em>' reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.IComment}. It is bidirectional and its opposite is '
	 * {@link org.eclipse.mylyn.reviews.core.model.IComment#getParentTopic <em>Parent Topic</em>}'. <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of the '<em>Comments</em>' reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Comments</em>' reference list.
	 * @see org.eclipse.mylyn.reviews.core.model.IComment#getParentTopic
	 * @generated
	 */
	List<IComment> getComments();

	/**
	 * Returns the value of the '<em><b>Review</b></em>' reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Review</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Review</em>' reference.
	 * @see #setReview(IReview)
	 * @generated
	 */
	IReview getReview();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.ITopic#getReview <em>Review</em>}' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Review</em>' reference.
	 * @see #getReview()
	 * @generated
	 */
	void setReview(IReview value);

	/**
	 * Returns the value of the '<em><b>Title</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Title</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Title</em>' attribute.
	 * @see #setTitle(String)
	 * @generated
	 */
	String getTitle();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.ITopic#getTitle <em>Title</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Title</em>' attribute.
	 * @see #getTitle()
	 * @generated
	 */
	void setTitle(String value);

	/**
	 * Returns the value of the '<em><b>Item</b></em>' reference. It is bidirectional and its opposite is '
	 * {@link org.eclipse.mylyn.reviews.core.model.ITopicContainer#getDirectTopics <em>Direct Topics</em>}'. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Item</em>' reference isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Item</em>' reference.
	 * @see #setItem(ITopicContainer)
	 * @see org.eclipse.mylyn.reviews.core.model.ITopicContainer#getDirectTopics
	 * @generated
	 */
	ITopicContainer getItem();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.core.model.ITopic#getItem <em>Item</em>}' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Item</em>' reference.
	 * @see #getItem()
	 * @generated
	 */
	void setItem(ITopicContainer value);

} // ITopic

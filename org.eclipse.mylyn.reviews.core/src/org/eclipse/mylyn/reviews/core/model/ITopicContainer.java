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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Topic Container</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.ITopicContainer#getAllComments <em>All Comments</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.ITopicContainer#getTopics <em>Topics</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.ITopicContainer#getDirectTopics <em>Direct Topics</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface ITopicContainer extends IReviewComponent {
	/**
	 * Returns the value of the '<em><b>All Comments</b></em>' reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.IComment}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>All Comments</em>' reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>All Comments</em>' reference list.
	 * @generated
	 */
	List<IComment> getAllComments();

	/**
	 * Returns the value of the '<em><b>Topics</b></em>' reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.ITopic}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Topics</em>' reference list isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Topics</em>' reference list.
	 * @generated
	 */
	List<ITopic> getTopics();

	/**
	 * Returns the value of the '<em><b>Direct Topics</b></em>' reference list. The list contents are of type
	 * {@link org.eclipse.mylyn.reviews.core.model.ITopic}. It is bidirectional and its opposite is '
	 * {@link org.eclipse.mylyn.reviews.core.model.ITopic#getItem <em>Item</em>}'. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Direct Topics</em>' reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Direct Topics</em>' reference list.
	 * @see org.eclipse.mylyn.reviews.core.model.ITopic#getItem
	 * @generated
	 */
	List<ITopic> getDirectTopics();

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	ITopic createTopicComment(ILocation initalLocation, String commentText);

} // ITopicContainer

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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.mylyn.reviews.core.model.ITopicContainer;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Topic Container</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.TopicContainer#getAllComments <em>All Comments</em>}</li>
 * <li>{@link org.eclipse.mylyn.reviews.internal.core.model.TopicContainer#getTopics <em>Topics</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public abstract class TopicContainer extends ReviewComponent implements ITopicContainer {
	/**
	 * The cached value of the '{@link #getTopics() <em>Topics</em>}' reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getTopics()
	 * @generated
	 * @ordered
	 */
	protected EList<ITopic> topics;

	/**
	 * The cached value of the '{@link #getDirectTopics() <em>Direct Topics</em>}' reference list. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @see #getDirectTopics()
	 * @generated
	 * @ordered
	 */
	protected EList<ITopic> directTopics;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected TopicContainer() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewsPackage.Literals.TOPIC_CONTAINER;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public List<IComment> getAllComments() {
		// TODO: implement this method to return the 'All Comments' reference list
		// Ensure that you remove @generated or mark it @generated NOT
		// The list is expected to implement org.eclipse.emf.ecore.util.InternalEList and org.eclipse.emf.ecore.EStructuralFeature.Setting
		// so it's likely that an appropriate subclass of org.eclipse.emf.ecore.util.EcoreEList should be used.
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public List<ITopic> getTopics() {
		return getDirectTopics();
	}

	/**
	 * <!-- begin-user-doc -->See Important note in {@link ITopicContainer#getDirectTopics()} <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public List<ITopic> getDirectTopics() {
		if (directTopics == null) {
			directTopics = new EObjectWithInverseResolvingEList<ITopic>(ITopic.class, this,
					ReviewsPackage.TOPIC_CONTAINER__DIRECT_TOPICS, ReviewsPackage.TOPIC__ITEM);
		}
		return directTopics;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public ITopic createTopicComment(ILocation initalLocation, String commentText) {
		ITopic topic = ReviewsFactory.eINSTANCE.createTopic();
		topic.setDraft(true);
		topic.setDescription(commentText);
		if (initalLocation != null) {
			topic.getLocations().add(initalLocation);
		}

		IComment comment = ReviewsFactory.eINSTANCE.createComment();
		comment.setDescription(topic.getDescription());
		comment.setDraft(true);
		Date created = new Date();
		comment.setCreationDate(created);
		topic.setCreationDate(created);
		topic.getComments().add(comment);
		getTopics().add(topic);
		return topic;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ReviewsPackage.TOPIC_CONTAINER__DIRECT_TOPICS:
			return ((InternalEList<InternalEObject>) (InternalEList<?>) getDirectTopics()).basicAdd(otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ReviewsPackage.TOPIC_CONTAINER__DIRECT_TOPICS:
			return ((InternalEList<?>) getDirectTopics()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case ReviewsPackage.TOPIC_CONTAINER__ALL_COMMENTS:
			return getAllComments();
		case ReviewsPackage.TOPIC_CONTAINER__TOPICS:
			return getTopics();
		case ReviewsPackage.TOPIC_CONTAINER__DIRECT_TOPICS:
			return getDirectTopics();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case ReviewsPackage.TOPIC_CONTAINER__TOPICS:
			getTopics().clear();
			getTopics().addAll((Collection<? extends ITopic>) newValue);
			return;
		case ReviewsPackage.TOPIC_CONTAINER__DIRECT_TOPICS:
			getDirectTopics().clear();
			getDirectTopics().addAll((Collection<? extends ITopic>) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case ReviewsPackage.TOPIC_CONTAINER__TOPICS:
			getTopics().clear();
			return;
		case ReviewsPackage.TOPIC_CONTAINER__DIRECT_TOPICS:
			getDirectTopics().clear();
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case ReviewsPackage.TOPIC_CONTAINER__ALL_COMMENTS:
			return !getAllComments().isEmpty();
		case ReviewsPackage.TOPIC_CONTAINER__TOPICS:
			return topics != null && !topics.isEmpty();
		case ReviewsPackage.TOPIC_CONTAINER__DIRECT_TOPICS:
			return directTopics != null && !directTopics.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //TopicContainer

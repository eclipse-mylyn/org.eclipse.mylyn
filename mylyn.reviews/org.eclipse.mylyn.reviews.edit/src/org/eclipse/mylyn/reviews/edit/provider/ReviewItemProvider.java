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
package org.eclipse.mylyn.reviews.edit.provider;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.model.ReviewStatus;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage;

/**
 * This is the item provider adapter for a {@link org.eclipse.mylyn.reviews.core.model.IReview} object. <!-- begin-user-doc --> <!--
 * end-user-doc -->
 *
 * @generated
 */
public class ReviewItemProvider extends CommentContainerItemProvider {
	/**
	 * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public ReviewItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

			addCreationDatePropertyDescriptor(object);
			addModificationDatePropertyDescriptor(object);
			addIdPropertyDescriptor(object);
			addKeyPropertyDescriptor(object);
			addSubjectPropertyDescriptor(object);
			addMessagePropertyDescriptor(object);
			addOwnerPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Creation Date feature. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addCreationDatePropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_Dated_creationDate_feature"), //$NON-NLS-1$
						getString("_UI_PropertyDescriptor_description", "_UI_Dated_creationDate_feature", //$NON-NLS-1$//$NON-NLS-2$
								"_UI_Dated_type"), //$NON-NLS-1$
						ReviewsPackage.Literals.DATED__CREATION_DATE, true, false, false,
						ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Modification Date feature. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addModificationDatePropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_Dated_modificationDate_feature"), //$NON-NLS-1$
						getString("_UI_PropertyDescriptor_description", "_UI_Dated_modificationDate_feature", //$NON-NLS-1$//$NON-NLS-2$
								"_UI_Dated_type"), //$NON-NLS-1$
						ReviewsPackage.Literals.DATED__MODIFICATION_DATE, true, false, false,
						ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Key feature. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addKeyPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_Change_key_feature"), //$NON-NLS-1$
						getString("_UI_PropertyDescriptor_description", "_UI_Change_key_feature", "_UI_Change_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						ReviewsPackage.Literals.CHANGE__KEY, true, false, false,
						ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Subject feature. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addSubjectPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_Change_subject_feature"), //$NON-NLS-1$
						getString("_UI_PropertyDescriptor_description", "_UI_Change_subject_feature", //$NON-NLS-1$//$NON-NLS-2$
								"_UI_Change_type"), //$NON-NLS-1$
						ReviewsPackage.Literals.CHANGE__SUBJECT, true, false, false,
						ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Message feature. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addMessagePropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_Change_message_feature"), //$NON-NLS-1$
						getString("_UI_PropertyDescriptor_description", "_UI_Change_message_feature", //$NON-NLS-1$//$NON-NLS-2$
								"_UI_Change_type"), //$NON-NLS-1$
						ReviewsPackage.Literals.CHANGE__MESSAGE, true, false, false,
						ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Owner feature. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addOwnerPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_Change_owner_feature"), //$NON-NLS-1$
						getString("_UI_PropertyDescriptor_description", "_UI_Change_owner_feature", "_UI_Change_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						ReviewsPackage.Literals.CHANGE__OWNER, true, false, true, null, null, null));
	}

	/**
	 * This adds a property descriptor for the Id feature. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addIdPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_Change_id_feature"), //$NON-NLS-1$
						getString("_UI_PropertyDescriptor_description", "_UI_Change_id_feature", "_UI_Change_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						ReviewsPackage.Literals.CHANGE__ID, true, false, false,
						ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
	 * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
	 * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		if (childrenFeatures == null) {
			super.getChildrenFeatures(object);
			childrenFeatures.add(ReviewsPackage.Literals.CHANGE__STATE);
			childrenFeatures.add(ReviewsPackage.Literals.REVIEW__SETS);
			childrenFeatures.add(ReviewsPackage.Literals.REVIEW__PARENTS);
			childrenFeatures.add(ReviewsPackage.Literals.REVIEW__CHILDREN);
			childrenFeatures.add(ReviewsPackage.Literals.REVIEW__REVIEWER_APPROVALS);
			childrenFeatures.add(ReviewsPackage.Literals.REVIEW__REQUIREMENTS);
		}
		return childrenFeatures;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected EStructuralFeature getChildFeature(Object object, Object child) {
		// Check the type of the specified child object and return the proper feature to use for
		// adding (see {@link AddCommand}) it as a child.

		return super.getChildFeature(object, child);
	}

	/**
	 * This returns Review.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/Review")); //$NON-NLS-1$
	}

	/**
	 * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	@Override
	public String getText(Object object) {
		String label = ((IReview) object).getId() + " / " + ((IReview) object).getMessage(); //$NON-NLS-1$
		return label == null || label.length() == 0 ? getString("_UI_Review_type") : //$NON-NLS-1$
				label;
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached children and by creating a viewer
	 * notification, which it passes to {@link #fireNotifyChanged}. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(IReview.class)) {
			case ReviewsPackage.REVIEW__CREATION_DATE:
			case ReviewsPackage.REVIEW__MODIFICATION_DATE:
			case ReviewsPackage.REVIEW__ID:
			case ReviewsPackage.REVIEW__KEY:
			case ReviewsPackage.REVIEW__SUBJECT:
			case ReviewsPackage.REVIEW__MESSAGE:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
				return;
			case ReviewsPackage.REVIEW__STATE:
			case ReviewsPackage.REVIEW__SETS:
			case ReviewsPackage.REVIEW__PARENTS:
			case ReviewsPackage.REVIEW__CHILDREN:
			case ReviewsPackage.REVIEW__REVIEWER_APPROVALS:
			case ReviewsPackage.REVIEW__REQUIREMENTS:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
				return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children that can be created under this object. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);

		newChildDescriptors.add(createChildParameter(ReviewsPackage.Literals.CHANGE__STATE, ReviewStatus.NEW));

		newChildDescriptors.add(createChildParameter(ReviewsPackage.Literals.REVIEW__SETS,
				IReviewsFactory.INSTANCE.createReviewItemSet()));

		newChildDescriptors.add(
				createChildParameter(ReviewsPackage.Literals.REVIEW__PARENTS, IReviewsFactory.INSTANCE.createChange()));

		newChildDescriptors.add(
				createChildParameter(ReviewsPackage.Literals.REVIEW__PARENTS, IReviewsFactory.INSTANCE.createReview()));

		newChildDescriptors.add(createChildParameter(ReviewsPackage.Literals.REVIEW__CHILDREN,
				IReviewsFactory.INSTANCE.createChange()));

		newChildDescriptors.add(createChildParameter(ReviewsPackage.Literals.REVIEW__CHILDREN,
				IReviewsFactory.INSTANCE.createReview()));

		newChildDescriptors.add(createChildParameter(ReviewsPackage.Literals.REVIEW__REVIEWER_APPROVALS,
				((EFactory) IReviewsFactory.INSTANCE).create(ReviewsPackage.Literals.USER_APPROVALS_MAP)));

		newChildDescriptors.add(createChildParameter(ReviewsPackage.Literals.REVIEW__REQUIREMENTS,
				((EFactory) IReviewsFactory.INSTANCE).create(ReviewsPackage.Literals.REVIEW_REQUIREMENTS_MAP)));
	}

	/**
	 * This returns the label text for {@link org.eclipse.emf.edit.command.CreateChildCommand}. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @generated
	 */
	@Override
	public String getCreateChildText(Object owner, Object feature, Object child, Collection<?> selection) {
		Object childFeature = feature;
		Object childObject = child;

		boolean qualify = childFeature == ReviewsPackage.Literals.COMMENT_CONTAINER__COMMENTS
				|| childFeature == ReviewsPackage.Literals.COMMENT_CONTAINER__DRAFTS
				|| childFeature == ReviewsPackage.Literals.REVIEW__PARENTS
				|| childFeature == ReviewsPackage.Literals.REVIEW__CHILDREN;

		if (qualify) {
			return getString("_UI_CreateChild_text2", //$NON-NLS-1$
					new Object[] { getTypeText(childObject), getFeatureText(childFeature), getTypeText(owner) });
		}
		return super.getCreateChildText(owner, feature, child, selection);
	}

}

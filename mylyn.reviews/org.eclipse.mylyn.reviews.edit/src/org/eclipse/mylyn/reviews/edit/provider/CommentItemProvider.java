/**
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
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
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage;

/**
 * This is the item provider adapter for a {@link org.eclipse.mylyn.reviews.core.model.IComment} object. <!-- begin-user-doc --> <!--
 * end-user-doc -->
 *
 * @generated
 */
public class CommentItemProvider extends ItemProviderAdapter implements IEditingDomainItemProvider,
		IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource {
	/**
	 * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public CommentItemProvider(AdapterFactory adapterFactory) {
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

			addIndexPropertyDescriptor(object);
			addCreationDatePropertyDescriptor(object);
			addModificationDatePropertyDescriptor(object);
			addAuthorPropertyDescriptor(object);
			addDescriptionPropertyDescriptor(object);
			addIdPropertyDescriptor(object);
			addDraftPropertyDescriptor(object);
			addReviewPropertyDescriptor(object);
			addTitlePropertyDescriptor(object);
			addMinePropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Index feature. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addIndexPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_Indexed_index_feature"), //$NON-NLS-1$
						getString("_UI_PropertyDescriptor_description", "_UI_Indexed_index_feature", //$NON-NLS-1$//$NON-NLS-2$
								"_UI_Indexed_type"), //$NON-NLS-1$
						ReviewsPackage.Literals.INDEXED__INDEX, false, false, false,
						ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
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
	 * This adds a property descriptor for the Author feature. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addAuthorPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_Comment_author_feature"), //$NON-NLS-1$
						getString("_UI_PropertyDescriptor_description", "_UI_Comment_author_feature", //$NON-NLS-1$//$NON-NLS-2$
								"_UI_Comment_type"), //$NON-NLS-1$
						ReviewsPackage.Literals.COMMENT__AUTHOR, true, false, true, null, null, null));
	}

	/**
	 * This adds a property descriptor for the Description feature. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addDescriptionPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_Comment_description_feature"), //$NON-NLS-1$
						getString("_UI_PropertyDescriptor_description", "_UI_Comment_description_feature", //$NON-NLS-1$//$NON-NLS-2$
								"_UI_Comment_type"), //$NON-NLS-1$
						ReviewsPackage.Literals.COMMENT__DESCRIPTION, true, false, false,
						ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Id feature. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addIdPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_Comment_id_feature"), //$NON-NLS-1$
						getString("_UI_PropertyDescriptor_description", "_UI_Comment_id_feature", "_UI_Comment_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						ReviewsPackage.Literals.COMMENT__ID, true, false, false,
						ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Draft feature. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addDraftPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_Comment_draft_feature"), //$NON-NLS-1$
						getString("_UI_PropertyDescriptor_description", "_UI_Comment_draft_feature", //$NON-NLS-1$//$NON-NLS-2$
								"_UI_Comment_type"), //$NON-NLS-1$
						ReviewsPackage.Literals.COMMENT__DRAFT, true, false, false,
						ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Review feature. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addReviewPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_Comment_review_feature"), //$NON-NLS-1$
						getString("_UI_PropertyDescriptor_description", "_UI_Comment_review_feature", //$NON-NLS-1$//$NON-NLS-2$
								"_UI_Comment_type"), //$NON-NLS-1$
						ReviewsPackage.Literals.COMMENT__REVIEW, true, false, true, null, null, null));
	}

	/**
	 * This adds a property descriptor for the Title feature. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addTitlePropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_Comment_title_feature"), //$NON-NLS-1$
						getString("_UI_PropertyDescriptor_description", "_UI_Comment_title_feature", //$NON-NLS-1$//$NON-NLS-2$
								"_UI_Comment_type"), //$NON-NLS-1$
						ReviewsPackage.Literals.COMMENT__TITLE, true, false, false,
						ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Mine feature. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addMinePropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_Comment_mine_feature"), //$NON-NLS-1$
						getString("_UI_PropertyDescriptor_description", "_UI_Comment_mine_feature", "_UI_Comment_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						ReviewsPackage.Literals.COMMENT__MINE, false, false, false,
						ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
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
			childrenFeatures.add(ReviewsPackage.Literals.COMMENT__REPLIES);
			childrenFeatures.add(ReviewsPackage.Literals.COMMENT__LOCATIONS);
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
	 * This returns Comment.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/Comment")); //$NON-NLS-1$
	}

	/**
	 * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		String label = ((IComment) object).getId();
		return label == null || label.length() == 0 ? getString("_UI_Comment_type") : //$NON-NLS-1$
				getString("_UI_Comment_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

		switch (notification.getFeatureID(IComment.class)) {
			case ReviewsPackage.COMMENT__INDEX:
			case ReviewsPackage.COMMENT__CREATION_DATE:
			case ReviewsPackage.COMMENT__MODIFICATION_DATE:
			case ReviewsPackage.COMMENT__DESCRIPTION:
			case ReviewsPackage.COMMENT__ID:
			case ReviewsPackage.COMMENT__DRAFT:
			case ReviewsPackage.COMMENT__TITLE:
			case ReviewsPackage.COMMENT__MINE:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
				return;
			case ReviewsPackage.COMMENT__REPLIES:
			case ReviewsPackage.COMMENT__LOCATIONS:
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

		newChildDescriptors.add(createChildParameter(ReviewsPackage.Literals.COMMENT__REPLIES,
				IReviewsFactory.INSTANCE.createComment()));

		newChildDescriptors.add(createChildParameter(ReviewsPackage.Literals.COMMENT__LOCATIONS,
				IReviewsFactory.INSTANCE.createLineLocation()));
	}

	/**
	 * Return the resource locator for this item provider's resources. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public ResourceLocator getResourceLocator() {
		return ReviewsEditPlugin.INSTANCE;
	}

}

/**
 * Copyright (c) 2013 Tasktop Technologies and others.
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

import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;

import org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage;

/**
 * This is the item provider adapter for a {@link org.eclipse.mylyn.reviews.core.model.IRepository} object. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 * 
 * @generated
 */
public class RepositoryItemProvider extends ItemProviderAdapter implements IEditingDomainItemProvider,
		IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource {
	/**
	 * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public RepositoryItemProvider(AdapterFactory adapterFactory) {
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

			addTaskRepositoryUrlPropertyDescriptor(object);
			addTaskConnectorKindPropertyDescriptor(object);
			addTaskRepositoryPropertyDescriptor(object);
			addAccountPropertyDescriptor(object);
			addDescriptionPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Task Repository Url feature. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 */
	protected void addTaskRepositoryUrlPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_Repository_taskRepositoryUrl_feature"), //$NON-NLS-1$
						getString("_UI_PropertyDescriptor_description", "_UI_Repository_taskRepositoryUrl_feature", //$NON-NLS-1$//$NON-NLS-2$
								"_UI_Repository_type"), //$NON-NLS-1$
						ReviewsPackage.Literals.REPOSITORY__TASK_REPOSITORY_URL, true, false, false,
						ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Task Connector Kind feature. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 */
	protected void addTaskConnectorKindPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_Repository_taskConnectorKind_feature"), //$NON-NLS-1$
						getString("_UI_PropertyDescriptor_description", "_UI_Repository_taskConnectorKind_feature", //$NON-NLS-1$//$NON-NLS-2$
								"_UI_Repository_type"), //$NON-NLS-1$
						ReviewsPackage.Literals.REPOSITORY__TASK_CONNECTOR_KIND, true, false, false,
						ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Task Repository feature. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void addTaskRepositoryPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_Repository_taskRepository_feature"), //$NON-NLS-1$
						getString("_UI_PropertyDescriptor_description", "_UI_Repository_taskRepository_feature", //$NON-NLS-1$//$NON-NLS-2$
								"_UI_Repository_type"), //$NON-NLS-1$
						ReviewsPackage.Literals.REPOSITORY__TASK_REPOSITORY, true, false, false,
						ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Account feature. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void addAccountPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_Repository_account_feature"), //$NON-NLS-1$
						getString("_UI_PropertyDescriptor_description", "_UI_Repository_account_feature", //$NON-NLS-1$//$NON-NLS-2$
								"_UI_Repository_type"), //$NON-NLS-1$
						ReviewsPackage.Literals.REPOSITORY__ACCOUNT, true, false, true, null, null, null));
	}

	/**
	 * This adds a property descriptor for the Description feature. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void addDescriptionPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_Repository_description_feature"), //$NON-NLS-1$
						getString("_UI_PropertyDescriptor_description", "_UI_Repository_description_feature", //$NON-NLS-1$//$NON-NLS-2$
								"_UI_Repository_type"), //$NON-NLS-1$
						ReviewsPackage.Literals.REPOSITORY__DESCRIPTION, true, false, false,
						ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
	 * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
	 * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		if (childrenFeatures == null) {
			super.getChildrenFeatures(object);
			childrenFeatures.add(ReviewsPackage.Literals.REPOSITORY__APPROVAL_TYPES);
			childrenFeatures.add(ReviewsPackage.Literals.REPOSITORY__REVIEWS);
			childrenFeatures.add(ReviewsPackage.Literals.REPOSITORY__USERS);
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
	 * This returns Repository.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/Repository")); //$NON-NLS-1$
	}

	/**
	 * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		String label = ((IRepository) object).getTaskRepositoryUrl();
		return label == null || label.length() == 0 ? getString("_UI_Repository_type") : //$NON-NLS-1$
				getString("_UI_Repository_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached children and by creating
	 * a viewer notification, which it passes to {@link #fireNotifyChanged}. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(IRepository.class)) {
		case ReviewsPackage.REPOSITORY__TASK_REPOSITORY_URL:
		case ReviewsPackage.REPOSITORY__TASK_CONNECTOR_KIND:
		case ReviewsPackage.REPOSITORY__TASK_REPOSITORY:
		case ReviewsPackage.REPOSITORY__DESCRIPTION:
			fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
			return;
		case ReviewsPackage.REPOSITORY__APPROVAL_TYPES:
		case ReviewsPackage.REPOSITORY__REVIEWS:
		case ReviewsPackage.REPOSITORY__USERS:
			fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
			return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children that can be created
	 * under this object. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);

		newChildDescriptors.add(createChildParameter(ReviewsPackage.Literals.REPOSITORY__APPROVAL_TYPES,
				IReviewsFactory.INSTANCE.createApprovalType()));

		newChildDescriptors.add(createChildParameter(ReviewsPackage.Literals.REPOSITORY__REVIEWS,
				IReviewsFactory.INSTANCE.createReview()));

		newChildDescriptors.add(
				createChildParameter(ReviewsPackage.Literals.REPOSITORY__USERS, IReviewsFactory.INSTANCE.createUser()));
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

/**
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.builds.internal.core;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.IOperation;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Element</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildElement#getUrl <em>Url</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildElement#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildElement#getOperations <em>Operations</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildElement#getElementStatus <em>Element Status</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildElement#getRefreshDate <em>Refresh Date</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.BuildElement#getAttributes <em>Attributes</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public abstract class BuildElement extends EObjectImpl implements IBuildElement {
	/**
	 * The default value of the '{@link #getUrl() <em>Url</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getUrl()
	 * @generated
	 * @ordered
	 */
	protected static final String URL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getUrl() <em>Url</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getUrl()
	 * @generated
	 * @ordered
	 */
	protected String url = URL_EDEFAULT;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getOperations() <em>Operations</em>}' attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getOperations()
	 * @generated
	 * @ordered
	 */
	protected EList<IOperation> operations;

	/**
	 * The default value of the '{@link #getElementStatus() <em>Element Status</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getElementStatus()
	 * @generated
	 * @ordered
	 */
	protected static final IStatus ELEMENT_STATUS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getElementStatus() <em>Element Status</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getElementStatus()
	 * @generated
	 * @ordered
	 */
	protected IStatus elementStatus = ELEMENT_STATUS_EDEFAULT;

	/**
	 * The default value of the '{@link #getRefreshDate() <em>Refresh Date</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getRefreshDate()
	 * @generated
	 * @ordered
	 */
	protected static final Date REFRESH_DATE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRefreshDate() <em>Refresh Date</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getRefreshDate()
	 * @generated
	 * @ordered
	 */
	protected Date refreshDate = REFRESH_DATE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getAttributes() <em>Attributes</em>}' map. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getAttributes()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, String> attributes;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected BuildElement() {
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BuildPackage.Literals.BUILD_ELEMENT;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getUrl() {
		return url;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setUrl(String newUrl) {
		String oldUrl = url;
		url = newUrl;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_ELEMENT__URL, oldUrl, url));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_ELEMENT__NAME, oldName, name));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public List<IOperation> getOperations() {
		if (operations == null) {
			operations = new EDataTypeUniqueEList<>(IOperation.class, this,
					BuildPackage.BUILD_ELEMENT__OPERATIONS);
		}
		return operations;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public IStatus getElementStatus() {
		return elementStatus;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setElementStatus(IStatus newElementStatus) {
		IStatus oldElementStatus = elementStatus;
		elementStatus = newElementStatus;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_ELEMENT__ELEMENT_STATUS,
					oldElementStatus, elementStatus));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Date getRefreshDate() {
		return refreshDate;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setRefreshDate(Date newRefreshDate) {
		Date oldRefreshDate = refreshDate;
		refreshDate = newRefreshDate;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BUILD_ELEMENT__REFRESH_DATE,
					oldRefreshDate, refreshDate));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Map<String, String> getAttributes() {
		if (attributes == null) {
			attributes = new EcoreEMap<>(BuildPackage.Literals.STRING_TO_STRING_MAP,
					StringToStringMap.class, this, BuildPackage.BUILD_ELEMENT__ATTRIBUTES);
		}
		return attributes.map();
	}

	@Override
	public abstract String getLabel();

	@Override
	public abstract IBuildServer getServer();

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case BuildPackage.BUILD_ELEMENT__ATTRIBUTES:
				return ((InternalEList<?>) ((EMap.InternalMapView<String, String>) getAttributes()).eMap())
						.basicRemove(otherEnd, msgs);
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
			case BuildPackage.BUILD_ELEMENT__URL:
				return getUrl();
			case BuildPackage.BUILD_ELEMENT__NAME:
				return getName();
			case BuildPackage.BUILD_ELEMENT__OPERATIONS:
				return getOperations();
			case BuildPackage.BUILD_ELEMENT__ELEMENT_STATUS:
				return getElementStatus();
			case BuildPackage.BUILD_ELEMENT__REFRESH_DATE:
				return getRefreshDate();
			case BuildPackage.BUILD_ELEMENT__ATTRIBUTES:
				if (coreType) {
					return ((EMap.InternalMapView<String, String>) getAttributes()).eMap();
				} else {
					return getAttributes();
				}
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
			case BuildPackage.BUILD_ELEMENT__URL:
				setUrl((String) newValue);
				return;
			case BuildPackage.BUILD_ELEMENT__NAME:
				setName((String) newValue);
				return;
			case BuildPackage.BUILD_ELEMENT__OPERATIONS:
				getOperations().clear();
				getOperations().addAll((Collection<? extends IOperation>) newValue);
				return;
			case BuildPackage.BUILD_ELEMENT__ELEMENT_STATUS:
				setElementStatus((IStatus) newValue);
				return;
			case BuildPackage.BUILD_ELEMENT__REFRESH_DATE:
				setRefreshDate((Date) newValue);
				return;
			case BuildPackage.BUILD_ELEMENT__ATTRIBUTES:
				((EStructuralFeature.Setting) ((EMap.InternalMapView<String, String>) getAttributes()).eMap())
						.set(newValue);
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
			case BuildPackage.BUILD_ELEMENT__URL:
				setUrl(URL_EDEFAULT);
				return;
			case BuildPackage.BUILD_ELEMENT__NAME:
				setName(NAME_EDEFAULT);
				return;
			case BuildPackage.BUILD_ELEMENT__OPERATIONS:
				getOperations().clear();
				return;
			case BuildPackage.BUILD_ELEMENT__ELEMENT_STATUS:
				setElementStatus(ELEMENT_STATUS_EDEFAULT);
				return;
			case BuildPackage.BUILD_ELEMENT__REFRESH_DATE:
				setRefreshDate(REFRESH_DATE_EDEFAULT);
				return;
			case BuildPackage.BUILD_ELEMENT__ATTRIBUTES:
				getAttributes().clear();
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
			case BuildPackage.BUILD_ELEMENT__URL:
				return URL_EDEFAULT == null ? url != null : !URL_EDEFAULT.equals(url);
			case BuildPackage.BUILD_ELEMENT__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case BuildPackage.BUILD_ELEMENT__OPERATIONS:
				return operations != null && !operations.isEmpty();
			case BuildPackage.BUILD_ELEMENT__ELEMENT_STATUS:
				return ELEMENT_STATUS_EDEFAULT == null
						? elementStatus != null
						: !ELEMENT_STATUS_EDEFAULT.equals(elementStatus);
			case BuildPackage.BUILD_ELEMENT__REFRESH_DATE:
				return REFRESH_DATE_EDEFAULT == null ? refreshDate != null : !REFRESH_DATE_EDEFAULT.equals(refreshDate);
			case BuildPackage.BUILD_ELEMENT__ATTRIBUTES:
				return attributes != null && !attributes.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) {
			return super.toString();
		}

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (url: "); //$NON-NLS-1$
		result.append(url);
		result.append(", name: "); //$NON-NLS-1$
		result.append(name);
		result.append(", operations: "); //$NON-NLS-1$
		result.append(operations);
		result.append(", elementStatus: "); //$NON-NLS-1$
		result.append(elementStatus);
		result.append(", refreshDate: "); //$NON-NLS-1$
		result.append(refreshDate);
		result.append(')');
		return result.toString();
	}

	protected BuildElement original;

	public BuildElement getOriginal() {
		return original;
	}

	public BuildElement createWorkingCopy() {
		EcoreUtil.Copier copier = new EcoreUtil.Copier();
		BuildElement newElement = (BuildElement) copier.copy(this);
		// FIXME clone containment hierarchy instead
		copier.copyReferences();
		newElement.original = this;
		return newElement;
	}

} //BuildElement

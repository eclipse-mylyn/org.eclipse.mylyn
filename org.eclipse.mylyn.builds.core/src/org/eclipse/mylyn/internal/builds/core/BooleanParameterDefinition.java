/**
 * <copyright>
 * </copyright>
 *
 * $Id: BooleanParameterDefinition.java,v 1.1 2010/08/27 06:49:23 spingel Exp $
 */
package org.eclipse.mylyn.internal.builds.core;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.mylyn.builds.core.IBuildPlan;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Boolean Parameter Definition</b></em>'.
 * <!-- end-user-doc -->
 * 
 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getBooleanParameterDefinition()
 * @model kind="class"
 * @generated
 */
public class BooleanParameterDefinition extends EObjectImpl implements IBooleanParameterDefinition {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

	/**
	 * The default value of the '{@link #isDefaultValue() <em>Default Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #isDefaultValue()
	 * @generated
	 * @ordered
	 */
	protected static final boolean DEFAULT_VALUE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isDefaultValue() <em>Default Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #isDefaultValue()
	 * @generated
	 * @ordered
	 */
	protected boolean defaultValue = DEFAULT_VALUE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected BooleanParameterDefinition() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BuildPackage.Literals.BOOLEAN_PARAMETER_DEFINITION;
	}

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIParameterDefinition_Name()
	 * @model
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BooleanParameterDefinition#getName
	 * <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BuildPackage.BOOLEAN_PARAMETER_DEFINITION__NAME,
					oldName, name));
	}

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Description</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIParameterDefinition_Description()
	 * @model
	 * @generated
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BooleanParameterDefinition#getDescription
	 * <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					BuildPackage.BOOLEAN_PARAMETER_DEFINITION__DESCRIPTION, oldDescription, description));
	}

	/**
	 * Returns the value of the '<em><b>Containing Build Plan</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.eclipse.mylyn.builds.core.IBuildPlan#getParameterDefinitions
	 * <em>Parameter Definitions</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Containing Build Plan</em>' container reference isn't clear, there really should be
	 * more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Containing Build Plan</em>' container reference.
	 * @see #setContainingBuildPlan(IBuildPlan)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIParameterDefinition_ContainingBuildPlan()
	 * @see org.eclipse.mylyn.builds.core.IBuildPlan#getParameterDefinitions
	 * @model type="org.eclipse.mylyn.internal.builds.core.IBuildPlan" opposite="parameterDefinitions" transient="false"
	 * @generated
	 */
	public IBuildPlan getContainingBuildPlan() {
		if (eContainerFeatureID() != BuildPackage.BOOLEAN_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN)
			return null;
		return (IBuildPlan) eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetContainingBuildPlan(IBuildPlan newContainingBuildPlan, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject) newContainingBuildPlan,
				BuildPackage.BOOLEAN_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN, msgs);
		return msgs;
	}

	/**
	 * Sets the value of the '
	 * {@link org.eclipse.mylyn.internal.builds.core.BooleanParameterDefinition#getContainingBuildPlan
	 * <em>Containing Build Plan</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Containing Build Plan</em>' container reference.
	 * @see #getContainingBuildPlan()
	 * @generated
	 */
	public void setContainingBuildPlan(IBuildPlan newContainingBuildPlan) {
		if (newContainingBuildPlan != eInternalContainer()
				|| (eContainerFeatureID() != BuildPackage.BOOLEAN_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN && newContainingBuildPlan != null)) {
			if (EcoreUtil.isAncestor(this, (EObject) newContainingBuildPlan))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newContainingBuildPlan != null)
				msgs = ((InternalEObject) newContainingBuildPlan).eInverseAdd(this,
						BuildPackage.IBUILD_PLAN__PARAMETER_DEFINITIONS, IBuildPlan.class, msgs);
			msgs = basicSetContainingBuildPlan(newContainingBuildPlan, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					BuildPackage.BOOLEAN_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN, newContainingBuildPlan,
					newContainingBuildPlan));
	}

	/**
	 * Returns the value of the '<em><b>Default Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Default Value</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Default Value</em>' attribute.
	 * @see #setDefaultValue(boolean)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBooleanParameterDefinition_DefaultValue()
	 * @model
	 * @generated
	 */
	public boolean isDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.BooleanParameterDefinition#isDefaultValue
	 * <em>Default Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Default Value</em>' attribute.
	 * @see #isDefaultValue()
	 * @generated
	 */
	public void setDefaultValue(boolean newDefaultValue) {
		boolean oldDefaultValue = defaultValue;
		defaultValue = newDefaultValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					BuildPackage.BOOLEAN_PARAMETER_DEFINITION__DEFAULT_VALUE, oldDefaultValue, defaultValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN:
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			return basicSetContainingBuildPlan((IBuildPlan) otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN:
			return basicSetContainingBuildPlan(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN:
			return eInternalContainer().eInverseRemove(this, BuildPackage.IBUILD_PLAN__PARAMETER_DEFINITIONS,
					IBuildPlan.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION__NAME:
			return getName();
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION__DESCRIPTION:
			return getDescription();
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN:
			return getContainingBuildPlan();
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION__DEFAULT_VALUE:
			return isDefaultValue();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION__NAME:
			setName((String) newValue);
			return;
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION__DESCRIPTION:
			setDescription((String) newValue);
			return;
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN:
			setContainingBuildPlan((IBuildPlan) newValue);
			return;
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION__DEFAULT_VALUE:
			setDefaultValue((Boolean) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION__NAME:
			setName(NAME_EDEFAULT);
			return;
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION__DESCRIPTION:
			setDescription(DESCRIPTION_EDEFAULT);
			return;
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN:
			setContainingBuildPlan((IBuildPlan) null);
			return;
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION__DEFAULT_VALUE:
			setDefaultValue(DEFAULT_VALUE_EDEFAULT);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION__DESCRIPTION:
			return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION__CONTAINING_BUILD_PLAN:
			return getContainingBuildPlan() != null;
		case BuildPackage.BOOLEAN_PARAMETER_DEFINITION__DEFAULT_VALUE:
			return defaultValue != DEFAULT_VALUE_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: ");
		result.append(name);
		result.append(", description: ");
		result.append(description);
		result.append(", defaultValue: ");
		result.append(defaultValue);
		result.append(')');
		return result.toString();
	}

} // BooleanParameterDefinition

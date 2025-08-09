/**
 * <copyright>
 * </copyright>
 *
 * $Id: ChoiceParameterDefinition.java,v 1.2 2010/08/28 09:21:40 spingel Exp $
 */
package org.eclipse.mylyn.builds.internal.core;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.mylyn.builds.core.IChoiceParameterDefinition;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Choice Parameter Definition</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.ChoiceParameterDefinition#getOptions <em>Options</em>}</li>
 * <li>{@link org.eclipse.mylyn.builds.internal.core.ChoiceParameterDefinition#getDefaultValue <em>Default Value</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ChoiceParameterDefinition extends ParameterDefinition implements IChoiceParameterDefinition {
	/**
	 * The cached value of the '{@link #getOptions() <em>Options</em>}' attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getOptions()
	 * @generated
	 * @ordered
	 */
	protected EList<String> options;

	/**
	 * The default value of the '{@link #getDefaultValue() <em>Default Value</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getDefaultValue()
	 * @generated
	 * @ordered
	 */
	protected static final String DEFAULT_VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDefaultValue() <em>Default Value</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getDefaultValue()
	 * @generated
	 * @ordered
	 */
	protected String defaultValue = DEFAULT_VALUE_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected ChoiceParameterDefinition() {
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BuildPackage.Literals.CHOICE_PARAMETER_DEFINITION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Options</em>' attribute list isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public List<String> getOptions() {
		if (options == null) {
			options = new EDataTypeUniqueEList<>(String.class, this,
					BuildPackage.CHOICE_PARAMETER_DEFINITION__OPTIONS);
		}
		return options;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setDefaultValue(String newDefaultValue) {
		String oldDefaultValue = defaultValue;
		defaultValue = newDefaultValue;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET,
					BuildPackage.CHOICE_PARAMETER_DEFINITION__DEFAULT_VALUE, oldDefaultValue, defaultValue));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case BuildPackage.CHOICE_PARAMETER_DEFINITION__OPTIONS:
				return getOptions();
			case BuildPackage.CHOICE_PARAMETER_DEFINITION__DEFAULT_VALUE:
				return getDefaultValue();
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
			case BuildPackage.CHOICE_PARAMETER_DEFINITION__OPTIONS:
				getOptions().clear();
				getOptions().addAll((Collection<? extends String>) newValue);
				return;
			case BuildPackage.CHOICE_PARAMETER_DEFINITION__DEFAULT_VALUE:
				setDefaultValue((String) newValue);
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
			case BuildPackage.CHOICE_PARAMETER_DEFINITION__OPTIONS:
				getOptions().clear();
				return;
			case BuildPackage.CHOICE_PARAMETER_DEFINITION__DEFAULT_VALUE:
				setDefaultValue(DEFAULT_VALUE_EDEFAULT);
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
			case BuildPackage.CHOICE_PARAMETER_DEFINITION__OPTIONS:
				return options != null && !options.isEmpty();
			case BuildPackage.CHOICE_PARAMETER_DEFINITION__DEFAULT_VALUE:
				return DEFAULT_VALUE_EDEFAULT == null
						? defaultValue != null
						: !DEFAULT_VALUE_EDEFAULT.equals(defaultValue);
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
		result.append(" (options: "); //$NON-NLS-1$
		result.append(options);
		result.append(", defaultValue: "); //$NON-NLS-1$
		result.append(defaultValue);
		result.append(')');
		return result.toString();
	}

} // ChoiceParameterDefinition

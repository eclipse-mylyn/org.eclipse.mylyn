/**
 * <copyright>
 * </copyright>
 *
 * $Id: IBooleanParameterDefinition.java,v 1.1 2010/08/27 06:49:23 spingel Exp $
 */
package org.eclipse.mylyn.internal.builds.core;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IBoolean Parameter Definition</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.internal.builds.core.IBooleanParameterDefinition#isDefaultValue <em>Default Value</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIBooleanParameterDefinition()
 * @model kind="class" interface="true" abstract="true"
 * @generated
 */
public interface IBooleanParameterDefinition extends IParameterDefinition {
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
	boolean isDefaultValue();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.internal.builds.core.IBooleanParameterDefinition#isDefaultValue
	 * <em>Default Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Default Value</em>' attribute.
	 * @see #isDefaultValue()
	 * @generated
	 */
	void setDefaultValue(boolean value);

} // IBooleanParameterDefinition

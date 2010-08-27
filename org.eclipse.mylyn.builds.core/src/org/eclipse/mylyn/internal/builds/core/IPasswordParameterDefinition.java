/**
 * <copyright>
 * </copyright>
 *
 * $Id: IPasswordParameterDefinition.java,v 1.1 2010/08/27 06:49:23 spingel Exp $
 */
package org.eclipse.mylyn.internal.builds.core;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IPassword Parameter Definition</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.internal.builds.core.IPasswordParameterDefinition#getDefaultValue <em>Default Value
 * </em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIPasswordParameterDefinition()
 * @model kind="class" interface="true" abstract="true"
 * @generated
 */
public interface IPasswordParameterDefinition extends IParameterDefinition {
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
	 * @see #setDefaultValue(String)
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIPasswordParameterDefinition_DefaultValue()
	 * @model
	 * @generated
	 */
	String getDefaultValue();

	/**
	 * Sets the value of the '
	 * {@link org.eclipse.mylyn.internal.builds.core.IPasswordParameterDefinition#getDefaultValue
	 * <em>Default Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Default Value</em>' attribute.
	 * @see #getDefaultValue()
	 * @generated
	 */
	void setDefaultValue(String value);

} // IPasswordParameterDefinition

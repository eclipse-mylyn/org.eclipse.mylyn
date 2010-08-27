/**
 * <copyright>
 * </copyright>
 *
 * $Id: IChoiceParameterDefinition.java,v 1.1 2010/08/27 06:49:23 spingel Exp $
 */
package org.eclipse.mylyn.internal.builds.core;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IChoice Parameter Definition</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.internal.builds.core.IChoiceParameterDefinition#getOptions <em>Options</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIChoiceParameterDefinition()
 * @model kind="class" interface="true" abstract="true"
 * @generated
 */
public interface IChoiceParameterDefinition extends IParameterDefinition {
	/**
	 * Returns the value of the '<em><b>Options</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Options</em>' attribute list isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Options</em>' attribute list.
	 * @see org.eclipse.mylyn.internal.builds.core.BuildPackage#getIChoiceParameterDefinition_Options()
	 * @model required="true"
	 * @generated
	 */
	EList<String> getOptions();

} // IChoiceParameterDefinition

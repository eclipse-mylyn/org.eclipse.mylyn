/**
 * <copyright>
 * </copyright>
 *
 * $Id: FileParameterDefinition.java,v 1.2 2010/08/28 09:21:40 spingel Exp $
 */
package org.eclipse.mylyn.builds.internal.core;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.mylyn.builds.core.IFileParameterDefinition;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>File Parameter Definition</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class FileParameterDefinition extends ParameterDefinition implements IFileParameterDefinition {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected FileParameterDefinition() {
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
		return BuildPackage.Literals.FILE_PARAMETER_DEFINITION;
	}

} // FileParameterDefinition

/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.ocf;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Root Files</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ocf.RootFiles#getRootfiles <em>Rootfiles</em>}</li>
 * </ul>
 *
 * @see org.eclipse.mylyn.docs.epub.ocf.OCFPackage#getRootFiles()
 * @model
 * @generated
 */
public interface RootFiles extends EObject {
	/**
	 * Returns the value of the '<em><b>Rootfiles</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.ocf.RootFile}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Rootfiles</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rootfiles</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.ocf.OCFPackage#getRootFiles_Rootfiles()
	 * @model containment="true" required="true"
	 *        extendedMetaData="name='rootfile' namespace='urn:oasis:names:tc:opendocument:xmlns:container'"
	 * @generated
	 */
	EList<RootFile> getRootfiles();

} // RootFiles

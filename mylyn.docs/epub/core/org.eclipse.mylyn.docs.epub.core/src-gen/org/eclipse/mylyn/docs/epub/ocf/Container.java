/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.ocf;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ocf.Container#getRootfiles <em>Rootfiles</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ocf.Container#getVersion <em>Version</em>}</li>
 * </ul>
 *
 * @see org.eclipse.mylyn.docs.epub.ocf.OCFPackage#getContainer()
 * @model extendedMetaData="name='container' namespace='urn:oasis:names:tc:opendocument:xmlns:container'"
 * @generated
 */
public interface Container extends EObject {
	/**
	 * Returns the value of the '<em><b>Rootfiles</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Rootfiles</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rootfiles</em>' containment reference.
	 * @see #setRootfiles(RootFiles)
	 * @see org.eclipse.mylyn.docs.epub.ocf.OCFPackage#getContainer_Rootfiles()
	 * @model containment="true" required="true"
	 *        extendedMetaData="name='rootfiles' namespace='urn:oasis:names:tc:opendocument:xmlns:container'"
	 * @generated
	 */
	RootFiles getRootfiles();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ocf.Container#getRootfiles <em>Rootfiles</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rootfiles</em>' containment reference.
	 * @see #getRootfiles()
	 * @generated
	 */
	void setRootfiles(RootFiles value);

	/**
	 * Returns the value of the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Version</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Version</em>' attribute.
	 * @see #setVersion(String)
	 * @see org.eclipse.mylyn.docs.epub.ocf.OCFPackage#getContainer_Version()
	 * @model
	 * @generated
	 */
	String getVersion();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ocf.Container#getVersion <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Version</em>' attribute.
	 * @see #getVersion()
	 * @generated
	 */
	void setVersion(String value);

} // Container

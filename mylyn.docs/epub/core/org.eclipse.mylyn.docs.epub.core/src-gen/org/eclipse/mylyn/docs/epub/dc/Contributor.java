/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.dc;

import org.eclipse.mylyn.docs.epub.opf.Role;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Contributor</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.dc.Contributor#getRole <em>Role</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.dc.Contributor#getFileAs <em>File As</em>}</li>
 * </ul>
 *
 * @see org.eclipse.mylyn.docs.epub.dc.DCPackage#getContributor()
 * @model extendedMetaData="kind='mixed'"
 * @generated
 */
public interface Contributor extends LocalizedDCType {
	/**
	 * Returns the value of the '<em><b>Role</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.mylyn.docs.epub.opf.Role}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Role</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Role</em>' attribute.
	 * @see org.eclipse.mylyn.docs.epub.opf.Role
	 * @see #setRole(Role)
	 * @see org.eclipse.mylyn.docs.epub.dc.DCPackage#getContributor_Role()
	 * @model extendedMetaData="namespace='http://www.idpf.org/2007/opf'"
	 * @generated
	 */
	Role getRole();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.dc.Contributor#getRole <em>Role</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Role</em>' attribute.
	 * @see org.eclipse.mylyn.docs.epub.opf.Role
	 * @see #getRole()
	 * @generated
	 */
	void setRole(Role value);

	/**
	 * Returns the value of the '<em><b>File As</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>File As</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>File As</em>' attribute.
	 * @see #setFileAs(String)
	 * @see org.eclipse.mylyn.docs.epub.dc.DCPackage#getContributor_FileAs()
	 * @model extendedMetaData="name='file-as' namespace='http://www.idpf.org/2007/opf'"
	 * @generated
	 */
	String getFileAs();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.dc.Contributor#getFileAs <em>File As</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>File As</em>' attribute.
	 * @see #getFileAs()
	 * @generated
	 */
	void setFileAs(String value);

} // Contributor

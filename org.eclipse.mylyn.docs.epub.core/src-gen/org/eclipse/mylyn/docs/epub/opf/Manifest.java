/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.opf;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Manifest</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Manifest#getItems <em>Items</em>}</li>
 * </ul>
 *
 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getManifest()
 * @model
 * @generated
 */
public interface Manifest extends EObject {
	/**
	 * Returns the value of the '<em><b>Items</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.opf.Item}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Items</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Items</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getManifest_Items()
	 * @model containment="true" required="true"
	 *        extendedMetaData="name='item' namespace='http://www.idpf.org/2007/opf'"
	 * @generated
	 */
	EList<Item> getItems();

} // Manifest

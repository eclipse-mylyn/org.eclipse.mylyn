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
 * A representation of the model object '<em><b>Spine</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Spine#getSpineItems <em>Spine Items</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Spine#getToc <em>Toc</em>}</li>
 * </ul>
 *
 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getSpine()
 * @model
 * @generated
 */
public interface Spine extends EObject {
	/**
	 * Returns the value of the '<em><b>Spine Items</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.opf.Itemref}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Spine Items</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Spine Items</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getSpine_SpineItems()
	 * @model containment="true"
	 *        extendedMetaData="name='itemref' namespace='http://www.idpf.org/2007/opf'"
	 * @generated
	 */
	EList<Itemref> getSpineItems();

	/**
	 * Returns the value of the '<em><b>Toc</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Toc</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Toc</em>' attribute.
	 * @see #setToc(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getSpine_Toc()
	 * @model required="true"
	 * @generated
	 */
	String getToc();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Spine#getToc <em>Toc</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Toc</em>' attribute.
	 * @see #getToc()
	 * @generated
	 */
	void setToc(String value);

} // Spine

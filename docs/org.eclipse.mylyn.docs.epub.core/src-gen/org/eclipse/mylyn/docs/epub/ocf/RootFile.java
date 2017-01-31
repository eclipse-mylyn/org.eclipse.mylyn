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
 * A representation of the model object '<em><b>Root File</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ocf.RootFile#getFullPath <em>Full Path</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ocf.RootFile#getMediaType <em>Media Type</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ocf.RootFile#getPublication <em>Publication</em>}</li>
 * </ul>
 *
 * @see org.eclipse.mylyn.docs.epub.ocf.OCFPackage#getRootFile()
 * @model
 * @generated
 */
public interface RootFile extends EObject {
	/**
	 * Returns the value of the '<em><b>Full Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Full Path</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Full Path</em>' attribute.
	 * @see #setFullPath(String)
	 * @see org.eclipse.mylyn.docs.epub.ocf.OCFPackage#getRootFile_FullPath()
	 * @model required="true"
	 *        extendedMetaData="name='full-path'"
	 * @generated
	 */
	String getFullPath();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ocf.RootFile#getFullPath <em>Full Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Full Path</em>' attribute.
	 * @see #getFullPath()
	 * @generated
	 */
	void setFullPath(String value);

	/**
	 * Returns the value of the '<em><b>Media Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Media Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Media Type</em>' attribute.
	 * @see #setMediaType(String)
	 * @see org.eclipse.mylyn.docs.epub.ocf.OCFPackage#getRootFile_MediaType()
	 * @model required="true"
	 *        extendedMetaData="name='media-type'"
	 * @generated
	 */
	String getMediaType();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ocf.RootFile#getMediaType <em>Media Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Media Type</em>' attribute.
	 * @see #getMediaType()
	 * @generated
	 */
	void setMediaType(String value);

	/**
	 * Returns the value of the '<em><b>Publication</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Publication</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Publication</em>' attribute.
	 * @see #setPublication(Object)
	 * @see org.eclipse.mylyn.docs.epub.ocf.OCFPackage#getRootFile_Publication()
	 * @model transient="true"
	 * @generated
	 */
	Object getPublication();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ocf.RootFile#getPublication <em>Publication</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Publication</em>' attribute.
	 * @see #getPublication()
	 * @generated
	 */
	void setPublication(Object value);

} // RootFile

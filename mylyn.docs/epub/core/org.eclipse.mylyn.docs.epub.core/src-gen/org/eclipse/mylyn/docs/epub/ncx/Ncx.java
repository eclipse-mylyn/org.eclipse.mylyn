/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.ncx;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Ncx</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getHead <em>Head</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getDocTitle <em>Doc Title</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getDocAuthors <em>Doc Authors</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getNavMap <em>Nav Map</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getPageList <em>Page List</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getNavLists <em>Nav Lists</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getDir <em>Dir</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getLang <em>Lang</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getVersion <em>Version</em>}</li>
 * </ul>
 *
 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getNcx()
 * @model extendedMetaData="name='ncx'"
 * @generated
 */
public interface Ncx extends EObject {
	/**
	 * Returns the value of the '<em><b>Head</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Head</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Head</em>' containment reference.
	 * @see #setHead(Head)
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getNcx_Head()
	 * @model containment="true" required="true"
	 *        extendedMetaData="kind='element' name='head' namespace='##targetNamespace'"
	 * @generated
	 */
	Head getHead();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getHead <em>Head</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Head</em>' containment reference.
	 * @see #getHead()
	 * @generated
	 */
	void setHead(Head value);

	/**
	 * Returns the value of the '<em><b>Doc Title</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Doc Title</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Doc Title</em>' containment reference.
	 * @see #setDocTitle(DocTitle)
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getNcx_DocTitle()
	 * @model containment="true" required="true"
	 *        extendedMetaData="kind='element' name='docTitle' namespace='##targetNamespace'"
	 * @generated
	 */
	DocTitle getDocTitle();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getDocTitle <em>Doc Title</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Doc Title</em>' containment reference.
	 * @see #getDocTitle()
	 * @generated
	 */
	void setDocTitle(DocTitle value);

	/**
	 * Returns the value of the '<em><b>Doc Authors</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.ncx.DocAuthor}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Doc Authors</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Doc Authors</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getNcx_DocAuthors()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='docAuthor' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<DocAuthor> getDocAuthors();

	/**
	 * Returns the value of the '<em><b>Nav Map</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Nav Map</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Nav Map</em>' containment reference.
	 * @see #setNavMap(NavMap)
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getNcx_NavMap()
	 * @model containment="true" required="true"
	 *        extendedMetaData="kind='element' name='navMap' namespace='##targetNamespace'"
	 * @generated
	 */
	NavMap getNavMap();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getNavMap <em>Nav Map</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Nav Map</em>' containment reference.
	 * @see #getNavMap()
	 * @generated
	 */
	void setNavMap(NavMap value);

	/**
	 * Returns the value of the '<em><b>Page List</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Page List</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Page List</em>' containment reference.
	 * @see #setPageList(PageList)
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getNcx_PageList()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='pageList' namespace='##targetNamespace'"
	 * @generated
	 */
	PageList getPageList();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getPageList <em>Page List</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Page List</em>' containment reference.
	 * @see #getPageList()
	 * @generated
	 */
	void setPageList(PageList value);

	/**
	 * Returns the value of the '<em><b>Nav Lists</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.ncx.NavList}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Nav Lists</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Nav Lists</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getNcx_NavLists()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='navList' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<NavList> getNavLists();

	/**
	 * Returns the value of the '<em><b>Dir</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.mylyn.docs.epub.ncx.DirType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Dir</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dir</em>' attribute.
	 * @see org.eclipse.mylyn.docs.epub.ncx.DirType
	 * @see #isSetDir()
	 * @see #unsetDir()
	 * @see #setDir(DirType)
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getNcx_Dir()
	 * @model unsettable="true"
	 *        extendedMetaData="kind='attribute' name='dir'"
	 * @generated
	 */
	DirType getDir();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getDir <em>Dir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Dir</em>' attribute.
	 * @see org.eclipse.mylyn.docs.epub.ncx.DirType
	 * @see #isSetDir()
	 * @see #unsetDir()
	 * @see #getDir()
	 * @generated
	 */
	void setDir(DirType value);

	/**
	 * Unsets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getDir <em>Dir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetDir()
	 * @see #getDir()
	 * @see #setDir(DirType)
	 * @generated
	 */
	void unsetDir();

	/**
	 * Returns whether the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getDir <em>Dir</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Dir</em>' attribute is set.
	 * @see #unsetDir()
	 * @see #getDir()
	 * @see #setDir(DirType)
	 * @generated
	 */
	boolean isSetDir();

	/**
	 * Returns the value of the '<em><b>Lang</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Lang</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Lang</em>' attribute.
	 * @see #setLang(String)
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getNcx_Lang()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.NMTOKEN"
	 *        extendedMetaData="kind='attribute' name='lang' namespace='http://www.w3.org/XML/1998/namespace'"
	 * @generated
	 */
	String getLang();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getLang <em>Lang</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Lang</em>' attribute.
	 * @see #getLang()
	 * @generated
	 */
	void setLang(String value);

	/**
	 * Returns the value of the '<em><b>Version</b></em>' attribute.
	 * The default value is <code>"2005-1"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Version</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Version</em>' attribute.
	 * @see #isSetVersion()
	 * @see #unsetVersion()
	 * @see #setVersion(String)
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#getNcx_Version()
	 * @model default="2005-1" unsettable="true" required="true"
	 *        extendedMetaData="kind='attribute' name='version'"
	 * @generated
	 */
	String getVersion();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getVersion <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Version</em>' attribute.
	 * @see #isSetVersion()
	 * @see #unsetVersion()
	 * @see #getVersion()
	 * @generated
	 */
	void setVersion(String value);

	/**
	 * Unsets the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getVersion <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetVersion()
	 * @see #getVersion()
	 * @see #setVersion(String)
	 * @generated
	 */
	void unsetVersion();

	/**
	 * Returns whether the value of the '{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getVersion <em>Version</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Version</em>' attribute is set.
	 * @see #unsetVersion()
	 * @see #getVersion()
	 * @see #setVersion(String)
	 * @generated
	 */
	boolean isSetVersion();

} // Ncx

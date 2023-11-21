/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.dc;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Date</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.dc.Date#getEvent <em>Event</em>}</li>
 * </ul>
 *
 * @see org.eclipse.mylyn.docs.epub.dc.DCPackage#getDate()
 * @model extendedMetaData="kind='mixed'"
 * @generated
 */
public interface Date extends DCType {
	/**
	 * Returns the value of the '<em><b>Event</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Event</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Event</em>' attribute.
	 * @see #setEvent(String)
	 * @see org.eclipse.mylyn.docs.epub.dc.DCPackage#getDate_Event()
	 * @model extendedMetaData="namespace='http://www.idpf.org/2007/opf'"
	 * @generated
	 */
	String getEvent();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.dc.Date#getEvent <em>Event</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Event</em>' attribute.
	 * @see #getEvent()
	 * @generated
	 */
	void setEvent(String value);

} // Date

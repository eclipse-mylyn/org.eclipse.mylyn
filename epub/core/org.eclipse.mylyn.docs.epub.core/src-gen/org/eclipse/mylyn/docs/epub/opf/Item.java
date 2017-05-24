/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.opf;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Item</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Item#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Item#getHref <em>Href</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Item#getMedia_type <em>Media type</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Item#getFallback <em>Fallback</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Item#getFallback_style <em>Fallback style</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Item#getRequired_namespace <em>Required namespace</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Item#getRequired_modules <em>Required modules</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Item#getFile <em>File</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Item#isNoToc <em>No Toc</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Item#getTitle <em>Title</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Item#isGenerated <em>Generated</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Item#getSourcePath <em>Source Path</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Item#getProperties <em>Properties</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Item#getMedia_overlay <em>Media overlay</em>}</li>
 * </ul>
 *
 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getItem()
 * @model
 * @generated
 */
public interface Item extends EObject {
	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getItem_Id()
	 * @model id="true" required="true"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Item#getId <em>Id</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @param value
	 *            the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Href</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Href</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Href</em>' attribute.
	 * @see #setHref(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getItem_Href()
	 * @model required="true"
	 * @generated
	 */
	String getHref();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Item#getHref <em>Href</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @param value
	 *            the new value of the '<em>Href</em>' attribute.
	 * @see #getHref()
	 * @generated
	 */
	void setHref(String value);

	/**
	 * Returns the value of the '<em><b>Media type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Media type</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Media type</em>' attribute.
	 * @see #setMedia_type(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getItem_Media_type()
	 * @model required="true"
	 *        extendedMetaData="name='media-type'"
	 * @generated
	 */
	String getMedia_type();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Item#getMedia_type <em>Media type</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Media type</em>' attribute.
	 * @see #getMedia_type()
	 * @generated
	 */
	void setMedia_type(String value);

	/**
	 * Returns the value of the '<em><b>Fallback</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Fallback</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Fallback</em>' attribute.
	 * @see #setFallback(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getItem_Fallback()
	 * @model
	 * @generated
	 */
	String getFallback();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Item#getFallback <em>Fallback</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Fallback</em>' attribute.
	 * @see #getFallback()
	 * @generated
	 */
	void setFallback(String value);

	/**
	 * Returns the value of the '<em><b>Fallback style</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Fallback style</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Fallback style</em>' attribute.
	 * @see #setFallback_style(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getItem_Fallback_style()
	 * @model extendedMetaData="name='fallback-style'"
	 * @generated
	 */
	String getFallback_style();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Item#getFallback_style <em>Fallback style</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Fallback style</em>' attribute.
	 * @see #getFallback_style()
	 * @generated
	 */
	void setFallback_style(String value);

	/**
	 * Returns the value of the '<em><b>Required namespace</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Required namespace</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Required namespace</em>' attribute.
	 * @see #setRequired_namespace(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getItem_Required_namespace()
	 * @model extendedMetaData="name='required-namespace'"
	 * @generated
	 */
	String getRequired_namespace();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Item#getRequired_namespace <em>Required namespace</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Required namespace</em>' attribute.
	 * @see #getRequired_namespace()
	 * @generated
	 */
	void setRequired_namespace(String value);

	/**
	 * Returns the value of the '<em><b>Required modules</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Required modules</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Required modules</em>' attribute.
	 * @see #setRequired_modules(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getItem_Required_modules()
	 * @model extendedMetaData="name='required-modules'"
	 * @generated
	 */
	String getRequired_modules();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Item#getRequired_modules <em>Required modules</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Required modules</em>' attribute.
	 * @see #getRequired_modules()
	 * @generated
	 */
	void setRequired_modules(String value);

	/**
	 * Returns the value of the '<em><b>File</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>File</em>' attribute isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc --> <!-- begin-model-doc --> Used by the tooling, is not serialized. <!-- end-model-doc -->
	 *
	 * @return the value of the '<em>File</em>' attribute.
	 * @see #setFile(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getItem_File()
	 * @model transient="true"
	 * @generated
	 */
	String getFile();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Item#getFile <em>File</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @param value
	 *            the new value of the '<em>File</em>' attribute.
	 * @see #getFile()
	 * @generated
	 */
	void setFile(String value);

	/**
	 * Returns the value of the '<em><b>No Toc</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>No Toc</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc --> <!-- begin-model-doc --> Used by the tooling, is not serialized. <!-- end-model-doc -->
	 *
	 * @return the value of the '<em>No Toc</em>' attribute.
	 * @see #setNoToc(boolean)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getItem_NoToc()
	 * @model transient="true"
	 * @generated
	 */
	boolean isNoToc();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Item#isNoToc <em>No Toc</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @param value
	 *            the new value of the '<em>No Toc</em>' attribute.
	 * @see #isNoToc()
	 * @generated
	 */
	void setNoToc(boolean value);

	/**
	 * Returns the value of the '<em><b>Title</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Title</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc --> <!-- begin-model-doc --> Used by the tooling, is not serialized. <!-- end-model-doc -->
	 *
	 * @return the value of the '<em>Title</em>' attribute.
	 * @see #setTitle(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getItem_Title()
	 * @model transient="true"
	 * @generated
	 */
	String getTitle();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Item#getTitle <em>Title</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @param value
	 *            the new value of the '<em>Title</em>' attribute.
	 * @see #getTitle()
	 * @generated
	 */
	void setTitle(String value);

	/**
	 * Returns the value of the '<em><b>Generated</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Generated</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc --> <!-- begin-model-doc --> Used by the tooling, is not serialized. <!-- end-model-doc -->
	 *
	 * @return the value of the '<em>Generated</em>' attribute.
	 * @see #setGenerated(boolean)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getItem_Generated()
	 * @model transient="true"
	 * @generated
	 */
	boolean isGenerated();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Item#isGenerated <em>Generated</em>}' attribute.
	 * <!-- begin-user-doc --> Indicates that the item has been generated by the EPUB tools. This applies for instance
	 * to the cover page HTML code. <!-- end-user-doc -->
	 * @param value the new value of the '<em>Generated</em>' attribute.
	 * @see #isGenerated()
	 * @generated
	 */
	void setGenerated(boolean value);

	/**
	 * Returns the value of the '<em><b>Source Path</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Source Path</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc --> <!-- begin-model-doc --> Used by the tooling, is not serialized. <!-- end-model-doc -->
	 *
	 * @return the value of the '<em>Source Path</em>' attribute.
	 * @see #setSourcePath(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getItem_SourcePath()
	 * @model transient="true"
	 * @generated
	 */
	String getSourcePath();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Item#getSourcePath <em>Source Path</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source Path</em>' attribute.
	 * @see #getSourcePath()
	 * @generated
	 */
	void setSourcePath(String value);

	/**
	 * Returns the value of the '<em><b>Properties</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Properties</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc --> <!-- begin-model-doc --> EPUB 3 -
	 * http://www.idpf.org/epub/301/spec/epub-publications.html#sec-item-elem
	 *
	 * @since 3.0 <!-- end-model-doc -->
	 * @return the value of the '<em>Properties</em>' attribute.
	 * @see #setProperties(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getItem_Properties()
	 * @model
	 * @generated
	 */
	String getProperties();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Item#getProperties <em>Properties</em>}' attribute.
	 * <!-- begin-user-doc -->
	 *
	 * @since 3.0 <!-- end-user-doc -->
	 * @param value the new value of the '<em>Properties</em>' attribute.
	 * @see #getProperties()
	 * @generated
	 */
	void setProperties(String value);

	/**
	 * Returns the value of the '<em><b>Media overlay</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * EPUB 3 - http://www.idpf.org/epub/301/spec/epub-publications.html#sec-item-elem
	 * @since 3.0
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Media overlay</em>' attribute.
	 * @see #setMedia_overlay(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getItem_Media_overlay()
	 * @model extendedMetaData="name='media-overlay' namespace='http://www.idpf.org/2007/opf'"
	 * @generated
	 */
	String getMedia_overlay();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Item#getMedia_overlay <em>Media overlay</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * 
	 * @since 3.0 <!-- end-user-doc -->
	 * @param value the new value of the '<em>Media overlay</em>' attribute.
	 * @see #getMedia_overlay()
	 * @generated
	 */
	void setMedia_overlay(String value);

} // Item

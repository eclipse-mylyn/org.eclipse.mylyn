/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.opf;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Package</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Package#getMetadata <em>Metadata</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Package#getManifest <em>Manifest</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Package#getSpine <em>Spine</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Package#getGuide <em>Guide</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Package#getTours <em>Tours</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Package#getVersion <em>Version</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Package#getUniqueIdentifier <em>Unique Identifier</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Package#isGenerateCoverHTML <em>Generate Cover HTML</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Package#isGenerateTableOfContents <em>Generate Table Of Contents</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Package#isIncludeReferencedResources <em>Include Referenced Resources</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Package#getPrefix <em>Prefix</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Package#getLang <em>Lang</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Package#getDir <em>Dir</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Package#getId <em>Id</em>}</li>
 * </ul>
 *
 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getPackage()
 * @model extendedMetaData="name='package' namespace='http://www.idpf.org/2007/opf'"
 * @generated
 */
public interface Package extends EObject {
	/**
	 * Returns the value of the '<em><b>Metadata</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Metadata</em>' containment reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Metadata</em>' containment reference.
	 * @see #setMetadata(Metadata)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getPackage_Metadata()
	 * @model containment="true" required="true"
	 *        extendedMetaData="namespace='http://www.idpf.org/2007/opf'"
	 * @generated
	 */
	Metadata getMetadata();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Package#getMetadata <em>Metadata</em>}' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Metadata</em>' containment reference.
	 * @see #getMetadata()
	 * @generated
	 */
	void setMetadata(Metadata value);

	/**
	 * Returns the value of the '<em><b>Manifest</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Manifest</em>' containment reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Manifest</em>' containment reference.
	 * @see #setManifest(Manifest)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getPackage_Manifest()
	 * @model containment="true" required="true"
	 *        extendedMetaData="namespace='http://www.idpf.org/2007/opf'"
	 * @generated
	 */
	Manifest getManifest();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Package#getManifest <em>Manifest</em>}' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Manifest</em>' containment reference.
	 * @see #getManifest()
	 * @generated
	 */
	void setManifest(Manifest value);

	/**
	 * Returns the value of the '<em><b>Spine</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Spine</em>' containment reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Spine</em>' containment reference.
	 * @see #setSpine(Spine)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getPackage_Spine()
	 * @model containment="true" required="true"
	 *        extendedMetaData="namespace='http://www.idpf.org/2007/opf'"
	 * @generated
	 */
	Spine getSpine();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Package#getSpine <em>Spine</em>}' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Spine</em>' containment reference.
	 * @see #getSpine()
	 * @generated
	 */
	void setSpine(Spine value);

	/**
	 * Returns the value of the '<em><b>Guide</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Guide</em>' containment reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Guide</em>' containment reference.
	 * @see #setGuide(Guide)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getPackage_Guide()
	 * @model containment="true"
	 *        extendedMetaData="namespace='http://www.idpf.org/2007/opf'"
	 * @generated
	 */
	Guide getGuide();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Package#getGuide <em>Guide</em>}' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Guide</em>' containment reference.
	 * @see #getGuide()
	 * @generated
	 */
	void setGuide(Guide value);

	/**
	 * Returns the value of the '<em><b>Tours</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Tours</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tours</em>' reference.
	 * @see #setTours(Tours)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getPackage_Tours()
	 * @model extendedMetaData="namespace='http://www.idpf.org/2007/opf'"
	 * @generated
	 */
	Tours getTours();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Package#getTours <em>Tours</em>}' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @param value
	 *            the new value of the '<em>Tours</em>' reference.
	 * @see #getTours()
	 * @generated
	 */
	void setTours(Tours value);

	/**
	 * Returns the value of the '<em><b>Version</b></em>' attribute. The default value is <code>"2.0"</code>. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Version</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Version</em>' attribute.
	 * @see #isSetVersion()
	 * @see #unsetVersion()
	 * @see #setVersion(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getPackage_Version()
	 * @model default="2.0" unsettable="true" required="true"
	 * @generated
	 */
	String getVersion();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Package#getVersion <em>Version</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Version</em>' attribute.
	 * @see #isSetVersion()
	 * @see #unsetVersion()
	 * @see #getVersion()
	 * @generated
	 */
	void setVersion(String value);

	/**
	 * Unsets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Package#getVersion <em>Version</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #isSetVersion()
	 * @see #getVersion()
	 * @see #setVersion(String)
	 * @generated
	 */
	void unsetVersion();

	/**
	 * Returns whether the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Package#getVersion <em>Version</em>}' attribute is set.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return whether the value of the '<em>Version</em>' attribute is set.
	 * @see #unsetVersion()
	 * @see #getVersion()
	 * @see #setVersion(String)
	 * @generated
	 */
	boolean isSetVersion();

	/**
	 * Returns the value of the '<em><b>Unique Identifier</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Unique Identifier</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Unique Identifier</em>' attribute.
	 * @see #setUniqueIdentifier(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getPackage_UniqueIdentifier()
	 * @model required="true"
	 *        extendedMetaData="name='unique-identifier'"
	 * @generated
	 */
	String getUniqueIdentifier();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Package#getUniqueIdentifier <em>Unique Identifier</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Unique Identifier</em>' attribute.
	 * @see #getUniqueIdentifier()
	 * @generated
	 */
	void setUniqueIdentifier(String value);

	/**
	 * Returns the value of the '<em><b>Generate Cover HTML</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Generate Cover HTML</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Generate Cover HTML</em>' attribute.
	 * @see #setGenerateCoverHTML(boolean)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getPackage_GenerateCoverHTML()
	 * @model transient="true"
	 * @generated
	 */
	boolean isGenerateCoverHTML();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Package#isGenerateCoverHTML <em>Generate Cover HTML</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Generate Cover HTML</em>' attribute.
	 * @see #isGenerateCoverHTML()
	 * @generated
	 */
	void setGenerateCoverHTML(boolean value);

	/**
	 * Returns the value of the '<em><b>Generate Table Of Contents</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Generate Table Of Contents</em>' attribute isn't clear, there really should be more of
	 * a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Generate Table Of Contents</em>' attribute.
	 * @see #setGenerateTableOfContents(boolean)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getPackage_GenerateTableOfContents()
	 * @model transient="true"
	 * @generated
	 */
	boolean isGenerateTableOfContents();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Package#isGenerateTableOfContents <em>Generate Table Of Contents</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Generate Table Of Contents</em>' attribute.
	 * @see #isGenerateTableOfContents()
	 * @generated
	 */
	void setGenerateTableOfContents(boolean value);

	/**
	 * Returns the value of the '<em><b>Include Referenced Resources</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Include Referenced Resources</em>' attribute isn't clear, there really should be more
	 * of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Include Referenced Resources</em>' attribute.
	 * @see #setIncludeReferencedResources(boolean)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getPackage_IncludeReferencedResources()
	 * @model transient="true"
	 * @generated
	 */
	boolean isIncludeReferencedResources();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Package#isIncludeReferencedResources <em>Include Referenced Resources</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Include Referenced Resources</em>' attribute.
	 * @see #isIncludeReferencedResources()
	 * @generated
	 */
	void setIncludeReferencedResources(boolean value);

	/**
	 * Returns the value of the '<em><b>Prefix</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc --> <!--
	 * begin-model-doc --> EPUB 3 - http://www.idpf.org/epub/30/spec/epub30-publications.html#sec-package-elem <!--
	 * end-model-doc -->
	 *
	 * @return the value of the '<em>Prefix</em>' attribute.
	 * @see #setPrefix(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getPackage_Prefix()
	 * @model
	 * @generated
	 * @since 3.0
	 */
	String getPrefix();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Package#getPrefix <em>Prefix</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @param value
	 *            the new value of the '<em>Prefix</em>' attribute.
	 * @see #getPrefix()
	 * @generated
	 * @since 3.0
	 */
	void setPrefix(String value);

	/**
	 * Returns the value of the '<em><b>Lang</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc --> <!--
	 * begin-model-doc --> EPUB 3 - http://www.idpf.org/epub/30/spec/epub30-publications.html#sec-package-elem <!--
	 * end-model-doc -->
	 *
	 * @return the value of the '<em>Lang</em>' attribute.
	 * @see #setLang(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getPackage_Lang()
	 * @model
	 * @generated
	 * @since 3.0
	 */
	String getLang();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Package#getLang <em>Lang</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @param value
	 *            the new value of the '<em>Lang</em>' attribute.
	 * @see #getLang()
	 * @generated
	 * @since 3.0
	 */
	void setLang(String value);

	/**
	 * Returns the value of the '<em><b>Dir</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc --> <!--
	 * begin-model-doc --> EPUB 3 - http://www.idpf.org/epub/30/spec/epub30-publications.html#sec-package-elem <!--
	 * end-model-doc -->
	 *
	 * @return the value of the '<em>Dir</em>' attribute.
	 * @see #setDir(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getPackage_Dir()
	 * @model
	 * @generated
	 * @since 3.0
	 */
	String getDir();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Package#getDir <em>Dir</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @param value
	 *            the new value of the '<em>Dir</em>' attribute.
	 * @see #getDir()
	 * @generated
	 * @since 3.0
	 */
	void setDir(String value);

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc --> <!--
	 * begin-model-doc --> EPUB 3 - http://www.idpf.org/epub/30/spec/epub30-publications.html#sec-package-elem <!--
	 * end-model-doc -->
	 *
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getPackage_Id()
	 * @model
	 * @generated
	 * @since 3.0
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.docs.epub.opf.Package#getId <em>Id</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @param value
	 *            the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 * @since 3.0
	 */
	void setId(String value);

} // Package

/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.opf;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.mylyn.docs.epub.dc.Contributor;
import org.eclipse.mylyn.docs.epub.dc.Coverage;
import org.eclipse.mylyn.docs.epub.dc.Creator;
import org.eclipse.mylyn.docs.epub.dc.Date;
import org.eclipse.mylyn.docs.epub.dc.Description;
import org.eclipse.mylyn.docs.epub.dc.Format;
import org.eclipse.mylyn.docs.epub.dc.Identifier;
import org.eclipse.mylyn.docs.epub.dc.Language;
import org.eclipse.mylyn.docs.epub.dc.Publisher;
import org.eclipse.mylyn.docs.epub.dc.Relation;
import org.eclipse.mylyn.docs.epub.dc.Rights;
import org.eclipse.mylyn.docs.epub.dc.Source;
import org.eclipse.mylyn.docs.epub.dc.Subject;
import org.eclipse.mylyn.docs.epub.dc.Title;
import org.eclipse.mylyn.docs.epub.dc.Type;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Metadata</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getTitles <em>Titles</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getCreators <em>Creators</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getSubjects <em>Subjects</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getDescriptions <em>Descriptions</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getPublishers <em>Publishers</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getContributors <em>Contributors</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getDates <em>Dates</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getTypes <em>Types</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getFormats <em>Formats</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getIdentifiers <em>Identifiers</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getSources <em>Sources</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getLanguages <em>Languages</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getRelations <em>Relations</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getCoverages <em>Coverages</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getRights <em>Rights</em>}</li>
 *   <li>{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getMetas <em>Metas</em>}</li>
 * </ul>
 *
 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMetadata()
 * @model
 * @generated
 */
public interface Metadata extends EObject {
	/**
	 * Returns the value of the '<em><b>Titles</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.dc.Title}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Titles</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Titles</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMetadata_Titles()
	 * @model containment="true" required="true"
	 *        extendedMetaData="name='title' namespace='http://purl.org/dc/elements/1.1/'"
	 * @generated
	 */
	EList<Title> getTitles();

	/**
	 * Returns the value of the '<em><b>Creators</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.dc.Creator}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Creators</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Creators</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMetadata_Creators()
	 * @model containment="true"
	 *        extendedMetaData="name='creator' namespace='http://purl.org/dc/elements/1.1/'"
	 * @generated
	 */
	EList<Creator> getCreators();

	/**
	 * Returns the value of the '<em><b>Subjects</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.dc.Subject}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Subjects</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Subjects</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMetadata_Subjects()
	 * @model containment="true" required="true"
	 *        extendedMetaData="name='subject' namespace='http://purl.org/dc/elements/1.1/'"
	 * @generated
	 */
	EList<Subject> getSubjects();

	/**
	 * Returns the value of the '<em><b>Descriptions</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.dc.Description}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Descriptions</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Descriptions</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMetadata_Descriptions()
	 * @model containment="true"
	 *        extendedMetaData="name='description' namespace='http://purl.org/dc/elements/1.1/'"
	 * @generated
	 */
	EList<Description> getDescriptions();

	/**
	 * Returns the value of the '<em><b>Publishers</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.dc.Publisher}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Publishers</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Publishers</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMetadata_Publishers()
	 * @model containment="true"
	 *        extendedMetaData="name='publisher' namespace='http://purl.org/dc/elements/1.1/'"
	 * @generated
	 */
	EList<Publisher> getPublishers();

	/**
	 * Returns the value of the '<em><b>Contributors</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.dc.Contributor}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Contributors</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Contributors</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMetadata_Contributors()
	 * @model containment="true"
	 *        extendedMetaData="name='contributor' namespace='http://purl.org/dc/elements/1.1/'"
	 * @generated
	 */
	EList<Contributor> getContributors();

	/**
	 * Returns the value of the '<em><b>Dates</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.dc.Date}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Dates</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dates</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMetadata_Dates()
	 * @model containment="true"
	 *        extendedMetaData="name='date' namespace='http://purl.org/dc/elements/1.1/'"
	 * @generated
	 */
	EList<Date> getDates();

	/**
	 * Returns the value of the '<em><b>Types</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.dc.Type}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Types</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Types</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMetadata_Types()
	 * @model containment="true"
	 *        extendedMetaData="name='type' namespace='http://purl.org/dc/elements/1.1/'"
	 * @generated
	 */
	EList<Type> getTypes();

	/**
	 * Returns the value of the '<em><b>Formats</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.dc.Format}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Formats</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Formats</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMetadata_Formats()
	 * @model containment="true"
	 *        extendedMetaData="name='format' namespace='http://purl.org/dc/elements/1.1/'"
	 * @generated
	 */
	EList<Format> getFormats();

	/**
	 * Returns the value of the '<em><b>Identifiers</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.dc.Identifier}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Identifiers</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Identifiers</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMetadata_Identifiers()
	 * @model containment="true" required="true"
	 *        extendedMetaData="name='identifier' namespace='http://purl.org/dc/elements/1.1/'"
	 * @generated
	 */
	EList<Identifier> getIdentifiers();

	/**
	 * Returns the value of the '<em><b>Sources</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.dc.Source}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sources</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sources</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMetadata_Sources()
	 * @model containment="true"
	 *        extendedMetaData="name='source' namespace='http://purl.org/dc/elements/1.1/'"
	 * @generated
	 */
	EList<Source> getSources();

	/**
	 * Returns the value of the '<em><b>Languages</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.dc.Language}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Languages</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Languages</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMetadata_Languages()
	 * @model containment="true" required="true"
	 *        extendedMetaData="name='language' namespace='http://purl.org/dc/elements/1.1/'"
	 * @generated
	 */
	EList<Language> getLanguages();

	/**
	 * Returns the value of the '<em><b>Relations</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.dc.Relation}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Relations</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Relations</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMetadata_Relations()
	 * @model containment="true"
	 *        extendedMetaData="name='relation' namespace='http://purl.org/dc/elements/1.1/'"
	 * @generated
	 */
	EList<Relation> getRelations();

	/**
	 * Returns the value of the '<em><b>Coverages</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.dc.Coverage}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Coverages</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Coverages</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMetadata_Coverages()
	 * @model containment="true"
	 *        extendedMetaData="name='coverage' namespace='http://purl.org/dc/elements/1.1/'"
	 * @generated
	 */
	EList<Coverage> getCoverages();

	/**
	 * Returns the value of the '<em><b>Rights</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.dc.Rights}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Rights</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rights</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMetadata_Rights()
	 * @model containment="true"
	 *        extendedMetaData="name='rights' namespace='http://purl.org/dc/elements/1.1/'"
	 * @generated
	 */
	EList<Rights> getRights();

	/**
	 * Returns the value of the '<em><b>Metas</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.docs.epub.opf.Meta}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Metas</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Metas</em>' containment reference list.
	 * @see org.eclipse.mylyn.docs.epub.opf.OPFPackage#getMetadata_Metas()
	 * @model containment="true"
	 *        extendedMetaData="name='meta' namespace='http://www.idpf.org/2007/opf'"
	 * @generated
	 */
	EList<Meta> getMetas();

} // Metadata

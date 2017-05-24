/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.dc;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.mylyn.docs.epub.dc.DCFactory
 * @model kind="package"
 * @generated
 */
public interface DCPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "dc"; //$NON-NLS-1$

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://purl.org/dc/elements/1.1/"; //$NON-NLS-1$

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "dc"; //$NON-NLS-1$

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	DCPackage eINSTANCE = org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.DCTypeImpl <em>Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCTypeImpl
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getDCType()
	 * @generated
	 */
	int DC_TYPE = 15;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DC_TYPE__ID = 0;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DC_TYPE__MIXED = 1;

	/**
	 * The number of structural features of the '<em>Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DC_TYPE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.LocalizedDCTypeImpl <em>Localized DC Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.LocalizedDCTypeImpl
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getLocalizedDCType()
	 * @generated
	 */
	int LOCALIZED_DC_TYPE = 16;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCALIZED_DC_TYPE__ID = DC_TYPE__ID;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCALIZED_DC_TYPE__MIXED = DC_TYPE__MIXED;

	/**
	 * The feature id for the '<em><b>Lang</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCALIZED_DC_TYPE__LANG = DC_TYPE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Localized DC Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCALIZED_DC_TYPE_FEATURE_COUNT = DC_TYPE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.TitleImpl <em>Title</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.TitleImpl
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getTitle()
	 * @generated
	 */
	int TITLE = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TITLE__ID = LOCALIZED_DC_TYPE__ID;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TITLE__MIXED = LOCALIZED_DC_TYPE__MIXED;

	/**
	 * The feature id for the '<em><b>Lang</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TITLE__LANG = LOCALIZED_DC_TYPE__LANG;

	/**
	 * The number of structural features of the '<em>Title</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TITLE_FEATURE_COUNT = LOCALIZED_DC_TYPE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.CreatorImpl <em>Creator</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.CreatorImpl
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getCreator()
	 * @generated
	 */
	int CREATOR = 1;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CREATOR__ID = LOCALIZED_DC_TYPE__ID;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CREATOR__MIXED = LOCALIZED_DC_TYPE__MIXED;

	/**
	 * The feature id for the '<em><b>Lang</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CREATOR__LANG = LOCALIZED_DC_TYPE__LANG;

	/**
	 * The feature id for the '<em><b>Role</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CREATOR__ROLE = LOCALIZED_DC_TYPE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>File As</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CREATOR__FILE_AS = LOCALIZED_DC_TYPE_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Creator</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CREATOR_FEATURE_COUNT = LOCALIZED_DC_TYPE_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.SubjectImpl <em>Subject</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.SubjectImpl
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getSubject()
	 * @generated
	 */
	int SUBJECT = 2;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBJECT__ID = LOCALIZED_DC_TYPE__ID;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBJECT__MIXED = LOCALIZED_DC_TYPE__MIXED;

	/**
	 * The feature id for the '<em><b>Lang</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBJECT__LANG = LOCALIZED_DC_TYPE__LANG;

	/**
	 * The number of structural features of the '<em>Subject</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUBJECT_FEATURE_COUNT = LOCALIZED_DC_TYPE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.DescriptionImpl <em>Description</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.DescriptionImpl
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getDescription()
	 * @generated
	 */
	int DESCRIPTION = 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION__ID = LOCALIZED_DC_TYPE__ID;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION__MIXED = LOCALIZED_DC_TYPE__MIXED;

	/**
	 * The feature id for the '<em><b>Lang</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION__LANG = LOCALIZED_DC_TYPE__LANG;

	/**
	 * The number of structural features of the '<em>Description</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESCRIPTION_FEATURE_COUNT = LOCALIZED_DC_TYPE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.PublisherImpl <em>Publisher</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.PublisherImpl
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getPublisher()
	 * @generated
	 */
	int PUBLISHER = 4;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PUBLISHER__ID = LOCALIZED_DC_TYPE__ID;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PUBLISHER__MIXED = LOCALIZED_DC_TYPE__MIXED;

	/**
	 * The feature id for the '<em><b>Lang</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PUBLISHER__LANG = LOCALIZED_DC_TYPE__LANG;

	/**
	 * The number of structural features of the '<em>Publisher</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PUBLISHER_FEATURE_COUNT = LOCALIZED_DC_TYPE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.ContributorImpl <em>Contributor</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.ContributorImpl
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getContributor()
	 * @generated
	 */
	int CONTRIBUTOR = 5;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTRIBUTOR__ID = LOCALIZED_DC_TYPE__ID;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTRIBUTOR__MIXED = LOCALIZED_DC_TYPE__MIXED;

	/**
	 * The feature id for the '<em><b>Lang</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTRIBUTOR__LANG = LOCALIZED_DC_TYPE__LANG;

	/**
	 * The feature id for the '<em><b>Role</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTRIBUTOR__ROLE = LOCALIZED_DC_TYPE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>File As</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTRIBUTOR__FILE_AS = LOCALIZED_DC_TYPE_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Contributor</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTRIBUTOR_FEATURE_COUNT = LOCALIZED_DC_TYPE_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.DateImpl <em>Date</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.DateImpl
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getDate()
	 * @generated
	 */
	int DATE = 6;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE__ID = DC_TYPE__ID;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE__MIXED = DC_TYPE__MIXED;

	/**
	 * The feature id for the '<em><b>Event</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE__EVENT = DC_TYPE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Date</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_FEATURE_COUNT = DC_TYPE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.TypeImpl <em>Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.TypeImpl
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getType()
	 * @generated
	 */
	int TYPE = 7;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE__ID = DC_TYPE__ID;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE__MIXED = DC_TYPE__MIXED;

	/**
	 * The number of structural features of the '<em>Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_FEATURE_COUNT = DC_TYPE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.FormatImpl <em>Format</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.FormatImpl
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getFormat()
	 * @generated
	 */
	int FORMAT = 8;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FORMAT__ID = DC_TYPE__ID;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FORMAT__MIXED = DC_TYPE__MIXED;

	/**
	 * The number of structural features of the '<em>Format</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FORMAT_FEATURE_COUNT = DC_TYPE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.IdentifierImpl <em>Identifier</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.IdentifierImpl
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getIdentifier()
	 * @generated
	 */
	int IDENTIFIER = 9;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IDENTIFIER__ID = 0;

	/**
	 * The feature id for the '<em><b>Scheme</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IDENTIFIER__SCHEME = 1;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IDENTIFIER__MIXED = 2;

	/**
	 * The number of structural features of the '<em>Identifier</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IDENTIFIER_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.SourceImpl <em>Source</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.SourceImpl
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getSource()
	 * @generated
	 */
	int SOURCE = 10;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SOURCE__ID = LOCALIZED_DC_TYPE__ID;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SOURCE__MIXED = LOCALIZED_DC_TYPE__MIXED;

	/**
	 * The feature id for the '<em><b>Lang</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SOURCE__LANG = LOCALIZED_DC_TYPE__LANG;

	/**
	 * The number of structural features of the '<em>Source</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SOURCE_FEATURE_COUNT = LOCALIZED_DC_TYPE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.LanguageImpl <em>Language</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.LanguageImpl
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getLanguage()
	 * @generated
	 */
	int LANGUAGE = 11;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LANGUAGE__ID = DC_TYPE__ID;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LANGUAGE__MIXED = DC_TYPE__MIXED;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LANGUAGE__TYPE = DC_TYPE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Language</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LANGUAGE_FEATURE_COUNT = DC_TYPE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.RelationImpl <em>Relation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.RelationImpl
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getRelation()
	 * @generated
	 */
	int RELATION = 12;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATION__ID = LOCALIZED_DC_TYPE__ID;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATION__MIXED = LOCALIZED_DC_TYPE__MIXED;

	/**
	 * The feature id for the '<em><b>Lang</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATION__LANG = LOCALIZED_DC_TYPE__LANG;

	/**
	 * The number of structural features of the '<em>Relation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RELATION_FEATURE_COUNT = LOCALIZED_DC_TYPE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.CoverageImpl <em>Coverage</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.CoverageImpl
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getCoverage()
	 * @generated
	 */
	int COVERAGE = 13;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COVERAGE__ID = LOCALIZED_DC_TYPE__ID;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COVERAGE__MIXED = LOCALIZED_DC_TYPE__MIXED;

	/**
	 * The feature id for the '<em><b>Lang</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COVERAGE__LANG = LOCALIZED_DC_TYPE__LANG;

	/**
	 * The number of structural features of the '<em>Coverage</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COVERAGE_FEATURE_COUNT = LOCALIZED_DC_TYPE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.RightsImpl <em>Rights</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.RightsImpl
	 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getRights()
	 * @generated
	 */
	int RIGHTS = 14;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RIGHTS__ID = LOCALIZED_DC_TYPE__ID;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RIGHTS__MIXED = LOCALIZED_DC_TYPE__MIXED;

	/**
	 * The feature id for the '<em><b>Lang</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RIGHTS__LANG = LOCALIZED_DC_TYPE__LANG;

	/**
	 * The number of structural features of the '<em>Rights</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RIGHTS_FEATURE_COUNT = LOCALIZED_DC_TYPE_FEATURE_COUNT + 0;


	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.dc.Title <em>Title</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Title</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Title
	 * @generated
	 */
	EClass getTitle();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.dc.Creator <em>Creator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Creator</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Creator
	 * @generated
	 */
	EClass getCreator();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.dc.Creator#getRole <em>Role</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Role</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Creator#getRole()
	 * @see #getCreator()
	 * @generated
	 */
	EAttribute getCreator_Role();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.dc.Creator#getFileAs <em>File As</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>File As</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Creator#getFileAs()
	 * @see #getCreator()
	 * @generated
	 */
	EAttribute getCreator_FileAs();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.dc.Subject <em>Subject</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Subject</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Subject
	 * @generated
	 */
	EClass getSubject();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.dc.Description <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Description</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Description
	 * @generated
	 */
	EClass getDescription();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.dc.Publisher <em>Publisher</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Publisher</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Publisher
	 * @generated
	 */
	EClass getPublisher();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.dc.Contributor <em>Contributor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Contributor</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Contributor
	 * @generated
	 */
	EClass getContributor();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.dc.Contributor#getRole <em>Role</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Role</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Contributor#getRole()
	 * @see #getContributor()
	 * @generated
	 */
	EAttribute getContributor_Role();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.dc.Contributor#getFileAs <em>File As</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>File As</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Contributor#getFileAs()
	 * @see #getContributor()
	 * @generated
	 */
	EAttribute getContributor_FileAs();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.dc.Date <em>Date</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Date</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Date
	 * @generated
	 */
	EClass getDate();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.dc.Date#getEvent <em>Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Event</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Date#getEvent()
	 * @see #getDate()
	 * @generated
	 */
	EAttribute getDate_Event();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.dc.Type <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Type</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Type
	 * @generated
	 */
	EClass getType();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.dc.Format <em>Format</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Format</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Format
	 * @generated
	 */
	EClass getFormat();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.dc.Identifier <em>Identifier</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Identifier</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Identifier
	 * @generated
	 */
	EClass getIdentifier();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.dc.Identifier#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Identifier#getId()
	 * @see #getIdentifier()
	 * @generated
	 */
	EAttribute getIdentifier_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.dc.Identifier#getScheme <em>Scheme</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Scheme</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Identifier#getScheme()
	 * @see #getIdentifier()
	 * @generated
	 */
	EAttribute getIdentifier_Scheme();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.mylyn.docs.epub.dc.Identifier#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Identifier#getMixed()
	 * @see #getIdentifier()
	 * @generated
	 */
	EAttribute getIdentifier_Mixed();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.dc.Source <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Source</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Source
	 * @generated
	 */
	EClass getSource();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.dc.Language <em>Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Language</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Language
	 * @generated
	 */
	EClass getLanguage();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.dc.Language#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Language#getType()
	 * @see #getLanguage()
	 * @generated
	 */
	EAttribute getLanguage_Type();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.dc.Relation <em>Relation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Relation</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Relation
	 * @generated
	 */
	EClass getRelation();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.dc.Coverage <em>Coverage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Coverage</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Coverage
	 * @generated
	 */
	EClass getCoverage();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.dc.Rights <em>Rights</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Rights</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.Rights
	 * @generated
	 */
	EClass getRights();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.dc.DCType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Type</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.DCType
	 * @generated
	 */
	EClass getDCType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.dc.DCType#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.DCType#getId()
	 * @see #getDCType()
	 * @generated
	 */
	EAttribute getDCType_Id();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.mylyn.docs.epub.dc.DCType#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.DCType#getMixed()
	 * @see #getDCType()
	 * @generated
	 */
	EAttribute getDCType_Mixed();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.dc.LocalizedDCType <em>Localized DC Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Localized DC Type</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.LocalizedDCType
	 * @generated
	 */
	EClass getLocalizedDCType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.dc.LocalizedDCType#getLang <em>Lang</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Lang</em>'.
	 * @see org.eclipse.mylyn.docs.epub.dc.LocalizedDCType#getLang()
	 * @see #getLocalizedDCType()
	 * @generated
	 */
	EAttribute getLocalizedDCType_Lang();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	DCFactory getDCFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.TitleImpl <em>Title</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.TitleImpl
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getTitle()
		 * @generated
		 */
		EClass TITLE = eINSTANCE.getTitle();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.CreatorImpl <em>Creator</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.CreatorImpl
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getCreator()
		 * @generated
		 */
		EClass CREATOR = eINSTANCE.getCreator();

		/**
		 * The meta object literal for the '<em><b>Role</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CREATOR__ROLE = eINSTANCE.getCreator_Role();

		/**
		 * The meta object literal for the '<em><b>File As</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CREATOR__FILE_AS = eINSTANCE.getCreator_FileAs();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.SubjectImpl <em>Subject</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.SubjectImpl
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getSubject()
		 * @generated
		 */
		EClass SUBJECT = eINSTANCE.getSubject();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.DescriptionImpl <em>Description</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.DescriptionImpl
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getDescription()
		 * @generated
		 */
		EClass DESCRIPTION = eINSTANCE.getDescription();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.PublisherImpl <em>Publisher</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.PublisherImpl
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getPublisher()
		 * @generated
		 */
		EClass PUBLISHER = eINSTANCE.getPublisher();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.ContributorImpl <em>Contributor</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.ContributorImpl
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getContributor()
		 * @generated
		 */
		EClass CONTRIBUTOR = eINSTANCE.getContributor();

		/**
		 * The meta object literal for the '<em><b>Role</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTRIBUTOR__ROLE = eINSTANCE.getContributor_Role();

		/**
		 * The meta object literal for the '<em><b>File As</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTRIBUTOR__FILE_AS = eINSTANCE.getContributor_FileAs();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.DateImpl <em>Date</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.DateImpl
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getDate()
		 * @generated
		 */
		EClass DATE = eINSTANCE.getDate();

		/**
		 * The meta object literal for the '<em><b>Event</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATE__EVENT = eINSTANCE.getDate_Event();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.TypeImpl <em>Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.TypeImpl
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getType()
		 * @generated
		 */
		EClass TYPE = eINSTANCE.getType();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.FormatImpl <em>Format</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.FormatImpl
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getFormat()
		 * @generated
		 */
		EClass FORMAT = eINSTANCE.getFormat();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.IdentifierImpl <em>Identifier</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.IdentifierImpl
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getIdentifier()
		 * @generated
		 */
		EClass IDENTIFIER = eINSTANCE.getIdentifier();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IDENTIFIER__ID = eINSTANCE.getIdentifier_Id();

		/**
		 * The meta object literal for the '<em><b>Scheme</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IDENTIFIER__SCHEME = eINSTANCE.getIdentifier_Scheme();

		/**
		 * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IDENTIFIER__MIXED = eINSTANCE.getIdentifier_Mixed();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.SourceImpl <em>Source</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.SourceImpl
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getSource()
		 * @generated
		 */
		EClass SOURCE = eINSTANCE.getSource();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.LanguageImpl <em>Language</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.LanguageImpl
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getLanguage()
		 * @generated
		 */
		EClass LANGUAGE = eINSTANCE.getLanguage();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LANGUAGE__TYPE = eINSTANCE.getLanguage_Type();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.RelationImpl <em>Relation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.RelationImpl
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getRelation()
		 * @generated
		 */
		EClass RELATION = eINSTANCE.getRelation();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.CoverageImpl <em>Coverage</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.CoverageImpl
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getCoverage()
		 * @generated
		 */
		EClass COVERAGE = eINSTANCE.getCoverage();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.RightsImpl <em>Rights</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.RightsImpl
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getRights()
		 * @generated
		 */
		EClass RIGHTS = eINSTANCE.getRights();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.DCTypeImpl <em>Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCTypeImpl
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getDCType()
		 * @generated
		 */
		EClass DC_TYPE = eINSTANCE.getDCType();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DC_TYPE__ID = eINSTANCE.getDCType_Id();

		/**
		 * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DC_TYPE__MIXED = eINSTANCE.getDCType_Mixed();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.dc.impl.LocalizedDCTypeImpl <em>Localized DC Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.LocalizedDCTypeImpl
		 * @see org.eclipse.mylyn.docs.epub.dc.impl.DCPackageImpl#getLocalizedDCType()
		 * @generated
		 */
		EClass LOCALIZED_DC_TYPE = eINSTANCE.getLocalizedDCType();

		/**
		 * The meta object literal for the '<em><b>Lang</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOCALIZED_DC_TYPE__LANG = eINSTANCE.getLocalizedDCType_Lang();

	}

} //DCPackage

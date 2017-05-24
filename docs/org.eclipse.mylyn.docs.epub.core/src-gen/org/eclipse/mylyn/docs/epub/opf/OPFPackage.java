/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.opf;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.mylyn.docs.epub.opf.OPFFactory
 * @model kind="package"
 * @generated
 */
public interface OPFPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "opf"; //$NON-NLS-1$

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.idpf.org/2007/opf"; //$NON-NLS-1$

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "opf"; //$NON-NLS-1$

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	OPFPackage eINSTANCE = org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.opf.impl.PackageImpl <em>Package</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.opf.impl.PackageImpl
	 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getPackage()
	 * @generated
	 */
	int PACKAGE = 0;

	/**
	 * The feature id for the '<em><b>Metadata</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE__METADATA = 0;

	/**
	 * The feature id for the '<em><b>Manifest</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE__MANIFEST = 1;

	/**
	 * The feature id for the '<em><b>Spine</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @generated
	 * @ordered
	 */
	int PACKAGE__SPINE = 2;

	/**
	 * The feature id for the '<em><b>Guide</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @generated
	 * @ordered
	 */
	int PACKAGE__GUIDE = 3;

	/**
	 * The feature id for the '<em><b>Tours</b></em>' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE__TOURS = 4;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE__VERSION = 5;

	/**
	 * The feature id for the '<em><b>Unique Identifier</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @generated
	 * @ordered
	 */
	int PACKAGE__UNIQUE_IDENTIFIER = 6;

	/**
	 * The feature id for the '<em><b>Generate Cover HTML</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @generated
	 * @ordered
	 */
	int PACKAGE__GENERATE_COVER_HTML = 7;

	/**
	 * The feature id for the '<em><b>Generate Table Of Contents</b></em>' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE__GENERATE_TABLE_OF_CONTENTS = 8;

	/**
	 * The feature id for the '<em><b>Include Referenced Resources</b></em>' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE__INCLUDE_REFERENCED_RESOURCES = 9;

	/**
	 * The feature id for the '<em><b>Prefix</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE__PREFIX = 10;

	/**
	 * The feature id for the '<em><b>Lang</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE__LANG = 11;

	/**
	 * The feature id for the '<em><b>Dir</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE__DIR = 12;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE__ID = 13;

	/**
	 * The number of structural features of the '<em>Package</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_FEATURE_COUNT = 14;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.opf.impl.MetadataImpl <em>Metadata</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.opf.impl.MetadataImpl
	 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getMetadata()
	 * @generated
	 */
	int METADATA = 1;

	/**
	 * The feature id for the '<em><b>Titles</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA__TITLES = 0;

	/**
	 * The feature id for the '<em><b>Creators</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA__CREATORS = 1;

	/**
	 * The feature id for the '<em><b>Subjects</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA__SUBJECTS = 2;

	/**
	 * The feature id for the '<em><b>Descriptions</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA__DESCRIPTIONS = 3;

	/**
	 * The feature id for the '<em><b>Publishers</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA__PUBLISHERS = 4;

	/**
	 * The feature id for the '<em><b>Contributors</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA__CONTRIBUTORS = 5;

	/**
	 * The feature id for the '<em><b>Dates</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA__DATES = 6;

	/**
	 * The feature id for the '<em><b>Types</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA__TYPES = 7;

	/**
	 * The feature id for the '<em><b>Formats</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA__FORMATS = 8;

	/**
	 * The feature id for the '<em><b>Identifiers</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA__IDENTIFIERS = 9;

	/**
	 * The feature id for the '<em><b>Sources</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA__SOURCES = 10;

	/**
	 * The feature id for the '<em><b>Languages</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA__LANGUAGES = 11;

	/**
	 * The feature id for the '<em><b>Relations</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA__RELATIONS = 12;

	/**
	 * The feature id for the '<em><b>Coverages</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA__COVERAGES = 13;

	/**
	 * The feature id for the '<em><b>Rights</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA__RIGHTS = 14;

	/**
	 * The feature id for the '<em><b>Metas</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA__METAS = 15;

	/**
	 * The number of structural features of the '<em>Metadata</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_FEATURE_COUNT = 16;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.opf.impl.ManifestImpl <em>Manifest</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.opf.impl.ManifestImpl
	 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getManifest()
	 * @generated
	 */
	int MANIFEST = 2;

	/**
	 * The feature id for the '<em><b>Items</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANIFEST__ITEMS = 0;

	/**
	 * The number of structural features of the '<em>Manifest</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MANIFEST_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.opf.impl.ItemImpl <em>Item</em>}' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.docs.epub.opf.impl.ItemImpl
	 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getItem()
	 * @generated
	 */
	int ITEM = 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITEM__ID = 0;

	/**
	 * The feature id for the '<em><b>Href</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITEM__HREF = 1;

	/**
	 * The feature id for the '<em><b>Media type</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITEM__MEDIA_TYPE = 2;

	/**
	 * The feature id for the '<em><b>Fallback</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITEM__FALLBACK = 3;

	/**
	 * The feature id for the '<em><b>Fallback style</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITEM__FALLBACK_STYLE = 4;

	/**
	 * The feature id for the '<em><b>Required namespace</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @generated
	 * @ordered
	 */
	int ITEM__REQUIRED_NAMESPACE = 5;

	/**
	 * The feature id for the '<em><b>Required modules</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @generated
	 * @ordered
	 */
	int ITEM__REQUIRED_MODULES = 6;

	/**
	 * The feature id for the '<em><b>File</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITEM__FILE = 7;

	/**
	 * The feature id for the '<em><b>No Toc</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITEM__NO_TOC = 8;

	/**
	 * The feature id for the '<em><b>Title</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITEM__TITLE = 9;

	/**
	 * The feature id for the '<em><b>Generated</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITEM__GENERATED = 10;

	/**
	 * The feature id for the '<em><b>Source Path</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITEM__SOURCE_PATH = 11;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITEM__PROPERTIES = 12;

	/**
	 * The feature id for the '<em><b>Media overlay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITEM__MEDIA_OVERLAY = 13;

	/**
	 * The number of structural features of the '<em>Item</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITEM_FEATURE_COUNT = 14;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.opf.impl.SpineImpl <em>Spine</em>}' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.docs.epub.opf.impl.SpineImpl
	 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getSpine()
	 * @generated
	 */
	int SPINE = 4;

	/**
	 * The feature id for the '<em><b>Spine Items</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SPINE__SPINE_ITEMS = 0;

	/**
	 * The feature id for the '<em><b>Toc</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SPINE__TOC = 1;

	/**
	 * The number of structural features of the '<em>Spine</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SPINE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.opf.impl.GuideImpl <em>Guide</em>}' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.docs.epub.opf.impl.GuideImpl
	 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getGuide()
	 * @generated
	 */
	int GUIDE = 5;

	/**
	 * The feature id for the '<em><b>Guide Items</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GUIDE__GUIDE_ITEMS = 0;

	/**
	 * The number of structural features of the '<em>Guide</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GUIDE_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.opf.impl.ReferenceImpl <em>Reference</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.opf.impl.ReferenceImpl
	 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getReference()
	 * @generated
	 */
	int REFERENCE = 6;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE__TYPE = 0;

	/**
	 * The feature id for the '<em><b>Title</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE__TITLE = 1;

	/**
	 * The feature id for the '<em><b>Href</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE__HREF = 2;

	/**
	 * The number of structural features of the '<em>Reference</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @generated
	 * @ordered
	 */
	int REFERENCE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.opf.impl.ItemrefImpl <em>Itemref</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.opf.impl.ItemrefImpl
	 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getItemref()
	 * @generated
	 */
	int ITEMREF = 7;

	/**
	 * The feature id for the '<em><b>Idref</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITEMREF__IDREF = 0;

	/**
	 * The feature id for the '<em><b>Linear</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITEMREF__LINEAR = 1;

	/**
	 * The number of structural features of the '<em>Itemref</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITEMREF_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.opf.impl.ToursImpl <em>Tours</em>}' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.docs.epub.opf.impl.ToursImpl
	 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getTours()
	 * @generated
	 */
	int TOURS = 8;

	/**
	 * The number of structural features of the '<em>Tours</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TOURS_FEATURE_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.opf.impl.MetaImpl <em>Meta</em>}' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.mylyn.docs.epub.opf.impl.MetaImpl
	 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getMeta()
	 * @generated
	 */
	int META = 9;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int META__NAME = 0;

	/**
	 * The feature id for the '<em><b>Content</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int META__CONTENT = 1;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int META__ID = 2;

	/**
	 * The feature id for the '<em><b>Property</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int META__PROPERTY = 3;

	/**
	 * The feature id for the '<em><b>Refines</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int META__REFINES = 4;

	/**
	 * The feature id for the '<em><b>Scheme</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int META__SCHEME = 5;

	/**
	 * The feature id for the '<em><b>Dir</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int META__DIR = 6;

	/**
	 * The number of structural features of the '<em>Meta</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int META_FEATURE_COUNT = 7;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.opf.Role <em>Role</em>}' enum.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.opf.Role
	 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getRole()
	 * @generated
	 */
	int ROLE = 10;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.opf.Type <em>Type</em>}' enum.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.opf.Type
	 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getType()
	 * @generated
	 */
	int TYPE = 11;

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.opf.Package <em>Package</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Package</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Package
	 * @generated
	 */
	EClass getPackage();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.opf.Package#getMetadata <em>Metadata</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Metadata</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Package#getMetadata()
	 * @see #getPackage()
	 * @generated
	 */
	EReference getPackage_Metadata();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.opf.Package#getManifest <em>Manifest</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Manifest</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Package#getManifest()
	 * @see #getPackage()
	 * @generated
	 */
	EReference getPackage_Manifest();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.opf.Package#getSpine <em>Spine</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Spine</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Package#getSpine()
	 * @see #getPackage()
	 * @generated
	 */
	EReference getPackage_Spine();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.opf.Package#getGuide <em>Guide</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Guide</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Package#getGuide()
	 * @see #getPackage()
	 * @generated
	 */
	EReference getPackage_Guide();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.docs.epub.opf.Package#getTours <em>Tours</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Tours</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Package#getTours()
	 * @see #getPackage()
	 * @generated
	 */
	EReference getPackage_Tours();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Package#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Package#getVersion()
	 * @see #getPackage()
	 * @generated
	 */
	EAttribute getPackage_Version();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Package#getUniqueIdentifier <em>Unique Identifier</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Unique Identifier</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Package#getUniqueIdentifier()
	 * @see #getPackage()
	 * @generated
	 */
	EAttribute getPackage_UniqueIdentifier();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Package#isGenerateCoverHTML <em>Generate Cover HTML</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Generate Cover HTML</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Package#isGenerateCoverHTML()
	 * @see #getPackage()
	 * @generated
	 */
	EAttribute getPackage_GenerateCoverHTML();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Package#isGenerateTableOfContents <em>Generate Table Of Contents</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Generate Table Of Contents</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Package#isGenerateTableOfContents()
	 * @see #getPackage()
	 * @generated
	 */
	EAttribute getPackage_GenerateTableOfContents();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Package#isIncludeReferencedResources <em>Include Referenced Resources</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Include Referenced Resources</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Package#isIncludeReferencedResources()
	 * @see #getPackage()
	 * @generated
	 */
	EAttribute getPackage_IncludeReferencedResources();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Package#getPrefix <em>Prefix</em>}'.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Prefix</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Package#getPrefix()
	 * @see #getPackage()
	 * @generated
	 */
	EAttribute getPackage_Prefix();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Package#getLang <em>Lang</em>}'.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Lang</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Package#getLang()
	 * @see #getPackage()
	 * @generated
	 */
	EAttribute getPackage_Lang();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Package#getDir <em>Dir</em>}'.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dir</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Package#getDir()
	 * @see #getPackage()
	 * @generated
	 */
	EAttribute getPackage_Dir();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Package#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Package#getId()
	 * @see #getPackage()
	 * @generated
	 */
	EAttribute getPackage_Id();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.opf.Metadata <em>Metadata</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Metadata</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Metadata
	 * @generated
	 */
	EClass getMetadata();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getTitles <em>Titles</em>}'.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Titles</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Metadata#getTitles()
	 * @see #getMetadata()
	 * @generated
	 */
	EReference getMetadata_Titles();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getCreators <em>Creators</em>}'.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Creators</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Metadata#getCreators()
	 * @see #getMetadata()
	 * @generated
	 */
	EReference getMetadata_Creators();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getSubjects <em>Subjects</em>}'.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Subjects</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Metadata#getSubjects()
	 * @see #getMetadata()
	 * @generated
	 */
	EReference getMetadata_Subjects();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getDescriptions <em>Descriptions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Descriptions</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Metadata#getDescriptions()
	 * @see #getMetadata()
	 * @generated
	 */
	EReference getMetadata_Descriptions();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getPublishers <em>Publishers</em>}'.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Publishers</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Metadata#getPublishers()
	 * @see #getMetadata()
	 * @generated
	 */
	EReference getMetadata_Publishers();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getContributors <em>Contributors</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Contributors</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Metadata#getContributors()
	 * @see #getMetadata()
	 * @generated
	 */
	EReference getMetadata_Contributors();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getDates <em>Dates</em>}'.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Dates</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Metadata#getDates()
	 * @see #getMetadata()
	 * @generated
	 */
	EReference getMetadata_Dates();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getTypes <em>Types</em>}'.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Types</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Metadata#getTypes()
	 * @see #getMetadata()
	 * @generated
	 */
	EReference getMetadata_Types();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getFormats <em>Formats</em>}'.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Formats</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Metadata#getFormats()
	 * @see #getMetadata()
	 * @generated
	 */
	EReference getMetadata_Formats();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getIdentifiers <em>Identifiers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Identifiers</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Metadata#getIdentifiers()
	 * @see #getMetadata()
	 * @generated
	 */
	EReference getMetadata_Identifiers();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getSources <em>Sources</em>}'.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Sources</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Metadata#getSources()
	 * @see #getMetadata()
	 * @generated
	 */
	EReference getMetadata_Sources();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getLanguages <em>Languages</em>}'.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Languages</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Metadata#getLanguages()
	 * @see #getMetadata()
	 * @generated
	 */
	EReference getMetadata_Languages();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getRelations <em>Relations</em>}'.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Relations</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Metadata#getRelations()
	 * @see #getMetadata()
	 * @generated
	 */
	EReference getMetadata_Relations();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getCoverages <em>Coverages</em>}'.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Coverages</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Metadata#getCoverages()
	 * @see #getMetadata()
	 * @generated
	 */
	EReference getMetadata_Coverages();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getRights <em>Rights</em>}'.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Rights</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Metadata#getRights()
	 * @see #getMetadata()
	 * @generated
	 */
	EReference getMetadata_Rights();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.opf.Metadata#getMetas <em>Metas</em>}'.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Metas</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Metadata#getMetas()
	 * @see #getMetadata()
	 * @generated
	 */
	EReference getMetadata_Metas();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.opf.Manifest <em>Manifest</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Manifest</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Manifest
	 * @generated
	 */
	EClass getManifest();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.opf.Manifest#getItems <em>Items</em>}'.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Items</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Manifest#getItems()
	 * @see #getManifest()
	 * @generated
	 */
	EReference getManifest_Items();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.opf.Item <em>Item</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Item</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Item
	 * @generated
	 */
	EClass getItem();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Item#getId <em>Id</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Item#getId()
	 * @see #getItem()
	 * @generated
	 */
	EAttribute getItem_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Item#getHref <em>Href</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Href</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Item#getHref()
	 * @see #getItem()
	 * @generated
	 */
	EAttribute getItem_Href();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Item#getMedia_type <em>Media type</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Media type</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Item#getMedia_type()
	 * @see #getItem()
	 * @generated
	 */
	EAttribute getItem_Media_type();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Item#getFallback <em>Fallback</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Fallback</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Item#getFallback()
	 * @see #getItem()
	 * @generated
	 */
	EAttribute getItem_Fallback();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Item#getFallback_style <em>Fallback style</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Fallback style</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Item#getFallback_style()
	 * @see #getItem()
	 * @generated
	 */
	EAttribute getItem_Fallback_style();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Item#getRequired_namespace <em>Required namespace</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Required namespace</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Item#getRequired_namespace()
	 * @see #getItem()
	 * @generated
	 */
	EAttribute getItem_Required_namespace();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Item#getRequired_modules <em>Required modules</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Required modules</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Item#getRequired_modules()
	 * @see #getItem()
	 * @generated
	 */
	EAttribute getItem_Required_modules();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Item#getFile <em>File</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>File</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Item#getFile()
	 * @see #getItem()
	 * @generated
	 */
	EAttribute getItem_File();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Item#isNoToc <em>No Toc</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>No Toc</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Item#isNoToc()
	 * @see #getItem()
	 * @generated
	 */
	EAttribute getItem_NoToc();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Item#getTitle <em>Title</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Title</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Item#getTitle()
	 * @see #getItem()
	 * @generated
	 */
	EAttribute getItem_Title();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Item#isGenerated <em>Generated</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Generated</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Item#isGenerated()
	 * @see #getItem()
	 * @generated
	 */
	EAttribute getItem_Generated();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Item#getSourcePath <em>Source Path</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Source Path</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Item#getSourcePath()
	 * @see #getItem()
	 * @generated
	 */
	EAttribute getItem_SourcePath();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Item#getProperties <em>Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Properties</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Item#getProperties()
	 * @see #getItem()
	 * @generated
	 */
	EAttribute getItem_Properties();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Item#getMedia_overlay <em>Media overlay</em>}'.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Media overlay</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Item#getMedia_overlay()
	 * @see #getItem()
	 * @generated
	 */
	EAttribute getItem_Media_overlay();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.opf.Spine <em>Spine</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Spine</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Spine
	 * @generated
	 */
	EClass getSpine();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.opf.Spine#getSpineItems <em>Spine Items</em>}'.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Spine Items</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Spine#getSpineItems()
	 * @see #getSpine()
	 * @generated
	 */
	EReference getSpine_SpineItems();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Spine#getToc <em>Toc</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Toc</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Spine#getToc()
	 * @see #getSpine()
	 * @generated
	 */
	EAttribute getSpine_Toc();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.opf.Guide <em>Guide</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Guide</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Guide
	 * @generated
	 */
	EClass getGuide();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.opf.Guide#getGuideItems <em>Guide Items</em>}'.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Guide Items</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Guide#getGuideItems()
	 * @see #getGuide()
	 * @generated
	 */
	EReference getGuide_GuideItems();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.opf.Reference <em>Reference</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Reference</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Reference
	 * @generated
	 */
	EClass getReference();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Reference#getType <em>Type</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Reference#getType()
	 * @see #getReference()
	 * @generated
	 */
	EAttribute getReference_Type();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Reference#getTitle <em>Title</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Title</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Reference#getTitle()
	 * @see #getReference()
	 * @generated
	 */
	EAttribute getReference_Title();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Reference#getHref <em>Href</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Href</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Reference#getHref()
	 * @see #getReference()
	 * @generated
	 */
	EAttribute getReference_Href();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.opf.Itemref <em>Itemref</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Itemref</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Itemref
	 * @generated
	 */
	EClass getItemref();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Itemref#getIdref <em>Idref</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Idref</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Itemref#getIdref()
	 * @see #getItemref()
	 * @generated
	 */
	EAttribute getItemref_Idref();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Itemref#getLinear <em>Linear</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Linear</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Itemref#getLinear()
	 * @see #getItemref()
	 * @generated
	 */
	EAttribute getItemref_Linear();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.opf.Tours <em>Tours</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Tours</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Tours
	 * @generated
	 */
	EClass getTours();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.opf.Meta <em>Meta</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Meta</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Meta
	 * @generated
	 */
	EClass getMeta();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Meta#getName <em>Name</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Meta#getName()
	 * @see #getMeta()
	 * @generated
	 */
	EAttribute getMeta_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Meta#getContent <em>Content</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Content</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Meta#getContent()
	 * @see #getMeta()
	 * @generated
	 */
	EAttribute getMeta_Content();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Meta#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Meta#getId()
	 * @see #getMeta()
	 * @generated
	 */
	EAttribute getMeta_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Meta#getProperty <em>Property</em>}'.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Property</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Meta#getProperty()
	 * @see #getMeta()
	 * @generated
	 */
	EAttribute getMeta_Property();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Meta#getRefines <em>Refines</em>}'.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Refines</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Meta#getRefines()
	 * @see #getMeta()
	 * @generated
	 */
	EAttribute getMeta_Refines();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Meta#getScheme <em>Scheme</em>}'.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Scheme</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Meta#getScheme()
	 * @see #getMeta()
	 * @generated
	 */
	EAttribute getMeta_Scheme();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.opf.Meta#getDir <em>Dir</em>}'.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dir</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Meta#getDir()
	 * @see #getMeta()
	 * @generated
	 */
	EAttribute getMeta_Dir();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.mylyn.docs.epub.opf.Role <em>Role</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for enum '<em>Role</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Role
	 * @generated
	 */
	EEnum getRole();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.mylyn.docs.epub.opf.Type <em>Type</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for enum '<em>Type</em>'.
	 * @see org.eclipse.mylyn.docs.epub.opf.Type
	 * @generated
	 */
	EEnum getType();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	OPFFactory getOPFFactory();

	/**
	 * <!-- begin-user-doc --> Defines literals for the meta objects that represent
	 * <ul>
	 * <li>each class,</li>
	 * <li>each feature of each class,</li>
	 * <li>each enum,</li>
	 * <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.opf.impl.PackageImpl <em>Package</em>}' class.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.opf.impl.PackageImpl
		 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getPackage()
		 * @generated
		 */
		EClass PACKAGE = eINSTANCE.getPackage();

		/**
		 * The meta object literal for the '<em><b>Metadata</b></em>' containment reference feature.
		 * <!-- begin-user-doc
		 * --> <!-- end-user-doc -->
		 * @generated
		 */
		EReference PACKAGE__METADATA = eINSTANCE.getPackage_Metadata();

		/**
		 * The meta object literal for the '<em><b>Manifest</b></em>' containment reference feature.
		 * <!-- begin-user-doc
		 * --> <!-- end-user-doc -->
		 * @generated
		 */
		EReference PACKAGE__MANIFEST = eINSTANCE.getPackage_Manifest();

		/**
		 * The meta object literal for the '<em><b>Spine</b></em>' containment reference feature.
		 * <!-- begin-user-doc
		 * --> <!-- end-user-doc -->
		 * @generated
		 */
		EReference PACKAGE__SPINE = eINSTANCE.getPackage_Spine();

		/**
		 * The meta object literal for the '<em><b>Guide</b></em>' containment reference feature.
		 * <!-- begin-user-doc
		 * --> <!-- end-user-doc -->
		 * @generated
		 */
		EReference PACKAGE__GUIDE = eINSTANCE.getPackage_Guide();

		/**
		 * The meta object literal for the '<em><b>Tours</b></em>' reference feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * @generated
		 */
		EReference PACKAGE__TOURS = eINSTANCE.getPackage_Tours();

		/**
		 * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * @generated
		 */
		EAttribute PACKAGE__VERSION = eINSTANCE.getPackage_Version();

		/**
		 * The meta object literal for the '<em><b>Unique Identifier</b></em>' attribute feature.
		 * <!-- begin-user-doc
		 * --> <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PACKAGE__UNIQUE_IDENTIFIER = eINSTANCE.getPackage_UniqueIdentifier();

		/**
		 * The meta object literal for the '<em><b>Generate Cover HTML</b></em>' attribute feature.
		 * <!-- begin-user-doc
		 * --> <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PACKAGE__GENERATE_COVER_HTML = eINSTANCE.getPackage_GenerateCoverHTML();

		/**
		 * The meta object literal for the '<em><b>Generate Table Of Contents</b></em>' attribute feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute PACKAGE__GENERATE_TABLE_OF_CONTENTS = eINSTANCE.getPackage_GenerateTableOfContents();

		/**
		 * The meta object literal for the '<em><b>Include Referenced Resources</b></em>' attribute feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute PACKAGE__INCLUDE_REFERENCED_RESOURCES = eINSTANCE.getPackage_IncludeReferencedResources();

		/**
		 * The meta object literal for the '<em><b>Prefix</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * @since 3.0
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PACKAGE__PREFIX = eINSTANCE.getPackage_Prefix();

		/**
		 * The meta object literal for the '<em><b>Lang</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * @since 3.0
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PACKAGE__LANG = eINSTANCE.getPackage_Lang();

		/**
		 * The meta object literal for the '<em><b>Dir</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * @since 3.0
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PACKAGE__DIR = eINSTANCE.getPackage_Dir();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * @since 3.0
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PACKAGE__ID = eINSTANCE.getPackage_Id();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.opf.impl.MetadataImpl <em>Metadata</em>}' class.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.opf.impl.MetadataImpl
		 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getMetadata()
		 * @generated
		 */
		EClass METADATA = eINSTANCE.getMetadata();

		/**
		 * The meta object literal for the '<em><b>Titles</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference METADATA__TITLES = eINSTANCE.getMetadata_Titles();

		/**
		 * The meta object literal for the '<em><b>Creators</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference METADATA__CREATORS = eINSTANCE.getMetadata_Creators();

		/**
		 * The meta object literal for the '<em><b>Subjects</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference METADATA__SUBJECTS = eINSTANCE.getMetadata_Subjects();

		/**
		 * The meta object literal for the '<em><b>Descriptions</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference METADATA__DESCRIPTIONS = eINSTANCE.getMetadata_Descriptions();

		/**
		 * The meta object literal for the '<em><b>Publishers</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference METADATA__PUBLISHERS = eINSTANCE.getMetadata_Publishers();

		/**
		 * The meta object literal for the '<em><b>Contributors</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference METADATA__CONTRIBUTORS = eINSTANCE.getMetadata_Contributors();

		/**
		 * The meta object literal for the '<em><b>Dates</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference METADATA__DATES = eINSTANCE.getMetadata_Dates();

		/**
		 * The meta object literal for the '<em><b>Types</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference METADATA__TYPES = eINSTANCE.getMetadata_Types();

		/**
		 * The meta object literal for the '<em><b>Formats</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference METADATA__FORMATS = eINSTANCE.getMetadata_Formats();

		/**
		 * The meta object literal for the '<em><b>Identifiers</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference METADATA__IDENTIFIERS = eINSTANCE.getMetadata_Identifiers();

		/**
		 * The meta object literal for the '<em><b>Sources</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference METADATA__SOURCES = eINSTANCE.getMetadata_Sources();

		/**
		 * The meta object literal for the '<em><b>Languages</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference METADATA__LANGUAGES = eINSTANCE.getMetadata_Languages();

		/**
		 * The meta object literal for the '<em><b>Relations</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference METADATA__RELATIONS = eINSTANCE.getMetadata_Relations();

		/**
		 * The meta object literal for the '<em><b>Coverages</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference METADATA__COVERAGES = eINSTANCE.getMetadata_Coverages();

		/**
		 * The meta object literal for the '<em><b>Rights</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference METADATA__RIGHTS = eINSTANCE.getMetadata_Rights();

		/**
		 * The meta object literal for the '<em><b>Metas</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference METADATA__METAS = eINSTANCE.getMetadata_Metas();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.opf.impl.ManifestImpl <em>Manifest</em>}' class.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.opf.impl.ManifestImpl
		 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getManifest()
		 * @generated
		 */
		EClass MANIFEST = eINSTANCE.getManifest();

		/**
		 * The meta object literal for the '<em><b>Items</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference MANIFEST__ITEMS = eINSTANCE.getManifest_Items();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.opf.impl.ItemImpl <em>Item</em>}' class.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.opf.impl.ItemImpl
		 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getItem()
		 * @generated
		 */
		EClass ITEM = eINSTANCE.getItem();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * @generated
		 */
		EAttribute ITEM__ID = eINSTANCE.getItem_Id();

		/**
		 * The meta object literal for the '<em><b>Href</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * @generated
		 */
		EAttribute ITEM__HREF = eINSTANCE.getItem_Href();

		/**
		 * The meta object literal for the '<em><b>Media type</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * @generated
		 */
		EAttribute ITEM__MEDIA_TYPE = eINSTANCE.getItem_Media_type();

		/**
		 * The meta object literal for the '<em><b>Fallback</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * @generated
		 */
		EAttribute ITEM__FALLBACK = eINSTANCE.getItem_Fallback();

		/**
		 * The meta object literal for the '<em><b>Fallback style</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITEM__FALLBACK_STYLE = eINSTANCE.getItem_Fallback_style();

		/**
		 * The meta object literal for the '<em><b>Required namespace</b></em>' attribute feature.
		 * <!-- begin-user-doc
		 * --> <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITEM__REQUIRED_NAMESPACE = eINSTANCE.getItem_Required_namespace();

		/**
		 * The meta object literal for the '<em><b>Required modules</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITEM__REQUIRED_MODULES = eINSTANCE.getItem_Required_modules();

		/**
		 * The meta object literal for the '<em><b>File</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * @generated
		 */
		EAttribute ITEM__FILE = eINSTANCE.getItem_File();

		/**
		 * The meta object literal for the '<em><b>No Toc</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * @generated
		 */
		EAttribute ITEM__NO_TOC = eINSTANCE.getItem_NoToc();

		/**
		 * The meta object literal for the '<em><b>Title</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * @generated
		 */
		EAttribute ITEM__TITLE = eINSTANCE.getItem_Title();

		/**
		 * The meta object literal for the '<em><b>Generated</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * @generated
		 */
		EAttribute ITEM__GENERATED = eINSTANCE.getItem_Generated();

		/**
		 * The meta object literal for the '<em><b>Source Path</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * @generated
		 */
		EAttribute ITEM__SOURCE_PATH = eINSTANCE.getItem_SourcePath();

		/**
		 * The meta object literal for the '<em><b>Properties</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * @since 3.0
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITEM__PROPERTIES = eINSTANCE.getItem_Properties();

		/**
		 * The meta object literal for the '<em><b>Media overlay</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * @since 3.0
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITEM__MEDIA_OVERLAY = eINSTANCE.getItem_Media_overlay();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.opf.impl.SpineImpl <em>Spine</em>}' class.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.opf.impl.SpineImpl
		 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getSpine()
		 * @generated
		 */
		EClass SPINE = eINSTANCE.getSpine();

		/**
		 * The meta object literal for the '<em><b>Spine Items</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference SPINE__SPINE_ITEMS = eINSTANCE.getSpine_SpineItems();

		/**
		 * The meta object literal for the '<em><b>Toc</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * @generated
		 */
		EAttribute SPINE__TOC = eINSTANCE.getSpine_Toc();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.opf.impl.GuideImpl <em>Guide</em>}' class.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.opf.impl.GuideImpl
		 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getGuide()
		 * @generated
		 */
		EClass GUIDE = eINSTANCE.getGuide();

		/**
		 * The meta object literal for the '<em><b>Guide Items</b></em>' containment reference list feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference GUIDE__GUIDE_ITEMS = eINSTANCE.getGuide_GuideItems();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.opf.impl.ReferenceImpl <em>Reference</em>}' class.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.opf.impl.ReferenceImpl
		 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getReference()
		 * @generated
		 */
		EClass REFERENCE = eINSTANCE.getReference();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * @generated
		 */
		EAttribute REFERENCE__TYPE = eINSTANCE.getReference_Type();

		/**
		 * The meta object literal for the '<em><b>Title</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * @generated
		 */
		EAttribute REFERENCE__TITLE = eINSTANCE.getReference_Title();

		/**
		 * The meta object literal for the '<em><b>Href</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * @generated
		 */
		EAttribute REFERENCE__HREF = eINSTANCE.getReference_Href();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.opf.impl.ItemrefImpl <em>Itemref</em>}' class.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.opf.impl.ItemrefImpl
		 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getItemref()
		 * @generated
		 */
		EClass ITEMREF = eINSTANCE.getItemref();

		/**
		 * The meta object literal for the '<em><b>Idref</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * @generated
		 */
		EAttribute ITEMREF__IDREF = eINSTANCE.getItemref_Idref();

		/**
		 * The meta object literal for the '<em><b>Linear</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * @generated
		 */
		EAttribute ITEMREF__LINEAR = eINSTANCE.getItemref_Linear();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.opf.impl.ToursImpl <em>Tours</em>}' class.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.opf.impl.ToursImpl
		 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getTours()
		 * @generated
		 */
		EClass TOURS = eINSTANCE.getTours();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.opf.impl.MetaImpl <em>Meta</em>}' class.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.opf.impl.MetaImpl
		 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getMeta()
		 * @generated
		 */
		EClass META = eINSTANCE.getMeta();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * @generated
		 */
		EAttribute META__NAME = eINSTANCE.getMeta_Name();

		/**
		 * The meta object literal for the '<em><b>Content</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * @generated
		 */
		EAttribute META__CONTENT = eINSTANCE.getMeta_Content();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * @since 3.0
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute META__ID = eINSTANCE.getMeta_Id();

		/**
		 * The meta object literal for the '<em><b>Property</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * @since 3.0
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute META__PROPERTY = eINSTANCE.getMeta_Property();

		/**
		 * The meta object literal for the '<em><b>Refines</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * @since 3.0
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute META__REFINES = eINSTANCE.getMeta_Refines();

		/**
		 * The meta object literal for the '<em><b>Scheme</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * @since 3.0
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute META__SCHEME = eINSTANCE.getMeta_Scheme();

		/**
		 * The meta object literal for the '<em><b>Dir</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * @since 3.0
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute META__DIR = eINSTANCE.getMeta_Dir();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.opf.Role <em>Role</em>}' enum. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.docs.epub.opf.Role
		 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getRole()
		 * @generated
		 */
		EEnum ROLE = eINSTANCE.getRole();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.opf.Type <em>Type</em>}' enum. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @see org.eclipse.mylyn.docs.epub.opf.Type
		 * @see org.eclipse.mylyn.docs.epub.opf.impl.OPFPackageImpl#getType()
		 * @generated
		 */
		EEnum TYPE = eINSTANCE.getType();

	}

} //OPFPackage

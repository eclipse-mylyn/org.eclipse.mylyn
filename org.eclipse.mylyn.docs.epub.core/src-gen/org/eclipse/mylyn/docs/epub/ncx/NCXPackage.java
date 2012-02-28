/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.ncx;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

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
 * @see org.eclipse.mylyn.docs.epub.ncx.NCXFactory
 * @model kind="package"
 * @generated
 */
public interface NCXPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "ncx";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.daisy.org/z3986/2005/ncx/";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "ncx";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	NCXPackage eINSTANCE = org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.AudioImpl <em>Audio</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.AudioImpl
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getAudio()
	 * @generated
	 */
	int AUDIO = 0;

	/**
	 * The feature id for the '<em><b>Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AUDIO__CLASS = 0;

	/**
	 * The feature id for the '<em><b>Clip Begin</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AUDIO__CLIP_BEGIN = 1;

	/**
	 * The feature id for the '<em><b>Clip End</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AUDIO__CLIP_END = 2;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AUDIO__ID = 3;

	/**
	 * The feature id for the '<em><b>Src</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AUDIO__SRC = 4;

	/**
	 * The number of structural features of the '<em>Audio</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AUDIO_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.ContentImpl <em>Content</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.ContentImpl
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getContent()
	 * @generated
	 */
	int CONTENT = 1;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTENT__ID = 0;

	/**
	 * The feature id for the '<em><b>Src</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTENT__SRC = 1;

	/**
	 * The number of structural features of the '<em>Content</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTENT_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.DocAuthorImpl <em>Doc Author</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.DocAuthorImpl
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getDocAuthor()
	 * @generated
	 */
	int DOC_AUTHOR = 2;

	/**
	 * The feature id for the '<em><b>Text</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOC_AUTHOR__TEXT = 0;

	/**
	 * The feature id for the '<em><b>Audio</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOC_AUTHOR__AUDIO = 1;

	/**
	 * The feature id for the '<em><b>Img</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOC_AUTHOR__IMG = 2;

	/**
	 * The feature id for the '<em><b>Dir</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOC_AUTHOR__DIR = 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOC_AUTHOR__ID = 4;

	/**
	 * The feature id for the '<em><b>Lang</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOC_AUTHOR__LANG = 5;

	/**
	 * The number of structural features of the '<em>Doc Author</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOC_AUTHOR_FEATURE_COUNT = 6;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.DocTitleImpl <em>Doc Title</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.DocTitleImpl
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getDocTitle()
	 * @generated
	 */
	int DOC_TITLE = 3;

	/**
	 * The feature id for the '<em><b>Text</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOC_TITLE__TEXT = 0;

	/**
	 * The feature id for the '<em><b>Audio</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOC_TITLE__AUDIO = 1;

	/**
	 * The feature id for the '<em><b>Img</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOC_TITLE__IMG = 2;

	/**
	 * The feature id for the '<em><b>Dir</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOC_TITLE__DIR = 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOC_TITLE__ID = 4;

	/**
	 * The feature id for the '<em><b>Lang</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOC_TITLE__LANG = 5;

	/**
	 * The number of structural features of the '<em>Doc Title</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOC_TITLE_FEATURE_COUNT = 6;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.HeadImpl <em>Head</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.HeadImpl
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getHead()
	 * @generated
	 */
	int HEAD = 4;

	/**
	 * The feature id for the '<em><b>Groups</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEAD__GROUPS = 0;

	/**
	 * The feature id for the '<em><b>Smil Custom Tests</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEAD__SMIL_CUSTOM_TESTS = 1;

	/**
	 * The feature id for the '<em><b>Metas</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEAD__METAS = 2;

	/**
	 * The number of structural features of the '<em>Head</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HEAD_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.ImgImpl <em>Img</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.ImgImpl
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getImg()
	 * @generated
	 */
	int IMG = 5;

	/**
	 * The feature id for the '<em><b>Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMG__CLASS = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMG__ID = 1;

	/**
	 * The feature id for the '<em><b>Src</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMG__SRC = 2;

	/**
	 * The number of structural features of the '<em>Img</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMG_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.MetaImpl <em>Meta</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.MetaImpl
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getMeta()
	 * @generated
	 */
	int META = 6;

	/**
	 * The feature id for the '<em><b>Content</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int META__CONTENT = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int META__NAME = 1;

	/**
	 * The feature id for the '<em><b>Scheme</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int META__SCHEME = 2;

	/**
	 * The number of structural features of the '<em>Meta</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int META_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavInfoImpl <em>Nav Info</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NavInfoImpl
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getNavInfo()
	 * @generated
	 */
	int NAV_INFO = 7;

	/**
	 * The feature id for the '<em><b>Text</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_INFO__TEXT = 0;

	/**
	 * The feature id for the '<em><b>Audio</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_INFO__AUDIO = 1;

	/**
	 * The feature id for the '<em><b>Img</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_INFO__IMG = 2;

	/**
	 * The feature id for the '<em><b>Dir</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_INFO__DIR = 3;

	/**
	 * The feature id for the '<em><b>Lang</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_INFO__LANG = 4;

	/**
	 * The number of structural features of the '<em>Nav Info</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_INFO_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavLabelImpl <em>Nav Label</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NavLabelImpl
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getNavLabel()
	 * @generated
	 */
	int NAV_LABEL = 8;

	/**
	 * The feature id for the '<em><b>Text</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_LABEL__TEXT = 0;

	/**
	 * The feature id for the '<em><b>Audio</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_LABEL__AUDIO = 1;

	/**
	 * The feature id for the '<em><b>Img</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_LABEL__IMG = 2;

	/**
	 * The feature id for the '<em><b>Dir</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_LABEL__DIR = 3;

	/**
	 * The feature id for the '<em><b>Lang</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_LABEL__LANG = 4;

	/**
	 * The number of structural features of the '<em>Nav Label</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_LABEL_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavListImpl <em>Nav List</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NavListImpl
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getNavList()
	 * @generated
	 */
	int NAV_LIST = 9;

	/**
	 * The feature id for the '<em><b>Nav Infos</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_LIST__NAV_INFOS = 0;

	/**
	 * The feature id for the '<em><b>Nav Labels</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_LIST__NAV_LABELS = 1;

	/**
	 * The feature id for the '<em><b>Nav Targets</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_LIST__NAV_TARGETS = 2;

	/**
	 * The feature id for the '<em><b>Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_LIST__CLASS = 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_LIST__ID = 4;

	/**
	 * The number of structural features of the '<em>Nav List</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_LIST_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavMapImpl <em>Nav Map</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NavMapImpl
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getNavMap()
	 * @generated
	 */
	int NAV_MAP = 10;

	/**
	 * The feature id for the '<em><b>Nav Infos</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_MAP__NAV_INFOS = 0;

	/**
	 * The feature id for the '<em><b>Nav Labels</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_MAP__NAV_LABELS = 1;

	/**
	 * The feature id for the '<em><b>Nav Points</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_MAP__NAV_POINTS = 2;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_MAP__ID = 3;

	/**
	 * The number of structural features of the '<em>Nav Map</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_MAP_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavPointImpl <em>Nav Point</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NavPointImpl
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getNavPoint()
	 * @generated
	 */
	int NAV_POINT = 11;

	/**
	 * The feature id for the '<em><b>Nav Labels</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_POINT__NAV_LABELS = 0;

	/**
	 * The feature id for the '<em><b>Content</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_POINT__CONTENT = 1;

	/**
	 * The feature id for the '<em><b>Nav Points</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_POINT__NAV_POINTS = 2;

	/**
	 * The feature id for the '<em><b>Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_POINT__CLASS = 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_POINT__ID = 4;

	/**
	 * The feature id for the '<em><b>Play Order</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_POINT__PLAY_ORDER = 5;

	/**
	 * The number of structural features of the '<em>Nav Point</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_POINT_FEATURE_COUNT = 6;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavTargetImpl <em>Nav Target</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NavTargetImpl
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getNavTarget()
	 * @generated
	 */
	int NAV_TARGET = 12;

	/**
	 * The feature id for the '<em><b>Nav Labels</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_TARGET__NAV_LABELS = 0;

	/**
	 * The feature id for the '<em><b>Content</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_TARGET__CONTENT = 1;

	/**
	 * The feature id for the '<em><b>Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_TARGET__CLASS = 2;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_TARGET__ID = 3;

	/**
	 * The feature id for the '<em><b>Play Order</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_TARGET__PLAY_ORDER = 4;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_TARGET__VALUE = 5;

	/**
	 * The number of structural features of the '<em>Nav Target</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAV_TARGET_FEATURE_COUNT = 6;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.NcxImpl <em>Ncx</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NcxImpl
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getNcx()
	 * @generated
	 */
	int NCX = 13;

	/**
	 * The feature id for the '<em><b>Head</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NCX__HEAD = 0;

	/**
	 * The feature id for the '<em><b>Doc Title</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NCX__DOC_TITLE = 1;

	/**
	 * The feature id for the '<em><b>Doc Authors</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NCX__DOC_AUTHORS = 2;

	/**
	 * The feature id for the '<em><b>Nav Map</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NCX__NAV_MAP = 3;

	/**
	 * The feature id for the '<em><b>Page List</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NCX__PAGE_LIST = 4;

	/**
	 * The feature id for the '<em><b>Nav Lists</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NCX__NAV_LISTS = 5;

	/**
	 * The feature id for the '<em><b>Dir</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NCX__DIR = 6;

	/**
	 * The feature id for the '<em><b>Lang</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NCX__LANG = 7;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NCX__VERSION = 8;

	/**
	 * The number of structural features of the '<em>Ncx</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NCX_FEATURE_COUNT = 9;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.PageListImpl <em>Page List</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.PageListImpl
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getPageList()
	 * @generated
	 */
	int PAGE_LIST = 14;

	/**
	 * The feature id for the '<em><b>Nav Infos</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_LIST__NAV_INFOS = 0;

	/**
	 * The feature id for the '<em><b>Nav Labels</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_LIST__NAV_LABELS = 1;

	/**
	 * The feature id for the '<em><b>Page Targets</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_LIST__PAGE_TARGETS = 2;

	/**
	 * The feature id for the '<em><b>Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_LIST__CLASS = 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_LIST__ID = 4;

	/**
	 * The number of structural features of the '<em>Page List</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_LIST_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.PageTargetImpl <em>Page Target</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.PageTargetImpl
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getPageTarget()
	 * @generated
	 */
	int PAGE_TARGET = 15;

	/**
	 * The feature id for the '<em><b>Nav Labels</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_TARGET__NAV_LABELS = 0;

	/**
	 * The feature id for the '<em><b>Content</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_TARGET__CONTENT = 1;

	/**
	 * The feature id for the '<em><b>Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_TARGET__CLASS = 2;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_TARGET__ID = 3;

	/**
	 * The feature id for the '<em><b>Play Order</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_TARGET__PLAY_ORDER = 4;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_TARGET__TYPE = 5;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_TARGET__VALUE = 6;

	/**
	 * The number of structural features of the '<em>Page Target</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_TARGET_FEATURE_COUNT = 7;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.SmilCustomTestImpl <em>Smil Custom Test</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.SmilCustomTestImpl
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getSmilCustomTest()
	 * @generated
	 */
	int SMIL_CUSTOM_TEST = 16;

	/**
	 * The feature id for the '<em><b>Book Struct</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SMIL_CUSTOM_TEST__BOOK_STRUCT = 0;

	/**
	 * The feature id for the '<em><b>Default State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SMIL_CUSTOM_TEST__DEFAULT_STATE = 1;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SMIL_CUSTOM_TEST__ID = 2;

	/**
	 * The feature id for the '<em><b>Override</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SMIL_CUSTOM_TEST__OVERRIDE = 3;

	/**
	 * The number of structural features of the '<em>Smil Custom Test</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SMIL_CUSTOM_TEST_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.TextImpl <em>Text</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.TextImpl
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getText()
	 * @generated
	 */
	int TEXT = 17;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEXT__MIXED = 0;

	/**
	 * The feature id for the '<em><b>Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEXT__CLASS = 1;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEXT__ID = 2;

	/**
	 * The number of structural features of the '<em>Text</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEXT_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.BookStruct <em>Book Struct</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.BookStruct
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getBookStruct()
	 * @generated
	 */
	int BOOK_STRUCT = 18;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.DefaultState <em>Default State</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.DefaultState
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getDefaultState()
	 * @generated
	 */
	int DEFAULT_STATE = 19;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.DirType <em>Dir Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.DirType
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getDirType()
	 * @generated
	 */
	int DIR_TYPE = 20;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.OverrideType <em>Override Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.OverrideType
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getOverrideType()
	 * @generated
	 */
	int OVERRIDE_TYPE = 21;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.docs.epub.ncx.Type <em>Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.Type
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getType()
	 * @generated
	 */
	int TYPE = 22;

	/**
	 * The meta object id for the '<em>Book Struct Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.docs.epub.ncx.BookStruct
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getBookStructObject()
	 * @generated
	 */
	int BOOK_STRUCT_OBJECT = 23;

	/**
	 * The meta object id for the '<em>Default State Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.common.util.Enumerator
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getDefaultStateObject()
	 * @generated
	 */
	int DEFAULT_STATE_OBJECT = 24;

	/**
	 * The meta object id for the '<em>Dir Type Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.common.util.Enumerator
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getDirTypeObject()
	 * @generated
	 */
	int DIR_TYPE_OBJECT = 25;

	/**
	 * The meta object id for the '<em>Override Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.common.util.Enumerator
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getOverrideObject()
	 * @generated
	 */
	int OVERRIDE_OBJECT = 26;

	/**
	 * The meta object id for the '<em>SMI Ltime Val</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.String
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getSMILtimeVal()
	 * @generated
	 */
	int SMI_LTIME_VAL = 27;

	/**
	 * The meta object id for the '<em>Type Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.common.util.Enumerator
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getTypeObject()
	 * @generated
	 */
	int TYPE_OBJECT = 28;

	/**
	 * The meta object id for the '<em>URI</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.String
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getURI()
	 * @generated
	 */
	int URI = 29;

	/**
	 * The meta object id for the '<em>Version Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.common.util.Enumerator
	 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getVersionObject()
	 * @generated
	 */
	int VERSION_OBJECT = 30;


	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.ncx.Audio <em>Audio</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Audio</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Audio
	 * @generated
	 */
	EClass getAudio();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.Audio#getClass_ <em>Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Class</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Audio#getClass_()
	 * @see #getAudio()
	 * @generated
	 */
	EAttribute getAudio_Class();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.Audio#getClipBegin <em>Clip Begin</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Clip Begin</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Audio#getClipBegin()
	 * @see #getAudio()
	 * @generated
	 */
	EAttribute getAudio_ClipBegin();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.Audio#getClipEnd <em>Clip End</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Clip End</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Audio#getClipEnd()
	 * @see #getAudio()
	 * @generated
	 */
	EAttribute getAudio_ClipEnd();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.Audio#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Audio#getId()
	 * @see #getAudio()
	 * @generated
	 */
	EAttribute getAudio_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.Audio#getSrc <em>Src</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Src</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Audio#getSrc()
	 * @see #getAudio()
	 * @generated
	 */
	EAttribute getAudio_Src();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.ncx.Content <em>Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Content</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Content
	 * @generated
	 */
	EClass getContent();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.Content#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Content#getId()
	 * @see #getContent()
	 * @generated
	 */
	EAttribute getContent_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.Content#getSrc <em>Src</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Src</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Content#getSrc()
	 * @see #getContent()
	 * @generated
	 */
	EAttribute getContent_Src();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.ncx.DocAuthor <em>Doc Author</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Doc Author</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.DocAuthor
	 * @generated
	 */
	EClass getDocAuthor();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.ncx.DocAuthor#getText <em>Text</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Text</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.DocAuthor#getText()
	 * @see #getDocAuthor()
	 * @generated
	 */
	EReference getDocAuthor_Text();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.ncx.DocAuthor#getAudio <em>Audio</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Audio</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.DocAuthor#getAudio()
	 * @see #getDocAuthor()
	 * @generated
	 */
	EReference getDocAuthor_Audio();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.ncx.DocAuthor#getImg <em>Img</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Img</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.DocAuthor#getImg()
	 * @see #getDocAuthor()
	 * @generated
	 */
	EReference getDocAuthor_Img();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.DocAuthor#getDir <em>Dir</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dir</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.DocAuthor#getDir()
	 * @see #getDocAuthor()
	 * @generated
	 */
	EAttribute getDocAuthor_Dir();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.DocAuthor#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.DocAuthor#getId()
	 * @see #getDocAuthor()
	 * @generated
	 */
	EAttribute getDocAuthor_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.DocAuthor#getLang <em>Lang</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Lang</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.DocAuthor#getLang()
	 * @see #getDocAuthor()
	 * @generated
	 */
	EAttribute getDocAuthor_Lang();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.ncx.DocTitle <em>Doc Title</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Doc Title</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.DocTitle
	 * @generated
	 */
	EClass getDocTitle();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.ncx.DocTitle#getText <em>Text</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Text</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.DocTitle#getText()
	 * @see #getDocTitle()
	 * @generated
	 */
	EReference getDocTitle_Text();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.ncx.DocTitle#getAudio <em>Audio</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Audio</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.DocTitle#getAudio()
	 * @see #getDocTitle()
	 * @generated
	 */
	EReference getDocTitle_Audio();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.ncx.DocTitle#getImg <em>Img</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Img</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.DocTitle#getImg()
	 * @see #getDocTitle()
	 * @generated
	 */
	EReference getDocTitle_Img();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.DocTitle#getDir <em>Dir</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dir</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.DocTitle#getDir()
	 * @see #getDocTitle()
	 * @generated
	 */
	EAttribute getDocTitle_Dir();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.DocTitle#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.DocTitle#getId()
	 * @see #getDocTitle()
	 * @generated
	 */
	EAttribute getDocTitle_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.DocTitle#getLang <em>Lang</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Lang</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.DocTitle#getLang()
	 * @see #getDocTitle()
	 * @generated
	 */
	EAttribute getDocTitle_Lang();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.ncx.Head <em>Head</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Head</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Head
	 * @generated
	 */
	EClass getHead();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.mylyn.docs.epub.ncx.Head#getGroups <em>Groups</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Groups</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Head#getGroups()
	 * @see #getHead()
	 * @generated
	 */
	EAttribute getHead_Groups();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.ncx.Head#getSmilCustomTests <em>Smil Custom Tests</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Smil Custom Tests</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Head#getSmilCustomTests()
	 * @see #getHead()
	 * @generated
	 */
	EReference getHead_SmilCustomTests();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.ncx.Head#getMetas <em>Metas</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Metas</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Head#getMetas()
	 * @see #getHead()
	 * @generated
	 */
	EReference getHead_Metas();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.ncx.Img <em>Img</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Img</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Img
	 * @generated
	 */
	EClass getImg();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.Img#getClass_ <em>Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Class</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Img#getClass_()
	 * @see #getImg()
	 * @generated
	 */
	EAttribute getImg_Class();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.Img#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Img#getId()
	 * @see #getImg()
	 * @generated
	 */
	EAttribute getImg_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.Img#getSrc <em>Src</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Src</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Img#getSrc()
	 * @see #getImg()
	 * @generated
	 */
	EAttribute getImg_Src();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.ncx.Meta <em>Meta</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Meta</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Meta
	 * @generated
	 */
	EClass getMeta();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.Meta#getContent <em>Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Content</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Meta#getContent()
	 * @see #getMeta()
	 * @generated
	 */
	EAttribute getMeta_Content();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.Meta#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Meta#getName()
	 * @see #getMeta()
	 * @generated
	 */
	EAttribute getMeta_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.Meta#getScheme <em>Scheme</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Scheme</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Meta#getScheme()
	 * @see #getMeta()
	 * @generated
	 */
	EAttribute getMeta_Scheme();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.ncx.NavInfo <em>Nav Info</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Nav Info</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavInfo
	 * @generated
	 */
	EClass getNavInfo();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.ncx.NavInfo#getText <em>Text</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Text</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavInfo#getText()
	 * @see #getNavInfo()
	 * @generated
	 */
	EReference getNavInfo_Text();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.ncx.NavInfo#getAudio <em>Audio</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Audio</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavInfo#getAudio()
	 * @see #getNavInfo()
	 * @generated
	 */
	EReference getNavInfo_Audio();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.ncx.NavInfo#getImg <em>Img</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Img</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavInfo#getImg()
	 * @see #getNavInfo()
	 * @generated
	 */
	EReference getNavInfo_Img();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.NavInfo#getDir <em>Dir</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dir</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavInfo#getDir()
	 * @see #getNavInfo()
	 * @generated
	 */
	EAttribute getNavInfo_Dir();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.NavInfo#getLang <em>Lang</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Lang</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavInfo#getLang()
	 * @see #getNavInfo()
	 * @generated
	 */
	EAttribute getNavInfo_Lang();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.ncx.NavLabel <em>Nav Label</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Nav Label</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavLabel
	 * @generated
	 */
	EClass getNavLabel();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.ncx.NavLabel#getText <em>Text</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Text</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavLabel#getText()
	 * @see #getNavLabel()
	 * @generated
	 */
	EReference getNavLabel_Text();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.ncx.NavLabel#getAudio <em>Audio</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Audio</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavLabel#getAudio()
	 * @see #getNavLabel()
	 * @generated
	 */
	EReference getNavLabel_Audio();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.ncx.NavLabel#getImg <em>Img</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Img</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavLabel#getImg()
	 * @see #getNavLabel()
	 * @generated
	 */
	EReference getNavLabel_Img();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.NavLabel#getDir <em>Dir</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dir</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavLabel#getDir()
	 * @see #getNavLabel()
	 * @generated
	 */
	EAttribute getNavLabel_Dir();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.NavLabel#getLang <em>Lang</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Lang</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavLabel#getLang()
	 * @see #getNavLabel()
	 * @generated
	 */
	EAttribute getNavLabel_Lang();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.ncx.NavList <em>Nav List</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Nav List</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavList
	 * @generated
	 */
	EClass getNavList();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.ncx.NavList#getNavInfos <em>Nav Infos</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Nav Infos</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavList#getNavInfos()
	 * @see #getNavList()
	 * @generated
	 */
	EReference getNavList_NavInfos();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.ncx.NavList#getNavLabels <em>Nav Labels</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Nav Labels</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavList#getNavLabels()
	 * @see #getNavList()
	 * @generated
	 */
	EReference getNavList_NavLabels();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.ncx.NavList#getNavTargets <em>Nav Targets</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Nav Targets</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavList#getNavTargets()
	 * @see #getNavList()
	 * @generated
	 */
	EReference getNavList_NavTargets();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.NavList#getClass_ <em>Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Class</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavList#getClass_()
	 * @see #getNavList()
	 * @generated
	 */
	EAttribute getNavList_Class();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.NavList#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavList#getId()
	 * @see #getNavList()
	 * @generated
	 */
	EAttribute getNavList_Id();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.ncx.NavMap <em>Nav Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Nav Map</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavMap
	 * @generated
	 */
	EClass getNavMap();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.ncx.NavMap#getNavInfos <em>Nav Infos</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Nav Infos</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavMap#getNavInfos()
	 * @see #getNavMap()
	 * @generated
	 */
	EReference getNavMap_NavInfos();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.ncx.NavMap#getNavLabels <em>Nav Labels</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Nav Labels</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavMap#getNavLabels()
	 * @see #getNavMap()
	 * @generated
	 */
	EReference getNavMap_NavLabels();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.ncx.NavMap#getNavPoints <em>Nav Points</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Nav Points</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavMap#getNavPoints()
	 * @see #getNavMap()
	 * @generated
	 */
	EReference getNavMap_NavPoints();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.NavMap#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavMap#getId()
	 * @see #getNavMap()
	 * @generated
	 */
	EAttribute getNavMap_Id();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.ncx.NavPoint <em>Nav Point</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Nav Point</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavPoint
	 * @generated
	 */
	EClass getNavPoint();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.ncx.NavPoint#getNavLabels <em>Nav Labels</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Nav Labels</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavPoint#getNavLabels()
	 * @see #getNavPoint()
	 * @generated
	 */
	EReference getNavPoint_NavLabels();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.ncx.NavPoint#getContent <em>Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Content</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavPoint#getContent()
	 * @see #getNavPoint()
	 * @generated
	 */
	EReference getNavPoint_Content();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.ncx.NavPoint#getNavPoints <em>Nav Points</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Nav Points</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavPoint#getNavPoints()
	 * @see #getNavPoint()
	 * @generated
	 */
	EReference getNavPoint_NavPoints();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.NavPoint#getClass_ <em>Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Class</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavPoint#getClass_()
	 * @see #getNavPoint()
	 * @generated
	 */
	EAttribute getNavPoint_Class();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.NavPoint#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavPoint#getId()
	 * @see #getNavPoint()
	 * @generated
	 */
	EAttribute getNavPoint_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.NavPoint#getPlayOrder <em>Play Order</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Play Order</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavPoint#getPlayOrder()
	 * @see #getNavPoint()
	 * @generated
	 */
	EAttribute getNavPoint_PlayOrder();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.ncx.NavTarget <em>Nav Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Nav Target</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavTarget
	 * @generated
	 */
	EClass getNavTarget();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.ncx.NavTarget#getNavLabels <em>Nav Labels</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Nav Labels</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavTarget#getNavLabels()
	 * @see #getNavTarget()
	 * @generated
	 */
	EReference getNavTarget_NavLabels();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.ncx.NavTarget#getContent <em>Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Content</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavTarget#getContent()
	 * @see #getNavTarget()
	 * @generated
	 */
	EReference getNavTarget_Content();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.NavTarget#getClass_ <em>Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Class</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavTarget#getClass_()
	 * @see #getNavTarget()
	 * @generated
	 */
	EAttribute getNavTarget_Class();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.NavTarget#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavTarget#getId()
	 * @see #getNavTarget()
	 * @generated
	 */
	EAttribute getNavTarget_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.NavTarget#getPlayOrder <em>Play Order</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Play Order</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavTarget#getPlayOrder()
	 * @see #getNavTarget()
	 * @generated
	 */
	EAttribute getNavTarget_PlayOrder();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.NavTarget#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.NavTarget#getValue()
	 * @see #getNavTarget()
	 * @generated
	 */
	EAttribute getNavTarget_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.ncx.Ncx <em>Ncx</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Ncx</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Ncx
	 * @generated
	 */
	EClass getNcx();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getHead <em>Head</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Head</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Ncx#getHead()
	 * @see #getNcx()
	 * @generated
	 */
	EReference getNcx_Head();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getDocTitle <em>Doc Title</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Doc Title</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Ncx#getDocTitle()
	 * @see #getNcx()
	 * @generated
	 */
	EReference getNcx_DocTitle();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getDocAuthors <em>Doc Authors</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Doc Authors</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Ncx#getDocAuthors()
	 * @see #getNcx()
	 * @generated
	 */
	EReference getNcx_DocAuthors();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getNavMap <em>Nav Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Nav Map</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Ncx#getNavMap()
	 * @see #getNcx()
	 * @generated
	 */
	EReference getNcx_NavMap();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getPageList <em>Page List</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Page List</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Ncx#getPageList()
	 * @see #getNcx()
	 * @generated
	 */
	EReference getNcx_PageList();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getNavLists <em>Nav Lists</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Nav Lists</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Ncx#getNavLists()
	 * @see #getNcx()
	 * @generated
	 */
	EReference getNcx_NavLists();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getDir <em>Dir</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dir</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Ncx#getDir()
	 * @see #getNcx()
	 * @generated
	 */
	EAttribute getNcx_Dir();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getLang <em>Lang</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Lang</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Ncx#getLang()
	 * @see #getNcx()
	 * @generated
	 */
	EAttribute getNcx_Lang();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.Ncx#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Ncx#getVersion()
	 * @see #getNcx()
	 * @generated
	 */
	EAttribute getNcx_Version();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.ncx.PageList <em>Page List</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Page List</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.PageList
	 * @generated
	 */
	EClass getPageList();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.ncx.PageList#getNavInfos <em>Nav Infos</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Nav Infos</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.PageList#getNavInfos()
	 * @see #getPageList()
	 * @generated
	 */
	EReference getPageList_NavInfos();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.ncx.PageList#getNavLabels <em>Nav Labels</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Nav Labels</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.PageList#getNavLabels()
	 * @see #getPageList()
	 * @generated
	 */
	EReference getPageList_NavLabels();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.ncx.PageList#getPageTargets <em>Page Targets</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Page Targets</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.PageList#getPageTargets()
	 * @see #getPageList()
	 * @generated
	 */
	EReference getPageList_PageTargets();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.PageList#getClass_ <em>Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Class</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.PageList#getClass_()
	 * @see #getPageList()
	 * @generated
	 */
	EAttribute getPageList_Class();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.PageList#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.PageList#getId()
	 * @see #getPageList()
	 * @generated
	 */
	EAttribute getPageList_Id();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.ncx.PageTarget <em>Page Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Page Target</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.PageTarget
	 * @generated
	 */
	EClass getPageTarget();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.mylyn.docs.epub.ncx.PageTarget#getNavLabels <em>Nav Labels</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Nav Labels</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.PageTarget#getNavLabels()
	 * @see #getPageTarget()
	 * @generated
	 */
	EReference getPageTarget_NavLabels();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.mylyn.docs.epub.ncx.PageTarget#getContent <em>Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Content</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.PageTarget#getContent()
	 * @see #getPageTarget()
	 * @generated
	 */
	EReference getPageTarget_Content();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.PageTarget#getClass_ <em>Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Class</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.PageTarget#getClass_()
	 * @see #getPageTarget()
	 * @generated
	 */
	EAttribute getPageTarget_Class();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.PageTarget#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.PageTarget#getId()
	 * @see #getPageTarget()
	 * @generated
	 */
	EAttribute getPageTarget_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.PageTarget#getPlayOrder <em>Play Order</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Play Order</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.PageTarget#getPlayOrder()
	 * @see #getPageTarget()
	 * @generated
	 */
	EAttribute getPageTarget_PlayOrder();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.PageTarget#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.PageTarget#getType()
	 * @see #getPageTarget()
	 * @generated
	 */
	EAttribute getPageTarget_Type();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.PageTarget#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.PageTarget#getValue()
	 * @see #getPageTarget()
	 * @generated
	 */
	EAttribute getPageTarget_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest <em>Smil Custom Test</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Smil Custom Test</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest
	 * @generated
	 */
	EClass getSmilCustomTest();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest#getBookStruct <em>Book Struct</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Book Struct</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest#getBookStruct()
	 * @see #getSmilCustomTest()
	 * @generated
	 */
	EAttribute getSmilCustomTest_BookStruct();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest#getDefaultState <em>Default State</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Default State</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest#getDefaultState()
	 * @see #getSmilCustomTest()
	 * @generated
	 */
	EAttribute getSmilCustomTest_DefaultState();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest#getId()
	 * @see #getSmilCustomTest()
	 * @generated
	 */
	EAttribute getSmilCustomTest_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest#getOverride <em>Override</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Override</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest#getOverride()
	 * @see #getSmilCustomTest()
	 * @generated
	 */
	EAttribute getSmilCustomTest_Override();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.docs.epub.ncx.Text <em>Text</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Text</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Text
	 * @generated
	 */
	EClass getText();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.mylyn.docs.epub.ncx.Text#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Text#getMixed()
	 * @see #getText()
	 * @generated
	 */
	EAttribute getText_Mixed();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.Text#getClass_ <em>Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Class</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Text#getClass_()
	 * @see #getText()
	 * @generated
	 */
	EAttribute getText_Class();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.docs.epub.ncx.Text#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Text#getId()
	 * @see #getText()
	 * @generated
	 */
	EAttribute getText_Id();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.mylyn.docs.epub.ncx.BookStruct <em>Book Struct</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Book Struct</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.BookStruct
	 * @generated
	 */
	EEnum getBookStruct();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.mylyn.docs.epub.ncx.DefaultState <em>Default State</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Default State</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.DefaultState
	 * @generated
	 */
	EEnum getDefaultState();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.mylyn.docs.epub.ncx.DirType <em>Dir Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Dir Type</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.DirType
	 * @generated
	 */
	EEnum getDirType();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.mylyn.docs.epub.ncx.OverrideType <em>Override Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Override Type</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.OverrideType
	 * @generated
	 */
	EEnum getOverrideType();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.mylyn.docs.epub.ncx.Type <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Type</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.Type
	 * @generated
	 */
	EEnum getType();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.mylyn.docs.epub.ncx.BookStruct <em>Book Struct Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Book Struct Object</em>'.
	 * @see org.eclipse.mylyn.docs.epub.ncx.BookStruct
	 * @model instanceClass="org.eclipse.mylyn.docs.epub.ncx.BookStruct"
	 *        extendedMetaData="name='bookStruct_._type:Object' baseType='bookStruct_._type'"
	 * @generated
	 */
	EDataType getBookStructObject();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.emf.common.util.Enumerator <em>Default State Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Default State Object</em>'.
	 * @see org.eclipse.emf.common.util.Enumerator
	 * @model instanceClass="org.eclipse.emf.common.util.Enumerator"
	 *        extendedMetaData="name='defaultState_._type:Object' baseType='defaultState_._type'"
	 * @generated
	 */
	EDataType getDefaultStateObject();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.emf.common.util.Enumerator <em>Dir Type Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Dir Type Object</em>'.
	 * @see org.eclipse.emf.common.util.Enumerator
	 * @model instanceClass="org.eclipse.emf.common.util.Enumerator"
	 * @generated
	 */
	EDataType getDirTypeObject();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.emf.common.util.Enumerator <em>Override Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Override Object</em>'.
	 * @see org.eclipse.emf.common.util.Enumerator
	 * @model instanceClass="org.eclipse.emf.common.util.Enumerator"
	 * @generated
	 */
	EDataType getOverrideObject();

	/**
	 * Returns the meta object for data type '{@link java.lang.String <em>SMI Ltime Val</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>SMI Ltime Val</em>'.
	 * @see java.lang.String
	 * @model instanceClass="java.lang.String"
	 *        extendedMetaData="name='SMILtimeVal' baseType='http://www.eclipse.org/emf/2003/XMLType#string'"
	 * @generated
	 */
	EDataType getSMILtimeVal();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.emf.common.util.Enumerator <em>Type Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Type Object</em>'.
	 * @see org.eclipse.emf.common.util.Enumerator
	 * @model instanceClass="org.eclipse.emf.common.util.Enumerator"
	 * @generated
	 */
	EDataType getTypeObject();

	/**
	 * Returns the meta object for data type '{@link java.lang.String <em>URI</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>URI</em>'.
	 * @see java.lang.String
	 * @model instanceClass="java.lang.String"
	 *        extendedMetaData="name='URI' baseType='http://www.eclipse.org/emf/2003/XMLType#string'"
	 * @generated
	 */
	EDataType getURI();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.emf.common.util.Enumerator <em>Version Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Version Object</em>'.
	 * @see org.eclipse.emf.common.util.Enumerator
	 * @model instanceClass="org.eclipse.emf.common.util.Enumerator"
	 *        extendedMetaData="name='version_._type:Object' baseType='version_._type'"
	 * @generated
	 */
	EDataType getVersionObject();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	NCXFactory getNCXFactory();

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
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.AudioImpl <em>Audio</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.AudioImpl
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getAudio()
		 * @generated
		 */
		EClass AUDIO = eINSTANCE.getAudio();

		/**
		 * The meta object literal for the '<em><b>Class</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute AUDIO__CLASS = eINSTANCE.getAudio_Class();

		/**
		 * The meta object literal for the '<em><b>Clip Begin</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute AUDIO__CLIP_BEGIN = eINSTANCE.getAudio_ClipBegin();

		/**
		 * The meta object literal for the '<em><b>Clip End</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute AUDIO__CLIP_END = eINSTANCE.getAudio_ClipEnd();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute AUDIO__ID = eINSTANCE.getAudio_Id();

		/**
		 * The meta object literal for the '<em><b>Src</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute AUDIO__SRC = eINSTANCE.getAudio_Src();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.ContentImpl <em>Content</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.ContentImpl
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getContent()
		 * @generated
		 */
		EClass CONTENT = eINSTANCE.getContent();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTENT__ID = eINSTANCE.getContent_Id();

		/**
		 * The meta object literal for the '<em><b>Src</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTENT__SRC = eINSTANCE.getContent_Src();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.DocAuthorImpl <em>Doc Author</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.DocAuthorImpl
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getDocAuthor()
		 * @generated
		 */
		EClass DOC_AUTHOR = eINSTANCE.getDocAuthor();

		/**
		 * The meta object literal for the '<em><b>Text</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOC_AUTHOR__TEXT = eINSTANCE.getDocAuthor_Text();

		/**
		 * The meta object literal for the '<em><b>Audio</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOC_AUTHOR__AUDIO = eINSTANCE.getDocAuthor_Audio();

		/**
		 * The meta object literal for the '<em><b>Img</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOC_AUTHOR__IMG = eINSTANCE.getDocAuthor_Img();

		/**
		 * The meta object literal for the '<em><b>Dir</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DOC_AUTHOR__DIR = eINSTANCE.getDocAuthor_Dir();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DOC_AUTHOR__ID = eINSTANCE.getDocAuthor_Id();

		/**
		 * The meta object literal for the '<em><b>Lang</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DOC_AUTHOR__LANG = eINSTANCE.getDocAuthor_Lang();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.DocTitleImpl <em>Doc Title</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.DocTitleImpl
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getDocTitle()
		 * @generated
		 */
		EClass DOC_TITLE = eINSTANCE.getDocTitle();

		/**
		 * The meta object literal for the '<em><b>Text</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOC_TITLE__TEXT = eINSTANCE.getDocTitle_Text();

		/**
		 * The meta object literal for the '<em><b>Audio</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOC_TITLE__AUDIO = eINSTANCE.getDocTitle_Audio();

		/**
		 * The meta object literal for the '<em><b>Img</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOC_TITLE__IMG = eINSTANCE.getDocTitle_Img();

		/**
		 * The meta object literal for the '<em><b>Dir</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DOC_TITLE__DIR = eINSTANCE.getDocTitle_Dir();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DOC_TITLE__ID = eINSTANCE.getDocTitle_Id();

		/**
		 * The meta object literal for the '<em><b>Lang</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DOC_TITLE__LANG = eINSTANCE.getDocTitle_Lang();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.HeadImpl <em>Head</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.HeadImpl
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getHead()
		 * @generated
		 */
		EClass HEAD = eINSTANCE.getHead();

		/**
		 * The meta object literal for the '<em><b>Groups</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute HEAD__GROUPS = eINSTANCE.getHead_Groups();

		/**
		 * The meta object literal for the '<em><b>Smil Custom Tests</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference HEAD__SMIL_CUSTOM_TESTS = eINSTANCE.getHead_SmilCustomTests();

		/**
		 * The meta object literal for the '<em><b>Metas</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference HEAD__METAS = eINSTANCE.getHead_Metas();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.ImgImpl <em>Img</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.ImgImpl
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getImg()
		 * @generated
		 */
		EClass IMG = eINSTANCE.getImg();

		/**
		 * The meta object literal for the '<em><b>Class</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMG__CLASS = eINSTANCE.getImg_Class();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMG__ID = eINSTANCE.getImg_Id();

		/**
		 * The meta object literal for the '<em><b>Src</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMG__SRC = eINSTANCE.getImg_Src();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.MetaImpl <em>Meta</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.MetaImpl
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getMeta()
		 * @generated
		 */
		EClass META = eINSTANCE.getMeta();

		/**
		 * The meta object literal for the '<em><b>Content</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute META__CONTENT = eINSTANCE.getMeta_Content();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute META__NAME = eINSTANCE.getMeta_Name();

		/**
		 * The meta object literal for the '<em><b>Scheme</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute META__SCHEME = eINSTANCE.getMeta_Scheme();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavInfoImpl <em>Nav Info</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NavInfoImpl
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getNavInfo()
		 * @generated
		 */
		EClass NAV_INFO = eINSTANCE.getNavInfo();

		/**
		 * The meta object literal for the '<em><b>Text</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NAV_INFO__TEXT = eINSTANCE.getNavInfo_Text();

		/**
		 * The meta object literal for the '<em><b>Audio</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NAV_INFO__AUDIO = eINSTANCE.getNavInfo_Audio();

		/**
		 * The meta object literal for the '<em><b>Img</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NAV_INFO__IMG = eINSTANCE.getNavInfo_Img();

		/**
		 * The meta object literal for the '<em><b>Dir</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAV_INFO__DIR = eINSTANCE.getNavInfo_Dir();

		/**
		 * The meta object literal for the '<em><b>Lang</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAV_INFO__LANG = eINSTANCE.getNavInfo_Lang();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavLabelImpl <em>Nav Label</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NavLabelImpl
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getNavLabel()
		 * @generated
		 */
		EClass NAV_LABEL = eINSTANCE.getNavLabel();

		/**
		 * The meta object literal for the '<em><b>Text</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NAV_LABEL__TEXT = eINSTANCE.getNavLabel_Text();

		/**
		 * The meta object literal for the '<em><b>Audio</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NAV_LABEL__AUDIO = eINSTANCE.getNavLabel_Audio();

		/**
		 * The meta object literal for the '<em><b>Img</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NAV_LABEL__IMG = eINSTANCE.getNavLabel_Img();

		/**
		 * The meta object literal for the '<em><b>Dir</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAV_LABEL__DIR = eINSTANCE.getNavLabel_Dir();

		/**
		 * The meta object literal for the '<em><b>Lang</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAV_LABEL__LANG = eINSTANCE.getNavLabel_Lang();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavListImpl <em>Nav List</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NavListImpl
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getNavList()
		 * @generated
		 */
		EClass NAV_LIST = eINSTANCE.getNavList();

		/**
		 * The meta object literal for the '<em><b>Nav Infos</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NAV_LIST__NAV_INFOS = eINSTANCE.getNavList_NavInfos();

		/**
		 * The meta object literal for the '<em><b>Nav Labels</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NAV_LIST__NAV_LABELS = eINSTANCE.getNavList_NavLabels();

		/**
		 * The meta object literal for the '<em><b>Nav Targets</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NAV_LIST__NAV_TARGETS = eINSTANCE.getNavList_NavTargets();

		/**
		 * The meta object literal for the '<em><b>Class</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAV_LIST__CLASS = eINSTANCE.getNavList_Class();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAV_LIST__ID = eINSTANCE.getNavList_Id();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavMapImpl <em>Nav Map</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NavMapImpl
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getNavMap()
		 * @generated
		 */
		EClass NAV_MAP = eINSTANCE.getNavMap();

		/**
		 * The meta object literal for the '<em><b>Nav Infos</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NAV_MAP__NAV_INFOS = eINSTANCE.getNavMap_NavInfos();

		/**
		 * The meta object literal for the '<em><b>Nav Labels</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NAV_MAP__NAV_LABELS = eINSTANCE.getNavMap_NavLabels();

		/**
		 * The meta object literal for the '<em><b>Nav Points</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NAV_MAP__NAV_POINTS = eINSTANCE.getNavMap_NavPoints();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAV_MAP__ID = eINSTANCE.getNavMap_Id();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavPointImpl <em>Nav Point</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NavPointImpl
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getNavPoint()
		 * @generated
		 */
		EClass NAV_POINT = eINSTANCE.getNavPoint();

		/**
		 * The meta object literal for the '<em><b>Nav Labels</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NAV_POINT__NAV_LABELS = eINSTANCE.getNavPoint_NavLabels();

		/**
		 * The meta object literal for the '<em><b>Content</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NAV_POINT__CONTENT = eINSTANCE.getNavPoint_Content();

		/**
		 * The meta object literal for the '<em><b>Nav Points</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NAV_POINT__NAV_POINTS = eINSTANCE.getNavPoint_NavPoints();

		/**
		 * The meta object literal for the '<em><b>Class</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAV_POINT__CLASS = eINSTANCE.getNavPoint_Class();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAV_POINT__ID = eINSTANCE.getNavPoint_Id();

		/**
		 * The meta object literal for the '<em><b>Play Order</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAV_POINT__PLAY_ORDER = eINSTANCE.getNavPoint_PlayOrder();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.NavTargetImpl <em>Nav Target</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NavTargetImpl
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getNavTarget()
		 * @generated
		 */
		EClass NAV_TARGET = eINSTANCE.getNavTarget();

		/**
		 * The meta object literal for the '<em><b>Nav Labels</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NAV_TARGET__NAV_LABELS = eINSTANCE.getNavTarget_NavLabels();

		/**
		 * The meta object literal for the '<em><b>Content</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NAV_TARGET__CONTENT = eINSTANCE.getNavTarget_Content();

		/**
		 * The meta object literal for the '<em><b>Class</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAV_TARGET__CLASS = eINSTANCE.getNavTarget_Class();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAV_TARGET__ID = eINSTANCE.getNavTarget_Id();

		/**
		 * The meta object literal for the '<em><b>Play Order</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAV_TARGET__PLAY_ORDER = eINSTANCE.getNavTarget_PlayOrder();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAV_TARGET__VALUE = eINSTANCE.getNavTarget_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.NcxImpl <em>Ncx</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NcxImpl
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getNcx()
		 * @generated
		 */
		EClass NCX = eINSTANCE.getNcx();

		/**
		 * The meta object literal for the '<em><b>Head</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NCX__HEAD = eINSTANCE.getNcx_Head();

		/**
		 * The meta object literal for the '<em><b>Doc Title</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NCX__DOC_TITLE = eINSTANCE.getNcx_DocTitle();

		/**
		 * The meta object literal for the '<em><b>Doc Authors</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NCX__DOC_AUTHORS = eINSTANCE.getNcx_DocAuthors();

		/**
		 * The meta object literal for the '<em><b>Nav Map</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NCX__NAV_MAP = eINSTANCE.getNcx_NavMap();

		/**
		 * The meta object literal for the '<em><b>Page List</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NCX__PAGE_LIST = eINSTANCE.getNcx_PageList();

		/**
		 * The meta object literal for the '<em><b>Nav Lists</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NCX__NAV_LISTS = eINSTANCE.getNcx_NavLists();

		/**
		 * The meta object literal for the '<em><b>Dir</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NCX__DIR = eINSTANCE.getNcx_Dir();

		/**
		 * The meta object literal for the '<em><b>Lang</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NCX__LANG = eINSTANCE.getNcx_Lang();

		/**
		 * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NCX__VERSION = eINSTANCE.getNcx_Version();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.PageListImpl <em>Page List</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.PageListImpl
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getPageList()
		 * @generated
		 */
		EClass PAGE_LIST = eINSTANCE.getPageList();

		/**
		 * The meta object literal for the '<em><b>Nav Infos</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PAGE_LIST__NAV_INFOS = eINSTANCE.getPageList_NavInfos();

		/**
		 * The meta object literal for the '<em><b>Nav Labels</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PAGE_LIST__NAV_LABELS = eINSTANCE.getPageList_NavLabels();

		/**
		 * The meta object literal for the '<em><b>Page Targets</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PAGE_LIST__PAGE_TARGETS = eINSTANCE.getPageList_PageTargets();

		/**
		 * The meta object literal for the '<em><b>Class</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGE_LIST__CLASS = eINSTANCE.getPageList_Class();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGE_LIST__ID = eINSTANCE.getPageList_Id();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.PageTargetImpl <em>Page Target</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.PageTargetImpl
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getPageTarget()
		 * @generated
		 */
		EClass PAGE_TARGET = eINSTANCE.getPageTarget();

		/**
		 * The meta object literal for the '<em><b>Nav Labels</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PAGE_TARGET__NAV_LABELS = eINSTANCE.getPageTarget_NavLabels();

		/**
		 * The meta object literal for the '<em><b>Content</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PAGE_TARGET__CONTENT = eINSTANCE.getPageTarget_Content();

		/**
		 * The meta object literal for the '<em><b>Class</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGE_TARGET__CLASS = eINSTANCE.getPageTarget_Class();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGE_TARGET__ID = eINSTANCE.getPageTarget_Id();

		/**
		 * The meta object literal for the '<em><b>Play Order</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGE_TARGET__PLAY_ORDER = eINSTANCE.getPageTarget_PlayOrder();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGE_TARGET__TYPE = eINSTANCE.getPageTarget_Type();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGE_TARGET__VALUE = eINSTANCE.getPageTarget_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.SmilCustomTestImpl <em>Smil Custom Test</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.SmilCustomTestImpl
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getSmilCustomTest()
		 * @generated
		 */
		EClass SMIL_CUSTOM_TEST = eINSTANCE.getSmilCustomTest();

		/**
		 * The meta object literal for the '<em><b>Book Struct</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SMIL_CUSTOM_TEST__BOOK_STRUCT = eINSTANCE.getSmilCustomTest_BookStruct();

		/**
		 * The meta object literal for the '<em><b>Default State</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SMIL_CUSTOM_TEST__DEFAULT_STATE = eINSTANCE.getSmilCustomTest_DefaultState();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SMIL_CUSTOM_TEST__ID = eINSTANCE.getSmilCustomTest_Id();

		/**
		 * The meta object literal for the '<em><b>Override</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SMIL_CUSTOM_TEST__OVERRIDE = eINSTANCE.getSmilCustomTest_Override();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.impl.TextImpl <em>Text</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.TextImpl
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getText()
		 * @generated
		 */
		EClass TEXT = eINSTANCE.getText();

		/**
		 * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TEXT__MIXED = eINSTANCE.getText_Mixed();

		/**
		 * The meta object literal for the '<em><b>Class</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TEXT__CLASS = eINSTANCE.getText_Class();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TEXT__ID = eINSTANCE.getText_Id();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.BookStruct <em>Book Struct</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.BookStruct
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getBookStruct()
		 * @generated
		 */
		EEnum BOOK_STRUCT = eINSTANCE.getBookStruct();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.DefaultState <em>Default State</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.DefaultState
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getDefaultState()
		 * @generated
		 */
		EEnum DEFAULT_STATE = eINSTANCE.getDefaultState();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.DirType <em>Dir Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.DirType
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getDirType()
		 * @generated
		 */
		EEnum DIR_TYPE = eINSTANCE.getDirType();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.OverrideType <em>Override Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.OverrideType
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getOverrideType()
		 * @generated
		 */
		EEnum OVERRIDE_TYPE = eINSTANCE.getOverrideType();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.docs.epub.ncx.Type <em>Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.Type
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getType()
		 * @generated
		 */
		EEnum TYPE = eINSTANCE.getType();

		/**
		 * The meta object literal for the '<em>Book Struct Object</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.docs.epub.ncx.BookStruct
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getBookStructObject()
		 * @generated
		 */
		EDataType BOOK_STRUCT_OBJECT = eINSTANCE.getBookStructObject();

		/**
		 * The meta object literal for the '<em>Default State Object</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.common.util.Enumerator
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getDefaultStateObject()
		 * @generated
		 */
		EDataType DEFAULT_STATE_OBJECT = eINSTANCE.getDefaultStateObject();

		/**
		 * The meta object literal for the '<em>Dir Type Object</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.common.util.Enumerator
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getDirTypeObject()
		 * @generated
		 */
		EDataType DIR_TYPE_OBJECT = eINSTANCE.getDirTypeObject();

		/**
		 * The meta object literal for the '<em>Override Object</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.common.util.Enumerator
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getOverrideObject()
		 * @generated
		 */
		EDataType OVERRIDE_OBJECT = eINSTANCE.getOverrideObject();

		/**
		 * The meta object literal for the '<em>SMI Ltime Val</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.lang.String
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getSMILtimeVal()
		 * @generated
		 */
		EDataType SMI_LTIME_VAL = eINSTANCE.getSMILtimeVal();

		/**
		 * The meta object literal for the '<em>Type Object</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.common.util.Enumerator
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getTypeObject()
		 * @generated
		 */
		EDataType TYPE_OBJECT = eINSTANCE.getTypeObject();

		/**
		 * The meta object literal for the '<em>URI</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.lang.String
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getURI()
		 * @generated
		 */
		EDataType URI = eINSTANCE.getURI();

		/**
		 * The meta object literal for the '<em>Version Object</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.common.util.Enumerator
		 * @see org.eclipse.mylyn.docs.epub.ncx.impl.NCXPackageImpl#getVersionObject()
		 * @generated
		 */
		EDataType VERSION_OBJECT = eINSTANCE.getVersionObject();

	}

} //NCXPackage

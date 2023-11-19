/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.ncx.impl;

import org.eclipse.emf.common.util.Enumerator;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

import org.eclipse.mylyn.docs.epub.ncx.Audio;
import org.eclipse.mylyn.docs.epub.ncx.BookStruct;
import org.eclipse.mylyn.docs.epub.ncx.Content;
import org.eclipse.mylyn.docs.epub.ncx.DefaultState;
import org.eclipse.mylyn.docs.epub.ncx.DirType;
import org.eclipse.mylyn.docs.epub.ncx.DocAuthor;
import org.eclipse.mylyn.docs.epub.ncx.DocTitle;
import org.eclipse.mylyn.docs.epub.ncx.Head;
import org.eclipse.mylyn.docs.epub.ncx.Img;
import org.eclipse.mylyn.docs.epub.ncx.Meta;
import org.eclipse.mylyn.docs.epub.ncx.NCXFactory;
import org.eclipse.mylyn.docs.epub.ncx.NCXPackage;
import org.eclipse.mylyn.docs.epub.ncx.NavInfo;
import org.eclipse.mylyn.docs.epub.ncx.NavLabel;
import org.eclipse.mylyn.docs.epub.ncx.NavList;
import org.eclipse.mylyn.docs.epub.ncx.NavMap;
import org.eclipse.mylyn.docs.epub.ncx.NavPoint;
import org.eclipse.mylyn.docs.epub.ncx.NavTarget;
import org.eclipse.mylyn.docs.epub.ncx.Ncx;
import org.eclipse.mylyn.docs.epub.ncx.OverrideType;
import org.eclipse.mylyn.docs.epub.ncx.PageList;
import org.eclipse.mylyn.docs.epub.ncx.PageTarget;
import org.eclipse.mylyn.docs.epub.ncx.SmilCustomTest;
import org.eclipse.mylyn.docs.epub.ncx.Text;
import org.eclipse.mylyn.docs.epub.ncx.Type;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class NCXPackageImpl extends EPackageImpl implements NCXPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass audioEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass contentEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass docAuthorEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass docTitleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass headEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass imgEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass metaEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass navInfoEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass navLabelEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass navListEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass navMapEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass navPointEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass navTargetEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass ncxEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass pageListEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass pageTargetEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass smilCustomTestEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass textEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum bookStructEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum defaultStateEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum dirTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum overrideTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum typeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType bookStructObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType defaultStateObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType dirTypeObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType overrideObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType smiLtimeValEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType typeObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType uriEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType versionObjectEDataType = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private NCXPackageImpl() {
		super(eNS_URI, NCXFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link NCXPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static NCXPackage init() {
		if (isInited) return (NCXPackage)EPackage.Registry.INSTANCE.getEPackage(NCXPackage.eNS_URI);

		// Obtain or create and register package
		NCXPackageImpl theNCXPackage = (NCXPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof NCXPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new NCXPackageImpl());

		isInited = true;

		// Initialize simple dependencies
		XMLTypePackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theNCXPackage.createPackageContents();

		// Initialize created meta-data
		theNCXPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theNCXPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(NCXPackage.eNS_URI, theNCXPackage);
		return theNCXPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAudio() {
		return audioEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAudio_Class() {
		return (EAttribute)audioEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAudio_ClipBegin() {
		return (EAttribute)audioEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAudio_ClipEnd() {
		return (EAttribute)audioEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAudio_Id() {
		return (EAttribute)audioEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAudio_Src() {
		return (EAttribute)audioEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getContent() {
		return contentEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getContent_Id() {
		return (EAttribute)contentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getContent_Src() {
		return (EAttribute)contentEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDocAuthor() {
		return docAuthorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDocAuthor_Text() {
		return (EReference)docAuthorEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDocAuthor_Audio() {
		return (EReference)docAuthorEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDocAuthor_Img() {
		return (EReference)docAuthorEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDocAuthor_Dir() {
		return (EAttribute)docAuthorEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDocAuthor_Id() {
		return (EAttribute)docAuthorEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDocAuthor_Lang() {
		return (EAttribute)docAuthorEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDocTitle() {
		return docTitleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDocTitle_Text() {
		return (EReference)docTitleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDocTitle_Audio() {
		return (EReference)docTitleEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDocTitle_Img() {
		return (EReference)docTitleEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDocTitle_Dir() {
		return (EAttribute)docTitleEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDocTitle_Id() {
		return (EAttribute)docTitleEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDocTitle_Lang() {
		return (EAttribute)docTitleEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getHead() {
		return headEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getHead_Groups() {
		return (EAttribute)headEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getHead_SmilCustomTests() {
		return (EReference)headEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getHead_Metas() {
		return (EReference)headEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getImg() {
		return imgEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getImg_Class() {
		return (EAttribute)imgEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getImg_Id() {
		return (EAttribute)imgEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getImg_Src() {
		return (EAttribute)imgEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMeta() {
		return metaEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMeta_Content() {
		return (EAttribute)metaEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMeta_Name() {
		return (EAttribute)metaEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMeta_Scheme() {
		return (EAttribute)metaEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNavInfo() {
		return navInfoEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNavInfo_Text() {
		return (EReference)navInfoEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNavInfo_Audio() {
		return (EReference)navInfoEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNavInfo_Img() {
		return (EReference)navInfoEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNavInfo_Dir() {
		return (EAttribute)navInfoEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNavInfo_Lang() {
		return (EAttribute)navInfoEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNavLabel() {
		return navLabelEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNavLabel_Text() {
		return (EReference)navLabelEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNavLabel_Audio() {
		return (EReference)navLabelEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNavLabel_Img() {
		return (EReference)navLabelEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNavLabel_Dir() {
		return (EAttribute)navLabelEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNavLabel_Lang() {
		return (EAttribute)navLabelEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNavList() {
		return navListEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNavList_NavInfos() {
		return (EReference)navListEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNavList_NavLabels() {
		return (EReference)navListEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNavList_NavTargets() {
		return (EReference)navListEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNavList_Class() {
		return (EAttribute)navListEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNavList_Id() {
		return (EAttribute)navListEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNavMap() {
		return navMapEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNavMap_NavInfos() {
		return (EReference)navMapEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNavMap_NavLabels() {
		return (EReference)navMapEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNavMap_NavPoints() {
		return (EReference)navMapEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNavMap_Id() {
		return (EAttribute)navMapEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNavPoint() {
		return navPointEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNavPoint_NavLabels() {
		return (EReference)navPointEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNavPoint_Content() {
		return (EReference)navPointEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNavPoint_NavPoints() {
		return (EReference)navPointEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNavPoint_Class() {
		return (EAttribute)navPointEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNavPoint_Id() {
		return (EAttribute)navPointEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNavPoint_PlayOrder() {
		return (EAttribute)navPointEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNavTarget() {
		return navTargetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNavTarget_NavLabels() {
		return (EReference)navTargetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNavTarget_Content() {
		return (EReference)navTargetEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNavTarget_Class() {
		return (EAttribute)navTargetEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNavTarget_Id() {
		return (EAttribute)navTargetEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNavTarget_PlayOrder() {
		return (EAttribute)navTargetEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNavTarget_Value() {
		return (EAttribute)navTargetEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNcx() {
		return ncxEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNcx_Head() {
		return (EReference)ncxEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNcx_DocTitle() {
		return (EReference)ncxEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNcx_DocAuthors() {
		return (EReference)ncxEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNcx_NavMap() {
		return (EReference)ncxEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNcx_PageList() {
		return (EReference)ncxEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNcx_NavLists() {
		return (EReference)ncxEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNcx_Dir() {
		return (EAttribute)ncxEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNcx_Lang() {
		return (EAttribute)ncxEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNcx_Version() {
		return (EAttribute)ncxEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getPageList() {
		return pageListEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getPageList_NavInfos() {
		return (EReference)pageListEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getPageList_NavLabels() {
		return (EReference)pageListEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getPageList_PageTargets() {
		return (EReference)pageListEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPageList_Class() {
		return (EAttribute)pageListEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPageList_Id() {
		return (EAttribute)pageListEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getPageTarget() {
		return pageTargetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getPageTarget_NavLabels() {
		return (EReference)pageTargetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getPageTarget_Content() {
		return (EReference)pageTargetEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPageTarget_Class() {
		return (EAttribute)pageTargetEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPageTarget_Id() {
		return (EAttribute)pageTargetEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPageTarget_PlayOrder() {
		return (EAttribute)pageTargetEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPageTarget_Type() {
		return (EAttribute)pageTargetEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPageTarget_Value() {
		return (EAttribute)pageTargetEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSmilCustomTest() {
		return smilCustomTestEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSmilCustomTest_BookStruct() {
		return (EAttribute)smilCustomTestEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSmilCustomTest_DefaultState() {
		return (EAttribute)smilCustomTestEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSmilCustomTest_Id() {
		return (EAttribute)smilCustomTestEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSmilCustomTest_Override() {
		return (EAttribute)smilCustomTestEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getText() {
		return textEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getText_Mixed() {
		return (EAttribute)textEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getText_Class() {
		return (EAttribute)textEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getText_Id() {
		return (EAttribute)textEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getBookStruct() {
		return bookStructEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getDefaultState() {
		return defaultStateEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getDirType() {
		return dirTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getOverrideType() {
		return overrideTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getType() {
		return typeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getBookStructObject() {
		return bookStructObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getDefaultStateObject() {
		return defaultStateObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getDirTypeObject() {
		return dirTypeObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getOverrideObject() {
		return overrideObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getSMILtimeVal() {
		return smiLtimeValEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getTypeObject() {
		return typeObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getURI() {
		return uriEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getVersionObject() {
		return versionObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NCXFactory getNCXFactory() {
		return (NCXFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		audioEClass = createEClass(AUDIO);
		createEAttribute(audioEClass, AUDIO__CLASS);
		createEAttribute(audioEClass, AUDIO__CLIP_BEGIN);
		createEAttribute(audioEClass, AUDIO__CLIP_END);
		createEAttribute(audioEClass, AUDIO__ID);
		createEAttribute(audioEClass, AUDIO__SRC);

		contentEClass = createEClass(CONTENT);
		createEAttribute(contentEClass, CONTENT__ID);
		createEAttribute(contentEClass, CONTENT__SRC);

		docAuthorEClass = createEClass(DOC_AUTHOR);
		createEReference(docAuthorEClass, DOC_AUTHOR__TEXT);
		createEReference(docAuthorEClass, DOC_AUTHOR__AUDIO);
		createEReference(docAuthorEClass, DOC_AUTHOR__IMG);
		createEAttribute(docAuthorEClass, DOC_AUTHOR__DIR);
		createEAttribute(docAuthorEClass, DOC_AUTHOR__ID);
		createEAttribute(docAuthorEClass, DOC_AUTHOR__LANG);

		docTitleEClass = createEClass(DOC_TITLE);
		createEReference(docTitleEClass, DOC_TITLE__TEXT);
		createEReference(docTitleEClass, DOC_TITLE__AUDIO);
		createEReference(docTitleEClass, DOC_TITLE__IMG);
		createEAttribute(docTitleEClass, DOC_TITLE__DIR);
		createEAttribute(docTitleEClass, DOC_TITLE__ID);
		createEAttribute(docTitleEClass, DOC_TITLE__LANG);

		headEClass = createEClass(HEAD);
		createEAttribute(headEClass, HEAD__GROUPS);
		createEReference(headEClass, HEAD__SMIL_CUSTOM_TESTS);
		createEReference(headEClass, HEAD__METAS);

		imgEClass = createEClass(IMG);
		createEAttribute(imgEClass, IMG__CLASS);
		createEAttribute(imgEClass, IMG__ID);
		createEAttribute(imgEClass, IMG__SRC);

		metaEClass = createEClass(META);
		createEAttribute(metaEClass, META__CONTENT);
		createEAttribute(metaEClass, META__NAME);
		createEAttribute(metaEClass, META__SCHEME);

		navInfoEClass = createEClass(NAV_INFO);
		createEReference(navInfoEClass, NAV_INFO__TEXT);
		createEReference(navInfoEClass, NAV_INFO__AUDIO);
		createEReference(navInfoEClass, NAV_INFO__IMG);
		createEAttribute(navInfoEClass, NAV_INFO__DIR);
		createEAttribute(navInfoEClass, NAV_INFO__LANG);

		navLabelEClass = createEClass(NAV_LABEL);
		createEReference(navLabelEClass, NAV_LABEL__TEXT);
		createEReference(navLabelEClass, NAV_LABEL__AUDIO);
		createEReference(navLabelEClass, NAV_LABEL__IMG);
		createEAttribute(navLabelEClass, NAV_LABEL__DIR);
		createEAttribute(navLabelEClass, NAV_LABEL__LANG);

		navListEClass = createEClass(NAV_LIST);
		createEReference(navListEClass, NAV_LIST__NAV_INFOS);
		createEReference(navListEClass, NAV_LIST__NAV_LABELS);
		createEReference(navListEClass, NAV_LIST__NAV_TARGETS);
		createEAttribute(navListEClass, NAV_LIST__CLASS);
		createEAttribute(navListEClass, NAV_LIST__ID);

		navMapEClass = createEClass(NAV_MAP);
		createEReference(navMapEClass, NAV_MAP__NAV_INFOS);
		createEReference(navMapEClass, NAV_MAP__NAV_LABELS);
		createEReference(navMapEClass, NAV_MAP__NAV_POINTS);
		createEAttribute(navMapEClass, NAV_MAP__ID);

		navPointEClass = createEClass(NAV_POINT);
		createEReference(navPointEClass, NAV_POINT__NAV_LABELS);
		createEReference(navPointEClass, NAV_POINT__CONTENT);
		createEReference(navPointEClass, NAV_POINT__NAV_POINTS);
		createEAttribute(navPointEClass, NAV_POINT__CLASS);
		createEAttribute(navPointEClass, NAV_POINT__ID);
		createEAttribute(navPointEClass, NAV_POINT__PLAY_ORDER);

		navTargetEClass = createEClass(NAV_TARGET);
		createEReference(navTargetEClass, NAV_TARGET__NAV_LABELS);
		createEReference(navTargetEClass, NAV_TARGET__CONTENT);
		createEAttribute(navTargetEClass, NAV_TARGET__CLASS);
		createEAttribute(navTargetEClass, NAV_TARGET__ID);
		createEAttribute(navTargetEClass, NAV_TARGET__PLAY_ORDER);
		createEAttribute(navTargetEClass, NAV_TARGET__VALUE);

		ncxEClass = createEClass(NCX);
		createEReference(ncxEClass, NCX__HEAD);
		createEReference(ncxEClass, NCX__DOC_TITLE);
		createEReference(ncxEClass, NCX__DOC_AUTHORS);
		createEReference(ncxEClass, NCX__NAV_MAP);
		createEReference(ncxEClass, NCX__PAGE_LIST);
		createEReference(ncxEClass, NCX__NAV_LISTS);
		createEAttribute(ncxEClass, NCX__DIR);
		createEAttribute(ncxEClass, NCX__LANG);
		createEAttribute(ncxEClass, NCX__VERSION);

		pageListEClass = createEClass(PAGE_LIST);
		createEReference(pageListEClass, PAGE_LIST__NAV_INFOS);
		createEReference(pageListEClass, PAGE_LIST__NAV_LABELS);
		createEReference(pageListEClass, PAGE_LIST__PAGE_TARGETS);
		createEAttribute(pageListEClass, PAGE_LIST__CLASS);
		createEAttribute(pageListEClass, PAGE_LIST__ID);

		pageTargetEClass = createEClass(PAGE_TARGET);
		createEReference(pageTargetEClass, PAGE_TARGET__NAV_LABELS);
		createEReference(pageTargetEClass, PAGE_TARGET__CONTENT);
		createEAttribute(pageTargetEClass, PAGE_TARGET__CLASS);
		createEAttribute(pageTargetEClass, PAGE_TARGET__ID);
		createEAttribute(pageTargetEClass, PAGE_TARGET__PLAY_ORDER);
		createEAttribute(pageTargetEClass, PAGE_TARGET__TYPE);
		createEAttribute(pageTargetEClass, PAGE_TARGET__VALUE);

		smilCustomTestEClass = createEClass(SMIL_CUSTOM_TEST);
		createEAttribute(smilCustomTestEClass, SMIL_CUSTOM_TEST__BOOK_STRUCT);
		createEAttribute(smilCustomTestEClass, SMIL_CUSTOM_TEST__DEFAULT_STATE);
		createEAttribute(smilCustomTestEClass, SMIL_CUSTOM_TEST__ID);
		createEAttribute(smilCustomTestEClass, SMIL_CUSTOM_TEST__OVERRIDE);

		textEClass = createEClass(TEXT);
		createEAttribute(textEClass, TEXT__MIXED);
		createEAttribute(textEClass, TEXT__CLASS);
		createEAttribute(textEClass, TEXT__ID);

		// Create enums
		bookStructEEnum = createEEnum(BOOK_STRUCT);
		defaultStateEEnum = createEEnum(DEFAULT_STATE);
		dirTypeEEnum = createEEnum(DIR_TYPE);
		overrideTypeEEnum = createEEnum(OVERRIDE_TYPE);
		typeEEnum = createEEnum(TYPE);

		// Create data types
		bookStructObjectEDataType = createEDataType(BOOK_STRUCT_OBJECT);
		defaultStateObjectEDataType = createEDataType(DEFAULT_STATE_OBJECT);
		dirTypeObjectEDataType = createEDataType(DIR_TYPE_OBJECT);
		overrideObjectEDataType = createEDataType(OVERRIDE_OBJECT);
		smiLtimeValEDataType = createEDataType(SMI_LTIME_VAL);
		typeObjectEDataType = createEDataType(TYPE_OBJECT);
		uriEDataType = createEDataType(URI);
		versionObjectEDataType = createEDataType(VERSION_OBJECT);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		XMLTypePackage theXMLTypePackage = (XMLTypePackage)EPackage.Registry.INSTANCE.getEPackage(XMLTypePackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes and features; add operations and parameters
		initEClass(audioEClass, Audio.class, "Audio", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAudio_Class(), theXMLTypePackage.getAnySimpleType(), "class", null, 0, 1, Audio.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAudio_ClipBegin(), this.getSMILtimeVal(), "clipBegin", null, 1, 1, Audio.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAudio_ClipEnd(), this.getSMILtimeVal(), "clipEnd", null, 1, 1, Audio.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAudio_Id(), theXMLTypePackage.getID(), "id", null, 0, 1, Audio.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAudio_Src(), this.getURI(), "src", null, 1, 1, Audio.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(contentEClass, Content.class, "Content", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getContent_Id(), theXMLTypePackage.getID(), "id", null, 0, 1, Content.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getContent_Src(), this.getURI(), "src", null, 1, 1, Content.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(docAuthorEClass, DocAuthor.class, "DocAuthor", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDocAuthor_Text(), this.getText(), null, "text", null, 1, 1, DocAuthor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDocAuthor_Audio(), this.getAudio(), null, "audio", null, 0, 1, DocAuthor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDocAuthor_Img(), this.getImg(), null, "img", null, 0, 1, DocAuthor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDocAuthor_Dir(), this.getDirType(), "dir", null, 0, 1, DocAuthor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDocAuthor_Id(), theXMLTypePackage.getID(), "id", null, 0, 1, DocAuthor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDocAuthor_Lang(), theXMLTypePackage.getNMTOKEN(), "lang", null, 0, 1, DocAuthor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(docTitleEClass, DocTitle.class, "DocTitle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDocTitle_Text(), this.getText(), null, "text", null, 1, 1, DocTitle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDocTitle_Audio(), this.getAudio(), null, "audio", null, 0, 1, DocTitle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDocTitle_Img(), this.getImg(), null, "img", null, 0, 1, DocTitle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDocTitle_Dir(), this.getDirType(), "dir", null, 0, 1, DocTitle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDocTitle_Id(), theXMLTypePackage.getID(), "id", null, 0, 1, DocTitle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDocTitle_Lang(), theXMLTypePackage.getNMTOKEN(), "lang", null, 0, 1, DocTitle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(headEClass, Head.class, "Head", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getHead_Groups(), ecorePackage.getEFeatureMapEntry(), "groups", null, 0, -1, Head.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getHead_SmilCustomTests(), this.getSmilCustomTest(), null, "smilCustomTests", null, 0, -1, Head.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getHead_Metas(), this.getMeta(), null, "metas", null, 0, -1, Head.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(imgEClass, Img.class, "Img", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getImg_Class(), theXMLTypePackage.getAnySimpleType(), "class", null, 0, 1, Img.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getImg_Id(), theXMLTypePackage.getID(), "id", null, 0, 1, Img.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getImg_Src(), this.getURI(), "src", null, 1, 1, Img.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(metaEClass, Meta.class, "Meta", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getMeta_Content(), theXMLTypePackage.getAnySimpleType(), "content", null, 1, 1, Meta.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMeta_Name(), theXMLTypePackage.getAnySimpleType(), "name", null, 1, 1, Meta.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMeta_Scheme(), theXMLTypePackage.getAnySimpleType(), "scheme", null, 0, 1, Meta.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(navInfoEClass, NavInfo.class, "NavInfo", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getNavInfo_Text(), this.getText(), null, "text", null, 0, 1, NavInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNavInfo_Audio(), this.getAudio(), null, "audio", null, 0, 1, NavInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNavInfo_Img(), this.getImg(), null, "img", null, 0, 1, NavInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNavInfo_Dir(), this.getDirType(), "dir", null, 0, 1, NavInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNavInfo_Lang(), theXMLTypePackage.getNMTOKEN(), "lang", null, 0, 1, NavInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(navLabelEClass, NavLabel.class, "NavLabel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getNavLabel_Text(), this.getText(), null, "text", null, 0, 1, NavLabel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNavLabel_Audio(), this.getAudio(), null, "audio", null, 0, 1, NavLabel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNavLabel_Img(), this.getImg(), null, "img", null, 0, 1, NavLabel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNavLabel_Dir(), this.getDirType(), "dir", null, 0, 1, NavLabel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNavLabel_Lang(), theXMLTypePackage.getNMTOKEN(), "lang", null, 0, 1, NavLabel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(navListEClass, NavList.class, "NavList", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getNavList_NavInfos(), this.getNavInfo(), null, "navInfos", null, 0, -1, NavList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNavList_NavLabels(), this.getNavLabel(), null, "navLabels", null, 1, -1, NavList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNavList_NavTargets(), this.getNavTarget(), null, "navTargets", null, 1, -1, NavList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNavList_Class(), theXMLTypePackage.getAnySimpleType(), "class", null, 0, 1, NavList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNavList_Id(), theXMLTypePackage.getID(), "id", null, 0, 1, NavList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(navMapEClass, NavMap.class, "NavMap", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getNavMap_NavInfos(), this.getNavInfo(), null, "navInfos", null, 0, -1, NavMap.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNavMap_NavLabels(), this.getNavLabel(), null, "navLabels", null, 0, -1, NavMap.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNavMap_NavPoints(), this.getNavPoint(), null, "navPoints", null, 1, -1, NavMap.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNavMap_Id(), theXMLTypePackage.getID(), "id", null, 0, 1, NavMap.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(navPointEClass, NavPoint.class, "NavPoint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getNavPoint_NavLabels(), this.getNavLabel(), null, "navLabels", null, 1, -1, NavPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNavPoint_Content(), this.getContent(), null, "content", null, 1, 1, NavPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNavPoint_NavPoints(), this.getNavPoint(), null, "navPoints", null, 0, -1, NavPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNavPoint_Class(), theXMLTypePackage.getAnySimpleType(), "class", null, 0, 1, NavPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNavPoint_Id(), theXMLTypePackage.getID(), "id", null, 1, 1, NavPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNavPoint_PlayOrder(), ecorePackage.getEInt(), "playOrder", null, 1, 1, NavPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(navTargetEClass, NavTarget.class, "NavTarget", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getNavTarget_NavLabels(), this.getNavLabel(), null, "navLabels", null, 1, -1, NavTarget.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNavTarget_Content(), this.getContent(), null, "content", null, 1, 1, NavTarget.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNavTarget_Class(), theXMLTypePackage.getAnySimpleType(), "class", null, 0, 1, NavTarget.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNavTarget_Id(), theXMLTypePackage.getID(), "id", null, 1, 1, NavTarget.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNavTarget_PlayOrder(), theXMLTypePackage.getAnySimpleType(), "playOrder", null, 1, 1, NavTarget.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNavTarget_Value(), theXMLTypePackage.getAnySimpleType(), "value", null, 0, 1, NavTarget.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(ncxEClass, Ncx.class, "Ncx", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getNcx_Head(), this.getHead(), null, "head", null, 1, 1, Ncx.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNcx_DocTitle(), this.getDocTitle(), null, "docTitle", null, 1, 1, Ncx.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNcx_DocAuthors(), this.getDocAuthor(), null, "docAuthors", null, 0, -1, Ncx.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNcx_NavMap(), this.getNavMap(), null, "navMap", null, 1, 1, Ncx.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNcx_PageList(), this.getPageList(), null, "pageList", null, 0, 1, Ncx.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNcx_NavLists(), this.getNavList(), null, "navLists", null, 0, -1, Ncx.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNcx_Dir(), this.getDirType(), "dir", null, 0, 1, Ncx.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNcx_Lang(), theXMLTypePackage.getNMTOKEN(), "lang", null, 0, 1, Ncx.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNcx_Version(), ecorePackage.getEString(), "version", "2005-1", 1, 1, Ncx.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(pageListEClass, PageList.class, "PageList", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getPageList_NavInfos(), this.getNavInfo(), null, "navInfos", null, 0, -1, PageList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPageList_NavLabels(), this.getNavLabel(), null, "navLabels", null, 0, -1, PageList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPageList_PageTargets(), this.getPageTarget(), null, "pageTargets", null, 1, -1, PageList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPageList_Class(), theXMLTypePackage.getAnySimpleType(), "class", null, 0, 1, PageList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPageList_Id(), theXMLTypePackage.getID(), "id", null, 0, 1, PageList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(pageTargetEClass, PageTarget.class, "PageTarget", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getPageTarget_NavLabels(), this.getNavLabel(), null, "navLabels", null, 1, -1, PageTarget.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPageTarget_Content(), this.getContent(), null, "content", null, 1, 1, PageTarget.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPageTarget_Class(), theXMLTypePackage.getAnySimpleType(), "class", null, 0, 1, PageTarget.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPageTarget_Id(), theXMLTypePackage.getID(), "id", null, 0, 1, PageTarget.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPageTarget_PlayOrder(), theXMLTypePackage.getAnySimpleType(), "playOrder", null, 1, 1, PageTarget.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPageTarget_Type(), this.getType(), "type", null, 1, 1, PageTarget.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPageTarget_Value(), theXMLTypePackage.getAnySimpleType(), "value", null, 0, 1, PageTarget.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(smilCustomTestEClass, SmilCustomTest.class, "SmilCustomTest", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSmilCustomTest_BookStruct(), this.getBookStruct(), "bookStruct", null, 0, 1, SmilCustomTest.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSmilCustomTest_DefaultState(), this.getDefaultState(), "defaultState", "false", 0, 1, SmilCustomTest.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSmilCustomTest_Id(), theXMLTypePackage.getID(), "id", null, 1, 1, SmilCustomTest.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSmilCustomTest_Override(), this.getOverrideType(), "override", "hidden", 0, 1, SmilCustomTest.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(textEClass, Text.class, "Text", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getText_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, Text.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getText_Class(), theXMLTypePackage.getAnySimpleType(), "class", null, 0, 1, Text.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getText_Id(), theXMLTypePackage.getID(), "id", null, 0, 1, Text.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(bookStructEEnum, BookStruct.class, "BookStruct");
		addEEnumLiteral(bookStructEEnum, BookStruct.PAGENUMBER);
		addEEnumLiteral(bookStructEEnum, BookStruct.NOTE);
		addEEnumLiteral(bookStructEEnum, BookStruct.NOTEREFERENCE);
		addEEnumLiteral(bookStructEEnum, BookStruct.ANNOTATION);
		addEEnumLiteral(bookStructEEnum, BookStruct.LINENUMBER);
		addEEnumLiteral(bookStructEEnum, BookStruct.OPTIONALSIDEBAR);
		addEEnumLiteral(bookStructEEnum, BookStruct.OPTIONALPRODUCERNOTE);

		initEEnum(defaultStateEEnum, DefaultState.class, "DefaultState");
		addEEnumLiteral(defaultStateEEnum, DefaultState.TRUE);
		addEEnumLiteral(defaultStateEEnum, DefaultState.FALSE);

		initEEnum(dirTypeEEnum, DirType.class, "DirType");
		addEEnumLiteral(dirTypeEEnum, DirType.LTR);
		addEEnumLiteral(dirTypeEEnum, DirType.RTL);

		initEEnum(overrideTypeEEnum, OverrideType.class, "OverrideType");
		addEEnumLiteral(overrideTypeEEnum, OverrideType.VISIBLE);
		addEEnumLiteral(overrideTypeEEnum, OverrideType.HIDDEN);

		initEEnum(typeEEnum, Type.class, "Type");
		addEEnumLiteral(typeEEnum, Type.FRONT);
		addEEnumLiteral(typeEEnum, Type.NORMAL);
		addEEnumLiteral(typeEEnum, Type.SPECIAL);

		// Initialize data types
		initEDataType(bookStructObjectEDataType, BookStruct.class, "BookStructObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS);
		initEDataType(defaultStateObjectEDataType, Enumerator.class, "DefaultStateObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(dirTypeObjectEDataType, Enumerator.class, "DirTypeObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(overrideObjectEDataType, Enumerator.class, "OverrideObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(smiLtimeValEDataType, String.class, "SMILtimeVal", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(typeObjectEDataType, Enumerator.class, "TypeObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(uriEDataType, String.class, "URI", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(versionObjectEDataType, Enumerator.class, "VersionObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http:///org/eclipse/emf/ecore/util/ExtendedMetaData
		createExtendedMetaDataAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createExtendedMetaDataAnnotations() {
		String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";	
		addAnnotation
		  (getAudio_Class(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "class"
		   });	
		addAnnotation
		  (getAudio_ClipBegin(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "clipBegin"
		   });	
		addAnnotation
		  (getAudio_ClipEnd(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "clipEnd"
		   });	
		addAnnotation
		  (getAudio_Id(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "id"
		   });	
		addAnnotation
		  (getAudio_Src(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "src"
		   });	
		addAnnotation
		  (bookStructEEnum, 
		   source, 
		   new String[] {
			 "name", "bookStruct_._type"
		   });	
		addAnnotation
		  (bookStructObjectEDataType, 
		   source, 
		   new String[] {
			 "name", "bookStruct_._type:Object",
			 "baseType", "bookStruct_._type"
		   });	
		addAnnotation
		  (getContent_Id(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "id"
		   });	
		addAnnotation
		  (getContent_Src(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "src"
		   });	
		addAnnotation
		  (defaultStateObjectEDataType, 
		   source, 
		   new String[] {
			 "name", "defaultState_._type:Object",
			 "baseType", "defaultState_._type"
		   });	
		addAnnotation
		  (getDocAuthor_Text(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "text",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getDocAuthor_Audio(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "audio",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getDocAuthor_Img(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "img",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getDocAuthor_Dir(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "dir"
		   });	
		addAnnotation
		  (getDocAuthor_Id(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "id"
		   });	
		addAnnotation
		  (getDocAuthor_Lang(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "lang",
			 "namespace", "http://www.w3.org/XML/1998/namespace"
		   });	
		addAnnotation
		  (getDocTitle_Text(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "text",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getDocTitle_Audio(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "audio",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getDocTitle_Img(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "img",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getDocTitle_Dir(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "dir"
		   });	
		addAnnotation
		  (getDocTitle_Id(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "id"
		   });	
		addAnnotation
		  (getDocTitle_Lang(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "lang",
			 "namespace", "http://www.w3.org/XML/1998/namespace"
		   });	
		addAnnotation
		  (getHead_Groups(), 
		   source, 
		   new String[] {
			 "kind", "group",
			 "name", "group:0"
		   });	
		addAnnotation
		  (getHead_SmilCustomTests(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "smilCustomTest",
			 "namespace", "##targetNamespace",
			 "group", "#group:0"
		   });	
		addAnnotation
		  (getHead_Metas(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "meta",
			 "namespace", "##targetNamespace",
			 "group", "#group:0"
		   });	
		addAnnotation
		  (getImg_Class(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "class"
		   });	
		addAnnotation
		  (getImg_Id(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "id"
		   });	
		addAnnotation
		  (getImg_Src(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "src"
		   });	
		addAnnotation
		  (getMeta_Content(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "content"
		   });	
		addAnnotation
		  (getMeta_Name(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "name"
		   });	
		addAnnotation
		  (getMeta_Scheme(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "scheme"
		   });	
		addAnnotation
		  (getNavInfo_Text(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "text",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNavInfo_Audio(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "audio",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNavInfo_Img(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "img",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNavInfo_Dir(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "dir"
		   });	
		addAnnotation
		  (getNavInfo_Lang(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "lang",
			 "namespace", "http://www.w3.org/XML/1998/namespace"
		   });	
		addAnnotation
		  (getNavLabel_Text(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "text",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNavLabel_Audio(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "audio",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNavLabel_Img(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "img",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNavLabel_Dir(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "dir"
		   });	
		addAnnotation
		  (getNavLabel_Lang(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "lang",
			 "namespace", "http://www.w3.org/XML/1998/namespace"
		   });	
		addAnnotation
		  (getNavList_NavInfos(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "navInfo",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNavList_NavLabels(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "navLabel",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNavList_NavTargets(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "navTarget",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNavList_Class(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "class"
		   });	
		addAnnotation
		  (getNavList_Id(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "id"
		   });	
		addAnnotation
		  (getNavMap_NavInfos(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "navInfo",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNavMap_NavLabels(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "navLabel",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNavMap_NavPoints(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "navPoint",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNavMap_Id(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "id"
		   });	
		addAnnotation
		  (getNavPoint_NavLabels(), 
		   source, 
		   new String[] {
			 "name", "navLabel",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNavPoint_Content(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "content",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNavPoint_NavPoints(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "navPoint",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNavPoint_Class(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "class"
		   });	
		addAnnotation
		  (getNavPoint_Id(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "id"
		   });	
		addAnnotation
		  (getNavPoint_PlayOrder(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "playOrder"
		   });	
		addAnnotation
		  (getNavTarget_NavLabels(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "navLabel",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNavTarget_Content(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "content",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNavTarget_Class(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "class"
		   });	
		addAnnotation
		  (getNavTarget_Id(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "id"
		   });	
		addAnnotation
		  (getNavTarget_PlayOrder(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "playOrder"
		   });	
		addAnnotation
		  (getNavTarget_Value(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "value"
		   });	
		addAnnotation
		  (ncxEClass, 
		   source, 
		   new String[] {
			 "name", "ncx"
		   });	
		addAnnotation
		  (getNcx_Head(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "head",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNcx_DocTitle(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "docTitle",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNcx_DocAuthors(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "docAuthor",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNcx_NavMap(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "navMap",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNcx_PageList(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "pageList",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNcx_NavLists(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "navList",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getNcx_Dir(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "dir"
		   });	
		addAnnotation
		  (getNcx_Lang(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "lang",
			 "namespace", "http://www.w3.org/XML/1998/namespace"
		   });	
		addAnnotation
		  (getNcx_Version(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "version"
		   });	
		addAnnotation
		  (getPageList_NavInfos(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "navInfo",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getPageList_NavLabels(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "navLabel",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getPageList_PageTargets(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "pageTarget",
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getPageList_Class(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "class"
		   });	
		addAnnotation
		  (getPageList_Id(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "id"
		   });	
		addAnnotation
		  (getPageTarget_NavLabels(), 
		   source, 
		   new String[] {
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (getPageTarget_Content(), 
		   source, 
		   new String[] {
			 "namespace", "##targetNamespace"
		   });	
		addAnnotation
		  (smiLtimeValEDataType, 
		   source, 
		   new String[] {
			 "name", "SMILtimeVal",
			 "baseType", "http://www.eclipse.org/emf/2003/XMLType#string"
		   });	
		addAnnotation
		  (textEClass, 
		   source, 
		   new String[] {
			 "kind", "mixed"
		   });	
		addAnnotation
		  (getText_Mixed(), 
		   source, 
		   new String[] {
			 "kind", "elementWildcard",
			 "name", ":mixed"
		   });	
		addAnnotation
		  (getText_Class(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "class"
		   });	
		addAnnotation
		  (getText_Id(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "id"
		   });	
		addAnnotation
		  (uriEDataType, 
		   source, 
		   new String[] {
			 "name", "URI",
			 "baseType", "http://www.eclipse.org/emf/2003/XMLType#string"
		   });	
		addAnnotation
		  (versionObjectEDataType, 
		   source, 
		   new String[] {
			 "name", "version_._type:Object",
			 "baseType", "version_._type"
		   });
	}

} //NCXPackageImpl

/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.docs.epub.ncx.util;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;
import org.eclipse.mylyn.docs.epub.ncx.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.mylyn.docs.epub.ncx.NCXPackage
 * @generated
 */
public class NCXSwitch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static NCXPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NCXSwitch() {
		if (modelPackage == null) {
			modelPackage = NCXPackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case NCXPackage.AUDIO: {
				Audio audio = (Audio)theEObject;
				T result = caseAudio(audio);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case NCXPackage.CONTENT: {
				Content content = (Content)theEObject;
				T result = caseContent(content);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case NCXPackage.DOC_AUTHOR: {
				DocAuthor docAuthor = (DocAuthor)theEObject;
				T result = caseDocAuthor(docAuthor);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case NCXPackage.DOC_TITLE: {
				DocTitle docTitle = (DocTitle)theEObject;
				T result = caseDocTitle(docTitle);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case NCXPackage.HEAD: {
				Head head = (Head)theEObject;
				T result = caseHead(head);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case NCXPackage.IMG: {
				Img img = (Img)theEObject;
				T result = caseImg(img);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case NCXPackage.META: {
				Meta meta = (Meta)theEObject;
				T result = caseMeta(meta);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case NCXPackage.NAV_INFO: {
				NavInfo navInfo = (NavInfo)theEObject;
				T result = caseNavInfo(navInfo);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case NCXPackage.NAV_LABEL: {
				NavLabel navLabel = (NavLabel)theEObject;
				T result = caseNavLabel(navLabel);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case NCXPackage.NAV_LIST: {
				NavList navList = (NavList)theEObject;
				T result = caseNavList(navList);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case NCXPackage.NAV_MAP: {
				NavMap navMap = (NavMap)theEObject;
				T result = caseNavMap(navMap);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case NCXPackage.NAV_POINT: {
				NavPoint navPoint = (NavPoint)theEObject;
				T result = caseNavPoint(navPoint);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case NCXPackage.NAV_TARGET: {
				NavTarget navTarget = (NavTarget)theEObject;
				T result = caseNavTarget(navTarget);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case NCXPackage.NCX: {
				Ncx ncx = (Ncx)theEObject;
				T result = caseNcx(ncx);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case NCXPackage.PAGE_LIST: {
				PageList pageList = (PageList)theEObject;
				T result = casePageList(pageList);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case NCXPackage.PAGE_TARGET: {
				PageTarget pageTarget = (PageTarget)theEObject;
				T result = casePageTarget(pageTarget);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case NCXPackage.SMIL_CUSTOM_TEST: {
				SmilCustomTest smilCustomTest = (SmilCustomTest)theEObject;
				T result = caseSmilCustomTest(smilCustomTest);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case NCXPackage.TEXT: {
				Text text = (Text)theEObject;
				T result = caseText(text);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Audio</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Audio</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAudio(Audio object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Content</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Content</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseContent(Content object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Doc Author</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Doc Author</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDocAuthor(DocAuthor object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Doc Title</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Doc Title</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDocTitle(DocTitle object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Head</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Head</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseHead(Head object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Img</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Img</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseImg(Img object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Meta</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Meta</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMeta(Meta object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Nav Info</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Nav Info</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNavInfo(NavInfo object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Nav Label</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Nav Label</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNavLabel(NavLabel object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Nav List</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Nav List</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNavList(NavList object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Nav Map</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Nav Map</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNavMap(NavMap object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Nav Point</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Nav Point</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNavPoint(NavPoint object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Nav Target</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Nav Target</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNavTarget(NavTarget object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Ncx</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Ncx</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNcx(Ncx object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Page List</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Page List</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePageList(PageList object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Page Target</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Page Target</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePageTarget(PageTarget object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Smil Custom Test</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Smil Custom Test</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSmilCustomTest(SmilCustomTest object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Text</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Text</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseText(Text object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(EObject object) {
		return null;
	}

} //NCXSwitch

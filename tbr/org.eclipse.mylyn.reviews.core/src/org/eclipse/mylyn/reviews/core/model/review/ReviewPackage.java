/**
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 */
package org.eclipse.mylyn.reviews.core.model.review;

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
 * @see org.eclipse.mylyn.reviews.core.model.review.ReviewFactory
 * @model kind="package"
 * @generated
 */
public interface ReviewPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "review";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "org.eclipse.mylyn.reviews";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ReviewPackage eINSTANCE = org.eclipse.mylyn.reviews.core.model.review.impl.ReviewPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.core.model.review.impl.ReviewImpl <em>Review</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.reviews.core.model.review.impl.ReviewImpl
	 * @see org.eclipse.mylyn.reviews.core.model.review.impl.ReviewPackageImpl#getReview()
	 * @generated
	 */
	int REVIEW = 0;

	/**
	 * The feature id for the '<em><b>Result</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REVIEW__RESULT = 0;

	/**
	 * The feature id for the '<em><b>Scope</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REVIEW__SCOPE = 1;

	/**
	 * The number of structural features of the '<em>Review</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REVIEW_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.core.model.review.impl.ReviewResultImpl <em>Result</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.reviews.core.model.review.impl.ReviewResultImpl
	 * @see org.eclipse.mylyn.reviews.core.model.review.impl.ReviewPackageImpl#getReviewResult()
	 * @generated
	 */
	int REVIEW_RESULT = 1;

	/**
	 * The feature id for the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REVIEW_RESULT__TEXT = 0;

	/**
	 * The feature id for the '<em><b>Rating</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REVIEW_RESULT__RATING = 1;

	/**
	 * The feature id for the '<em><b>Reviewer</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REVIEW_RESULT__REVIEWER = 2;

	/**
	 * The number of structural features of the '<em>Result</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REVIEW_RESULT_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.core.model.review.impl.ScopeItemImpl <em>Scope Item</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.reviews.core.model.review.impl.ScopeItemImpl
	 * @see org.eclipse.mylyn.reviews.core.model.review.impl.ReviewPackageImpl#getScopeItem()
	 * @generated
	 */
	int SCOPE_ITEM = 3;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCOPE_ITEM__AUTHOR = 0;

	/**
	 * The number of structural features of the '<em>Scope Item</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCOPE_ITEM_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.core.model.review.impl.PatchImpl <em>Patch</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.reviews.core.model.review.impl.PatchImpl
	 * @see org.eclipse.mylyn.reviews.core.model.review.impl.ReviewPackageImpl#getPatch()
	 * @generated
	 */
	int PATCH = 2;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATCH__AUTHOR = SCOPE_ITEM__AUTHOR;

	/**
	 * The feature id for the '<em><b>Contents</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATCH__CONTENTS = SCOPE_ITEM_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Creation Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATCH__CREATION_DATE = SCOPE_ITEM_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>File Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATCH__FILE_NAME = SCOPE_ITEM_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Patch</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PATCH_FEATURE_COUNT = SCOPE_ITEM_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link org.eclipse.mylyn.reviews.core.model.review.Rating <em>Rating</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.mylyn.reviews.core.model.review.Rating
	 * @see org.eclipse.mylyn.reviews.core.model.review.impl.ReviewPackageImpl#getRating()
	 * @generated
	 */
	int RATING = 4;

	/**
	 * The meta object id for the '<em>IFile Patch2</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.compare.patch.IFilePatch2
	 * @see org.eclipse.mylyn.reviews.core.model.review.impl.ReviewPackageImpl#getIFilePatch2()
	 * @generated
	 */
	int IFILE_PATCH2 = 5;

	/**
	 * The meta object id for the '<em>IProgress Monitor</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.core.runtime.IProgressMonitor
	 * @see org.eclipse.mylyn.reviews.core.model.review.impl.ReviewPackageImpl#getIProgressMonitor()
	 * @generated
	 */
	int IPROGRESS_MONITOR = 6;


	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.review.Review <em>Review</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Review</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.review.Review
	 * @generated
	 */
	EClass getReview();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.mylyn.reviews.core.model.review.Review#getResult <em>Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Result</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.review.Review#getResult()
	 * @see #getReview()
	 * @generated
	 */
	EReference getReview_Result();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.mylyn.reviews.core.model.review.Review#getScope <em>Scope</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Scope</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.review.Review#getScope()
	 * @see #getReview()
	 * @generated
	 */
	EReference getReview_Scope();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.review.ReviewResult <em>Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Result</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.review.ReviewResult
	 * @generated
	 */
	EClass getReviewResult();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.review.ReviewResult#getText <em>Text</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Text</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.review.ReviewResult#getText()
	 * @see #getReviewResult()
	 * @generated
	 */
	EAttribute getReviewResult_Text();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.review.ReviewResult#getRating <em>Rating</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rating</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.review.ReviewResult#getRating()
	 * @see #getReviewResult()
	 * @generated
	 */
	EAttribute getReviewResult_Rating();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.review.ReviewResult#getReviewer <em>Reviewer</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Reviewer</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.review.ReviewResult#getReviewer()
	 * @see #getReviewResult()
	 * @generated
	 */
	EAttribute getReviewResult_Reviewer();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.review.Patch <em>Patch</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Patch</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.review.Patch
	 * @generated
	 */
	EClass getPatch();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.review.Patch#getContents <em>Contents</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Contents</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.review.Patch#getContents()
	 * @see #getPatch()
	 * @generated
	 */
	EAttribute getPatch_Contents();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.review.Patch#getCreationDate <em>Creation Date</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Creation Date</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.review.Patch#getCreationDate()
	 * @see #getPatch()
	 * @generated
	 */
	EAttribute getPatch_CreationDate();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.review.Patch#getFileName <em>File Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>File Name</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.review.Patch#getFileName()
	 * @see #getPatch()
	 * @generated
	 */
	EAttribute getPatch_FileName();

	/**
	 * Returns the meta object for class '{@link org.eclipse.mylyn.reviews.core.model.review.ScopeItem <em>Scope Item</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Scope Item</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.review.ScopeItem
	 * @generated
	 */
	EClass getScopeItem();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.mylyn.reviews.core.model.review.ScopeItem#getAuthor <em>Author</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Author</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.review.ScopeItem#getAuthor()
	 * @see #getScopeItem()
	 * @generated
	 */
	EAttribute getScopeItem_Author();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.mylyn.reviews.core.model.review.Rating <em>Rating</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Rating</em>'.
	 * @see org.eclipse.mylyn.reviews.core.model.review.Rating
	 * @generated
	 */
	EEnum getRating();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.compare.patch.IFilePatch2 <em>IFile Patch2</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>IFile Patch2</em>'.
	 * @see org.eclipse.compare.patch.IFilePatch2
	 * @model instanceClass="org.eclipse.compare.patch.IFilePatch2"
	 * @generated
	 */
	EDataType getIFilePatch2();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.core.runtime.IProgressMonitor <em>IProgress Monitor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>IProgress Monitor</em>'.
	 * @see org.eclipse.core.runtime.IProgressMonitor
	 * @model instanceClass="org.eclipse.core.runtime.IProgressMonitor"
	 * @generated
	 */
	EDataType getIProgressMonitor();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ReviewFactory getReviewFactory();

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
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.core.model.review.impl.ReviewImpl <em>Review</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.reviews.core.model.review.impl.ReviewImpl
		 * @see org.eclipse.mylyn.reviews.core.model.review.impl.ReviewPackageImpl#getReview()
		 * @generated
		 */
		EClass REVIEW = eINSTANCE.getReview();

		/**
		 * The meta object literal for the '<em><b>Result</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REVIEW__RESULT = eINSTANCE.getReview_Result();

		/**
		 * The meta object literal for the '<em><b>Scope</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REVIEW__SCOPE = eINSTANCE.getReview_Scope();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.core.model.review.impl.ReviewResultImpl <em>Result</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.reviews.core.model.review.impl.ReviewResultImpl
		 * @see org.eclipse.mylyn.reviews.core.model.review.impl.ReviewPackageImpl#getReviewResult()
		 * @generated
		 */
		EClass REVIEW_RESULT = eINSTANCE.getReviewResult();

		/**
		 * The meta object literal for the '<em><b>Text</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REVIEW_RESULT__TEXT = eINSTANCE.getReviewResult_Text();

		/**
		 * The meta object literal for the '<em><b>Rating</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REVIEW_RESULT__RATING = eINSTANCE.getReviewResult_Rating();

		/**
		 * The meta object literal for the '<em><b>Reviewer</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REVIEW_RESULT__REVIEWER = eINSTANCE.getReviewResult_Reviewer();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.core.model.review.impl.PatchImpl <em>Patch</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.reviews.core.model.review.impl.PatchImpl
		 * @see org.eclipse.mylyn.reviews.core.model.review.impl.ReviewPackageImpl#getPatch()
		 * @generated
		 */
		EClass PATCH = eINSTANCE.getPatch();

		/**
		 * The meta object literal for the '<em><b>Contents</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PATCH__CONTENTS = eINSTANCE.getPatch_Contents();

		/**
		 * The meta object literal for the '<em><b>Creation Date</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PATCH__CREATION_DATE = eINSTANCE.getPatch_CreationDate();

		/**
		 * The meta object literal for the '<em><b>File Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PATCH__FILE_NAME = eINSTANCE.getPatch_FileName();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.core.model.review.impl.ScopeItemImpl <em>Scope Item</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.reviews.core.model.review.impl.ScopeItemImpl
		 * @see org.eclipse.mylyn.reviews.core.model.review.impl.ReviewPackageImpl#getScopeItem()
		 * @generated
		 */
		EClass SCOPE_ITEM = eINSTANCE.getScopeItem();

		/**
		 * The meta object literal for the '<em><b>Author</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCOPE_ITEM__AUTHOR = eINSTANCE.getScopeItem_Author();

		/**
		 * The meta object literal for the '{@link org.eclipse.mylyn.reviews.core.model.review.Rating <em>Rating</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.mylyn.reviews.core.model.review.Rating
		 * @see org.eclipse.mylyn.reviews.core.model.review.impl.ReviewPackageImpl#getRating()
		 * @generated
		 */
		EEnum RATING = eINSTANCE.getRating();

		/**
		 * The meta object literal for the '<em>IFile Patch2</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.compare.patch.IFilePatch2
		 * @see org.eclipse.mylyn.reviews.core.model.review.impl.ReviewPackageImpl#getIFilePatch2()
		 * @generated
		 */
		EDataType IFILE_PATCH2 = eINSTANCE.getIFilePatch2();

		/**
		 * The meta object literal for the '<em>IProgress Monitor</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.core.runtime.IProgressMonitor
		 * @see org.eclipse.mylyn.reviews.core.model.review.impl.ReviewPackageImpl#getIProgressMonitor()
		 * @generated
		 */
		EDataType IPROGRESS_MONITOR = eINSTANCE.getIProgressMonitor();

	}

} //ReviewPackage

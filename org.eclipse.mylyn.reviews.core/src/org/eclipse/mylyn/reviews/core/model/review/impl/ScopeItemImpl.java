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
package org.eclipse.mylyn.reviews.core.model.review.impl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.mylyn.reviews.core.model.review.ReviewPackage;
import org.eclipse.mylyn.reviews.core.model.review.ScopeItem;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Scope Item</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.mylyn.reviews.core.model.review.impl.ScopeItemImpl#getAuthor <em>Author</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ScopeItemImpl extends CDOObjectImpl implements ScopeItem {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ScopeItemImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewPackage.Literals.SCOPE_ITEM;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected int eStaticFeatureCount() {
		return 0;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAuthor() {
		return (String)eGet(ReviewPackage.Literals.SCOPE_ITEM__AUTHOR, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAuthor(String newAuthor) {
		eSet(ReviewPackage.Literals.SCOPE_ITEM__AUTHOR, newAuthor);
	}

} //ScopeItemImpl

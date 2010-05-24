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

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;

import org.eclipse.compare.patch.IFilePatch2;
import org.eclipse.compare.patch.PatchParser;
import org.eclipse.compare.patch.ReaderCreator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.mylyn.reviews.core.model.review.Patch;
import org.eclipse.mylyn.reviews.core.model.review.ReviewPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Patch</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>
 * {@link org.eclipse.mylyn.reviews.core.model.review.impl.PatchImpl#getContents
 * <em>Contents</em>}</li>
 * <li>
 * {@link org.eclipse.mylyn.reviews.core.model.review.impl.PatchImpl#getCreationDate
 * <em>Creation Date</em>}</li>
 * <li>
 * {@link org.eclipse.mylyn.reviews.core.model.review.impl.PatchImpl#getFileName
 * <em>File Name</em>}</li>
 * </ul>
 * </p>
 *
 * @author Kilian Matt
 * @generated
 */
public class PatchImpl extends ScopeItemImpl implements Patch {
	/*
	 * owner* <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected PatchImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewPackage.Literals.PATCH;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getContents() {
		return (String) eGet(ReviewPackage.Literals.PATCH__CONTENTS, true);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setContents(String newContents) {
		eSet(ReviewPackage.Literals.PATCH__CONTENTS, newContents);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Date getCreationDate() {
		return (Date) eGet(ReviewPackage.Literals.PATCH__CREATION_DATE, true);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setCreationDate(Date newCreationDate) {
		eSet(ReviewPackage.Literals.PATCH__CREATION_DATE, newCreationDate);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getFileName() {
		return (String) eGet(ReviewPackage.Literals.PATCH__FILE_NAME, true);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setFileName(String newFileName) {
		eSet(ReviewPackage.Literals.PATCH__FILE_NAME, newFileName);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 */
	public EList<IFilePatch2> parse() {
		try {
			IFilePatch2[] patches = PatchParser.parsePatch(new ReaderCreator() {
				@Override
				public Reader createReader() throws CoreException {
					return new InputStreamReader(new ByteArrayInputStream(
							getContents().getBytes()));
				}
			});

			EList<IFilePatch2> list = new BasicEList<IFilePatch2>();

			for (IFilePatch2 patch : patches) {
				list.add(patch);
			}
			return list;
		} catch (Exception ex) {
			// TODO
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

} // PatchImpl

/**
 * Copyright (c) 2011, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.reviews.core.model;

import java.util.Comparator;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Indexed</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.mylyn.reviews.core.model.IIndexed#getIndex <em>Index</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public interface IIndexed extends EObject {

	public static final Comparator<IIndexed> COMPARATOR = new Comparator<IIndexed>() {

		public int compare(IIndexed o1, IIndexed o2) {
			return ((Long) o1.getIndex()).compareTo(o2.getIndex());
		}
	};

	/**
	 * Returns the value of the '<em><b>Index</b></em>' attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Index</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Index</em>' attribute.
	 * @generated
	 */
	long getIndex();

} // IIndexed

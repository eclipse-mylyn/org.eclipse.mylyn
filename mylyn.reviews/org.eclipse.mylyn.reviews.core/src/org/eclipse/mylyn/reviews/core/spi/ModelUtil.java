/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.core.spi;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class ModelUtil {

	private final static int PRIME = 31;

	public static int ecoreHash(EObject object) {
		return ecoreHash(0, object);
	}

	public static int ecoreHash(int current, EObject object) {
		return ecoreHash(current, object, new HashSet<EObject>());
	}

	private static int ecoreHash(int current, EObject object, Set<EObject> references) {
		if (object != null) {
			references.add(object);
			for (EAttribute attribute : object.eClass().getEAllAttributes()) {
				Object value = object.eGet(attribute);
				if (value != null) {
					current = PRIME * current + value.hashCode();
				}
			}
			for (EReference reference : object.eClass().getEAllReferences()) {
				Object value = object.eGet(reference);
				if (value instanceof EObject && !references.contains(value)) {
					current = ecoreHash(current, (EObject) value, references);
				}
			}
		}
		return current;
	}

}

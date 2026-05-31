/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.parser.css;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A selector that can select based on a set of delegates.
 *
 * @author David Green
 * @since 3.0
 */
public class CompositeSelector extends Selector {

	private final boolean and;

	private final List<Selector> delegates;

	public CompositeSelector(boolean and, List<Selector> delegates) {
		this.and = and;
		this.delegates = delegates;
	}

	@Override
	public boolean select(ElementInfo info) {
		if (and) {
			for (Selector selector : delegates) {
				if (!selector.select(info)) {
					return false;
				}
			}
			return true;
		} else {
			for (Selector selector : delegates) {
				if (selector.select(info)) {
					return true;
				}
			}
			return false;
		}
	}

	public boolean isAnd() {
		return and;
	}

	public List<Selector> getComponents() {
		return delegates;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CompositeSelector other)) {
			return false;
		}
		return and == other.and && Objects.equals(delegates, other.delegates);
	}

	@Override
	public int hashCode() {
		return Objects.hash(and, delegates);
	}

	@Override
	public String toString() {
		String separator = and ? "" : ", "; //$NON-NLS-1$//$NON-NLS-2$
		return delegates.stream().map(Selector::toString).collect(Collectors.joining(separator));
	}
}
/*******************************************************************************
 * Copyright (c) 2015 David Green.
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

package org.eclipse.mylyn.wikitext.commonmark.internal;

import org.apache.commons.lang3.Validate;

class LookAheadLineSequence extends LineSequence {

	private final ForwardLineSequence lineSequence;

	private Line currentLine;

	private final Line referenceLine;

	private int index;

	public LookAheadLineSequence(ForwardLineSequence lineSequence) {
		this.lineSequence = lineSequence;
		currentLine = lineSequence.getCurrentLine();
		referenceLine = currentLine;
		index = -1;
	}

	public LookAheadLineSequence(LookAheadLineSequence lookAheadLineSequence) {
		lineSequence = lookAheadLineSequence.lineSequence;
		currentLine = lookAheadLineSequence.currentLine;
		referenceLine = lookAheadLineSequence.referenceLine;
		index = lookAheadLineSequence.index;
	}

	@Override
	public Line getCurrentLine() {
		return currentLine;
	}

	@Override
	public Line getNextLine() {
		checkConcurrentModification();
		return lineSequence.getNextLine(index + 1);
	}

	@Override
	public void advance() {
		checkConcurrentModification();
		currentLine = getNextLine();
		++index;
	}

	private void checkConcurrentModification() {
		Validate.isTrue(referenceLine == lineSequence.getCurrentLine());
	}

	@Override
	public LineSequence lookAhead() {
		return new LookAheadLineSequence(this);
	}

}

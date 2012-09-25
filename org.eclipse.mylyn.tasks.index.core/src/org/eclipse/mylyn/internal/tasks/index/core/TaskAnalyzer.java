/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Red Hat, Inc. Bug 384685 - consume Apache Lucene 3.x
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.index.core;

import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

/**
 * An analyzer that is aware of task fields
 * 
 * @author David Green
 */
class TaskAnalyzer {

	public static PerFieldAnalyzerWrapper instance() {
		PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(Version.LUCENE_CURRENT));
		wrapper.addAnalyzer(TaskListIndex.FIELD_IDENTIFIER.getIndexKey(), new KeywordAnalyzer());
		wrapper.addAnalyzer(TaskListIndex.FIELD_TASK_KEY.getIndexKey(), new KeywordAnalyzer());
		wrapper.addAnalyzer(TaskListIndex.FIELD_REPOSITORY_URL.getIndexKey(), new KeywordAnalyzer());
		return wrapper;
	}
}

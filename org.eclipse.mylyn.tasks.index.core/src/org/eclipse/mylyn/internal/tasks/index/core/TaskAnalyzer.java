/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
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
class TaskAnalyzer extends PerFieldAnalyzerWrapper {

	public TaskAnalyzer() {
		super(new StandardAnalyzer(Version.LUCENE_CURRENT));
		addAnalyzer(TaskListIndex.IndexField.IDENTIFIER.fieldName(), new KeywordAnalyzer());
		addAnalyzer(TaskListIndex.IndexField.TASK_KEY.fieldName(), new KeywordAnalyzer());
		addAnalyzer(TaskListIndex.IndexField.REPOSITORY_URL.fieldName(), new KeywordAnalyzer());
	}

}

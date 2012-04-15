/*******************************************************************************
 * Copyright (c) 2011 Torkild U. Resheim.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.docs.epub.ant.core;

import java.io.File;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.eclipse.mylyn.docs.epub.core.EPUB;
import org.eclipse.mylyn.docs.epub.core.OPS2Publication;
import org.eclipse.mylyn.docs.epub.core.OPSPublication;
import org.eclipse.mylyn.docs.epub.opf.Role;
import org.eclipse.mylyn.docs.epub.opf.Type;

/**
 * Assemble a new EPUB.
 * 
 * 
 * @author Torkild U. Resheim
 * @ant.task name="epub" category="epub"
 */
public class EpubTask extends Task {

	private OPSPublication ops = null;

	private ArrayList<FileSetType> filesets = null;

	private TocType toc = null;

	private File workingFolder;

	private File epubFile;

	private AntLogger logger;

	public EpubTask() {
		super();
		try {
			logger = new AntLogger(this);
			ops = new OPS2Publication(logger);
			filesets = new ArrayList<FileSetType>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addConfiguredContributor(ContributorType item) {
		if (item.role == null) {
			ops.addContributor(item.id, item.lang, item.name, null, item.fileAs);
		} else {
			ops.addContributor(item.id, item.lang, item.name,
					Role.get(item.role), item.fileAs);
		}
	}

	public void addConfiguredCover(CoverType item) {
		ops.setCover(new File(item.image), item.value);
	}

	public void addConfiguredCoverage(CoverageType coverage) {
		ops.addCoverage(coverage.id, coverage.lang, coverage.text);
	}

	public void addConfiguredCreator(CreatorType item) {
		if (item.role == null) {
			ops.addCreator(item.id, item.lang, item.name, null, item.fileAs);
		} else {
			ops.addCreator(item.id, item.lang, item.name, Role.get(item.role),
					item.fileAs);
		}
	}

	public void addConfiguredDate(DateType item) {
		ops.addDate(item.id, item.date, item.event);
	}

	/**
	 * The FileSet sub-element is used to add EPUB artifacts that are not a part
	 * of the main text. This can be graphical items and styling (CSS).
	 * 
	 * @param fs
	 *            the fileset to add
	 */
	public void addConfiguredFileSet(FileSetType fs) {
		filesets.add(fs);
	}

	public void addConfiguredFormat(FormatType format) {
		ops.addFormat(format.id, format.text);
	}

	/**
	 * @ant.required
	 */
	public void addConfiguredIdentifier(IdentifierType identifier) {
		ops.addIdentifier(identifier.id, identifier.scheme, identifier.value);
	}

	/**
	 * @ant.required
	 */
	public void addConfiguredItem(ItemType item) {
		ops.addItem(item.id, item.lang, item.file, item.dest, item.type,
				item.spine, item.linear, item.noToc);
	}

	/**
	 * @ant.required
	 */
	public void addConfiguredLanguage(LanguageType language) {
		ops.addLanguage(language.id, language.code);
	}

	public void addConfiguredMeta(MetaType item) {
		ops.addMeta(item.name, item.content);
	}

	public void addConfiguredPublisher(PublisherType publisher) {
		ops.addPublisher(publisher.id, publisher.lang, publisher.text);
	}

	public void addConfiguredReference(ReferenceType reference) {
		Type type = Type.get(reference.type);
		if (type == null) {
			throw new BuildException("Unknown reference type " + reference.type);
		}
		ops.addReference(reference.href, reference.title, type);
	}

	public void addConfiguredRelation(RelationType relation) {
		ops.addRelation(relation.id, relation.lang, relation.text);
	}

	public void addConfiguredRights(RightsType rights) {
		ops.addRights(rights.id, rights.lang, rights.text);
	}

	public void addConfiguredSource(SourceType source) {
		ops.addSource(source.id, source.lang, source.text);
	}

	public void addConfiguredSubject(SubjectType subject) {
		ops.addSubject(subject.id, subject.lang, subject.text);
	}

	/**
	 * @ant.required
	 */
	public void addConfiguredTitle(TitleType title) {
		ops.addTitle(title.id, title.lang, title.text);
	}

	public void addConfiguredToc(TocType toc) {
		if (this.toc != null) {
			throw new BuildException(
					"Only one table of contents (toc) declaration is allowed.");
		}
		this.toc = toc;
	}

	public void addConfiguredType(
			org.eclipse.mylyn.docs.epub.ant.core.TypeType type) {
		ops.addType(type.id, type.text);
	}

	private void addFilesets() {
		for (FileSetType fs : filesets) {
			final File fsDir = fs.getDir(getProject());
			if (fsDir == null) {
				throw new BuildException(
						"File or Resource without directory or file specified");
			} else if (!fsDir.isDirectory()) {
				throw new BuildException("Directory does not exist:" + fsDir);
			}
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			String[] includedFiles = ds.getIncludedFiles();
			for (int i = 0; i < includedFiles.length; i++) {
				String filename = includedFiles[i].replace('\\', '/');
				filename = filename.substring(filename.lastIndexOf("/") + 1);
				File base = ds.getBasedir();
				File found = new File(base, includedFiles[i]);
				ops.addItem(null, fs.lang, found, fs.dest, null, false, true,
						false);
			}

		}

	}

	@Override
	public void execute() throws BuildException {
		// When running from within Eclipse, the project may not have been set
		if (getProject() == null) {
			Project project = new Project();
			setProject(project);
		}

		validate();
		addFilesets();
		if (toc != null) {
			if (toc.generate) {
				ops.setGenerateToc(true);
			} else if (toc.file != null) {
				ops.setTableOfContents(toc.file);
			}
		}
		try {
			EPUB epub = new EPUB(logger);
			epub.add(ops);
			if (workingFolder == null) {
				epub.pack(epubFile);
			} else {
				epub.pack(epubFile, workingFolder);
			}
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}

	/**
	 * @ant.not-required Automatically add referenced resources.
	 */
	public void setIncludeReferenced(boolean automatic) {
		ops.setIncludeReferencedResources(automatic);
	}

	/**
	 * 
	 * 
	 * @param file
	 *            path to the generated EPUB file.
	 */
	public void setFile(File file) {
		this.epubFile = file;
	}

	public void setIdentifierId(String identifierId) {
		ops.setIdentifierId(identifierId);
	}

	public void setWorkingFolder(File workingFolder) {
		this.workingFolder = workingFolder;
	}

	private void validate() {
	}


}

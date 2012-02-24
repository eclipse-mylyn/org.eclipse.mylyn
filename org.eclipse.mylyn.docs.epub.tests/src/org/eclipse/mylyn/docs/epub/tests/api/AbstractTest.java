package org.eclipse.mylyn.docs.epub.tests.api;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil.FeatureEList;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.mylyn.docs.epub.dc.DCType;
import org.eclipse.mylyn.docs.epub.dc.Identifier;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractTest extends TestCase {
	protected final File epubFile = new File("test" + File.separator + "test.epub");

	protected final File epubFolder = new File("test" + File.separator + "epub");

	protected static final EStructuralFeature TEXT = XMLTypePackage.eINSTANCE.getXMLTypeDocumentRoot_Text();

	@SuppressWarnings("rawtypes")
	public String getText(DCType identifier) {
		FeatureMap fm = identifier.getMixed();
			Object o = fm.get(TEXT, false);
			if (o instanceof FeatureEList) {
				if (((FeatureEList) o).size() > 0) {
					return ((FeatureEList) o).get(0).toString();
				}
			}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public String getText(Identifier element) {
		FeatureMap fm = element.getMixed();
		Object o = fm.get(TEXT, false);
		if (o instanceof FeatureEList) {
			if (((FeatureEList) o).size() > 0) {
				return ((FeatureEList) o).get(0).toString();
			}
		}
		return null;
	}
	/**
	 * @throws java.lang.Exception
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		if (epubFile.exists()) {
			epubFile.delete();
		}
		if (epubFolder.exists()) {
			deleteFolder(epubFolder);
		}
		epubFolder.mkdirs();
	}

	private boolean deleteFolder(File folder) {
		if (folder.isDirectory()) {
			String[] children = folder.list();
			for (int i = 0; i < children.length; i++) {
				boolean ok = deleteFolder(new File(folder, children[i]));
				if (!ok) {
					return false;
				}
			}
		}
		return folder.delete();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	@After
	public void tearDown() throws Exception {
		if (epubFolder.exists()) {
			deleteFolder(epubFolder);
		}
		if (epubFile.exists()) {
			epubFile.delete();
		}
	}

}

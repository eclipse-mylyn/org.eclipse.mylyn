Note that externalisation of strings in types contained within this bundle is
done using standard Java instead of NLS which is common elsewhere in Mylyn.
This is done to avoid having dependencies on OSGi since the Ant task for
handling EPUBs also use these types.

Require-Bundle: org.eclipse.core.runtime,
 org.eclipse.emf.ecore;bundle-version="2.5.0",
 org.eclipse.emf.ecore.xmi;bundle-version="2.5.0",
 org.eclipse.emf.common;bundle-version="2.5.0",
 org.eclipse.mylyn.wikitext.core;bundle-version="1.11.0",
 org.apache.tika.core;bundle-version="1.3.0",
 org.apache.tika.parsers;bundle-version="1.3.0"
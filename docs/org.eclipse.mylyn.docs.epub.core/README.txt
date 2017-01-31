Note that externalisation of strings in types contained within this bundle is
done using standard Java instead of NLS which is common elsewhere in Mylyn.
This is done to avoid having dependencies on OSGi since the Ant task for
handling EPUBs also use these types.
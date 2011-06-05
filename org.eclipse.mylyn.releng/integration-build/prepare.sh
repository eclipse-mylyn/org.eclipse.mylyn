#!/bin/bash -e

BRANCH=e_3_7_m_3_6_x

cat > keyring.password << EOF
.
EOF

cat > credentials.properties << EOF
user: tests@mylyn.eclipse.org
pass: mylyntest
EOF

if [ "$1" == "e4.1" ]; then
	cvs up -r e_4_0_m_3_x org.eclipse.mylyn.contexts/org.eclipse.mylyn.context.ui
	cvs up -A org.eclipse.mylyn.commons/org.eclipse.mylyn.monitor.tests
fi

#echo "Updating Mylyn Commons, Mylyn Context, Mylyn Incubator, Mylyn Tasks..."
#if [ ! -e org.eclipse.mylyn ]; then
#    cvs -d :pserver:anonymous@dev.eclipse.org:/cvsroot/mylyn co .
#else
#    cvs up -Pd
#fi

echo "Updating Mylyn Builds..."
if [ ! -e org.eclipse.mylyn.builds ]; then
    git clone git://git.eclipse.org/gitroot/mylyn/org.eclipse.mylyn.builds.git
    if [ -n "$BRANCH" ]; then
        cd org.eclipse.mylyn.builds
        git checkout -b $BRANCH origin/$BRANCH
        cd ..
    fi
else
    cd org.eclipse.mylyn.builds
    git pull
    cd ..
fi

echo "Updating Mylyn Docs..."
if [ ! -e org.eclipse.mylyn.docs ]; then
    git clone git://git.eclipse.org/gitroot/mylyn/org.eclipse.mylyn.docs.git
    if [ -n "$BRANCH" ]; then
        cd org.eclipse.mylyn.docs
        git checkout -b $BRANCH origin/$BRANCH
        cd ..
    fi
else
    cd org.eclipse.mylyn.docs
    git pull
    cd ..
fi

echo "Updating Mylyn Reviews..."
if [ ! -e org.eclipse.mylyn.reviews ]; then
    git clone git://git.eclipse.org/gitroot/mylyn/org.eclipse.mylyn.reviews.git
    if [ -n "$BRANCH" ]; then
        cd org.eclipse.mylyn.reviews
        git checkout -b $BRANCH origin/$BRANCH
        cd ..
    fi
else
    cd org.eclipse.mylyn.reviews
    git pull
    cd ..
fi

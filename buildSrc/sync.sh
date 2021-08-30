#!/usr/bin/env bash
function finish() { set +x ; }
trap finish EXIT

SCRIPTDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
LOCALDISPLAYDIR="${SCRIPTDIR#${SCRIPTDIR%/*/*}/}" # <projectRoot>/buildSrc

SYNCDIR_base="$SCRIPTDIR/../../../Deps/buildSrc"
SYNCDIR_display="../../Deps/buildSrc"
OFFSET="src/main/kotlin"
SYNCDIR="$SYNCDIR_base/$OFFSET"
for syncfilepath in $SYNCDIR/* ; do
    filename=${syncfilepath##*/}
    localfilepath=$SCRIPTDIR/$OFFSET/$filename
    localDisplay=$LOCALDISPLAYDIR/$OFFSET/$filename
    if [[ ! -f $localfilepath ]]; then echo "$localDisplay doesn't exist" ; continue ; fi
    if cmp --silent $syncfilepath $localfilepath ; then
        echo -e "${filename} is ${colGreen}equal${colReset}";
    else
        echo -e "opendiff ./buildSrc/$OFFSET/$filename  $SYNCDIR_display/$OFFSET/$filename -merge ./buildSrc/$OFFSET/$filename"
        /usr/bin/opendiff "${localfilepath}" "${syncfilepath}" -merge "$localfilepath"
    fi
done

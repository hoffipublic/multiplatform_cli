#!/usr/bin/env bash
function finish() { set +x ; }
trap finish EXIT

SCRIPTDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
LOCALDISPLAYDIR="${SCRIPTDIR#${SCRIPTDIR%/*/*}/}" # <projectRoot>/buildSrc

colRed="\033[0;31m"
colGreen="\033[0;32m"
colBold="\033[1m"
colReset="\033[0m"

SYNCDIR_base="$SCRIPTDIR/../../../Deps/buildSrc"
SYNCDIR_display="../../Deps/buildSrc"
OFFSETS=( "src/main/kotlin" "snippets" )
for OFFSET in "${OFFSETS[@]}" ; do
  SYNCDIR="$SYNCDIR_base/$OFFSET"
  for syncfilepath in $SYNCDIR/* ; do
      filename=${syncfilepath##*/}
      localfilepath=$SCRIPTDIR/$OFFSET/$filename
      localDisplay=$LOCALDISPLAYDIR/$OFFSET/$filename
      if [[ ! -f $localfilepath ]]; then echo "$localDisplay doesn't exist" ; continue ; fi
      if cmp --silent $syncfilepath $localfilepath ; then
          echo -e "is ${colGreen}equal${colReset}: ${OFFSET}/${colBold}${filename}${colReset}";
      else
          if [[ -n "$IS_WSL" || -n "$WSL_DISTRO_NAME" ]]; then
              echo -e "has ${colRed}diff${colReset}: ${OFFSET}/${colBold}${filename}${colReset} -> winmerge ./buildSrc/$OFFSET/$filename  $SYNCDIR_display/$OFFSET/$filename"
              "/mnt/c/Program Files/WinMerge/WinMergeU.exe" "$(wslpath -aw "${localfilepath}")" "$(wslpath -aw "${syncfilepath}")"
          elif [[  "$OSTYPE" = "msys" ]]; then
              echo -e "has ${colRed}diff${colReset}: ${OFFSET}/${colBold}${filename}${colReset} -> winmerge ./buildSrc/$OFFSET/$filename  $SYNCDIR_display/$OFFSET/$filename"
              /usr/bin/opendiff "${localfilepath}" "${syncfilepath}" -merge "$localfilepath"
          else
              echo -e "has ${colRed}diff${colReset}: ${OFFSET}/${colBold}${filename}${colReset} -> opendiff ./buildSrc/$OFFSET/$filename  $SYNCDIR_display/$OFFSET/$filename -merge ./buildSrc/$OFFSET/$filename"
              /usr/bin/opendiff "${localfilepath}" "${syncfilepath}" -merge "$localfilepath"
          fi
      fi
  done
done

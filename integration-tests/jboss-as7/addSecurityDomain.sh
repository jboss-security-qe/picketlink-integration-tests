#!/bin/bash

EAPZIP=$1

cd dist
unzip -q $EAPZIP
UNZIPDIST="`find -maxdepth 1 -type d -name jboss-\*`"

if [ ! -d "$UNZIPDIST" ]; then
    echo "Cannot find top-level directory with extracted EAP"
    exit 1
fi

cd "$UNZIPDIST/bin"
./standalone.sh &
sleep 30
./jboss-cli.sh -c --file=../../../add-security-domains.cli
cd ../..
zip -u "$EAPZIP" "$UNZIPDIST/standalone/configuration/standalone.xml"

# clean up
rm -rf "$UNZIPDIST"
cd ..

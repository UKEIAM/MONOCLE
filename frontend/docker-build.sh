#!/bin/bash

if [ -z "$VERSION" ]; then
  echo "VERSION must be specified"
  echo "example: VERSION=1.2.3 docker-build.sh"
  exit 1
fi

if [ "$TARGET" = "dev" ]; then
  ENV_FILE=".env.development"
  NPM_BUID_COMMAND="build:development"
elif [ "$TARGET" = "gwis" ]; then
  ENV_FILE=".env.staging"
  NPM_BUID_COMMAND="build:staging"
elif [ "$TARGET" = "kis" ]; then
  # FIXME remove lines once tested
  printf "\nWARNING: This target is untested/has not been deployed to yet.\n"
  printf "         Please check if outcome is as desired.\n\n"
  ENV_FILE=".env.production"
  NPM_BUID_COMMAND="build:production"
else
  echo "TARGET must be one of dev|gwis|kis"
  echo "example: TARGET=dev docker-build.sh" # dev/gwis/kis or dev/testing|staging/production ?
  exit 1
fi

BUILD_ARGS_REACT="$(grep '^REACT_APP_' $ENV_FILE | xargs -d '\r\n' | sed -e 's/^REACT_APP_/ --build-arg REACT_APP_/')"

# build image
echo docker build \
  -t dockerreg.iam-extern.de/mtb-gui:"$VERSION"-"$TARGET" \
  --build-arg NPM_BUID_COMMAND="$NPM_BUID_COMMAND" \
  $BUILD_ARGS_REACT \
  .
docker build \
  -t dockerreg.iam-extern.de/mtb-gui:"$VERSION"-"$TARGET" \
  --build-arg NPM_BUID_COMMAND="$NPM_BUID_COMMAND" \
  $BUILD_ARGS_REACT \
  .

# push image
if [ "$TARGET" != "dev" ]; then
  # FIXME check that develop is clean  if ... && output=$(git status --porcelain) && [ -z "$output" ]; then
  echo "WARNING: Make sure that your repo is clean"
  # FIXME check that branch is develop if ...  && [[ "$(git rev-parse --abbrev-ref HEAD)" = "develop" ]]; then
  echo "WARNING: Make sure your branch is develop"
  echo "To manualy push image run 'docker push dockerreg.iam-extern.de/mtb-gui:$VERSION-$TARGET'"
  #  docker push dockerreg.iam-extern.de/mtb-gui:"$VERSION"-"$TARGET" # FIXME uncomment once above checks are implemented
  # TODO push to kis via 'docker save <image> | ssh user@server -J jumper@jumphost1,jumper@jumphost2 docker load'?
fi

# deploy image ?

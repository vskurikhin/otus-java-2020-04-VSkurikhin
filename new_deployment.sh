#!/bin/sh
if [ "0$1" != "0" ]
then
  VERSION=$1
  ./gradlew clean build
  docker build -t gcr.io/${PROJECT_ID}/otus-java-2020-04-home-work-1:$VERSION .
  docker push gcr.io/${PROJECT_ID}/otus-java-2020-04-home-work-1:$VERSION
  kubectl delete deployment home-work-1 
  kubectl create deployment home-work-1 --image=gcr.io/${PROJECT_ID}/otus-java-2020-04-home-work-1:$VERSION
else
  echo "Usage:"
  echo "  $0 <version>"
fi


@echo off
@REM set title of command window
title %0

java -jar .\keiyaku-viewer-java-1.0.0.jar  --thin.offline=true --thin.root=libs

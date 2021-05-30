
@echo off
@REM set title of command window
title %0

SET JAVA_EXE="%JAVA_HOME%\bin\java.exe"
SET EXE_JAR=".\test-kotlin-boot-1.0.0.jar"
%JAVA_EXE% -jar %EXE_JAR%

if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%

exit /B %ERROR_CODE%

@echo off

@rem Maven�����s����ƒx���̂ŁA�ȒP�ɂ��������s����o�b�`�X�N���v�g
@rem mvn dependency:build-classpath�ŏo�͂����N���X�p�X�ꗗ�����ׂĐݒ肵�Ď��s����
@rem �R���p�C�����Ƀp�X��ʂ���kotlin�R���p�C�����G���[�ɂ��Ă��܂����̂�����̂�
@rem �����^�C����p�Ƃ���ȊO�ŕ����Ă���
@rem ���Ȃ݂ɁA�R���p�C�����ɕs�v�ȃN���X�p�X��ʂ��Ă��A�ʂ��Ȃ��Ă����܂�R���p�C�����s���x�͕ς��Ȃ�����


@rem required in compile time
SET RUNTIME_CLASSPATH=
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\org\jetbrains\kotlin\kotlin-stdlib-jdk8\1.4.21\kotlin-stdlib-jdk8-1.4.21.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\org\jetbrains\kotlin\kotlin-stdlib\1.4.21\kotlin-stdlib-1.4.21.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\netty\netty-codec-http2\4.1.63.Final\netty-codec-http2-4.1.63.Final.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\netty\netty-common\4.1.63.Final\netty-common-4.1.63.Final.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\netty\netty-buffer\4.1.63.Final\netty-buffer-4.1.63.Final.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\netty\netty-transport\4.1.63.Final\netty-transport-4.1.63.Final.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\netty\netty-resolver\4.1.63.Final\netty-resolver-4.1.63.Final.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\netty\netty-codec\4.1.63.Final\netty-codec-4.1.63.Final.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\netty\netty-handler\4.1.63.Final\netty-handler-4.1.63.Final.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\netty\netty-codec-http\4.1.63.Final\netty-codec-http-4.1.63.Final.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\netty\netty-transport-native-kqueue\4.1.63.Final\netty-transport-native-kqueue-4.1.63.Final.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\netty\netty-transport-native-unix-common\4.1.63.Final\netty-transport-native-unix-common-4.1.63.Final.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\netty\netty-transport-native-epoll\4.1.63.Final\netty-transport-native-epoll-4.1.63.Final.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\org\eclipse\jetty\jetty-alpn-openjdk8-client\9.4.31.v20200723\jetty-alpn-openjdk8-client-9.4.31.v20200723.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\org\eclipse\jetty\jetty-alpn-java-client\9.4.31.v20200723\jetty-alpn-java-client-9.4.31.v20200723.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\org\eclipse\jetty\http2\http2-client\9.4.31.v20200723\http2-client-9.4.31.v20200723.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\org\eclipse\jetty\http2\http2-common\9.4.31.v20200723\http2-common-9.4.31.v20200723.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\org\eclipse\jetty\http2\http2-hpack\9.4.31.v20200723\http2-hpack-9.4.31.v20200723.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\org\eclipse\jetty\jetty-alpn-client\9.4.31.v20200723\jetty-alpn-client-9.4.31.v20200723.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\org\eclipse\jetty\jetty-client\9.4.31.v20200723\jetty-client-9.4.31.v20200723.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\org\eclipse\jetty\jetty-http\9.4.31.v20200723\jetty-http-9.4.31.v20200723.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\org\eclipse\jetty\jetty-util\9.4.31.v20200723\jetty-util-9.4.31.v20200723.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\org\eclipse\jetty\jetty-io\9.4.31.v20200723\jetty-io-9.4.31.v20200723.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\org\eclipse\jetty\http2\http2-http-client-transport\9.4.31.v20200723\http2-http-client-transport-9.4.31.v20200723.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\net\bytebuddy\byte-buddy\1.11.0\byte-buddy-1.11.0.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\net\bytebuddy\byte-buddy-agent\1.11.0\byte-buddy-agent-1.11.0.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\org\objenesis\objenesis\3.2\objenesis-3.2.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\ktor\ktor-server-tests\1.5.4\ktor-server-tests-1.5.4.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\ktor\ktor-server-core\1.5.4\ktor-server-core-1.5.4.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\ktor\ktor-utils-jvm\1.5.4\ktor-utils-jvm-1.5.4.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\ktor\ktor-io-jvm\1.5.4\ktor-io-jvm-1.5.4.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\ktor\ktor-http-jvm\1.5.4\ktor-http-jvm-1.5.4.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\ktor\ktor-server-test-host\1.5.4\ktor-server-test-host-1.5.4.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\ktor\ktor-network-tls-jvm\1.5.4\ktor-network-tls-jvm-1.5.4.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\ktor\ktor-network-tls-certificates\1.5.4\ktor-network-tls-certificates-1.5.4.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\ktor\ktor-client-core-jvm\1.5.4\ktor-client-core-jvm-1.5.4.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\ktor\ktor-client-jetty\1.5.4\ktor-client-jetty-1.5.4.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\ktor\ktor-client-cio-jvm\1.5.4\ktor-client-cio-jvm-1.5.4.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\ktor\ktor-websockets\1.5.4\ktor-websockets-1.5.4.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\ktor\ktor-server-host-common\1.5.4\ktor-server-host-common-1.5.4.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\ktor\ktor-http-cio-jvm\1.5.4\ktor-http-cio-jvm-1.5.4.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\ktor\ktor-network-jvm\1.5.4\ktor-network-jvm-1.5.4.jar;
SET RUNTIME_CLASSPATH=%RUNTIME_CLASSPATH%;C:\java\m2repo\io\ktor\ktor-server-netty\1.5.4\ktor-server-netty-1.5.4.jar;

@rem ! no doubl/single quotation
SET CLASSPATH=
@rem not required
@rem SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\org\jetbrains\kotlin\kotlin-stdlib-common\1.4.21\kotlin-stdlib-common-1.4.21.jar;
@rem SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\org\jetbrains\kotlin\kotlin-stdlib-jdk7\1.4.21\kotlin-stdlib-jdk7-1.4.21.jar;
@rem SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\org\jetbrains\kotlin\kotlin-reflect\1.4.32\kotlin-reflect-1.4.32.jar;

@rem required in both compile and runtime
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\org\mockito\mockito-inline\3.10.0\mockito-inline-3.10.0.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\org\mockito\mockito-core\3.10.0\mockito-core-3.10.0.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\org\jetbrains\annotations\13.0\annotations-13.0.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\org\slf4j\slf4j-api\1.7.30\slf4j-api-1.7.30.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\org\jetbrains\kotlinx\kotlinx-coroutines-jdk8\1.4.3-native-mt\kotlinx-coroutines-jdk8-1.4.3-native-mt.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\org\jetbrains\kotlinx\kotlinx-coroutines-core-jvm\1.4.3-native-mt\kotlinx-coroutines-core-jvm-1.4.3-native-mt.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\org\eclipse\jetty\alpn\alpn-api\1.1.3.v20160715\alpn-api-1.1.3.v20160715.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\ch\qos\logback\logback-classic\1.2.1\logback-classic-1.2.1.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\ch\qos\logback\logback-core\1.2.1\logback-core-1.2.1.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\com\typesafe\config\1.3.1\config-1.3.1.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\org\jetbrains\kotlin\kotlin-test-junit\1.4.32\kotlin-test-junit-1.4.32.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\org\jetbrains\kotlin\kotlin-test\1.4.32\kotlin-test-1.4.32.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\org\jetbrains\kotlinx\kotlinx-coroutines-debug\1.4.3-native-mt\kotlinx-coroutines-debug-1.4.3-native-mt.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\net\java\dev\jna\jna\5.5.0\jna-5.5.0.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\net\java\dev\jna\jna-platform\5.5.0\jna-platform-5.5.0.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\io\ktor\ktor-thymeleaf\1.5.4\ktor-thymeleaf-1.5.4.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\org\thymeleaf\thymeleaf\3.0.12.RELEASE\thymeleaf-3.0.12.RELEASE.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\ognl\ognl\3.1.26\ognl-3.1.26.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\org\javassist\javassist\3.20.0-GA\javassist-3.20.0-GA.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\org\attoparser\attoparser\2.0.5.RELEASE\attoparser-2.0.5.RELEASE.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\org\unbescape\unbescape\1.1.6.RELEASE\unbescape-1.1.6.RELEASE.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\io\insert-koin\koin-ktor\3.0.2\koin-ktor-3.0.2.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\io\insert-koin\koin-core-jvm\3.0.2\koin-core-jvm-3.0.2.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\io\insert-koin\koin-core-ext\3.0.2\koin-core-ext-3.0.2.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\io\insert-koin\koin-test-junit4\3.0.2\koin-test-junit4-3.0.2.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\io\insert-koin\koin-test-jvm\3.0.2\koin-test-jvm-3.0.2.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\org\jetbrains\kotlin\kotlin-test-common\1.5.0\kotlin-test-common-1.5.0.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\org\jetbrains\kotlin\kotlin-test-annotations-common\1.5.0\kotlin-test-annotations-common-1.5.0.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\junit\junit\4.13.1\junit-4.13.1.jar;
SET CLASSPATH=%CLASSPATH%;C:\java\m2repo\org\hamcrest\hamcrest-core\1.3\hamcrest-core-1.3.jar;
SET CLASSPATH=%CLASSPATH%;.\target\kotlin-ic\test\classes;
SET CLASSPATH=%CLASSPATH%;.\target\kotlin-ic\compile\classes;
SET CLASSPATH=%CLASSPATH%;.\target\classes;


if "%1" == "compile" ( 
  echo "kotlinc compiling.."
  call kotlinc -classpath "%CLASSPATH%" test\TestMain.kt  -d target\kotlin-ic\test\classes
  echo "DONE compile"
)

@rem echo "running jUnit.. "
@rem java -classpath "%CLASSPATH%;%RUNTIME_CLASSPATH%"  org.junit.runner.JUnitCore TestMain

java -classpath "%CLASSPATH%;%RUNTIME_CLASSPATH%"  MainKt


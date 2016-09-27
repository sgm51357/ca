@echo off
SET KEYTOOL="C:\Program Files\Java\jre1.8.0_101\bin\keytool"
rem echo ---------------------rootCA.pem--------------------------
rem %KEYTOOL% -printcert -v -file response.crt
rem echo --------------------------keystore-------------------------------------
rem %KEYTOOL% -list -v -keystore keystore.p12 -storepass xxx123
rem echo --------------------------keystore_finished-------------------------------------
%KEYTOOL% -list -v -keystore keystore.p12 -storepass storePassword
pause
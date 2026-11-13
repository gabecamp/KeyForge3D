#!/usr/bin/env bash
# Builds KeyForge3D.apk without Gradle. Needs JDK + Android SDK (platform 34, build-tools 34.0.0).
set -euo pipefail
SDK=${ANDROID_HOME:-/opt/android-sdk}; BT=$SDK/build-tools/34.0.0; AJ=$SDK/platforms/android-34/android.jar
cd "$(dirname "$0")"; OUT=build; rm -rf $OUT; mkdir -p $OUT/{res,cls,dex}
$BT/aapt2 compile --dir app/res -o $OUT/res.zip
$BT/aapt2 link -o $OUT/base.apk -I $AJ --manifest app/AndroidManifest.xml -A app/assets $OUT/res.zip --min-sdk-version 29 --target-sdk-version 34
javac --release 8 -cp $AJ -d $OUT/cls $(find app/java -name '*.java') 2>&1 | grep -v warning || true
$BT/d8 --lib $AJ --min-api 29 --output $OUT/dex $(find $OUT/cls -name '*.class')
cp $OUT/base.apk $OUT/unsigned.apk; (cd $OUT/dex && zip -q ../unsigned.apk classes.dex)
$BT/zipalign -f 4 $OUT/unsigned.apk $OUT/aligned.apk
[ -f debug.keystore ] || keytool -genkeypair -keystore debug.keystore -storepass android -keypass android -alias key -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=KeyForge3D" 2>/dev/null
$BT/apksigner sign --ks debug.keystore --ks-pass pass:android --out KeyForge3D.apk $OUT/aligned.apk
$BT/apksigner verify KeyForge3D.apk && echo OK

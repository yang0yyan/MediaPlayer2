function merge_static_to_share() {
  "${CROSS_PREFIX}"ld \
  -rpath-link="$SYSROOT"/usr/lib \
  -L"$SYSROOT"/usr/lib \
  -soname libffmpeg.so -shared -Bsymbolic --whole-archive -o \
  "$PREFIX"/lib/libffmpeg.so \
  "$PREFIX"/lib/libavcodec.a \
  "$PREFIX"/lib/libavfilter.a \
  "$PREFIX"/lib/libswresample.a \
  "$PREFIX"/lib/libavformat.a \
  "$PREFIX"/lib/libavutil.a \
  "$PREFIX"/lib/libswscale.a \
  -lstdc++ -fPIC -lc -lm -lz -ldl -llog --dynamic-linker=/system/bin/linker \
  "$CROSS_GCC_LIB"/libgcc.a
}
export NDK=/D/java/ff/ndk/android-ndk-r21e
HOST_OS_ARCH=windows-x86_64

TOOLCHAIN=$NDK/toolchains/llvm/prebuilt/windows-x86_64
SYSROOT="${TOOLCHAIN}/sysroot"
OUTPUT_FOLDER="x86"

PREFIX="${PWD}/android/$OUTPUT_FOLDER"

TOOLCHAIN_PATH=$NDK/toolchains/x86-4.9/prebuilt/$HOST_OS_ARCH
TOOLCHAIN_PREFIX=i686-linux-android

CROSS_PREFIX=$TOOLCHAIN_PATH/bin/$TOOLCHAIN_PREFIX-
CROSS_GCC_LIB=$TOOLCHAIN_PATH/lib/gcc/$TOOLCHAIN_PREFIX/4.9.x

merge_static_to_share

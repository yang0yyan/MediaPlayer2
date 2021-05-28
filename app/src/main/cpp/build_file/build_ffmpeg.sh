#!/bin/bash
#未开启硬件编码
# 清空上次的编译
make clean

#你自己的NDK路径.
export NDK=/D/java/ff/ndk/android-ndk-r21e

function build() {
  echo "Compiling FFmpeg for $CPU"
  ./configure \
    --prefix="$PREFIX" \
    --libdir="$LIB_DIR" \
    --enable-small \
    --enable-shared \
    --enable-static \
	  --enable-jni \
    --disable-asm \
    --disable-programs \
    --disable-avdevice \
    --disable-encoders \
    --disable-muxers \
    --disable-filters \
    --enable-cross-compile \
    --cross-prefix="$CROSS_PREFIX" \
    --cc="$CC" \
    --cxx="$CXX" \
    --sysroot="$SYSROOT" \
    --extra-cflags="-O3 -fpic $OPTIMIZE_CFLAGS" \
    --extra-ldflags="-O3 -fpic $OPTIMIZE_CFLAGS" \
    --arch="$ARCH" \
    --target-os=android

  make clean
  make
  make install
  echo "The Compilation of FFmpeg for $CPU is completed"
}

#function build_shared() {
#  make clean
#  $CC "$C_FLAGS" -shared -o libffmpeg.so -Wl,--whole-archive libavcodec.a libavformat.a libswresample.a libavfilter.a libavutil.a libswscale.a -Wl,--no-whole-archive
#}

# 编译工具链目录，ndk17版本以上用的是clang，以下是gcc
TOOLCHAIN=$NDK/toolchains/llvm/prebuilt/windows-x86_64
# 版本号
API=21
# 交叉编译树的根目录(查找相应头文件和库用)
SYSROOT="${TOOLCHAIN}/sysroot"

#ADDI_CFLAGS="-fPIE -pie -march=armv7-a -mfloat-abi=softfp -mfpu=neon"
ADDI_LDFLAGS="-fPIE -pie"

#armv7-a
OUTPUT_FOLDER="armeabi-v7a"
ARCH="arm"
CPU="armv7-a"
PREFIX="${PWD}/android/$OUTPUT_FOLDER"
LIB_DIR="$PREFIX/libs"
CC=$TOOLCHAIN/bin/armv7a-linux-androideabi$API-clang
CXX=$TOOLCHAIN/bin/armv7a-linux-androideabi$API-clang++
CROSS_PREFIX=$TOOLCHAIN/bin/arm-linux-androideabi-
OPTIMIZE_CFLAGS="-mfloat-abi=softfp -mfpu=vfp -marm -march=$CPU "

#armv8-a
#OUTPUT_FOLDER="arm64-v8a"
#ARCH=arm64
#CPU=armv8-a
#PREFIX="${PWD}/android/$OUTPUT_FOLDER"
#LIB_DIR="$PREFIX/libs"
#CC=$TOOLCHAIN/bin/aarch64-linux-android$API-clang
#CXX=$TOOLCHAIN/bin/aarch64-linux-android$API-clang++
#CROSS_PREFIX=$TOOLCHAIN/bin/aarch64-linux-android-
#OPTIMIZE_CFLAGS="-march=$CPU"

#x86 针对x86需要设置--disable-asm \，不然运行时会报错：“文件中有重定位”
#OUTPUT_FOLDER="x86"
#ARCH=x86
#CPU=x86
#PREFIX="${PWD}/android/$OUTPUT_FOLDER"
#LIB_DIR="$PREFIX/libs"
#CC=$TOOLCHAIN/bin/i686-linux-android$API-clang
#CXX=$TOOLCHAIN/bin/i686-linux-android$API-clang++
#CROSS_PREFIX=$TOOLCHAIN/bin/i686-linux-android-
#OPTIMIZE_CFLAGS="-march=i686 -mtune=intel -mssse3 -mfpmath=sse -m32"

#C_FLAGS="--sysroot=/root/NDK/android-ndk-r17c/platforms/android-21/arch-arm -isystem /root/NDK/android-ndk-r17c/sysroot/usr/include -isystem /root/NDK/android-ndk-r17c/sysroot/usr/include/arm-linux-androideabi"

#x86_64
#OUTPUT_FOLDER="x86_64"
#ARCH=x86_64
#CPU=x86_64
#PREFIX="${PWD}/android/$OUTPUT_FOLDER"
#LIB_DIR="$PREFIX/libs"
#CC=$TOOLCHAIN/bin/x86_64-linux-android$API-clang
#CXX=$TOOLCHAIN/bin/x86_64-linux-android$API-clang++
#CROSS_PREFIX=$TOOLCHAIN/bin/x86_64-linux-android-
#OPTIMIZE_CFLAGS="-march=$CPU -msse4.2 -mpopcnt -m64 -mtune=intel"

build
#build_shared

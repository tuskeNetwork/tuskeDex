#!/bin/bash

cd ../../

version="0.0.1-SNAPSHOT"

target_dir="releases/$version"

# Set TUSKEX_GPG_USER as environment var to the email address used for gpg signing. e.g. TUSKEX_GPG_USER=manfred@bitsquare.io
# Set TUSKEX_VM_PATH as environment var to the directory where your shared folders for virtual box are residing

vmPath=$TUSKEX_VM_PATH
linux64=$vmPath/vm_shared_ubuntu
win64=$vmPath/vm_shared_windows
macos=$vmPath/vm_shared_macosx

deployDir=deploy

rm -r $target_dir

mkdir -p $target_dir

# sig key mkarrer
cp "$target_dir/../../package/F379A1C6.asc" "$target_dir/"
# sig key cbeams
cp "$target_dir/../../package/5BC5ED73.asc" "$target_dir/"
# sig key Christoph Atteneder
cp "$target_dir/../../package/29CDFD3B.asc" "$target_dir/"
# signing key
cp "$target_dir/../../package/signingkey.asc" "$target_dir/"

dmg="Tuskex-$version.dmg"
cp "$macos/$dmg" "$target_dir/"

deb="tuskex_$version-1_amd64.deb"
deb64="Tuskex-64bit-$version.deb"
cp "$linux64/$deb" "$target_dir/$deb64"

rpm="tuskex-$version-1.x86_64.rpm"
rpm64="Tuskex-64bit-$version.rpm"
cp "$linux64/$rpm" "$target_dir/$rpm64"

exe="Tuskex-$version.exe"
exe64="Tuskex-64bit-$version.exe"
cp "$win64/$exe" "$target_dir/$exe64"

rpi="jar-lib-for-raspberry-pi-$version.zip"
cp "$deployDir/$rpi" "$target_dir/"

cd "$target_dir"

echo Create signatures
gpg --digest-algo SHA256 --local-user $TUSKEX_GPG_USER --output $dmg.asc --detach-sig --armor $dmg
gpg --digest-algo SHA256 --local-user $TUSKEX_GPG_USER --output $deb64.asc --detach-sig --armor $deb64
gpg --digest-algo SHA256 --local-user $TUSKEX_GPG_USER --output $rpm64.asc --detach-sig --armor $rpm64
gpg --digest-algo SHA256 --local-user $TUSKEX_GPG_USER --output $exe64.asc --detach-sig --armor $exe64
gpg --digest-algo SHA256 --local-user $TUSKEX_GPG_USER --output $rpi.asc --detach-sig --armor $rpi

echo Verify signatures
gpg --digest-algo SHA256 --verify $dmg{.asc*,}
gpg --digest-algo SHA256 --verify $deb64{.asc*,}
gpg --digest-algo SHA256 --verify $rpm64{.asc*,}
gpg --digest-algo SHA256 --verify $exe64{.asc*,}
gpg --digest-algo SHA256 --verify $rpi{.asc*,}

mkdir $win64/$version
cp -r . $win64/$version

open "."

#
# Copyright (C) 2020-2023 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

import common
import re

def FullOTA_InstallEnd(info):
  OTA_InstallEnd(info)
  return

def IncrementalOTA_InstallEnd(info):
  OTA_InstallEnd(info)
  return

def AddImage(info, basename, dest):
  name = basename
  path = "IMAGES/" + name
  if path not in info.input_zip.namelist():
    return

  data = info.input_zip.read(path)
  common.ZipWriteStr(info.output_zip, name, data)
  info.script.Print("Patching {} image unconditionally...".format(dest.split('/')[-1]))
  info.script.AppendExtra('package_extract_file("%s", "%s");' % (name, dest))

def AddImageRadio(info, basename, dest):
  name = basename
  path = "RADIO/" + name
  if path not in info.input_zip.namelist():
    return

  data = info.input_zip.read("RADIO/" + basename)
  fwpath =  "firmware/" + name
  common.ZipWriteStr(info.output_zip, fwpath, data)
  info.script.Print("Patching {} image unconditionally...".format(dest.split('/')[-1]))
  info.script.AppendExtra('package_extract_file("%s", "%s");' % (fwpath, dest))

def OTA_InstallEnd(info):
  AddImage(info, "dtbo.img", "/dev/block/bootdevice/by-name/dtbo")
  AddImage(info, "vbmeta.img", "/dev/block/bootdevice/by-name/vbmeta")
  AddImage(info, "vbmeta_system.img", "/dev/block/bootdevice/by-name/vbmeta_system")

  # Firmware
  AddImageRadio(info, "cmnlib64.mbn", "/dev/block/bootdevice/by-name/cmnlib64")
  AddImageRadio(info, "imagefv.elf", "/dev/block/bootdevice/by-name/imagefv")
  AddImageRadio(info, "cmnlib.mbn", "/dev/block/bootdevice/by-name/cmnlib")
  AddImageRadio(info, "hyp.mbn", "/dev/block/bootdevice/by-name/hyp")
  AddImageRadio(info, "km4.mbn", "/dev/block/bootdevice/by-name/keymaster")
  AddImageRadio(info, "tz.mbn", "/dev/block/bootdevice/by-name/tz")
  AddImageRadio(info, "aop.mbn", "/dev/block/bootdevice/by-name/aop")
  AddImageRadio(info, "xbl_config.elf", "/dev/block/bootdevice/by-name/xbl_config")
  AddImageRadio(info, "BTFM.bin", "/dev/block/bootdevice/by-name/bluetooth")
  AddImageRadio(info, "uefi_sec.mbn", "/dev/block/bootdevice/by-name/uefisecapp")
  AddImageRadio(info, "NON-HLOS.bin", "/dev/block/bootdevice/by-name/modem")
  AddImageRadio(info, "qupv3fw.elf", "/dev/block/bootdevice/by-name/qupfw")
  AddImageRadio(info, "abl.elf", "/dev/block/bootdevice/by-name/abl")
  AddImageRadio(info, "dspso.bin", "/dev/block/bootdevice/by-name/dsp")
  AddImageRadio(info, "devcfg.mbn", "/dev/block/bootdevice/by-name/devcfg")
  AddImageRadio(info, "storsec.mbn", "/dev/block/bootdevice/by-name/storsec")
  AddImageRadio(info, "xbl.elf", "/dev/block/bootdevice/by-name/xbl")
  AddImageRadio(info, "ffu.img", "/dev/block/bootdevice/by-name/ffu")
  return

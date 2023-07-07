PRODUCT_PACKAGES += \
    XiaomiParts

PRODUCT_SYSTEM_EXT_PROPERTIES += \
    persist.lcd.hbm_mode=0 \
    persist.lcd.cabc_mode=1

SYSTEM_EXT_PRIVATE_SEPOLICY_DIRS += \
    device/xiaomi/surya/parts/sepolicy/private

BOARD_VENDOR_SEPOLICY_DIRS += \
    device/xiaomi/surya/parts/sepolicy/vendor

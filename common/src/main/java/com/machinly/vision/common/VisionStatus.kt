package com.machinly.vision.common

enum class VisionStatus(val option: VisionOption) {
    REC_ON(VisionOption.REC),
    REC_OFF(VisionOption.REC),
    RECOG_ON(VisionOption.RECOG),
    RECOG_OFF(VisionOption.RECOG),
}

enum class VisionAction {
    REC_START,
    REC_STOP,
    RECOG_START,
    RECOG_STOP,
}

enum class VisionOption {
    REC, RECOG
}


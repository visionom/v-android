package com.machinly.vision.service

class VisionConfig {
    val VoiceCacheTime: Long = 60
}

class VisionBuilder {
    companion object {
        fun GetDefaultConfig(): VisionConfig {
            return VisionConfig()
        }
    }
}

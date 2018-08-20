package com.machinly.vision.common

import android.util.Log

class VisionStatusMgr {
    companion object {
        private const val TAG = "vision_status_manager"
        private val defaultStatusMap = HashMap<VisionOption, VisionStatus>()

        init {
            defaultStatusMap[VisionOption.REC] = VisionStatus.REC_OFF
            defaultStatusMap[VisionOption.RECOG] = VisionStatus.RECOG_OFF
        }
    }

    private var statusMap: HashMap<VisionOption, VisionStatus> = HashMap()

    init {
        statusMap.putAll(defaultStatusMap)
    }

    fun CheckStatus(status: VisionStatus): Boolean {
        return GetStatus(status.op) == status
    }

    fun SetStatus(status: VisionStatus) {
        statusMap[status.op] = status
    }

    fun GetStatus(op: VisionOption): VisionStatus {
        if (statusMap.containsKey(op))
            return statusMap[op]!!
        val status = defaultStatusMap[op]!!
        statusMap[op] = status
        return status
    }

    fun GetStatus(opOrder: Int): VisionStatus {
        val op = GetOptionByOrder(opOrder)
        Log.d(TAG, op.name)
        return GetStatus(op)
    }

    fun GetStatusByOrder(order: Int): VisionStatus {
        if (order < VisionStatus.values().size && order > 0)
            return VisionStatus.values()[order]
        return VisionStatus.REC_OFF
    }

    fun GetOptionByOrder(order: Int): VisionOption {
        if (order < VisionOption.values().size && order > 0)
            return VisionOption.values()[order]
        return VisionOption.REC
    }

    fun PrintStatusMap() {
        Log.d(TAG, "Status Map")
        statusMap.forEach {
            Log.d(TAG, "${it.key}: ${it.value} ${it.value.ordinal}")
        }
    }
}

enum class VisionStatus(val op: VisionOption) {
    REC_ON(VisionOption.REC),
    REC_OFF(VisionOption.REC),
    RECOG_ON(VisionOption.RECOG),
    RECOG_OFF(VisionOption.RECOG),
}

enum class VisionAction(val op: VisionOption) {
    REC_START(VisionOption.REC),
    REC_STOP(VisionOption.REC),
    RECOG_START(VisionOption.RECOG),
    RECOG_STOP(VisionOption.RECOG),
}

enum class VisionOption {
    REC, RECOG
}


package io.element.android.libraries.matrix.api.encryption

enum class RecoveryState {
    /**
     * 特殊值，当 SDK 正在等待首次同步完成时。
     */
    WAITING_FOR_SYNC,

    /**
     * 从 SDK 映射的各状态值。
     */

    /**
     * 未知状态。
     */
    UNKNOWN,

    /**
     * 已启用。
     */
    ENABLED,

    /**
     * 已禁用。
     */
    DISABLED,

    /**
     * 未完成（例如：已开启备份但尚未验证，或缺少必要的恢复信息）。
     */
    INCOMPLETE,
}

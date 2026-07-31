package me.rerere.workspace

/**
 * Workspace 运行环境类型
 *
 * PROOT   - 原有精简 Proot 沙盒（轻量，工具少，网络受限）
 * TERMUX  - Termux 完整 Linux 环境（完整 apt、网络全通、工具丰富）
 */
enum class WorkspaceEnvironment {
    PROOT,
    TERMUX,
}

data class Workspace(
    val id: String,
    val name: String,
    val root: String,
    val environment: WorkspaceEnvironment = WorkspaceEnvironment.PROOT,
    val shellStatus: WorkspaceShellStatus = WorkspaceShellStatus.DISABLED,
    val createdAt: Long,
    val updatedAt: Long,
    val lastAccessAt: Long? = null,
)
enum class WorkspaceShellStatus {
    DISABLED,
    INSTALLING,
    READY,
    BROKEN,
}
enum class WorkspaceStorageArea {
    FILES,
    LINUX,
}
enum class RootfsInstallStage {
    DOWNLOADING,
    EXTRACTING,
    INSTALLED,
}

data class RootfsInstallProgress(
    val stage: RootfsInstallStage,
    val bytesRead: Long = 0,
    val totalBytes: Long? = null,
    val entriesExtracted: Int = 0,
    val currentEntry: String? = null,
)

data class WorkspaceConfig(
    val maxReadBytes: Long = 512 * 1024,
    val maxWriteBytes: Long = 2 * 1024 * 1024,
    val maxListEntries: Int = 500,
    val maxSearchResults: Int = 100,
)

data class WorkspaceFileEntry(
    val path: String,
    val name: String,
    val isDirectory: Boolean,
    val sizeBytes: Long,
    val updatedAt: Long,
)

data class WorkspaceSearchMatch(
    val path: String,
    val line: Int,
    val text: String,
)

data class WorkspaceCommandResult(
    val exitCode: Int,
    val stdout: String,
    val stderr: String,
    val timedOut: Boolean = false,
    val truncated: Boolean = false,
)

package me.rerere.workspace
import java.io.File

data class WorkspaceBindMount(
    val source: File,
    val target: String,
) {
    init {
        require(target.startsWith("/")) { "Bind mount target must be absolute: $target" }
    }
}

class ProotShellRunner(
    private val nativeLibraryDir: File,
    private val patcher: RootfsPatcher = RootfsPatcher(),
) : WorkspaceShellRunner {
    override fun execute(context: WorkspaceShellContext): WorkspaceCommandResult {
        if (!context.linuxDir.hasUsableRootfs()) {
            return WorkspaceCommandResult(
                exitCode = 127,
                stdout = "",
                stderr = "Rootfs is not installed",
            )
        }

        val proot = File(nativeLibraryDir, PROOT_EXEC)
        val loader = File(nativeLibraryDir, PROOT_LOADER)
        if (!proot.isFile) {
            return WorkspaceCommandResult(
                exitCode = 127,
                stdout = "",
                stderr = "proot executable not found: ${proot.absolutePath}",
            )
        }
        if (!loader.isFile) {
            return WorkspaceCommandResult(
                exitCode = 127,
                stdout = "",
                stderr = "proot loader not found: ${loader.absolutePath}",
            )
        }

        context.tempDir.mkdirs()

        val patchOptions = if (context.environment == WorkspaceEnvironment.TERMUX) {
            RootfsPatchOptions(
                nameservers = listOf("8.8.8.8", "8.8.4.4", "223.5.5.5"),
                hostname = "termux",
                locale = "C.UTF-8",
                termuxMode = true,
            )
        } else {
            RootfsPatchOptions()
        }
        patcher.patch(context.linuxDir, patchOptions)

        val process = ProcessBuilder(buildCommand(context, proot))
            .directory(context.filesDir)
            .redirectErrorStream(false)
            .apply {
                environment()["PROOT_LOADER"] = loader.absolutePath
                environment()["PROOT_TMP_DIR"] = context.tempDir.absolutePath
                environment()["TMPDIR"] = context.tempDir.absolutePath
            }
            .start()
        return process.readResult(context.timeoutMillis, context.stdin)
    }

    private fun buildCommand(
        context: WorkspaceShellContext,
        proot: File,
    ): List<String> {
        val isTermux = context.environment == WorkspaceEnvironment.TERMUX
        val command = mutableListOf(
            proot.absolutePath,
            "--root-id",
            "--link2symlink",
            "--kill-on-exit",
            "-r",
            context.linuxDir.absolutePath,
            "-w",
            context.prootCwd(),
        )

        if (isTermux) {
            val termuxHome = "/data/data/com.termux/files/home"
            val termuxPrefix = "/data/data/com.termux/files/usr"
            val sdcardPath = "/sdcard"

            listOf(
                termuxHome to termuxHome,
                termuxPrefix to termuxPrefix,
                sdcardPath to sdcardPath,
            ).forEach { (source, target) ->
                val srcFile = File(source)
                if (srcFile.exists()) {
                    command += "-b"
                    command += "${srcFile.absolutePath}:$target"
                }
            }
        } else {
            command += "-b"
            command += "${context.filesDir.absolutePath}:$WORKSPACE_DIR"
        }

        context.bindMounts.forEach { mount ->
            if (mount.source.exists()) {
                command += "-b"
                command += "${mount.source.absolutePath}:${mount.target.trimEnd('/')}"
            }
        }

        if (!isTermux) {
            WorkspaceManager.KERNEL_FS_MOUNTS.forEach { path ->
                if (File(path).exists()) {
                    command += "-b"
                    command += path
                }
            }
        }

        val envVars = if (isTermux) {
            listOf(
                "HOME=/data/data/com.termux/files/home",
                "PREFIX=/data/data/com.termux/files/usr",
                "PATH=/data/data/com.termux/files/usr/bin:/data/data/com.termux/files/usr/bin/applets:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
                "TERM=xterm-256color",
                "LANG=C.UTF-8",
                "LC_ALL=C.UTF-8",
                "ANDROID_DATA=/data",
                "ANDROID_ROOT=/system",
                "EXTERNAL_STORAGE=/sdcard",
            )
        } else {
            listOf(
                "HOME=/root",
                "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
                "TERM=xterm-256color",
                "LANG=C.UTF-8",
                "LC_ALL=C.UTF-8",
            )
        }

        command += listOf(
            "/usr/bin/env",
            "-i",
        )
        command.addAll(envVars)
        command.addAll(
            listOf(
                "/bin/bash",
                "-l",
                "-c",
                "cd -- \"\$1\" && eval \"\$2\"",
                "rikkahub",
                context.prootCwd(),
                context.command,
            )
        )
        return command
    }

    private fun WorkspaceShellContext.prootCwd(): String {
        val normalized = cwd.trim().trim('/')
        return if (normalized.isBlank()) {
            WORKSPACE_DIR
        } else {
            "$WORKSPACE_DIR/$normalized"
        }
    }

    private fun File.hasUsableRootfs(): Boolean =
        isDirectory && File(this, "bin/sh").isFile

    private companion object {
        private const val PROOT_EXEC = "libproot_exec.so"
        private const val PROOT_LOADER = "libproot_loader.so"
        private val WORKSPACE_DIR = WorkspaceManager.ROOTFS_WORKSPACE_DIR
    }
}

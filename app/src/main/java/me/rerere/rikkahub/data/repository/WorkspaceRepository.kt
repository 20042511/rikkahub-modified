package me.rerere.rikkahub.data.repository

import kotlinx.coroutines.flow.Flow
import me.rerere.rikkahub.data.db.dao.WorkspaceDao
import me.rerere.rikkahub.data.db.entity.WorkspaceEntity
import me.rerere.rikkahub.utils.JsonInstant
import me.rerere.workspace.Workspace
import me.rerere.workspace.WorkspaceEnvironment
import me.rerere.workspace.WorkspaceManager
import kotlin.uuid.Uuid

class WorkspaceRepository(
    private val dao: WorkspaceDao,
    private val manager: WorkspaceManager,
    private val json: JsonInstant,
) {
    fun listFlow(): Flow<List<WorkspaceEntity>> = dao.listFlow()

    suspend fun list(): List<WorkspaceEntity> = dao.list()

    suspend fun getById(id: String): WorkspaceEntity? = dao.getById(id)

    suspend fun create(name: String, environment: WorkspaceEnvironment = WorkspaceEnvironment.PROOT): WorkspaceEntity {
        val id = Uuid.random().toString()
        val now = System.currentTimeMillis()
        val finalName = name.trim().ifBlank { "Workspace" }
        require(!isNameTaken(finalName, excludeId = null)) {
            "Workspace name already exists: $finalName"
        }
        val workspace = WorkspaceEntity(
            id = id,
            name = finalName,
            root = id,
            environment = environment.name,
            createdAt = now,
            updatedAt = now,
            lastAccessAt = null,
        )
        manager.ensureWorkspace(workspace.root)
        dao.upsert(workspace)
        return workspace
    }

    suspend fun rename(id: String, name: String): Boolean {
        val workspace = dao.getById(id) ?: return false
        val finalName = name.trim().ifBlank { return false }
        require(!isNameTaken(finalName, excludeId = id)) {
            "Workspace name already exists: $finalName"
        }
        dao.upsert(workspace.copy(name = finalName, updatedAt = System.currentTimeMillis()))
        return true
    }

    suspend fun delete(id: String) {
        val workspace = dao.getById(id) ?: return
        dao.delete(workspace)
        manager.deleteWorkspace(workspace.root)
    }

    suspend fun getWorkspace(id: String): Workspace? {
        return dao.getById(id)?.toWorkspace()
    }

    suspend fun ensureWorkspace(workspaceId: String) {
        val entity = dao.getById(workspaceId) ?: return
        manager.ensureWorkspace(entity.root)
    }

    suspend fun executeCommand(
        workspaceId: String,
        command: String,
        cwd: String? = null,
        timeoutMillis: Long = WorkspaceManager.DEFAULT_COMMAND_TIMEOUT_MS,
    ): WorkspaceManager.CommandResult {
        val workspace = dao.getById(workspaceId) ?: error("Workspace not found: $workspaceId")
        manager.ensureWorkspace(workspace.root)
        return manager.executeCommand(
            workspace = workspace.toWorkspace(),
            command = command,
            cwd = cwd,
            timeoutMillis = timeoutMillis,
        )
    }

    suspend fun readText(workspaceId: String, path: String): String? {
        val workspace = dao.getById(workspaceId) ?: return null
        manager.ensureWorkspace(workspace.root)
        return manager.readText(workspace.toWorkspace(), path)
    }

    suspend fun writeText(workspaceId: String, path: String, content: String) {
        val workspace = dao.getById(workspaceId) ?: return
        manager.ensureWorkspace(workspace.root)
        manager.writeText(workspace.toWorkspace(), path, content)
    }

    suspend fun deleteFile(workspaceId: String, path: String) {
        val workspace = dao.getById(workspaceId) ?: return
        manager.ensureWorkspace(workspace.root)
        manager.deleteFile(workspace.toWorkspace(), path)
    }

    suspend fun listFiles(workspaceId: String, path: String): List<WorkspaceManager.FileInfo> {
        val workspace = dao.getById(workspaceId) ?: return emptyList()
        manager.ensureWorkspace(workspace.root)
        return manager.listFiles(workspace.toWorkspace(), path)
    }

    suspend fun exportFile(workspaceId: String, path: String, destination: java.io.File) {
        val workspace = dao.getById(workspaceId) ?: return
        manager.ensureWorkspace(workspace.root)
        manager.exportFile(workspace.toWorkspace(), path, destination)
    }

    suspend fun importFile(workspaceId: String, source: java.io.File, path: String) {
        val workspace = dao.getById(workspaceId) ?: return
        manager.ensureWorkspace(workspace.root)
        manager.importFile(workspace.toWorkspace(), source, path)
    }

    private suspend fun isNameTaken(name: String, excludeId: String?): Boolean {
        return dao.list().any { it.name.trim().equals(name, ignoreCase = true) && it.id != excludeId }
    }
}

package me.rerere.rikkahub.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.rerere.rikkahub.data.db.dao.KnowledgeDAO
import me.rerere.rikkahub.data.db.entity.KnowledgeEntity
import me.rerere.rikkahub.data.db.entity.KnowledgeMetadata
import me.rerere.rikkahub.data.db.entity.KnowledgeSearchResult
import me.rerere.rikkahub.data.model.KnowledgeItem

class KnowledgeRepository(private val knowledgeDAO: KnowledgeDAO) {
    
    companion object {
        const val GLOBAL_KNOWLEDGE_ID = "__global__"
    }
    
    // ==================== 基础查询 ====================
    
    fun getKnowledgeFlow(assistantId: String): Flow<List<KnowledgeItem>> =
        knowledgeDAO.getKnowledgeFlow(assistantId)
            .map { entities -> entities.map { toKnowledgeItem(it) } }
    
    suspend fun getKnowledge(assistantId: String): List<KnowledgeItem> =
        knowledgeDAO.getKnowledge(assistantId).map { toKnowledgeItem(it) }
    
    fun getGlobalKnowledgeFlow(): Flow<List<KnowledgeItem>> =
        knowledgeDAO.getGlobalKnowledgeFlow()
            .map { entities -> entities.map { toKnowledgeItem(it) } }
    
    suspend fun getGlobalKnowledge(): List<KnowledgeItem> =
        knowledgeDAO.getGlobalKnowledge().map { toKnowledgeItem(it) }
    
    fun getAllKnowledgeFlow(): Flow<List<KnowledgeItem>> =
        knowledgeDAO.getAllKnowledgeFlow()
            .map { entities -> entities.map { toKnowledgeItem(it) } }
    
    suspend fun getAllKnowledge(): List<KnowledgeItem> =
        knowledgeDAO.getAllKnowledge().map { toKnowledgeItem(it) }
    
    suspend fun getKnowledgeById(id: Int): KnowledgeItem? =
        knowledgeDAO.getKnowledgeById(id)?.let { toKnowledgeItem(it) }
    
    // ==================== CRUD 操作 ====================
    
    suspend fun addKnowledge(assistantId: String?, title: String, content: String, 
                           category: String = "study", tags: String = "", 
                           sourceType: String = "manual", sourcePath: String? = null): KnowledgeItem {
        val entity = KnowledgeEntity(
            assistantId = assistantId,
            title = title,
            content = content,
            category = category,
            tags = tags,
            sourceType = sourceType,
            sourcePath = sourcePath
        )
        val id = knowledgeDAO.insertKnowledge(entity).toInt()
        // 建立 FTS 索引
        knowledgeDAO.indexToFTS(id, title, content)
        return toKnowledgeItem(entity.copy(id = id))
    }
    
    suspend fun updateKnowledge(id: Int, title: String, content: String, 
                               category: String, tags: String) {
        val old = knowledgeDAO.getKnowledgeById(id) 
            ?: error("Knowledge record #$id not found")
        val updated = old.copy(
            title = title,
            content = content,
            category = category,
            tags = tags,
            updatedAt = System.currentTimeMillis()
        )
        knowledgeDAO.updateKnowledge(updated)
        knowledgeDAO.updateFTS(id, title, content)
    }
    
    suspend fun deleteKnowledge(id: Int) {
        knowledgeDAO.deleteKnowledge(id)
        knowledgeDAO.removeFromFTS(id)
    }
    
    suspend fun deleteAllKnowledge(assistantId: String) {
        knowledgeDAO.deleteKnowledgeOfAssistant(assistantId)
    }
    
    // ==================== 分类和标签 ====================
    
    suspend fun getKnowledgeByCategory(category: String, assistantId: String): List<KnowledgeItem> =
        knowledgeDAO.getKnowledgeByCategory(category, assistantId).map { toKnowledgeItem(it) }
    
    suspend fun getGlobalKnowledgeByCategory(category: String): List<KnowledgeItem> =
        knowledgeDAO.getGlobalKnowledgeByCategory(category).map { toKnowledgeItem(it) }
    
    suspend fun getKnowledgeByTag(tag: String, assistantId: String): List<KnowledgeItem> =
        knowledgeDAO.getKnowledgeByTag(tag, assistantId).map { toKnowledgeItem(it) }
    
    suspend fun getGlobalKnowledgeByTag(tag: String): List<KnowledgeItem> =
        knowledgeDAO.getGlobalKnowledgeByTag(tag).map { toKnowledgeItem(it) }
    
    suspend fun getCategories(assistantId: String): List<String> =
        knowledgeDAO.getCategories(assistantId)
    
    suspend fun getTags(assistantId: String): List<String> =
        knowledgeDAO.getTags(assistantId)
    
    // ==================== 搜索 ====================
    
    suspend fun searchKnowledge(query: String, assistantId: String, limit: Int = 10): List<KnowledgeSearchResult> =
        knowledgeDAO.searchKnowledge(query, limit)
    
    suspend fun searchKnowledgeSimple(query: String, assistantId: String, limit: Int = 10): List<KnowledgeItem> =
        knowledgeDAO.searchKnowledgeSimple(query, assistantId, limit).map { toKnowledgeItem(it) }
    
    suspend fun getPopularKnowledge(assistantId: String, limit: Int = 10): List<KnowledgeItem> =
        knowledgeDAO.getPopularKnowledge(assistantId, limit).map { toKnowledgeItem(it) }
    
    // ==================== 统计 ====================
    
    suspend fun getKnowledgeCount(assistantId: String): Int =
        knowledgeDAO.getKnowledgeCount(assistantId)
    
    suspend fun getGlobalKnowledgeCount(): Int =
        knowledgeDAO.getGlobalKnowledgeCount()
    
    // ==================== 工具方法 ====================
    
    private fun toKnowledgeItem(entity: KnowledgeEntity): KnowledgeItem {
        return KnowledgeItem(
            id = entity.id,
            title = entity.title,
            content = entity.content,
            category = entity.category,
            tags = entity.tags.split(",").filter { it.isNotEmpty() },
            sourceType = entity.sourceType,
            sourcePath = entity.sourcePath,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            usageCount = entity.usageCount
        )
    }
}

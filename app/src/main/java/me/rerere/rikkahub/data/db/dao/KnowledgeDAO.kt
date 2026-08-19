package me.rerere.rikkahub.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.rerere.rikkahub.data.db.entity.KnowledgeEntity
import me.rerere.rikkahub.data.db.entity.KnowledgeSearchResult

@Dao
interface KnowledgeDAO {
    
    // ==================== 基础 CRUD ====================
    
    @Query("SELECT * FROM knowledge WHERE assistant_id = :assistantId ORDER BY updated_at DESC")
    fun getKnowledgeFlow(assistantId: String): Flow<List<KnowledgeEntity>>
    
    @Query("SELECT * FROM knowledge WHERE assistant_id = :assistantId ORDER BY updated_at DESC")
    suspend fun getKnowledge(assistantId: String): List<KnowledgeEntity>
    
    @Query("SELECT * FROM knowledge WHERE assistant_id IS NULL ORDER BY updated_at DESC")
    fun getGlobalKnowledgeFlow(): Flow<List<KnowledgeEntity>>
    
    @Query("SELECT * FROM knowledge WHERE assistant_id IS NULL ORDER BY updated_at DESC")
    suspend fun getGlobalKnowledge(): List<KnowledgeEntity>
    
    @Query("SELECT * FROM knowledge ORDER BY updated_at DESC")
    fun getAllKnowledgeFlow(): Flow<List<KnowledgeEntity>>
    
    @Query("SELECT * FROM knowledge ORDER BY updated_at DESC")
    suspend fun getAllKnowledge(): List<KnowledgeEntity>
    
    @Query("SELECT * FROM knowledge WHERE id = :id")
    suspend fun getKnowledgeById(id: Int): KnowledgeEntity?
    
    @Insert
    suspend fun insertKnowledge(knowledge: KnowledgeEntity): Long
    
    @Update
    suspend fun updateKnowledge(knowledge: KnowledgeEntity)
    
    @Query("DELETE FROM knowledge WHERE id = :id")
    suspend fun deleteKnowledge(id: Int)
    
    @Query("DELETE FROM knowledge WHERE assistant_id = :assistantId")
    suspend fun deleteKnowledgeOfAssistant(assistantId: String)
    
    // ==================== 分类查询 ====================
    
    @Query("SELECT * FROM knowledge WHERE category = :category AND assistant_id = :assistantId ORDER BY updated_at DESC")
    suspend fun getKnowledgeByCategory(category: String, assistantId: String): List<KnowledgeEntity>
    
    @Query("SELECT * FROM knowledge WHERE category = :category ORDER BY updated_at DESC")
    suspend fun getGlobalKnowledgeByCategory(category: String): List<KnowledgeEntity>
    
    // ==================== 标签查询 ====================
    
    @Query("SELECT * FROM knowledge WHERE tags LIKE '%' || :tag || '%' AND assistant_id = :assistantId ORDER BY updated_at DESC")
    suspend fun getKnowledgeByTag(tag: String, assistantId: String): List<KnowledgeEntity>
    
    @Query("SELECT * FROM knowledge WHERE tags LIKE '%' || :tag || '%' ORDER BY updated_at DESC")
    suspend fun getGlobalKnowledgeByTag(tag: String): List<KnowledgeEntity>
    
    // ==================== 搜索 ====================
    
    /**
     * 基于 FTS5 的全文搜索
     */
    @Query("""
        SELECT k.id, k.title, k.content, k.category, rank as score, '' as relevance
        FROM knowledge k
        JOIN knowledge_fts ON knowledge_fts.rowid = k.id
        WHERE knowledge_fts MATCH :query
        ORDER BY rank
        LIMIT :limit
    """)
    suspend fun searchKnowledge(query: String, limit: Int = 10): List<KnowledgeSearchResult>
    
    /**
     * 基于标题的搜索
     */
    @Query("SELECT * FROM knowledge WHERE title LIKE '%' || :query || '%' AND assistant_id = :assistantId ORDER BY updated_at DESC LIMIT :limit")
    suspend fun searchByTitle(query: String, assistantId: String, limit: Int = 10): List<KnowledgeEntity>
    
    /**
     * 综合搜索（标题 + 内容）
     */
    @Query("""
        SELECT * FROM knowledge 
        WHERE (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%')
        AND assistant_id = :assistantId 
        ORDER BY updated_at DESC 
        LIMIT :limit
    """)
    suspend fun searchKnowledgeSimple(query: String, assistantId: String, limit: Int = 10): List<KnowledgeEntity>
    
    // ==================== 统计 ====================
    
    @Query("SELECT COUNT(*) FROM knowledge WHERE assistant_id = :assistantId")
    suspend fun getKnowledgeCount(assistantId: String): Int
    
    @Query("SELECT COUNT(*) FROM knowledge WHERE assistant_id IS NULL")
    suspend fun getGlobalKnowledgeCount(): Int
    
    @Query("SELECT DISTINCT category FROM knowledge WHERE assistant_id = :assistantId")
    suspend fun getCategories(assistantId: String): List<String>
    
    @Query("SELECT DISTINCT tags FROM knowledge WHERE assistant_id = :assistantId")
    suspend fun getTags(assistantId: String): List<String>
    
    // ==================== 高频使用 ====================
    
    @Query("SELECT * FROM knowledge WHERE assistant_id = :assistantId ORDER BY usage_count DESC LIMIT :limit")
    suspend fun getPopularKnowledge(assistantId: String, limit: Int = 10): List<KnowledgeEntity>
    
    // ==================== FTS5 初始化 ====================
    
    @Query("CREATE VIRTUAL TABLE IF NOT EXISTS knowledge_fts USING fts5(title, content, content='knowledge', content_rowid='id')")
    suspend fun initFTS()
    
    @Query("INSERT INTO knowledge_fts(rowid, title, content) VALUES (:id, :title, :content)")
    suspend fun indexToFTS(id: Int, title: String, content: String)
    
    @Query("DELETE FROM knowledge_fts WHERE rowid = :id")
    suspend fun removeFromFTS(id: Int)
    
    @Query("UPDATE knowledge_fts SET title = :title, content = :content WHERE rowid = :id")
    suspend fun updateFTS(id: Int, title: String, content: String)
}

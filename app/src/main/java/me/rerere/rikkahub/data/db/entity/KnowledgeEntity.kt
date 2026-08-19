package me.rerere.rikkahub.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 知识库实体
 * 
 * 支持本地文件和云端文档的统一管理，包含元数据和文本内容
 */
@Entity(
    tableName = "knowledge",
    indices = [
        Index(value = ["assistant_id"]),
        Index(value = ["category"]),
        Index(value = ["source_type"])
    ]
)
data class KnowledgeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    /** 关联的助手 ID，null 表示全局知识库 */
    @ColumnInfo(name = "assistant_id")
    val assistantId: String? = null,
    
    /** 知识标题 */
    @ColumnInfo(name = "title")
    val title: String = "",
    
    /** 知识内容 */
    @ColumnInfo(name = "content")
    val content: String = "",
    
    /** 知识类别：study|work|personal|project|reference 等 */
    @ColumnInfo(name = "category")
    val category: String = "study",
    
    /** 来源类型：local|cloud|import|manual */
    @ColumnInfo(name = "source_type")
    val sourceType: String = "manual",
    
    /** 来源文件路径或 URL */
    @ColumnInfo(name = "source_path")
    val sourcePath: String? = null,
    
    /** 来源元数据（JSON） */
    @ColumnInfo(name = "source_metadata")
    val sourceMetadata: String? = null,
    
    /** 标签列表（逗号分隔） */
    @ColumnInfo(name = "tags")
    val tags: String = "",
    
    /** 嵌入向量（JSON 数组，用于语义搜索） */
    @ColumnInfo(name = "embedding")
    val embedding: String? = null,
    
    /** 创建时间 */
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    /** 最后更新时间 */
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    
    /** 被引用的次数（用于排序） */
    @ColumnInfo(name = "usage_count")
    val usageCount: Int = 0
)

/**
 * 知识库元数据 DTO
 */
data class KnowledgeMetadata(
    val id: Int,
    val title: String,
    val category: String,
    val tags: List<String>,
    val sourceType: String,
    val sourcePath: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val usageCount: Int
) {
    companion object {
        fun from(entity: KnowledgeEntity): KnowledgeMetadata {
            return KnowledgeMetadata(
                id = entity.id,
                title = entity.title,
                category = entity.category,
                tags = if (entity.tags.isEmpty()) emptyList() else entity.tags.split(",").map { it.trim() },
                sourceType = entity.sourceType,
                sourcePath = entity.sourcePath,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                usageCount = entity.usageCount
            )
        }
    }
}

/**
 * 知识库搜索结果的轻量级 DTO
 */
data class KnowledgeSearchResult(
    val id: Int,
    val title: String,
    val content: String,
    val category: String,
    val score: Float = 1.0f,
    val relevance: String = ""
)

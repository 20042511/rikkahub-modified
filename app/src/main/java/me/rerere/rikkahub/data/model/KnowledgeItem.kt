package me.rerere.rikkahub.data.model

import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * 知识库条目模型
 */
@Serializable
data class KnowledgeItem(
    val id: Int = 0,
    val title: String = "",
    val content: String = "",
    val category: String = "study",
    val tags: List<String> = emptyList(),
    val sourceType: String = "manual",
    val sourcePath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val usageCount: Int = 0
) {
    val dateCreated: String
        get() = Instant.ofEpochMilli(createdAt).toString().take(10)
    
    val dateUpdated: String
        get() = Instant.ofEpochMilli(updatedAt).toString().take(10)
    
    fun getTagList(): List<String> = tags
    
    companion object {
        val CATEGORIES = listOf(
            "study" to "学习研究",
            "work" to "工作",
            "personal" to "个人",
            "project" to "项目",
            "reference" to "参考资料",
            "code" to "代码",
            "design" to "设计"
        )
        
        val SOURCE_TYPES = listOf(
            "manual" to "手动录入",
            "local" to "本地文件",
            "cloud" to "云端同步",
            "import" to "导入"
        )
    }
}

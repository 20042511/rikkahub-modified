package me.rerere.rikkahub.data.ai.tools

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import me.rerere.ai.core.InputSchema
import me.rerere.ai.core.Tool
import me.rerere.ai.ui.UIMessagePart
import me.rerere.rikkahub.data.model.KnowledgeItem
import me.rerere.rikkahub.data.repository.KnowledgeRepository

/**
 * 知识库工具集合
 * 
 * 提供 LLM 检索知识库的能力，支持语义搜索和关键词匹配
 */
fun buildKnowledgeTools(
    json: Json,
    repository: KnowledgeRepository,
    assistantId: String
): List<Tool> = listOf(
    // 搜索知识库
    Tool(
        name = "knowledge_search",
        description = """
            搜索个人知识库获取相关信息。
            当用户询问特定领域知识、之前讨论过的概念、需要参考文档内容时使用。
            
            使用步骤:
            1. 分析用户问题，提取关键信息
            2. 调用 search 操作进行知识检索
            3. 根据返回结果整理回答
            
            知识库内容可能包括学习笔记、技术文档、项目资料等。
        """.trimIndent(),
        parameters = {
            InputSchema.Obj(
                properties = buildJsonObject {
                    put("action", buildJsonObject {
                        put("type", "string")
                        put("enum", buildJsonArray {
                            add("search")
                            add("get_by_category")
                            add("get_by_tag")
                            add("get_popular")
                        })
                        put("description", "操作类型: search(搜索), get_by_category(按分类获取), get_by_tag(按标签获取), get_popular(获取热门)")
                    })
                    put("query", buildJsonObject {
                        put("type", "string")
                        put("description", "搜索关键词（search 操作时必填）")
                    })
                    put("category", buildJsonObject {
                        put("type", "string")
                        put("description", "知识分类: study|work|personal|project|reference|code|design")
                    })
                    put("tag", buildJsonObject {
                        put("type", "string")
                        put("description", "标签名称")
                    })
                    put("limit", buildJsonObject {
                        put("type", "integer")
                        put("description", "返回结果数量上限，默认 5")
                    })
                },
                required = listOf("action")
            )
        },
        execute = { params ->
            val action = params["action"]?.jsonPrimitive?.contentOrNull ?: error("action is required")
            val limit = params["limit"]?.jsonPrimitive?.intOrNull ?: 5
            
            val result = when (action) {
                "search" -> {
                    val query = params["query"]?.jsonPrimitive?.contentOrNull ?: error("query is required for search")
                    repository.searchKnowledgeSimple(query, assistantId, limit)
                }
                
                "get_by_category" -> {
                    val category = params["category"]?.jsonPrimitive?.contentOrNull ?: error("category is required")
                    repository.getKnowledgeByCategory(category, assistantId)
                }
                
                "get_by_tag" -> {
                    val tag = params["tag"]?.jsonPrimitive?.contentOrNull ?: error("tag is required")
                    repository.getKnowledgeByTag(tag, assistantId)
                }
                
                "get_popular" -> {
                    repository.getPopularKnowledge(assistantId, limit)
                }
                
                else -> error("unknown action: $action")
            }
            
            // 格式化为可读文本
            val formatted = result.joinToString("\n\n---\n\n") { item ->
                buildString {
                    appendLine("【${item.title}】")
                    appendLine("分类: ${item.category} | 标签: ${item.tags.joinToString(", ")}")
                    appendLine("")
                    append(item.content.take(500))
                    if (item.content.length > 500) append("...")
                }
            }
            
            listOf(UIMessagePart.Text(formatted))
        }
    ),
    
    // 获取所有分类
    Tool(
        name = "knowledge_list_categories",
        description = "列出知识库中的所有分类和对应的条目数量",
        parameters = {
            InputSchema.Obj(emptyJsonObject())
        },
        execute = {
            val categories = repository.getCategories(assistantId)
            val result = categories.joinToString("\n") { cat ->
                val count = repository.getKnowledgeByCategory(cat, assistantId).size
                "- $cat ($count 条)"
            }
            listOf(UIMessagePart.Text(result))
        }
    ),
    
    // 获取所有标签
    Tool(
        name = "knowledge_list_tags",
        description = "列出知识库中的所有标签",
        parameters = {
            InputSchema.Obj(emptyJsonObject())
        },
        execute = {
            val tags = repository.getTags(assistantId)
            listOf(UIMessagePart.Text(tags.joinToString(", ")))
        }
    )
)

private val emptyJsonObject = buildJsonObject {}

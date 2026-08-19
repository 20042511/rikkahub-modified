# RikkaHub 知识库功能改造方案

## 概述

为 RikkaHub Android 应用添加知识库功能，支持本地知识管理与云端协同，使 LLM 能够检索用户个人知识库回答问题。

## 架构设计

```
┌─────────────────────────────────────────────────────────────┐
│                    RikkaHub App                            │
├─────────────────────────────────────────────────────────────┤
│  UI Layer                                                   │
│  ├── KnowledgePage.kt          知识库主页                   │
│  └── KnowledgeViewModel.kt      ViewModel                   │
├─────────────────────────────────────────────────────────────┤
│  Tool Layer                                                 │
│  └── KnowledgeTools.kt          LLM 工具接口                │
├─────────────────────────────────────────────────────────────┤
│  Data Layer                                                 │
│  ├── KnowledgeRepository.kt     数据仓库                    │
│  ├── KnowledgeDAO.kt             数据库访问层                │
│  └── KnowledgeEntity.kt          实体定义                    │
├─────────────────────────────────────────────────────────────┤
│  Storage                                                    │
│  ├── SQLite (Room)              本地存储                     │
│  └── FTS5                       全文搜索索引                │
└─────────────────────────────────────────────────────────────┘
```

## 新增文件

| 文件 | 路径 | 说明 |
|------|------|------|
| KnowledgeEntity.kt | data/db/entity/ | 知识库实体，支持分类、标签、来源 |
| KnowledgeDAO.kt | data/db/dao/ | DAO 接口，含 FTS5 查询 |
| KnowledgeRepository.kt | data/repository/ | 仓库层，封装 CRUD 和搜索 |
| KnowledgeItem.kt | data/model/ | 数据模型 |
| KnowledgeTools.kt | data/ai/tools/ | LLM 工具，提供 search/list_categories 等 |
| KnowledgePage.kt | ui/pages/knowledge/ | Compose UI 页面 |
| KnowledgeViewModel.kt | ui/pages/knowledge/ | ViewModel |
| Migration_23_24.kt | data/db/migrations/ | FTS5 初始化迁移 |
| Migration_24_25.kt | data/db/migrations/ | 知识库表创建迁移 |

## 修改文件

| 文件 | 修改内容 |
|------|---------|
| AppDatabase.kt | 添加 KnowledgeEntity、KnowledgeDAO、版本升级至 25 |
| RepositoryModule.kt | 注册 KnowledgeRepository |
| ViewModelModule.kt | 注册 KnowledgeViewModel |
| DataSourceModule.kt | 注册 KnowledgeDAO、更新 GenerationHandler |
| GenerationHandler.kt | 添加知识库工具注入 |
| LocalToolOption.kt | 添加 KnowledgeBase 选项 |
| RouteActivity.kt | 添加 Knowledge 路由和 Screen |

## 核心功能

### 1. 知识库管理
- 添加、编辑、删除知识条目
- 分类：study/work/personal/project/reference/code/design
- 标签系统（逗号分隔）
- 来源追踪：manual/local/cloud/import

### 2. 检索能力
- FTS5 全文搜索
- 按分类过滤
- 按标签过滤
- 热门知识排序（usage_count）

### 3. LLM 集成
- `knowledge_search` 工具：支持 search/get_by_category/get_by_tag/get_popular
- `knowledge_list_categories` 工具：列出所有分类及数量
- `knowledge_list_tags` 工具：列出所有标签
- 通过 `LocalToolOption.KnowledgeBase` 启用

### 4. 数据同步扩展点
```kotlin
// 云端同步预留字段
sourceType: "cloud"  // 来源类型
sourcePath: String?  // 文件路径或 URL
sourceMetadata: String?  // JSON 元数据
embedding: String?  // 向量嵌入（用于语义搜索）
```

## 使用方式

### 启用知识库工具
在助手设置中添加 `KnowledgeBase` 到 `localTools`：
```kotlin
val assistant = Assistant(
    localTools = listOf(
        LocalToolOption.TimeInfo,
        LocalToolOption.KnowledgeBase  // 新增
    )
)
```

### LLM 调用示例
```json
// 搜索知识
{"action": "search", "query": "半导体物理", "limit": 5}

// 按分类获取
{"action": "get_by_category", "category": "study"}

// 按标签获取
{"action": "get_by_tag", "tag": "AI"}

// 获取热门
{"action": "get_popular", "limit": 10}
```

## 与现有 Memory 系统的关系

| 特性 | Memory | Knowledge |
|------|--------|-----------|
| 用途 | 对话记忆（简短） | 知识库（详细） |
| 大小 | 几十字到几百字 | 可扩展到数千字 |
| 结构 | 纯文本 | 标题+内容+分类+标签 |
| 检索 | 全量加载 | 全文搜索+过滤 |
| 触发 | enableMemory | LocalToolOption |

## 后续扩展建议

1. **文件导入**：支持 PDF/Markdown/TXT 导入，自动分块
2. **语义搜索**：集成嵌入模型，将 embedding 字段填入
3. **云端同步**：通过 WebDAV/S3 同步知识库
4. **批量操作**：支持导入/导出 JSON
5. **关联推荐**：根据当前对话自动推荐相关知识

## 编译检查

需要验证：
1. AppDatabase.kt 中是否有重复的 folderDao() 声明（需修复）
2. Migration_24_25.kt 中的 @ColumnInfo 注解是否正确
3. 所有新文件的 import 语句是否完整

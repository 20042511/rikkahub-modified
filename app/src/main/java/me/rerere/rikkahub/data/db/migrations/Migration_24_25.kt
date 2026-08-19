package me.rerere.rikkahub.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * 迁移 24 -> 25: 创建知识库表 + FTS5 全文索引
 */
val MIGRATION_24_25 = object : Migration(24, 25) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 1. 创建 knowledge 表
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS knowledge (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                assistant_id TEXT,
                title TEXT NOT NULL DEFAULT '',
                content TEXT NOT NULL DEFAULT '',
                category TEXT NOT NULL DEFAULT 'study',
                source_type TEXT NOT NULL DEFAULT 'manual',
                source_path TEXT,
                source_metadata TEXT,
                tags TEXT NOT NULL DEFAULT '',
                embedding TEXT,
                created_at INTEGER NOT NULL DEFAULT 0,
                updated_at INTEGER NOT NULL DEFAULT 0,
                usage_count INTEGER NOT NULL DEFAULT 0
            )
        """)
        
        database.execSQL("CREATE INDEX IF NOT EXISTS idx_knowledge_assistant ON knowledge(assistant_id)")
        database.execSQL("CREATE INDEX IF NOT EXISTS idx_knowledge_category ON knowledge(category)")
        database.execSQL("CREATE INDEX IF NOT EXISTS idx_knowledge_source_type ON knowledge(source_type)")
        
        // 2. 创建 FTS5 虚拟表（外部内容表模式，关联 knowledge）
        database.execSQL("""
            CREATE VIRTUAL TABLE IF NOT EXISTS knowledge_fts USING fts5(
                title,
                content,
                content='knowledge',
                content_rowid='id'
            )
        """)
        
        // 3. 填充现有数据
        database.execSQL("""
            INSERT INTO knowledge_fts(rowid, title, content)
            SELECT id, title, content FROM knowledge
        """)
        
        // 4. 创建同步触发器
        database.execSQL("""
            CREATE TRIGGER knowledge_ai AFTER INSERT ON knowledge
            BEGIN
                INSERT INTO knowledge_fts(rowid, title, content)
                VALUES (new.id, new.title, new.content);
            END
        """)
        
        database.execSQL("""
            CREATE TRIGGER knowledge_ad AFTER DELETE ON knowledge
            BEGIN
                DELETE FROM knowledge_fts WHERE rowid = old.id;
            END
        """)
        
        database.execSQL("""
            CREATE TRIGGER knowledge_au AFTER UPDATE ON knowledge
            BEGIN
                UPDATE knowledge_fts 
                SET title = new.title, content = new.content 
                WHERE rowid = old.id;
            END
        """)
    }
}

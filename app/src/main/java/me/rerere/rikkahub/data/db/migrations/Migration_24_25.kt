package me.rerere.rikkahub.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * 迁移 24 -> 25: 添加知识库表
 */
val MIGRATION_24_25 = object : Migration(24, 25) {
    override fun migrate(database: SupportSQLiteDatabase) {
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
    }
}

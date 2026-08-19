package me.rerere.rikkahub.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * 迁移 23 -> 24: 添加 FTS5 触发器
 */
val MIGRATION_23_24 = object : Migration(23, 24) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 创建 FTS5 虚拟表
        database.execSQL("""
            CREATE VIRTUAL TABLE IF NOT EXISTS knowledge_fts USING fts5(
                title,
                content,
                content='knowledge',
                content_rowid='id'
            )
        """)
        
        // 填充现有数据
        database.execSQL("""
            INSERT INTO knowledge_fts(rowid, title, content)
            SELECT id, title, content FROM knowledge
        """)
        
        // 创建触发器
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

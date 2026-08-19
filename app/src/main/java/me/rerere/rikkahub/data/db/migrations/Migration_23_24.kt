package me.rerere.rikkahub.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * 迁移 23 -> 24: 空迁移
 *
 * 注：knowledge 表在 24->25 才创建，FTS 虚拟表和触发器
 * 随 knowledge 表一并创建（见 Migration_24_25），避免引用不存在的表。
 */
val MIGRATION_23_24 = object : Migration(23, 24) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 无操作：knowledge 表尚未创建
    }
}

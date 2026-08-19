# RikkaHub 知识库功能 - CI/CD 部署指南

## 完成状态

✅ 代码已修改并提交（commit: `570b680`）
- 18 个文件变更，+2044 / -730 行
- 新增 9 个文件，修改 7 个文件

## 推送步骤

### 1. Fork 项目到 GitHub
1. 打开 https://github.com/rikkahub/rikkahub
2. 点击右上角 **Fork** 按钮
3. 创建到你自己的账号

### 2. 添加远程仓库并推送

```bash
# 克隆你的 fork（如果你有本地副本）
cd /var/minis/workspace/rikkahub

# 添加你的 fork 为远程仓库（替换 YOUR_USERNAME）
git remote add myfork https://github.com/YOUR_USERNAME/rikkahub.git

# 推送到你的 fork
git push myfork master
```

或者直接在 GitHub 网页操作：
1. 打开 https://github.com/YOUR_USERNAME/rikkahub
2. 点击 **Code** → **Add file** → **Upload files**
3. 上传所有修改的文件

### 3. 配置 GitHub Secrets

在你的 GitHub 仓库页面：
1. 进入 **Settings** → **Secrets and variables** → **Actions**
2. 添加以下 Secrets：

| Secret 名称 | 说明 |
|------------|------|
| `KEY_BASE64` | keystore 文件的 base64 编码 |
| `SIGNING_CONFIG` | 签名配置（见下方示例） |
| `GOOGLE_SERVICES_JSON` | google-services.json 内容 |

#### SIGNING_CONFIG 格式示例
```properties
storeFile=/path/to/keystore.jks
storePassword=your_store_password
keyAlias=your_key_alias
keyPassword=your_key_password
```

#### 生成 keystore
```bash
keytool -genkeypair -v -dname "CN=RikkaHub, OU=RikkaHub, O=RikkaHub, L=City, ST=State, C=CN" \
  -keystore rikkahub.keystore -alias rikkahub \
  -keyalg RSA -keysize 2048 -validity 10000
# 然后 base64 编码
base64 rikkahub.keystore > rikkahub.keystore.b64
```

### 4. 触发构建

有两种方式：

**方式 A：手动触发（推荐）**
1. 进入仓库的 **Actions** 标签
2. 选择 **Daily Build** workflow
3. 点击 **Run workflow** → **Run workflow**

**方式 B：提交代码后自动触发**
- 下次 `schedule` cron 触发时（UTC 18:00 / 北京时间次日 02:00）

## 构建产物

构建成功后，APK 会自动发布为 **Prerelease**：
- 固定 tag: `nightly`
- 位置：https://github.com/YOUR_USERNAME/rikkahub/releases

## 使用新代码

构建完成后，你可以：
1. 下载 nightly APK 安装测试
2. 在助手中启用 `KnowledgeBase` 工具：
   ```kotlin
   localTools = listOf(
       LocalToolOption.TimeInfo,
       LocalToolOption.KnowledgeBase  // 新增
   )
   ```
3. 导航到知识库页面管理知识条目

## 注意事项

- 项目需要 `google-services.json`，可从原项目获取或暂时禁用 Firebase
- 如需跳过 Firebase 签名，可修改 `build.gradle.kts` 中的插件依赖
- CI 会同时构建 arm64-v8a 和 x86_64 两个 ABI 的 APK

# Element Call 本地仓库与 Git 远程说明

本仓库根目录下的 `element-call/` 为 [element-hq/element-call](https://github.com/element-hq/element-call) 的本地克隆，用于本地构建嵌入式前端、对照源码或打补丁。模块 **`features/call/impl` 已不再依赖 Maven 上的 `io.element.android:element-call-embedded`**：每次 Android `preBuild` 前会把 `element-call/dist` 复制到该模块的生成式 assets（路径与 `WebViewAssetLoader` 使用的 `element-call/index.html` 一致）。上游打包说明见 [Embedded vs Standalone](https://github.com/element-hq/element-call/blob/livekit/docs/embedded-standalone.md)。

## 与 Android 工程集成（构建通话 Web 资源）

1. 在 `element-call` 目录生成嵌入式前端产物（需 **Node 22+**、Yarn 4；首次会下载依赖）：

   ```bash
   cd element-call
   corepack prepare yarn@4.7.0 --activate
   corepack yarn install --immutable
   ```

   若 `yarn install` 在拉取 Git 依赖 `matrix-js-sdk` 时报 `invalid key:  core.autocrlf`，多为本机 Git/Yarn 在 Windows 上的已知问题。本仓库已改为使用 **`vendor/matrix-js-sdk` 本地克隆 + `package.json` 中 `portal:`** 指向该目录，避免 Yarn 再执行 `git clone`。首次仍需在 SDK 目录编译出 `lib/`：

   ```bash
   cd vendor/matrix-js-sdk
   corepack yarn install
   corepack yarn build
   cd ../..
   ```

   再执行嵌入式生产构建（Windows 下若 `yarn build:embedded:production` 因 `NODE_OPTIONS=` 语法失败，可用下面等价命令）：

   ```bash
   set NODE_OPTIONS=--max-old-space-size=16384
   corepack yarn exec vite build --config vite-embedded.config.ts
   ```

   成功后应存在 **`element-call/dist/index.html`**。

2. 回到仓库根目录照常编译应用（例如 `./gradlew :app:compileGplayDebugKotlin`）。若缺少 `dist`，Gradle 会失败并提示上述命令。

3. 修改 Element Call 前端后，重新执行 `yarn build:embedded:production` 再编 Android，即可带上新版本。

说明：官方 CI 在发布 Release 时仍会把同一套产物打成 AAR 发到 Maven；本仓库选择本地 `dist` 直连，便于与你 fork 的 `element-call` 联调。

## 远程仓库约定

与主工程（`origin` = 你的 fork、`upstream` = 官方）对齐，`element-call` 目录内建议：

| 远程名    | 含义     | 示例 URL |
|-----------|----------|----------|
| `origin`  | 你的 fork | `https://github.com/<你的用户名>/element-call.git` |
| `upstream` | 上游官方 | `https://github.com/element-hq/element-call.git` |

当前本机若已按此配置，则 `git remote -v` 应类似：

```text
origin   https://github.com/<你的用户名>/element-call.git (fetch/push)
upstream https://github.com/element-hq/element-call.git (fetch/push)
```

### 首次配置（若尚未设置）

在仓库根目录执行：

```bash
cd element-call
git remote rename origin upstream
git remote add origin https://github.com/<你的用户名>/element-call.git
git fetch origin
git branch --set-upstream-to=origin/livekit livekit
```

将 `<你的用户名>` 换成你的 GitHub 用户名。主开发分支一般为 `livekit`。

## 日常 Git 操作备忘

### 从官方同步到本地

```bash
cd element-call
git fetch upstream
git checkout livekit
git merge upstream/livekit
# 或使用 rebase，按团队习惯任选其一
```

### 将本地提交推送到你的 fork

```bash
cd element-call
git push origin livekit
```

若当前分支已跟踪 `origin/livekit`，可直接：

```bash
git push
```

### 主工程（Element X Android）的远程（参考）

- `origin`：你的 fork（例如 `ElementX`）
- `upstream`：`element-hq/element-x-android`

与 `element-call` 的 `upstream` 无冲突，二者独立维护。

## 克隆深度说明

若曾使用 `git clone --depth 1` 浅克隆，后续需要完整历史时可：

```bash
cd element-call
git fetch --unshallow
```

## 相关链接

- 上游仓库：<https://github.com/element-hq/element-call>
- 嵌入式包与 AAR 发布说明：仓库内 `docs/embedded-standalone.md`（以你本地的 `element-call` 为准）

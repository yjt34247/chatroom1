# 在线聊天室系统 - 1.0版本

## 项目概述

这是一个基于Java Servlet技术实现的简单在线聊天室系统。系统支持用户登录、实时消息发送、在线用户管理等功能，使用纯Servlet/JSP技术栈实现，无需数据库依赖。

## 系统架构

### 技术栈
- **后端**: Java 17 + Servlet 6.0 (Jakarta EE)
- **前端**: HTML + CSS+ JavaScript
- **构建工具**: Maven
- **服务器**: 支持Jakarta EE 6.0的Servlet容器 (如Tomcat 10+)

### 项目结构
```
chatroom/
├── src/main/java/com/example/chatroom/
│   ├── model/
│   │   └── Message.java           # 消息数据模型
│   └── servlet/
│       ├── LoginServlet.java      # 登录处理
│       ├── LogoutServlet.java     # 退出处理
│       ├── SendMessageServlet.java # 消息发送
│       └── GetMessagesServlet.java # 消息获取
├── src/main/webapp/
│   ├── index.html                 # 登录页面
│   ├── chat.html                  # 聊天页面
│   ├── WEB-INF/
│   │   └── web.xml               # Servlet配置
│   ├── css/
│   │   └── style.css             # 样式文件
│   └── js/
│       ├── auth.js               # 登录相关JS
│       └── chat.js               # 聊天相关JS
├── pom.xml                        # Maven配置
└── README.md                     # 项目说明
```
# 系统架构设计

## 1. 整体架构层次

### 表现层（前端）
- **HTML页面**：index.html（登录页）、chat.html（聊天页）
- **CSS样式**：style.css，统一界面风格
- **JavaScript**：
  - auth.js：登录验证和错误处理
  - chat.js：消息轮询和界面更新

### 控制层（Servlet）
- **LoginServlet**：处理用户登录，验证用户名唯一性
- **LogoutServlet**：处理用户退出，清理会话数据
- **SendMessageServlet**：接收和存储聊天消息
- **GetMessagesServlet**：提供消息查询接口

### 数据层（存储）
- **ServletContext**：全局共享存储
  - onlineUsers：在线用户列表（String数组）
  - messages：聊天消息记录（Message对象数组）
- **HttpSession**：用户会话存储
  - username：当前登录用户名

## 2. 核心功能模块详细设计

### 2.1 用户管理模块

**登录功能**：
1. **客户端验证**：JavaScript检查用户名非空
2. **服务端验证**：
   - 空用户名检测：`username.trim().isEmpty()`
   - 重复登录检测：遍历onlineUsers数组比对
3. **会话创建**：验证通过后创建HttpSession
4. **状态更新**：将用户名添加到全局在线列表

**会话管理**：
- 会话创建时机：仅在登录成功时
- 会话验证机制：所有受限操作前检查session有效性
- 会话属性：存储username标识用户身份

**在线用户列表**：
- 存储位置：ServletContext中的onlineUsers数组
- 更新机制：
  - 登录时：数组扩容，末尾添加新用户
  - 退出时：数组缩容，移除相应用户
- 并发安全：依赖Servlet单线程处理模型

### 2.2 消息管理模块

**消息发送**：
1. 接收POST请求，获取message参数
2. 验证用户会话有效性
3. 检查消息内容非空
4. 创建Message对象（username + content）
5. 将消息添加到全局消息数组

**消息存储**：
- 数据结构：Message数组，按发送顺序存储
- 存储位置：ServletContext中的messages属性
- 扩容机制：每次添加新消息时创建新数组并复制

**实时刷新**：
- 轮询机制：前端每1秒请求`/getMessages`
- 数据格式：Servlet返回格式化HTML片段
- 更新方式：JavaScript替换messages容器内容

### 2.3 会话安全模块

**访问控制**：
- 拦截策略：所有聊天相关操作前验证session
- 未登录处理：重定向到登录页面
- 验证代码示例：
  ```java
  HttpSession session = request.getSession(false);
  if (session == null || session.getAttribute("username") == null) {
      response.sendRedirect("index.html");
      return;
  }
  ```

**会话验证**：
- 验证范围：
  - 发送消息（SendMessageServlet）
  - 获取消息（GetMessagesServlet）
  - 页面访问（chat.html由Servlet保护）
- 会话获取方式：`getSession(false)`避免创建新会话

**安全退出**：
1. 从在线用户列表移除用户名
2. 使当前会话失效（session.invalidate()）
3. 显示退出确认页面
4. 提供返回登录页链接

**数据一致性保障**：
- 登录/退出时同步更新在线用户列表
- 消息始终关联有效用户名
- 防止已退出用户继续操作系统

## 3. 数据流设计

### 登录数据流
用户输入 → 客户端验证 → POST /login → 服务端验证 → 更新在线列表 → 创建会话 → 重定向到聊天页

### 消息数据流
用户输入消息 → POST /sendMessage → 会话验证 → 消息存储 → 返回聊天页 → 定时轮询获取消息 → 前端渲染

### 退出数据流
点击退出链接 → GET /logout → 从在线列表移除用户 → 会话失效 → 显示退出页面

### 环境要求
- JDK 17 或更高版本
- Apache Maven 3.6+
- Servlet容器 (Tomcat 10+ 或 Jetty 11+)


## 使用说明

### 1. 登录聊天室
1. 访问应用首页
2. 输入唯一的用户名
3. 点击"进入聊天室"按钮

### 2. 发送消息
1. 在聊天页面底部的输入框中输入消息
2. 点击"发送"按钮或按Enter键
3. 消息将立即显示在聊天区域

### 3. 查看在线用户
- 系统自动维护在线用户列表（当前版本在ServletContext中存储）

### 4. 退出聊天室
- 点击页面右上角的"退出"链接
- 系统将清理会话并跳转到登录页面


### 应用配置
- 消息存储：使用ServletContext属性存储
- 在线用户：使用ServletContext属性存储
- 会话超时：依赖容器的默认会话超时设置

## 设计特点

### 优势
1. **无数据库依赖**: 使用内存存储，适合演示和小规模使用
2. **简单轻量**: 纯Servlet实现，无复杂框架依赖
3. **实时性**: 轮询方式实现准实时消息更新
4. **会话安全**: 完善的会话验证机制

### 限制
1. **单节点存储**: 所有数据存储在单个JVM内存中
2. **无持久化**: 服务器重启后数据丢失
3. **扩展性有限**: 不适合大规模并发场景
4. **基础功能**: 仅实现核心聊天功能

## 扩展建议

### 功能增强
1. **消息持久化**: 添加数据库存储消息记录
2. **私聊功能**: 实现用户之间的私密聊天
3. **文件传输**: 支持图片、文件上传
4. **用户状态**: 显示用户在线/离开/忙碌状态
5. **聊天室管理**: 创建多个聊天室

### 技术改进
1. **WebSocket**: 替换轮询为WebSocket实现真正实时
2. **Spring Boot**: 迁移到Spring Boot简化配置
3. **数据库**: 添加MySQL/PostgreSQL持久层
4. **前端框架**: 使用React/Vue.js改进前端体验


## 版本信息

### v1.0 功能
- ✓ 用户登录/退出
- ✓ 实时消息发送
- ✓ 在线用户管理
- ✓ 基础UI界面
- ✓ 会话安全控制

# 基于 Spring Boot 的大学生就业信息管理系统

## 项目简介

本系统是一个基于 Spring Boot 和 Vue 3 的大学生就业信息管理系统，旨在为学生、企业和管理员提供便捷的就业信息管理服务。系统采用前后端分离架构，后端使用 Spring Boot 3.2.0，前端使用 Vue 3 + Element Plus，数据库使用 MySQL。

## 系统功能

### 学生功能

- 用户注册和登录
- 浏览和搜索就业岗位
- 查看岗位详情并投递简历
- 个人简历管理（基本信息、工作经历）
- 查看申请状态和面试安排
- 取消岗位申请
- 个人中心管理

### 企业功能

- 用户注册和登录
- 首次登录创建企业信息
- 发布和管理招聘岗位
- 查看收到的简历和申请
- 处理投递申请（安排面试、拒绝）
- 安排面试（时间、地点、备注）
- 录入面试结果（通过/未通过）
- 企业资料管理（含营业执照上传）

### 管理员功能

- 用户管理（学生、企业用户）
- 企业认证审核
- 统计分析（用户数、岗位数、投递数等）
- 系统管理

## 技术栈

### 后端

- **框架**：Spring Boot 3.2.0、Spring Security、Spring Data JPA
- **数据库**：MySQL 8.0+
- **构建工具**：Maven
- **其他**：Lombok、Jackson

### 前端

- **框架**：Vue 3.5.13、Vue Router 4.4.5
- **UI 组件**：Element Plus 2.13.2
- **HTTP 客户端**：Axios 1.7.9
- **构建工具**：Vite 6.0.5
- **样式**：SCSS

## 项目结构

```
employment-management/
├── src/
│   ├── main/
│   │   ├── frontend/           # 前端项目
│   │   │   ├── src/
│   │   │   │   ├── assets/      # 静态资源
│   │   │   │   ├── components/  # 组件
│   │   │   │   │   ├── company/ # 企业相关组件
│   │   │   │   │   ├── student/ # 学生相关组件
│   │   │   │   │   └── resume/  # 简历相关组件
│   │   │   │   ├── router/      # 路由配置
│   │   │   │   ├── views/       # 页面
│   │   │   │   │   ├── admin/   # 管理员页面
│   │   │   │   │   ├── company/ # 企业页面
│   │   │   │   │   └── student/ # 学生页面
│   │   │   │   └── main.js      # 应用入口
│   │   │   ├── index.html       # HTML 模板
│   │   │   ├── package.json     # 前端依赖
│   │   │   └── vite.config.js   # Vite 配置
│   │   ├── java/                # 后端代码
│   │   │   └── com/employment/
│   │   │       ├── Application.java          # 应用主类
│   │   │       ├── config/                   # 配置类
│   │   │       ├── controller/               # 控制器
│   │   │       ├── entity/                   # 实体类
│   │   │       ├── repository/               # 数据仓库
│   │   │       └── service/                  # 服务层
│   │   └── resources/                        # 资源
│   │       ├── application.properties        # 应用配置
│   │       ├── static/                       # 静态文件（前端构建产物）
│   │       └── templates/                    # 模板文件
│   └── test/                                 # 测试资源
├── .gitignore                                # Git 忽略文件
├── pom.xml                                   # Maven 配置
├── README.md                                 # 项目说明
└── 环境配置指南.md                            # 环境配置指南
```

## 环境配置

### 1. 安装 JDK 17

1. 下载 JDK 17 安装包
   - 官方下载地址：https://www.oracle.com/java/technologies/downloads/
   - 选择 Windows x64 Installer

2. 安装 JDK
   - 双击安装包，按照向导完成安装
   - 记住安装路径（默认：C:\Program Files\Java\jdk-17）

3. 配置环境变量
   - 右键"此电脑" → "属性" → "高级系统设置" → "环境变量"
   - 在"系统变量"中点击"新建"，添加：
     - 变量名：JAVA_HOME
     - 变量值：JDK 安装路径（如：C:\Program Files\Java\jdk-17）
   - 编辑"Path"变量，在开头添加：%JAVA_HOME%\bin
   - 点击"确定"保存

4. 验证安装
   - 打开命令提示符，输入：`java -version`
   - 若显示 JDK 版本信息，则安装成功

### 2. 安装 MySQL

1. 下载 MySQL 安装包
   - 官方下载地址：https://dev.mysql.com/downloads/installer/

2. 安装 MySQL
   - 按照安装向导完成安装
   - 设置 root 用户密码为：123456

3. 创建数据库
   - 打开命令行或 MySQL Workbench
   - 执行：`CREATE DATABASE employment_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`

### 3. 安装 Node.js

1. 下载 Node.js 安装包
   - 官方下载地址：https://nodejs.org/en/download/
   - 选择 LTS 版本

2. 安装 Node.js
   - 双击安装包，按照向导完成安装

3. 验证安装
   - 打开命令提示符，输入：`node -v`
   - 若显示 Node.js 版本信息，则安装成功

## 项目运行

### 方法一：完整运行（推荐）

1. **启动后端服务**
   - 打开命令提示符，进入项目根目录：`cd employment-management`
   - 执行：`mvn spring-boot:run`
   - 等待后端服务启动完成

2. **启动前端服务**
   - 打开新的命令提示符，进入前端目录：`cd src\main\frontend`
   - 执行：`npm install`（首次运行需要安装依赖）
   - 执行：`npm run dev`
   - 等待前端服务启动完成

3. **访问系统**
   - 前端：http://localhost:3000
   - 后端 API：http://localhost:8080/api

### 方法二：使用构建产物运行

1. **构建前端**
   - 进入前端目录：`cd src\main\frontend`
   - 执行：`npm install`
   - 执行：`npm run build`

2. **构建并运行后端**
   - 回到项目根目录
   - 执行：`mvn clean package`
   - 执行：`java -jar target/employment-management-0.0.1-SNAPSHOT.jar`

3. **访问系统**
   - 前端：http://localhost:8080
   - 后端 API：http://localhost:8080/api

## 系统使用

### 1. 注册账号

- 学生用户：选择"学生"角色
- 企业用户：选择"企业"角色
- 管理员账号：默认已配置（用户名：admin，密码：admin123）

### 2. 登录系统

- 使用注册的账号登录
- 根据角色进入相应的功能界面
- 企业用户首次登录需创建企业信息

### 3. 功能使用

#### 学生端

- **就业岗位**：浏览和搜索岗位，查看详情，投递申请
- **我的申请**：查看投递记录，跟踪申请状态（待处理/已查看/面试中/已录用/已拒绝），查看面试安排，取消申请
- **简历管理**：编辑个人简历，添加工作经历
- **个人中心**：管理个人信息

#### 企业端

- **岗位管理**：发布、编辑、删除招聘岗位
- **申请管理**：查看收到的简历，处理申请（安排面试/拒绝），录入面试结果
- **企业资料**：创建和编辑企业信息，上传营业执照

#### 管理员端

- **用户管理**：管理学生和企业用户，审核企业认证
- **统计分析**：查看系统统计数据和趋势

## 核心功能模块

### 1. 认证与授权

- 基于 Spring Security 的认证机制
- JWT 令牌验证
- 基于角色的权限控制

### 2. 岗位管理

- 岗位发布、编辑、删除
- 岗位搜索和筛选
- 岗位状态管理（上架/下架）

### 3. 简历管理

- 学生基本信息维护
- 工作经历添加、编辑、删除
- 简历预览

### 4. 申请与面试流程

- 学生投递简历
- 企业查看申请和简历
- 企业安排面试（时间、地点、备注）
- 学生查看面试安排
- 企业录入面试结果（通过→录用/未通过→拒绝）
- 学生查看面试结果
- 申请状态流转：待处理 → 已查看 → 面试中 → 已录用/已拒绝

### 5. 企业管理

- 企业资料创建和编辑
- 营业执照上传和预览
- 首次登录引导创建企业

### 6. 用户管理

- 用户注册和登录
- 用户信息管理
- 角色管理

### 7. 统计分析

- 用户数量统计
- 岗位数量统计
- 申请数量统计
- 趋势分析

## API 接口

### 认证接口

- `POST /api/login` - 用户登录
- `POST /api/register` - 用户注册
- `GET /api/check-username` - 检查用户名是否存在

### 岗位接口

- `GET /api/jobs` - 获取岗位列表
- `GET /api/jobs/{id}` - 获取岗位详情
- `POST /api/company/jobs` - 发布岗位（企业）
- `PUT /api/company/jobs/{id}` - 更新岗位（企业）
- `DELETE /api/company/jobs/{id}` - 删除岗位（企业）

### 申请接口

- `POST /api/applications/job/{jobId}` - 投递申请（学生）
- `GET /api/applications/student` - 获取学生申请列表
- `GET /api/company/applications` - 获取企业收到的申请列表
- `DELETE /api/applications/{id}` - 取消申请（学生）

### 面试流程接口

- `PUT /api/company/applications/{id}/schedule-interview` - 安排面试（企业）
- `PUT /api/company/applications/{id}/interview-result` - 更新面试结果（企业）
- `GET /api/company/applications/{id}/student-resume` - 获取学生简历详情（企业）

### 简历接口

- `GET /api/students/{id}/resume` - 获取学生简历
- `PUT /api/students/{id}/resume` - 更新学生简历
- `POST /api/students/{id}/work-experiences` - 添加工作经历
- `PUT /api/students/{id}/work-experiences/{expId}` - 更新工作经历
- `DELETE /api/students/{id}/work-experiences/{expId}` - 删除工作经历

### 企业接口

- `GET /api/company/profile` - 获取企业资料
- `POST /api/company/profile` - 创建企业资料
- `PUT /api/company/profile` - 更新企业资料

### 用户接口

- `GET /api/users/{id}` - 获取用户信息
- `PUT /api/users/{id}` - 更新用户信息
- `GET /api/users` - 获取用户列表（管理员）

### 统计接口

- `GET /api/admin/dashboard` - 获取仪表盘统计数据

## 注意事项

1. 首次运行时，系统会自动创建数据库表结构
2. 企业用户首次登录需要创建企业信息后才能发布岗位
3. 系统默认使用 MySQL 数据库，确保数据库服务已启动
4. 请确保 JDK 17、MySQL 和 Node.js 已正确安装和配置
5. 面试流程支持完整的状态流转：待处理 → 已查看 → 面试中 → 已录用/已拒绝

## 联系方式

如有问题，请联系系统管理员。

# 基于Spring Boot的大学生就业信息管理系统

## 项目简介

本系统是一个基于Spring Boot和Vue 3的大学生就业信息管理系统，旨在为学生、企业和管理员提供便捷的就业信息管理服务。系统采用前后端分离架构，后端使用Spring Boot 3.2.0，前端使用Vue 3 + Element Plus，数据库使用MySQL。

## 系统功能

### 学生功能

- 用户注册和登录
- 浏览和搜索就业岗位
- 投递简历 查看岗位详情
- --个人信息管理
  接收系统通知

### 企业功能

- 用户注册和登录
- 发布和管理招聘岗位
- 查看收到的简历
- 处理投递申请（通过/拒绝）
- 企业信息管理

### 管理员功能

- 用户管理（学生、企业用户）
- 企业认证管理
- 统计分析（用户数、岗位数、投递数等）
  系 系统管理

## 技术栈

### 后端

- **框架**：Spring Boot 3.2.0、Spring Security、Spring Data JPA
- **数据库**：MySQL 8.0+
- **构建工具**：Maven
- **其他**：Lombok、Jackson

### 前端

- **框架**：Vue 3.5.13、Vue Router 4.4.5
- **UI组件**：Element Plus 2.13.2
- **HTTP客户端**：Axios 1.7.9
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
│   │   │   │   │   └── resume/  # 简历相关组件
│   │   │   │   ├── router/      # 路由配置
│   │   │   │   ├── views/       # 页面
│   │   │   │   │   ├── admin/   # 管理员页面
│   │   │   │   │   ├── company/ # 企业页面
│   │   │   │   │   └── student/ # 学生页面
│   │   │   │   └── main.js      # 应用入口
│   │   │   ├── index.html       # HTML模板
│   │   │   ├── package.json     # 前端依赖
│   │   │   └── vite.config.js   # Vite配置
│   │   ├── java/                # 后端代码
│   │   │   └── com/employment/
│   │   │       ├── Application.java          # 应用主类
│   │   │       ├── config/                   # 配置类
│   │   │       ├── controller/               # 控制器
│   │   │       ├── entity/                   # 实体类
│   │   │       └── repository/               # 数据仓库
│   │   └── resources/                        # 资源
│   │       ├── application.properties        # 应用配置
│   │       ├── static/                       # 静态文件（前端构建产物）
│   │       └── templates/                    # 模板文件
│   └── test/                                 # 测试资源
├── .gitignore                                # Git忽略文件
├── pom.xml                                   # Maven配置
├── README.md                                 # 项目说明
└── 环境配置指南.md                            # 环境配置指南
```

## 环境配置

### 1. 安装JDK 17

1. 下载JDK 17安装包
   - 官方下载地址：https://www.oracle.com/java/technologies/downloads/
   - 选择Windows x64 Installer

2. 安装JDK
   - 双击安装包，按照向导完成安装
   - 记住安装路径（默认：C:\Program Files\Java\jdk-17）

3. 配置环境变量
   - 右键"此电脑" → "属性" → "高级系统设置" → "环境变量"
   - 在"系统变量"中点击"新建"，添加：
     - 变量名：JAVA_HOME
     - 变量值：JDK安装路径（如：C:\Program Files\Java\jdk-17）
   - 编辑"Path"变量，在开头添加：%JAVA_HOME%\bin
   - 点击"确定"保存

4. 验证安装
   - 打开命令提示符，输入：`java -version`
   - 若显示JDK版本信息，则安装成功

### 2. 安装MySQL

1. 下载MySQL安装包
   - 官方下载地址：https://dev.mysql.com/downloads/installer/

2. 安装MySQL
   - 按照安装向导完成安装
   - 设置root用户密码为：123456

3. 创建数据库
   - 打开命令行或MySQL Workbench
   - 执行：`CREATE DATABASE employment CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`

### 3. 安装Node.js

1. 下载Node.js安装包
   - 官方下载地址：https://nodejs.org/en/download/
   - 选择LTS版本

2. 安装Node.js
   - 双击安装包，按照向导完成安装

3. 验证安装
   - 打开命令提示符，输入：`node -v`
   - 若显示Node.js版本信息，则安装成功

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
   - 后端API：http://localhost:8080/api

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
   - 后端API：http://localhost:8080/api

## 系统使用

### 1. 注册账号

- 学生用户：选择"学生"角色
- 企业用户：选择"企业"角色
- 管理员账号：默认已配置（用户名：admin，密码：admin123）

### 2. 登录系统

- 使用注册的账号登录
- 根据角色进入相应的功能界面

### 3. 功能使用

####学生端
- **就业岗位**：浏览和搜索岗位，查看详情，投递申请
- **我的申请**：查看投递记录，跟踪申请状态
- **个人中心**：管理个人信息

#### 企业端
- **岗位管理**：发布、编辑、删除招聘岗位
- **申请管理**：查看收到的简历，处理申请（通过/拒绝）
- **企业资料**：管理企业信息

#### 管理员端
- **用户管理**：管理学生和企业用户，审核企业认证
- **统计分析**：查看系统统计数据和趋势

## 核心功能模块

### 1. 认证与授权
- 基于Spring Security的认证机制
- JWT令牌验证
- 基于角色的权限控制

### 2. 岗位管理
- 岗位发布、编辑、删除
- 岗位搜索和筛选
- 岗位状态管理（上架/下架）

### 3. 申请管理
- 简历投递
- 申请状态跟踪
- 企业审核（通过/拒绝）

### 4. 用户管理
- 用户注册和登录
- 用户信息管理
- 角色管理

### 5. 统计分析
- 用户数量统计
- 岗位数量统计
- 申请数量统计
- 趋势分析

## API接口

### 认证接口
- `POST /api/login` - 用户登录
- `POST /api/register` - 用户注册
- `GET /api/check-username` - 检查用户名是否存在

### 岗位接口
- `GET /api/jobs` - 获取岗位列表
- `GET /api/jobs/{id}` - 获取岗位详情
- `POST /api/jobs` - 发布岗位（企业）
- `PUT /api/jobs/{id}` - 更新岗位（企业）
- `DELETE /api/jobs/{id}` - 删除岗位（企业）

### 申请接口
- `POST /api/applications` - 投递申请（学生）
- `GET /api/applications/student` - 获取学生申请列表
- `GET /api/applications/company` - 获取企业收到的申请列表
- `PUT /api/applications/{id}/approve` - 批准申请（企业）
- `PUT /api/applications/{id}/reject` - 拒绝申请（企业）

### 用户接口
- `GET /api/users/{id}` - 获取用户信息
- `PUT /api/users/{id}` - 更新用户信息
- `GET /api/users` - 获取用户列表（管理员）

### 统计接口
- `GET /api/admin/dashboard` - 获取仪表盘统计数据

## 注意事项

1. 首次运行时，系统会自动创建数据库表结构
2. 企业用户需要等待管理员审核后才能发布岗位
3. 系统默认使用MySQL数据库，确保数据库服务已启动
4. 请确保JDK 17、MySQL和Node.js已正确安装和配置

## 联系方式

如有问题，请联系系统管理员。
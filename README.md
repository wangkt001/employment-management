# 基于Spring Boot的大学生就业信息管理系统

## 项目简介

本系统是一个基于Spring Boot的大学生就业信息管理系统，旨在为学生、企业和管理员提供便捷的就业信息管理服务。

## 系统功能

### 学生功能
- 用户注册和登录
- 浏览和搜索就业岗位
- 查看岗位详情
- 投递简历
- 查看投递记录
- 接收系统通知

### 企业功能
- 用户注册和登录
- 发布和管理招聘岗位
- 查看收到的简历
- 处理投递申请
- 接收系统通知

### 管理员功能
- 用户管理
- 统计分析
- 系统管理
- 接收系统通知

## 技术栈

- **后端**：Spring Boot 3.2.0、Spring Security、Spring Data JPA
- **前端**：Thymeleaf、Bootstrap 5
- **数据库**：MySQL
- **构建工具**：Maven

## 环境配置

### 1. 安装JDK 17

1. 下载JDK 17安装包：
   - 官方下载地址：https://www.oracle.com/java/technologies/downloads/#jdk17-windows
   - 选择Windows x64 Installer

2. 安装JDK：
   - 双击安装包，按照向导完成安装
   - 记住安装路径（默认：C:\Program Files\Java\jdk-17）

3. 配置环境变量：
   - 右键"此电脑" → "属性" → "高级系统设置" → "环境变量"
   - 在"系统变量"中点击"新建"，添加：
     - 变量名：JAVA_HOME
     - 变量值：JDK安装路径（如：C:\Program Files\Java\jdk-17）
   - 编辑"Path"变量，在开头添加：%JAVA_HOME%\bin
   - 点击"确定"保存

4. 验证安装：
   - 打开命令提示符，输入：`java -version`
   - 若显示JDK版本信息，则安装成功

### 2. 安装MySQL

1. 下载MySQL安装包：
   - 官方下载地址：https://dev.mysql.com/downloads/installer/

2. 安装MySQL：
   - 按照安装向导完成安装
   - 设置root用户密码为：root

3. 创建数据库：
   - 打开MySQL命令行或MySQL Workbench
   - 执行：`CREATE DATABASE employment_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`

### 3. 运行项目

1. 克隆项目到本地

2. 修改数据库配置（可选）：
   - 编辑 `src/main/resources/application.properties` 文件
   - 修改数据库连接信息（默认使用root/root）

3. 构建项目：
   - 打开命令提示符，进入项目目录
   - 执行：`mvn clean package`

4. 运行项目：
   - 执行：`java -jar target/employment-management-system-1.0.0.jar`
   - 或使用IDE运行 `Application.java` 类

5. 访问系统：
   - 打开浏览器，访问：http://localhost:8080/employment

## 系统使用

### 1. 注册账号
- 学生用户：选择"学生"角色
- 企业用户：选择"企业"角色
- 管理员账号：默认已配置（用户名：admin，密码：admin123）

### 2. 登录系统
- 使用注册的账号登录
- 根据角色进入相应的功能界面

### 3. 功能使用
- **学生**：浏览岗位、投递简历、查看投递记录
- **企业**：发布岗位、管理岗位、查看收到的简历
- **管理员**：管理用户、查看统计分析

## 项目结构

```
employment-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/employment/
│   │   │   ├── Application.java          # 应用主类
│   │   │   ├── config/                   # 配置类
│   │   │   ├── controller/               # 控制器
│   │   │   ├── entity/                  # 实体类
│   │   │   └── repository/              # 数据仓库
│   │   └── resources/
│   │       ├── templates/                # Thymeleaf模板
│   │       └── application.properties    # 应用配置
│   └── test/                             # 测试代码
├── pom.xml                               # Maven配置
└── README.md                             # 项目说明
```

## 注意事项

1. 首次运行时，系统会自动创建数据库表结构
2. 企业用户需要等待管理员审核后才能发布岗位
3. 系统默认使用H2内存数据库，生产环境建议使用MySQL
4. 请确保JDK 17和MySQL已正确安装和配置

## 联系方式

如有问题，请联系系统管理员。
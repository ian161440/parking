# 校园停车场管理系统（Parking System）

基于 **Spring Boot + MyBatis + MySQL** 后端、**Vue 3 + Vite** 前端，以及 **Python 大数据/推荐算法示例** 的校园停车场管理系统。

本项目用于毕业设计答辩，重点展示：

- 完整的前后端分离架构与数据库设计；
- 普通用户与管理员两种角色的业务流程；
- 车位管理、预约管理、公告管理、违规管理等核心功能；
- 车位推荐与数据可视化示例；
- 预留的大数据/推荐算法 Python 示例代码，便于在论文中展开说明。

---

## 一、项目结构

```text
xiaoyu/
├─ parking-backend/       # Spring Boot 后端工程
│  ├─ src/main/java/
│  ├─ src/main/resources/
│  └─ sql/parking_system.sql   # 数据库建表及初始数据脚本
├─ parking-frontend/      # Vue 3 + Vite 前端工程
│  └─ src/views/
│     ├─ UserDashboard.vue    # 普通用户端
│     └─ AdminDashboard.vue   # 管理员端
└─ bigdata/               # 大数据/推荐算法示例（Python）
   └─ simple_cf_recommendation.py
```

---

## 二、技术栈与环境要求

- **后端**
  - JDK 17
  - Maven 3.8+
  - Spring Boot 2.7.x
  - MyBatis
  - MySQL 8.x

- **前端**
  - Node.js 18+（推荐 LTS）
  - npm 或 pnpm
  - Vue 3
  - Vite 5
  - Axios
  - ECharts（数据可视化）

- **数据库**
  - MySQL 8.x
  - 默认数据库名：`parking_system`
  - 示例开发环境的 root 密码：`123456`（请按自己本地环境调整）

- **大数据/推荐算法示例（可选运行）**
  - Python 3.9+（推荐 3.10）
  - 依赖：`pymysql`、`numpy`

---

## 三、数据库初始化

1. 启动 MySQL，并创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS parking_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 在项目根目录下找到 SQL 脚本：

```text
parking-backend/sql/parking_system.sql
```

3. 使用命令导入（根据你本机密码调整）：

```bash
mysql -u root -p parking_system < parking-backend/sql/parking_system.sql
mysql -u root -p parking_system < \Users\ian\Desktop\xiaoyu\xiaoyu\parking-backend\sql\parking_system.sql
```

4. 如需测试演示数据（车位、公告等），可以在 MySQL 中手动插入，也可以使用 README 中提供的示例 SQL。

---

## 四、后端运行步骤（parking-backend）

1. 修改数据库配置（如有需要）：

编辑 `parking-backend/src/main/resources/application.yml` 中的数据源配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/parking_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456  # 根据本地实际密码修改
```

2. 在后端工程目录下构建并运行：

```bash
cd parking-backend
mvn clean package -DskipTests

# 运行（任选其一）
# 方式1：使用 JAR
java -jar target/parking-backend-*.jar

# 方式2：直接运行 Spring Boot
mvn spring-boot:run
```

3. 后端默认地址：

- `http://localhost:8080`

---

## 五、前端运行步骤（parking-frontend）

1. 安装依赖：

```bash
cd parking-frontend
npm install
```

2. 开发模式运行：

```bash
npm run dev
```

3. 在浏览器访问：

- `http://localhost:5173`

如需打包构建：

```bash
npm run build
```

---

## 六、功能模块说明

### 1. 角色与登录

- **普通用户**
  - 注册/登录
  - 进入 `/user` 用户端主页

- **管理员**
  - 登录（通过数据库中 `sys_user.role = 'ADMIN'` 区分）
  - 进入 `/admin` 管理员后台

> 提示：可以在 `sys_user` 表中将某个用户的 `role` 字段修改为 `ADMIN` 来创建管理员账号，例如：
>
> ```sql
> UPDATE sys_user SET role = 'ADMIN' WHERE username = 'admin';
> ```

### 2. 普通用户端（/user）

入口：登录成功后，如果角色为 `USER`，前端路由跳转到 `/user`。

- **公告查看**
  - 接口：`GET /api/announcements`
  - 展示管理员发布的公告列表，支持置顶标识、发布时间展示等。

- **车位预约**
  - 可用车位列表：`GET /api/spaces/available`
  - 我的车辆列表：`GET /api/vehicles/my?userId=...`
  - 新建预约：`POST /api/reservations`
  - 我的预约记录：`GET /api/reservations/my?userId=...`

- **数据可视化（10张图表）**
  - 使用 ECharts 展示多维度数据分析，包含以下10张图表：
    1. **各楼层可用车位数量**（柱状图）- 展示不同楼层的车位分布
    2. **车位使用率**（饼图）- 已使用 vs 空闲车位比例
    3. **每日预约趋势**（折线图）- 一周内预约量变化趋势
    4. **预约状态分布**（饼图）- 待确认/使用中/已完成/已取消比例
    5. **违规记录统计**（柱状图）- 不同违规类型的数量统计
    6. **用户活跃度趋势**（折线图）- 月度用户活跃度变化
    7. **车辆类型分布**（饼图）- 小型车/新能源/大型车/特种车比例
    8. **时段预约热力**（柱状图）- 不同时段的预约热度
    9. **平均停车时长分布**（柱状图）- 停车时长区间统计
    10. **收入统计趋势**（折线图）- 月度收入变化趋势
  - 数据来源：系统各模块的真实业务数据
  - 支持实时数据更新和交互式分析

- **停车场推荐（爬虫 + 智能推荐）**
  - 接口：`GET /api/external-parking-lots/recommend`
  - 展示通过爬虫采集的周边停车场信息
  - 综合推荐评分算法：
    - 距离因素（权重40%）：距离越近分数越高
    - 价格因素（权重30%）：价格越低分数越高
    - 空余率因素（权重30%）：空余车位越多分数越高
  - 前端展示：推荐列表按评分降序排列，包含停车场名称、地址、距离、价格、空余车位、营业时间、联系电话、推荐评分等信息
  - 数据来源：Python 爬虫脚本 `bigdata/parking_lot_crawler.py` 定期采集并存入 `external_parking_lot` 表

- **车位推荐**
  - 接口：`GET /api/spaces/recommend?userId=...`
  - 前端展示推荐车位列表（车位 ID、编号、楼层、类型、状态、备注）。
  - 后端推荐逻辑：
    - 基于可用车位列表（`status = 'FREE'`），
    - 按楼层从低到高排序（楼层字符串尽量解析为数字），
    - 同一楼层内按车位编号（`spaceCode`）升序排序，
    - 返回前若干个（例如前 20 个）作为推荐结果。
  - 在论文中可以将此描述为“规则推荐算法”，同时说明已预留接口将来接入 Python/大数据模块输出。

- **个人中心（车辆管理）**
  - 查询当前用户车辆：`GET /api/vehicles/my?userId=...`
  - 新增车辆：`POST /api/vehicles`
  - 删除车辆：`DELETE /api/vehicles/{id}`

### 3. 管理员端（/admin）

入口：登录成功后，如果角色为 `ADMIN`，前端路由跳转到 `/admin`。

- **公告管理**
  - 查询公告列表：`GET /api/announcements`
  - 新建公告：`POST /api/announcements/admin`
  - 更新公告：`PUT /api/announcements/admin/{id}`
  - 删除公告：`DELETE /api/announcements/admin/{id}`

- **车位信息管理**
  - 查询全部车位：`GET /api/spaces/admin/all`
  - 新建车位：`POST /api/spaces/admin`
  - 更新车位：`PUT /api/spaces/admin/{id}`
  - 删除车位：`DELETE /api/spaces/admin/{id}`

- **车位预定管理**
  - 查询所有预约：`GET /api/reservations/admin/all`
  - 更新预约状态：`PUT /api/reservations/{id}/status?status=...`

- **违规管理**
  - 查询全部违规记录：`GET /api/violations/admin/all`
  - 新建违规记录：`POST /api/violations/admin`
  - 更新违规处理状态：`PUT /api/violations/admin/{id}/status?status=...&handlerId=...`

---

## 七、大数据 / 推荐算法示例（Python）

为满足毕业设计对"大数据分析/推荐算法"的要求，项目在 `bigdata/` 目录下提供了两个核心脚本：

### 1. 停车场爬虫脚本（parking_lot_crawler.py）

**功能说明：**
- 模拟爬取周边停车场数据（实际项目中可对接高德/百度地图API）
- 采集停车场名称、地址、距离、价格、空余车位等信息
- 计算综合推荐评分（距离40% + 价格30% + 空余率30%）
- 将数据存入 `external_parking_lot` 表

**使用方法：**

```bash
cd bigdata

# 安装依赖
pip install pymysql

# 运行爬虫
python parking_lot_crawler.py
```

运行成功后，会在控制台输出爬取的停车场数据和推荐排序结果，同时数据会存入数据库。

**前端展示：**
- 用户端 `/user` → "停车场推荐" 菜单
- 点击"获取周边停车场推荐"按钮
- 展示按评分排序的周边停车场列表

### 2. 协同过滤推荐脚本（simple_cf_recommendation.py）

**功能说明：**
- 从 MySQL 中读取 `reservation` 和 `parking_space` 数据
- 构建「用户-车位」交互矩阵（谁在哪些车位有过预约）
- 基于用户相似度的简单协同过滤思路，为每个用户计算推荐车位
- 将推荐结果打印出来，便于分析和在论文中展示

**使用方法：**

```bash
cd bigdata

# 安装依赖
pip install pymysql numpy

# 运行协同过滤推荐
python simple_cf_recommendation.py
```

运行成功后，会在控制台输出每个用户推荐的若干车位 ID 及对应的简单评分。

**扩展方向：**
- 将推荐结果写回 MySQL 某张表
- 在 Java 后端的 `ParkingSpaceServiceImpl.recommendSpacesForUser` 中读取该结果进行融合排序

> 注意：两个脚本中默认的数据库连接配置与后端保持一致（`localhost:3306`, 数据库名 `parking_system`, 用户名 `root`, 密码 `123456`），可根据实际环境自行修改。

---

## 八、推荐的演示流程（答辩用）

下面是一条推荐的演示脚本，可在答辩时按此顺序操作：

1. **管理员端**
   - 使用管理员账号登录（例如 `admin / 123456`）。
   - 在“公告管理”中新建一条维护/优惠活动公告。
   - 在“车位信息管理”中新增若干车位，设置不同楼层和状态（FREE / USING / REPAIR）。

2. **普通用户端**
   - 使用普通用户账号登录（可通过注册或数据库插入）。
   - 在“公告查看”中看到刚刚发布的公告。
   - 在“个人中心 → 车辆信息”中添加一辆车辆。
   - 在“车位预约”页面：
     - 选择一个可用车位 + 刚添加的车辆 + 时间段，提交预约；
     - 查看“我的预约记录”中是否有该条记录。
   - 切换到“车位推荐”菜单：
     - 点击“获取推荐车位”；
     - 演示系统如何根据当前可用车位给出推荐列表，并解释当前使用的是规则推荐算法，后续可以接入 Python 大数据模块的结果。
   - 切换到“数据可视化”菜单：
     - 展示 ECharts 绘制的“各楼层可用车位数量”柱状图，说明数据来源和未来扩展方向（车位使用率、各时段预约量等）。

3. **管理员端（数据管理与风控）**
   - 在“车位预定管理”中查看所有预约记录，并修改某条记录状态（例如从 `PENDING` 改为 `USING` 或 `FINISHED`）。
   - 在“违规管理”中为某个用户录入一条违规记录，并更新其处理状态。

4. **大数据/推荐算法示例（Python）**
   - 简要展示 `bigdata/simple_cf_recommendation.py` 的核心代码：
     - 如何从 MySQL 读取数据；
     - 如何构建用户-车位矩阵；
     - 如何根据相似用户的行为给出推荐结果。
   - 运行脚本，展示终端输出的推荐列表，作为协同过滤推荐的佐证。

---

## 九、后续可扩展方向

- 将 Python 推荐结果写回 MySQL，并在 Java 后端融合规则推荐与协同过滤推荐得分；
- 引入 Spark/Hadoop，将预约日志导出到 HDFS，使用 Spark MLlib 实现更大规模的协同过滤和聚类；
- 在前端增加更多数据可视化图表（如每日预约量折线图、车位利用率饼图等）；
- 增加短信/邮件通知、车牌识别接入等高级功能。

本版本已经具备完整的前后端功能与基础推荐/可视化能力，可以直接用于毕业设计答辩和项目交付。

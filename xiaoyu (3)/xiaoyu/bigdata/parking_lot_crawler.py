"""
停车场爬虫脚本 - 用于爬取周边停车场数据并存入数据库

在实际项目中，可以对接：
1. 高德地图 API - 周边搜索接口
2. 百度地图 API - POI检索
3. 腾讯地图 API
4. 或爬取公开的停车场信息网站

本脚本为演示版本，使用模拟数据展示爬虫逻辑和数据处理流程。
"""

import random
import time
from datetime import datetime
import pymysql

# 数据库配置
DB_CONFIG = {
    "host": "localhost",
    "port": 3306,
    "user": "root",
    "password": "4499",
    "database": "parking_system",
    "charset": "utf8mb4",
}

# 模拟的周边停车场数据（实际应从API或网页爬取）
MOCK_PARKING_LOTS = [
    {
        "name": "中电科信息科技大厦停车场",
        "address": "白莲泾路127号",
        "latitude": 31.2304,
        "longitude": 121.4737,
        "distance": 453,
        "total_spaces": 519,
        "price_per_hour": 8.0,
        "business_hours": "00:00-24:00",
        "contact": "63581117"
    },
    {
        "name": "绿谷东区地下停车场",
        "address": "白莲泾路88号",
        "latitude": 31.2298,
        "longitude": 121.4720,
        "distance": 620,
        "total_spaces": 280,
        "price_per_hour": 6.0,
        "business_hours": "06:00-22:00",
        "contact": "62345678"
    },
    {
        "name": "浦发银行大厦停车场",
        "address": "白莲泾路200号",
        "latitude": 31.2315,
        "longitude": 121.4750,
        "distance": 380,
        "total_spaces": 150,
        "price_per_hour": 10.0,
        "business_hours": "00:00-24:00",
        "contact": "63456789"
    },
    {
        "name": "吉富大楼停车场",
        "address": "中电路99号",
        "latitude": 31.2290,
        "longitude": 121.4710,
        "distance": 750,
        "total_spaces": 200,
        "price_per_hour": 5.0,
        "business_hours": "07:00-23:00",
        "contact": "62567890"
    },
    {
        "name": "华商科技大厦停车场",
        "address": "白莲泾路300号",
        "latitude": 31.2320,
        "longitude": 121.4760,
        "distance": 580,
        "total_spaces": 320,
        "price_per_hour": 7.0,
        "business_hours": "00:00-24:00",
        "contact": "63678901"
    },
    {
        "name": "绿谷广场地下车库",
        "address": "中电路150号",
        "latitude": 31.2285,
        "longitude": 121.4700,
        "distance": 890,
        "total_spaces": 450,
        "price_per_hour": 6.5,
        "business_hours": "06:00-24:00",
        "contact": "62789012"
    },
    {
        "name": "科技园区停车场",
        "address": "白莲泾路50号",
        "latitude": 31.2310,
        "longitude": 121.4725,
        "distance": 520,
        "total_spaces": 180,
        "price_per_hour": 9.0,
        "business_hours": "00:00-24:00",
        "contact": "63890123"
    },
    {
        "name": "商业中心停车楼",
        "address": "中电路200号",
        "latitude": 31.2295,
        "longitude": 121.4715,
        "distance": 680,
        "total_spaces": 600,
        "price_per_hour": 8.5,
        "business_hours": "00:00-24:00",
        "contact": "62901234"
    }
]


def simulate_crawl():
    """
    模拟爬虫过程
    实际项目中这里应该：
    1. 调用地图API获取周边停车场POI
    2. 或使用requests + BeautifulSoup爬取网页
    3. 解析数据并清洗
    """
    print("[爬虫] 开始爬取周边停车场数据...")
    time.sleep(0.5)  # 模拟网络请求延迟
    
    # 为每个停车场随机生成当前空余车位数
    crawled_data = []
    for lot in MOCK_PARKING_LOTS:
        # 模拟实时空余车位（占总车位的20%-80%）
        available = int(lot["total_spaces"] * random.uniform(0.2, 0.8))
        
        crawled_data.append({
            **lot,
            "available_spaces": available,
            "crawl_time": datetime.now()
        })
    
    print(f"[爬虫] 成功爬取 {len(crawled_data)} 个停车场数据")
    return crawled_data


def save_to_database(parking_lots):
    """将爬取的数据存入 external_parking_lot 表"""
    conn = pymysql.connect(**DB_CONFIG)
    try:
        with conn.cursor() as cursor:
            # 先清空旧数据（实际项目中可能需要更新策略）
            cursor.execute("DELETE FROM external_parking_lot")
            
            # 批量插入（根据实际表结构调整字段）
            sql = """
                INSERT INTO external_parking_lot 
                (name, address, phone, distance_km, available_spaces, 
                 price_per_hour, parking_type, business_hours, score)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
            """
            
            for lot in parking_lots:
                cursor.execute(sql, (
                    lot["name"],
                    lot["address"],
                    lot["contact"],
                    lot["distance"],
                    lot["available_spaces"],
                    lot["price_per_hour"],
                    f"总车位:{lot['total_spaces']}",  # 使用parking_type字段存储总车位信息
                    lot["business_hours"],
                    lot["score"]
                ))
            
            conn.commit()
            print(f"[数据库] 成功存入 {len(parking_lots)} 条停车场记录")
            
    finally:
        conn.close()


def calculate_recommendation_score(lot):
    """
    计算推荐评分（多因素加权）
    
    评分因素：
    1. 距离（越近越好）- 权重40%
    2. 价格（越便宜越好）- 权重30%
    3. 空余车位比例（越多越好）- 权重30%
    """
    # 距离评分：距离越近分数越高（归一化到0-100）
    # 假设最远1000米，最近100米
    distance_score = max(0, 100 - (lot["distance"] - 100) / 900 * 100)
    
    # 价格评分：价格越低分数越高
    # 假设价格范围5-10元/小时
    price_score = max(0, 100 - (lot["price_per_hour"] - 5) / 5 * 100)
    
    # 空余率评分
    available_ratio = lot["available_spaces"] / lot["total_spaces"]
    availability_score = available_ratio * 100
    
    # 加权总分
    total_score = (
        distance_score * 0.4 +
        price_score * 0.3 +
        availability_score * 0.3
    )
    
    return round(total_score, 2)


def main():
    """主函数"""
    print("=" * 60)
    print("停车场爬虫系统 - 周边停车场数据采集")
    print("=" * 60)
    
    # 1. 爬取数据
    parking_lots = simulate_crawl()
    
    # 2. 计算推荐评分
    for lot in parking_lots:
        lot["score"] = calculate_recommendation_score(lot)
    
    # 3. 存入数据库
    save_to_database(parking_lots)
    
    # 4. 展示推荐结果（按评分排序）
    print("\n[推荐排序] 综合评分Top 5:")
    sorted_lots = sorted(parking_lots, key=lambda x: x["score"], reverse=True)
    for i, lot in enumerate(sorted_lots[:5], 1):
        print(f"{i}. {lot['name']}")
        print(f"   距离: {lot['distance']}m | 价格: {lot['price_per_hour']}元/时 | "
              f"空余: {lot['available_spaces']}/{lot['total_spaces']} | "
              f"评分: {lot['score']}")
    
    print("\n[完成] 爬虫任务执行完毕！")


if __name__ == "__main__":
    main()

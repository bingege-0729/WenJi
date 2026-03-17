-- 2. 创建旅游心得打卡表 (类似博客)
CREATE TABLE travel_blog (
    blog_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL COMMENT '用户ID',
    site_id VARCHAR(32) NOT NULL COMMENT '关联景点ID',
    title VARCHAR(200) NOT NULL COMMENT '博客标题',
    content TEXT NOT NULL COMMENT '心得体会内容',
    images JSON COMMENT '图片URL列表 (JSON数组)',
    points_earned INT DEFAULT 0 COMMENT '本次获得积分',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_site (user_id, site_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户旅游心得打卡表';

-- 3. 创建徽章定义表
CREATE TABLE badge (
    badge_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '徽章名称',
    description VARCHAR(200) COMMENT '徽章描述',
    icon_url VARCHAR(500) COMMENT '徽章图标URL',
    type VARCHAR(20) COMMENT '徽章类型: footprint/blog/level',
    threshold INT COMMENT '获得阈值(如打卡次数)',
    points_bonus INT DEFAULT 0 COMMENT '获得徽章奖励积分',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='徽章定义表';

-- 4. 创建用户获得徽章关联表
CREATE TABLE user_badge (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    badge_id INT NOT NULL,
    get_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_badge (user_id, badge_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户获得徽章关联表';

-- 5. 创建积分流水表
CREATE TABLE point_record (
    record_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    type TINYINT NOT NULL COMMENT '类型: 1-获得, 2-消耗',
    source VARCHAR(50) NOT NULL COMMENT '来源: blog/badge/mall_exchange',
    points INT NOT NULL COMMENT '变动积分值',
    description VARCHAR(200) COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分流水表';

-- 6. 创建积分商城奖品表
CREATE TABLE mall_item (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price INT NOT NULL COMMENT '所需积分',
    stock INT DEFAULT 0 COMMENT '库存',
    image_url VARCHAR(500),
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分商城奖品表';

-- 7. 填充初始数据
INSERT INTO badge (name, description, type, threshold, points_bonus) VALUES 
('初试啼声', '发布第一篇旅游心得', 'blog', 1, 50),
('足迹遍布', '在5个不同的景点打卡', 'footprint', 5, 200),
('资深驴友', '累计发布10篇旅游心得', 'blog', 10, 500),
('文化传承者', '在非遗类景点打卡', 'footprint', 1, 100);

INSERT INTO mall_item (name, description, price, stock) VALUES 
('定制帆布袋', '文迹APP限量版帆布袋', 500, 100),
('手绘明信片', '一张来自景德镇的手绘明信片', 100, 500),
('深度游优惠券', '指定景区导览服务8折券', 200, 1000),
('精美书签', '景德镇陶瓷纹样书签', 150, 300);

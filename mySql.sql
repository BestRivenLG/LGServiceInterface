-- 用户列表 account
-- product.account definition
CREATE TABLE `account` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `phone` varchar(11) NOT NULL,
  `nickname` varchar(20) DEFAULT NULL,
  `token` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

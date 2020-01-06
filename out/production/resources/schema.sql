create table if not exists products
(
    id          bigint unsigned primary key auto_increment comment '商品id',
    title       varchar(100) not null unique key comment '商品タイトル',
    description varchar(500) not null comment '商品説明文',
    price       int unsigned not null comment '商品価格',
    image_path  text comment '商品画像パス',
    create_time datetime     not null default current_timestamp comment '作成日時',
    update_time datetime     not null default current_timestamp on update current_timestamp comment '更新日時'
)
    default charset = utf8mb4
    comment '商品テーブル';


create table if not exists access_token
(
    id           bigint unsigned primary key auto_increment comment 'アクセストークンid',
    access_token char(48) not null unique key comment 'アクセストークン',
    create_time  datetime not null default current_timestamp comment '作成日時',
    update_time  datetime not null default current_timestamp on update current_timestamp comment '更新日時'
)
    default charset = utf8mb4
    comment 'アクセストークンテーブル';

create table if not exists api_log
(
    id               bigint unsigned primary key auto_increment comment 'ログid',
    api_name         varchar(100)    not null comment 'api名',
    http_method      varchar(100)    not null comment 'httpメソッド',
    http_status_code int             not null comment 'httpステータスコード',
    access_count     int unsigned    not null comment 'APIのアクセス回数',
    execution_time   double unsigned not null comment 'APIの実行にかかった時間の平均',
    date             date            not null comment '集計日',
    file_name        varchar(100)    not null comment 'ログファイル名'
)
    default charset = utf8mb4
    comment 'APIログテーブル';

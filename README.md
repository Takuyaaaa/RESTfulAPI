natsumetakuya-RESTfulAPI


### 概要  
API課題(Java)  
以下のことが実行できるRESTfulなAPI  
* Productの取得  Get("/api/products?title="), Get("/api/products/{id}")  
* Productの追加  Post("/api/products")  
* Productの更新  Put("/api/products/{Id}")  
* Productの削除  Delete("/api/products/{Id}")  
* Product画像のアップロード  Patch("/api/products/{Id}/images")  
* Product画像の取得  Get("/api/products/{Id}/images/{path}.{suffix}")  

### 使用技術
*Java 11  
*Spring Boot 2.2  
    *project: Gradle  
    *Dependencies:   
    *Spring Boot DevTools,  
    *Spring Web Starter,  
    *Spring Data JPA,   
    *MySQL Driver  
    *Lombok  
    *io:commons  
**MySQL 5.7  
*Spring Initializr  
*swagger 3.0.0
  
 ### DB設計  
 Product　テーブル

|Column      | Type        | Null | Key | Default | Extra          |
|------------|------------ |------|-----|---------|----------------|
| id         | bigint     | NO   | PRI | -    | auto_increment, unsigned |
| title      | varchar(100)| NO   |     | -    |                |
| description | varchar(500)  | NO   |     | -    |                |
| price   | int             | NO   |     | -    |    unsigned   |
| image_path | text       | YES  |     | NULL    |                |
| create_time | datetime   | NO   |     | default current_timestamp   |                |
| update_time | datetime   | NO   |     | default current_timestamp on update current_timestamp    |                |  
  
  
 Access_Token　テーブル    
     
|Column      | Type        | Null | Key | Default | Extra          |
|------------|------------ |------|-----|---------|----------------|
| id         | bigint     | NO   | PRI | -    | auto_increment, unsigned |
| access_token   | char(48)| NO   | UNI  | -    |                |
| create_time | datetime   | NO   |     | default current_timestamp   |                |
| update_time | datetime   | NO   |     | default current_timestamp on update current_timestamp    |                |    
  

### Oauth URL設計  
下記のようにURLが対応   
* トップ画面表示  : /  
* ログイン実行  : /github/login  
* コールバック処理  : /github/callback  
* トップ画面表示  : /github  



### セットアップ手順

* SDKMANをhttps://sdkman.io/installからインストールし、  

``$ sdk list java`` 

のコマンドをターミナルで実行。  
AdoptOpenJDK の 11.xx.xx.hs-adpt(xxはマイナーバージョン を探し,  

``$ sdk install java 11.0.4.hs-adpt ``（バージョンはその時の最新ものものを選択）  

としてjava をインストール。  

MySQLを  

``  $ brew install mysql@5.7  ``    

としてインストール。  

``~/.bash_profileに[export PATH=/usr/local/bin:$PATH``  
``export PATH=“/usr/local/opt/mysql@5.7/bin:$PATH``  

の二行を追加して、  

`` $ source ~/.bash_profile ``  

よりその変更を読み込む。また、~/.my.cnfに下記のように追加し、サーバーの設定を行う。  
[client]  
default-character-set = utf8mb4  
[mysql]  
default-character-set = utf8mb4  
[mysqld]  
character-set-server = utf8mb4  
collation-server = utf8mb4_unicode_ci  
init-connect=‘SET NAMES utf8mb4;SET AUTOCOMMIT=0’  
skip-character-set-client-handshake  
lower_case_table_names=1  
sql_mode=NO_ENGINE_SUBSTITUTION  
max_allowed_packet=32MB

SpringInitializerを利用し、Spring bootの雛形を作成。依存ライブラリなどは上記「使用技術」を参照。  
  
  
### 参照するapplication.ymlファイルの切り替え  
デフォルトではapplication-local.ymlを参照。本番環境用に設定を切り替える場合にはapplication.ymlより、[active:]の項目をproductionに変更することで可能。


### githubOAuth認証のためのClientID、ClientSecretの取得、設定について
自身のGithubアカウントの開発設定ページに(https://github.com/settings/developers)よりアクセスし、 画面右上の New OAuth App からアプリケーションを登録。  
ここでClient IDおよびClient Secretの取得が完了したので、これをSpringに設定していく。  
IntellijのRun/Debug Configurationより、VM Optionsの項目に  

``-Dgithub.clientId=<取得したClientId>``  
``-Dgithub.clientSecret=<取得したClientSecret>``  
``-Dgithub.callbackUrl=<設定したCallbackUrl>``  
  
として、spring起動時に自動的に読み込むよう設定を行う。  



### ProductAPIを用いたデータの取得について  
対応URLなどの詳細はswagger.ymlを参照。APIでデータの取得を行うには、API用のアクセストークンが必要となる。
(http://localhost:8080/)より自身のGithubアカウントでログインすると、トークンが確認できる。
APIを利用する際には、リクエストヘッダーにKey:Authorization、 Value:Bearer <Access Token>の情報を渡して送信を行う必要がある。
なお、アクセストークンの有効期限は発行あるいは最終利用時より30分間。それ以降再度利用したい場合には、上記URLよりログインを行い再度トークンの発行を行う。

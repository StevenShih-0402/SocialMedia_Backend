# 🛠️ Social Media - Backend

本專案為簡易社群媒體系統的後端服務，使用 Spring Boot 開發，提供完整的 RESTful API 功能，支援用戶註冊、登入、發文、留言與貼文管理。

🔗 前端專案：[SocialMedia_Frontend](https://github.com/StevenShih-0402/SocialMedia_Frontend)

![流程圖](https://github.com/user-attachments/assets/05361ca8-0abb-4353-86df-dfd35ab8192e)

---

## ⚙️ 開發環境

| 項目 | 技術 |
|------|------|
| 開發工具 | IntelliJ IDEA + Gradle |
| JDK 版本 | JDK 17 |
| 框架 | Spring Boot 3.4.4 |
| 資料庫 | Oracle 19c（搭配 SQL Developer） |
| 驗證機制 | JWT |
| 啟動位址 | `localhost:8080` |

資料庫中每張表皆設有自動編號的序列與觸發器（如 `SEQ_USERS`、`TRG_USERS_ID`）。

---

## 🧱 系統架構分層

```
├── Controller         # 展示層，負責處理 API 請求
├── Service            # 業務層，處理商業邏輯
├── Repository         # JPA 方法
├── DAO                # DAO 層，操作資料庫
├── Entity             # ORM 映射資料表
├── DTO                # 資料傳輸物件
├── Utils              # 共用層，包含 JWT 等
├── Exception          # 自訂例外處理
├── Response           # 統一回傳格式封裝
├── Enums              # 枚舉類型定義
├── Config             # Spring 設定（CORS）
└── Valid              # 自訂驗證規則
```

---

## 📌 API 清單（共 10 支）

| 編號 | 功能 | 路徑 |
|------|------|------|
| 1 | 使用者註冊 | `POST /api/user/register` |
| 2 | 使用者登入 | `POST /api/user/login` |
| 3 | 使用者登出 | `POST /api/user/logout` |
| 4 | 查詢使用者資訊 | `GET /api/user/query-user` |
| 5 | 新增貼文 | `POST /api/post/create-post` |
| 6 | 查詢所有貼文（分頁查詢） | `GET /api/post/query-posts` |
| 7 | 編輯貼文 | `PUT /api/post/update-post` |
| 8 | 刪除貼文（含留言） | `DELETE /api/post/delete-post` |
| 9 | 新增留言 | `POST /api/comment/create-comment` |
| 10 | 查詢貼文留言 | `GET /api/comment/query-comments` |

🧪 Postman 測試檔案：[下載連結](https://drive.google.com/uc?export=download&id=1KBRUL9vdq2cnxV6Og4wrYUjTZnAXpCUF)

---

## 🛢 資料庫建置流程

### 🔌 連線到 Oracle 容器
```bash
docker exec -it oracle19c bash
```

### 🔐 進入 SQL*Plus（以 sysdba 身分）
```bash
sqlplus / as sysdba
```

### 📦 建立 PDB
```sql
CREATE PLUGGABLE DATABASE SMDB 
ADMIN USER steven IDENTIFIED BY steven 
FILE_NAME_CONVERT = ('/opt/oracle/oradata/ORCLCDB/', '/opt/oracle/oradata/SMDB/');
```

>如果是在本地 Oracle，`FILE_NAME_CONVERT` 的路徑要存放 `PDBSEED\SYSTEM01.DBF` 所在的路徑。PDBSEED 是 Oracle 19c 建立 Pluggable Database (PDB) 的「母體」，如果它找不到或壞掉，建立 PDB 或啟動 CDB 都會失敗。
>![image](https://github.com/user-attachments/assets/d5fa567d-8a31-4e12-8f54-3fea5bf103fd)


### ▶️ 開啟 PDB
```sql
ALTER PLUGGABLE DATABASE SMDB OPEN;
```

### 🔍 確認目前 PDB 狀態
```sql
SHOW PDBS;
```

```sql
CON_ID CON_NAME     OPEN MODE  RESTRICTED
------ ------------- ---------- ----------
2      PDB$SEED     READ ONLY  NO
3      ORCLPDB1     READ WRITE NO
4      SMDB         READ WRITE NO
```

### ❌ 離開 SQL*Plus
```bash
exit
```

---

### 🔑 連線至 PDB 並授權
```bash
sqlplus SYS/steven051225@localhost:1521/SMDB as sysdba
```

```sql
-- 測試連線
SELECT 'Connection Successful' AS TEST FROM dual;

-- 切換容器
ALTER SESSION SET CONTAINER = SMDB;

-- 建立用戶
CREATE USER tom IDENTIFIED BY tom;

-- 授權用戶
GRANT CONNECT, RESOURCE TO tom;
GRANT DBA TO tom;

-- 確認用戶建立成功
SELECT username FROM all_users WHERE username = 'TOM';
```

---

## 🗃 資料表自動編號設置

### 📌 建立序列
```sql
CREATE SEQUENCE SEQ_USERS START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_POSTS START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_COMMENTS START WITH 1 INCREMENT BY 1;
```

### 🔄 重設序列
```sql
ALTER SEQUENCE SEQ_USERS RESTART START WITH 1;
ALTER SEQUENCE SEQ_POSTS RESTART START WITH 1;
ALTER SEQUENCE SEQ_COMMENTS RESTART START WITH 1;
```

---

### ⚡ 建立觸發器：自動產生 ID

```sql
-- USERS
CREATE OR REPLACE TRIGGER TRG_USERS_ID
BEFORE INSERT ON USERS
FOR EACH ROW
BEGIN
    IF :NEW.USER_ID IS NULL THEN
        SELECT SEQ_USERS.NEXTVAL INTO :NEW.USER_ID FROM DUAL;
    END IF;
END;
/

-- POSTS
CREATE OR REPLACE TRIGGER TRG_POSTS_ID
BEFORE INSERT ON POSTS
FOR EACH ROW
BEGIN
    IF :NEW.POST_ID IS NULL THEN
        SELECT SEQ_POSTS.NEXTVAL INTO :NEW.POST_ID FROM DUAL;
    END IF;
END;
/

-- COMMENTS
CREATE OR REPLACE TRIGGER TRG_COMMENTS_ID
BEFORE INSERT ON COMMENTS
FOR EACH ROW
BEGIN
    IF :NEW.COMMENT_ID IS NULL THEN
        SELECT SEQ_COMMENTS.NEXTVAL INTO :NEW.COMMENT_ID FROM DUAL;
    END IF;
END;
/
```

---

### 🕒 建立觸發器：自動更新 `UPDATED_AT`

```sql
CREATE OR REPLACE TRIGGER TRG_POST_UPDATED_AT
BEFORE UPDATE ON POSTS
FOR EACH ROW
BEGIN
    :NEW.UPDATED_AT := CURRENT_TIMESTAMP;
END;
/
```

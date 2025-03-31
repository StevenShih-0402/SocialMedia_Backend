# Social Media - Backend

> Frontend：https://github.com/StevenShih-0402/SocialMedia_Frontend

Note:
- IntelliJ IDEA + Gradle
- JDK 17
- Spring Boot 3.4.4
- Oracle 19c + SQL Developer
- localhost:8080
- 身分驗證：JWT

每張資料表有設定序列(例如：SEQ_USERS) 與觸發器(例如：TRG_USERS_ID) 自動新增 id

系統架構：
1. 展示層(Controller)
2. 業務層(Service)
3. 資料層(DAO、Repository、Entity)
4. 共用層(DTO、Utils、Exception、Response、Enums、Config、Valid)

總共有 10 支 API：
1. 使用者註冊(`/api/user/register`)
2. 使用者登入(`/api/user/login`)
3. 使用者登出(`/api/user/logout`)
4. 查詢使用者資訊(`/api/user/query-user`)
5. 新增貼文(`/api/post/create-post`)：新增時會自動添加一條留言，內容為發文時間。
6. 查詢所有貼文(`/api/post/query-posts`)：加入分頁(Pageable)查詢設計，讓前端不會一次加載太多內容
7. 編輯貼文(`/api/post/update-post`)
8. 刪除貼文(`/api/post/delete-post`)：會連貼文內的留言一併刪除。
9. 新增留言(`/api/comment/create-comment`)
10. 查詢一篇貼文的所有留言(`/api/comment/query-comments`)

[PostMan 測試檔案](https://drive.google.com/uc?export=download&id=1KBRUL9vdq2cnxV6Og4wrYUjTZnAXpCUF)

資料庫設定：

0. 連線到容器

    `docker exec -it oracle19c bash`

1. 透過 SQL*Plus 連線到 Oracle

    `sqlplus / as sysdba`

2. 建立 PDB

    `CREATE PLUGGABLE DATABASE SMDB ADMIN USER steven IDENTIFIED BY steven FILE_NAME_CONVERT = ('/opt/oracle/oradata/ORCLCDB/', '/opt/oracle/oradata/SMDB/');`

3. 連線到 PDB

    `ALTER PLUGGABLE DATABASE SMDB OPEN;`

4. 確認連線狀態

    `SHOW PDBS;`

```sql
    CON_ID CON_NAME                       OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
         2 PDB$SEED                       READ ONLY  NO
         3 ORCLPDB1                       READ WRITE NO
         4 SMDB                           READ WRITE NO
```

5. 離開

    `exit;`

6. 用 SYS 身分連 PDB

    `sqlplus SYS/steven051225@localhost:1521/SMDB as sysdba`

7. 測試連線

    `SELECT 'Connection Successful' AS TEST FROM dual;`

8. 確保切換到 PDB

    `ALTER SESSION SET CONTAINER = SMDB;`

9. 建立用戶

    `CREATE USER tom IDENTIFIED BY tom;`

10. 授予用戶創建和連線的權限

    `GRANT CONNECT, RESOURCE TO tom;`

11. 確認有建立到用戶 tom

    `SELECT username FROM all_users WHERE username = 'TOM';`

12. 授予用戶資料庫管理員 (DBA) 權限

    `GRANT DBA TO tom;`

13. 打開 SQL Developer 建立連線、資料表、序列、觸發器、DDL 和 DML

- 建立 ID 序列

```sql
-- CREATE SEQUENCE
CREATE SEQUENCE SEQ_USERS START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_POSTS START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_COMMENTS START WITH 1 INCREMENT BY 1;

-- UPDATE SEQUENCE
ALTER SEQUENCE SEQ_USERS RESTART START WITH 1;
ALTER SEQUENCE SEQ_POSTS RESTART START WITH 1;
ALTER SEQUENCE SEQ_COMMENTS RESTART START WITH 1;
```

- 建立新增 ID 與更新時間的觸發器

```sql
-- 新增資料表 ID

CREATE OR REPLACE TRIGGER TRG_USERS_ID
BEFORE INSERT ON USERS
FOR EACH ROW
BEGIN
    IF :NEW.USER_ID IS NULL THEN
        SELECT SEQ_USERS.NEXTVAL INTO :NEW.USER_ID FROM DUAL;
    END IF;
END;
/
CREATE OR REPLACE TRIGGER TRG_POSTS_ID
BEFORE INSERT ON POSTS
FOR EACH ROW
BEGIN
    IF :NEW.POST_ID IS NULL THEN
        SELECT SEQ_POSTS.NEXTVAL INTO :NEW.POST_ID FROM DUAL;
    END IF;
END;
/
CREATE OR REPLACE TRIGGER TRG_COMMENTS_ID
BEFORE INSERT ON COMMENTS
FOR EACH ROW
BEGIN
    IF :NEW.COMMENT_ID IS NULL THEN
        SELECT SEQ_COMMENTS.NEXTVAL INTO :NEW.COMMENT_ID FROM DUAL;
    END IF;
END;

-- 新增資料更新時間
CREATE OR REPLACE TRIGGER TRG_POST_UPDATED_AT
BEFORE UPDATE ON POSTS
FOR EACH ROW
BEGIN
    :NEW.UPDATED_AT := CURRENT_TIMESTAMP;
END;
```

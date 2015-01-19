package com.cnx.ptt.db;

public class DBInfo {

    public static class DB {
        /**
         * database name
         */
        public static final String DB_NAME = "timetrackmobile.db";
        /**
         * database version
         */
        public static final int VERSION = 1;
    }

    public static class Table {
        /**
         * user table
         */
        public static final String USER_TABLE = "user";
        /**
         * key
         */
        public static final String _ID = "_id";
        /**
         * user id
         */
        public static final String USER_ID = "user_id";
        /**
         * user name
         */
        public static final String USER_NAME = "user_name";
        /**
         * user email address
         */
        public static final String USER_EMAIL = "user_email";
        /**
         * user password
         */
        public static final String USER_PASSWORD = "user_password";
        /**
         * user session id
         */
        public static final String USER_SESSION = "user_session";
        /**
         * description
         */
        public static final String USER_DESC = "user_desc";
       
        /**
         * create user table
         */
        public static final String CREATE_USER_TABLE = "create table if not exists "
                + USER_TABLE
                + "("
                + _ID
                + " integer primary key autoincrement, "
                + USER_ID + " text, "
                + USER_NAME + " text, "
                + USER_EMAIL + " text, "
                + USER_PASSWORD + " text, "
                + USER_SESSION +" text," 
                + USER_DESC+" text);";
        
        /**
         * delete user table
         */
        public static final String DROP_USER_TABLE = "drop table "+USER_TABLE;

    }

}

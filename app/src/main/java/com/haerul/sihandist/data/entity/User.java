package com.haerul.sihandist.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
   @PrimaryKey @NonNull
   public String user_sid;
   public String user_uid;
   public String user_no_induk;
   public String user_name;
   public String user_phone;
   public String user_email;
   public String user_jabatan;
   public String user_bagian;
   public String user_unit;
   public String user_login_name;
   public String user_password;
   public String user_role_sid;
   public int is_active;
   public String date_created;
   public String date_modified;
}

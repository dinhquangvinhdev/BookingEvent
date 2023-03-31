package com.example.bookingevent.model;

public class Department {
     private int id;
     private String full_name;
     private String nick_name;
     int number_member;

     public int getId() {
          return id;
     }

     public void setId(int id) {
          this.id = id;
     }

     public String getFull_name() {
          return full_name;
     }

     public void setFull_name(String full_name) {
          this.full_name = full_name;
     }

     public String getNick_name() {
          return nick_name;
     }

     public void setNick_name(String nick_name) {
          this.nick_name = nick_name;
     }

     public int getNumber_member() {
          return number_member;
     }

     public void setNumber_member(int number_member) {
          this.number_member = number_member;
     }
}

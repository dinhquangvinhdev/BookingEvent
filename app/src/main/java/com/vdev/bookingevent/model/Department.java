package com.vdev.bookingevent.model;

public class Department {
     private int id;
     private String fullName;
     private String nickName;
     int numberMember;

     public Department() {
     }

     public int getId() {
          return id;
     }

     public void setId(int id) {
          this.id = id;
     }

     public String getFullName() {
          return fullName;
     }

     public void setFullName(String fullName) {
          this.fullName = fullName;
     }

     public String getNickName() {
          return nickName;
     }

     public void setNickName(String nickName) {
          this.nickName = nickName;
     }

     public int getNumberMember() {
          return numberMember;
     }

     public void setNumberMember(int numberMember) {
          this.numberMember = numberMember;
     }
}

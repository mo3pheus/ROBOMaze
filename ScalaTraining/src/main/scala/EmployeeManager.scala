package com.egen.scala

case class Employee(id: Int, name: String)

class EmployeeManager {

  def create(id: Int, name: String): Employee = {
    Employee(id, name)
  }

  def find(id: Int, name: String): Employee = {
    Employee(id, name)
  }

  def delete(id: Int, name: String): Employee = {
    Employee(id, name)
  }

  def update(id: Int, name: String): Employee = {
    Employee(id, name)
  }
}

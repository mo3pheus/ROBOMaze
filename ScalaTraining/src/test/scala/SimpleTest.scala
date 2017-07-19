import com.egen.scala.EmployeeManager

import org.scalatest._

class SimpleTest extends FlatSpec with Matchers{
  println("Test executing")
  "EmployeeManager.create" should "create an employee when passed a valid id and name" in {
    val emp = new EmployeeManager().create(2,"Sanket")
    emp.id shouldBe (2)
  }
}

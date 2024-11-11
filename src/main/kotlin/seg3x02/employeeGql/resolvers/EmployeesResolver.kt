package seg3x02.employeeGql.resolvers

import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import seg3x02.employeeGql.entity.Employee
import seg3x02.employeeGql.repository.EmployeesRepository
import seg3x02.employeeGql.resolvers.types.CreateEmployeeInput
import java.util.*

@Controller
class EmployeesResolver(private val employeesRepository: EmployeesRepository,
                      private val mongoOperations: MongoOperations
) {
    @QueryMapping
    fun employees(): List<Employee> {
        return employeesRepository.findAll()
    }

    @QueryMapping
    fun employeeById(@Argument id: String): Employee? {
        val employee = employeesRepository.findById(id)
        return employee.orElse(null)
    }

    @MutationMapping
    fun newEmployee(@Argument("createEmployeeInput") input: CreateEmployeeInput) : Employee {
        if (input.name != null &&
            input.dateOfBirth != null &&
            input.city != null && input.salary != null) {
            val employee = Employee(input.name, input.dateOfBirth, input.city, input.salary, input.gender, input.email)
            employee.id = UUID.randomUUID().toString()
            employeeRepository.save(employee)
            return employee
        } else {
            throw Exception("Invalid input")
        }
    }

    @MutationMapping
    fun deleteEmployee(@Argument("id") id: String) : Boolean {
        employeeRepository.deleteById(id)
        return true
    }

    @MutationMapping
    fun updateEmployee(@Argument id: String, @Argument("createEmployeeInput") input: CreateEmployeeInput) : Employee {
        val employee = employeeRepository.findById(id)
        employee.ifPresent {
            if (input.name != null) {
                it.name = input.name
            }
            if (input.dateOfBirth != null) {
                it.dateOfBirth = input.dateOfBirth
            }
            if (input.city != null) {
                it.city = input.city
            }
            if (input.salary != null) {
                it.salary = input.salary
            }
            if (input.gender != null) {
                it.gender = input.gender
            }
            if (input.email != null) {
                it.email = input.email
            }
            employeeRepository.save(it)
        }
        return employee.get()
    }
}
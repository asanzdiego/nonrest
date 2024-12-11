from flask import Flask, jsonify, request, abort
from flask.views import MethodView
from flask_sqlalchemy import SQLAlchemy
from werkzeug.exceptions import NotFound

# tag::hateoas-imports[]
# end::hateoas-imports[]

# REST controller for managing Employee resources.
# Provides endpoints for standard CRUD operations and includes functionality to return HATEOAS-compliant responses.

class EmployeeController:

    def __init__(self, repository, abstract_script_database_initializer):
        self.repository = repository
        self.abstract_script_database_initializer = abstract_script_database_initializer

    # Aggregate root

    # Retrieves a collection of all employees as HATEOAS-compliant resources.
    # Generates links for each employee resource and includes a self-referential link for the collection.
    #
    # @return A CollectionModel containing EntityModel representations of all employees,
    #         each with its own self-referential link and a link to the collection.
    # tag::get-aggregate-root[]
    def all(self):
        employees = [
            {
                "employee": employee,
                "_links": {
                    "self": f"/employees/{employee['id']}",
                    "employees": "/employees"
                }
            }
            for employee in self.repository.find_all()
        ]

        return {
            "employees": employees,
            "_links": {"self": "/employees"}
        }
    # end::get-aggregate-root[]

    def new_employee(self, new_employee):
        return self.repository.save(new_employee)

    # Single item

    # tag::get-single-item[]
    def one(self, id):
        employee = self.repository.find_by_id(id)
        if not employee:
            raise NotFound(f"Employee with ID {id} not found")

        return {
            "employee": employee,
            "_links": {
                "self": f"/employees/{id}",
                "employees": "/employees"
            }
        }
    # end::get-single-item[]

    def update_existing_employee(self, new_employee, id):
        regex_mail = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"

        existing_employee = self.repository.find_by_id(id)
        if existing_employee:
            existing_employee['name'] = new_employee.get('name')
            existing_employee['role'] = new_employee.get('role')
            return self.repository.save(existing_employee)
        else:
            return self.repository.save(new_employee)

    def delete_employee(self, id):
        self.repository.delete_by_id(id)
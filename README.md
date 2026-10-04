# JDBC Mini Projects

Console-based Java and MySQL projects built using JDBC and PreparedStatement.

## Projects

| Package | Project | What it covers |
|---------|---------|----------------|
| `banking` | Banking Management System | Account creation, deposit, withdraw, fund transfer, transaction history. Uses commit and rollback. |
| `hospital` | Hospital Management System | Patients, doctors, appointments, prescriptions. Uses foreign keys and joins. |
| `payroll` | Payroll Management System | Employees, salaries, payroll, tax deduction, payslip. Uses aggregate functions and batch processing. |

## Requirements

- Java (built with JavaSE-21)
- MySQL Server
- MySQL Connector/J jar added to the project build path
- Eclipse IDE

## Setup

1. Create these schemas in MySQL: `bank_db`, `hospital_db`, `payroll_db`
2. Add the MySQL Connector/J jar to the build path of the project.
3. In each App file, set your MySQL password in the `PASS` variable.
4. Run the main class of the project you want:
   - `BankingApp`
   - `HospitalApp`
   - `PayrollApp`
5. Tables are created automatically when the program starts.

## Code structure

Each package has the same 5 files:

- `...App.java`: main method, loads the driver, creates the connection once, shows the menu
- `TableSetup.java`: creates the tables
- `...Operations.java`: insert, update, delete and other operations
- `...Queries.java`: join and aggregate queries, and one common method to display results
- `Validator.java`: not null, name, phone, email and other input checks

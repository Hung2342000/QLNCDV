package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.CountEmployee;
import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.domain.Ticket;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Employee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query(
        "select new Employee(a.id,a.codeEmployee,a.name,a.birthday, a.otherId, a.address, a.mobilePhone, a.workPhone, a.workEmail, a.privateEmail, t.department, a.startDate, a.closeDate,a.basicSalary, t.serviceType, t.serviceTypeName, a.region, a.rank, a.mucChiTraToiThieu,t.nhom,a.diaBan, t.status, a.note, a.ngayNghiSinh, a.ngayDieuChuyen) from Employee a join Transfer t on a.id = t.employeeId where LOWER(a.codeEmployee) LIKE %:searchCode% " +
        "AND LOWER(a.name) LIKE %:searchName% and a.department LIKE %:searchDepartment% " +
        "and t.nhom = :searchNhom and t.status LIKE %:searchStatus% and t.serviceTypeName LIKE %:searchService% and (t.startDate <= TO_DATE(:searchStartDate, 'YYYY-MM-DD') or a.startDate is null ) and t.closeDate > TO_DATE(:searchStartDate, 'YYYY-MM-DD')"
    )
    Page<Employee> listAllEmployees(
        @Param("searchCode") String searchCode,
        @Param("searchName") String searchName,
        @Param("searchDepartment") String searchDepartment,
        @Param("searchNhom") String searchNhom,
        @Param("searchStatus") String searchStatus,
        @Param("searchService") String searchService,
        @Param("searchStartDate") String searchStartDate,
        Pageable pageable
    );

    @Query(
        "select new Employee(a.id,a.codeEmployee,a.name,a.birthday, a.otherId, a.address, a.mobilePhone, a.workPhone, a.workEmail, a.privateEmail, t.department, a.startDate, a.closeDate,a.basicSalary, t.serviceType, t.serviceTypeName, a.region, a.rank, a.mucChiTraToiThieu,t.nhom,a.diaBan, t.status, a.note, a.ngayNghiSinh, a.ngayDieuChuyen) from Employee a join Transfer t on a.id = t.employeeId where LOWER(a.codeEmployee) LIKE %:searchCode% " +
        "AND LOWER(a.name) LIKE %:searchName% and t.department LIKE %:searchDepartment% and t.status LIKE %:searchStatus% " +
        "and t.serviceTypeName LIKE %:searchService% and ( t.startDate <= TO_DATE(:searchStartDate, 'YYYY-MM-DD') or a.startDate is null ) and t.closeDate > TO_DATE(:searchStartDate, 'YYYY-MM-DD')"
    )
    Page<Employee> listAllEmployeesNoNhom(
        @Param("searchCode") String searchCode,
        @Param("searchName") String searchName,
        @Param("searchDepartment") String searchDepartment,
        @Param("searchStatus") String searchStatus,
        @Param("searchService") String searchService,
        @Param("searchStartDate") String searchStartDate,
        Pageable pageable
    );

    @Query(
        "select new Employee(a.id,a.codeEmployee,a.name,a.birthday, a.otherId, a.address, a.mobilePhone, a.workPhone, a.workEmail, a.privateEmail, t.department, a.startDate, a.closeDate,a.basicSalary, t.serviceType, t.serviceTypeName, a.region, a.rank, a.mucChiTraToiThieu,t.nhom,a.diaBan, t.status, a.note, a.ngayNghiSinh, a.ngayDieuChuyen) from Employee a join Transfer t on a.id = t.employeeId where LOWER(a.codeEmployee) LIKE %:searchCode% " +
        "AND LOWER(a.name) LIKE %:searchName% and t.department = :department and t.nhom LIKE %:searchNhom% and t.status LIKE %:searchStatus% and a.serviceTypeName " +
        "LIKE %:searchService% and (t.startDate <= TO_DATE(:searchStartDate, 'YYYY-MM-DD') or a.startDate is null ) and t.closeDate > TO_DATE(:searchStartDate, 'YYYY-MM-DD')"
    )
    Page<Employee> listAllEmployeesDepartment(
        @Param("searchCode") String searchCode,
        @Param("searchName") String searchName,
        @Param("department") String department,
        @Param("searchNhom") String searchNhom,
        @Param("searchStatus") String searchStatus,
        @Param("searchService") String searchService,
        @Param("searchStartDate") String searchStartDate,
        Pageable pageable
    );

    @Query(
        "select a from Employee a where LOWER(a.codeEmployee) LIKE %:searchCode% AND LOWER(a.name) LIKE %:searchName% and a.department LIKE %:searchDepartment% and a.nhom = :searchNhom AND a.status = 'Đang làm việc'"
    )
    Page<Employee> listAllEmployeesBox(
        @Param("searchCode") String searchCode,
        @Param("searchName") String searchName,
        @Param("searchDepartment") String searchDepartment,
        @Param("searchNhom") String searchNhom,
        Pageable pageable
    );

    @Query(
        "select a from Employee a where LOWER(a.codeEmployee) LIKE %:searchCode% AND LOWER(a.name) LIKE %:searchName% and a.department LIKE %:searchDepartment% AND a.status = 'Đang làm việc'"
    )
    Page<Employee> listAllEmployeesNoNhomBox(
        @Param("searchCode") String searchCode,
        @Param("searchName") String searchName,
        @Param("searchDepartment") String searchDepartment,
        Pageable pageable
    );

    @Query(
        "select a from Employee a where LOWER(a.codeEmployee) LIKE %:searchCode% AND LOWER(a.name) LIKE %:searchName% and a.department = :department and a.nhom LIKE %:searchNhom% AND a.status = 'Đang làm việc'"
    )
    Page<Employee> listAllEmployeesDepartmentBox(
        @Param("searchCode") String searchCode,
        @Param("searchName") String searchName,
        @Param("department") String department,
        @Param("searchNhom") String searchNhom,
        Pageable pageable
    );

    @Query(
        "select new Employee(a.id,a.codeEmployee,a.name,a.birthday, a.otherId, a.address, a.mobilePhone, a.workPhone, a.workEmail, a.privateEmail, a.department, a.startDate, a.closeDate,a.basicSalary, a.serviceType, a.serviceTypeName, a.region, a.rank, a.mucChiTraToiThieu,a.nhom,a.diaBan, a.status, a.note, a.ngayNghiSinh, a.ngayDieuChuyen) from Employee a  where a.codeEmployee = :codeEmployee"
    )
    Page<Employee> listOneEmployee(@Param("codeEmployee") String codeEmployee, Pageable pageable);

    @Query(
        "select a from Employee a where LOWER(a.name) LIKE %:searchName% and a.department LIKE %:searchDepartment% and a.nhom = :searchNhom AND a.status = 'Đang làm việc'"
    )
    List<Employee> listAllEmployees(
        @Param("searchName") String searchName,
        @Param("searchDepartment") String searchDepartment,
        @Param("searchNhom") String searchNhom
    );

    @Query(
        "select a from Employee a where LOWER(a.name) LIKE %:searchName% and a.department LIKE %:searchDepartment% AND a.status = 'Đang làm việc'"
    )
    List<Employee> listAllEmployeesNoNhom(@Param("searchName") String searchName, @Param("searchDepartment") String searchDepartment);

    @Query("select a from Employee a where a.department = :department")
    List<Employee> listAllEmployeesDepartmentNoPage(@Param("department") String department);

    @Query("select COUNT(a) from Employee a where a.department = :department and a.status = 'Đang làm việc'")
    Long countEmployeeDLVDepartment(@Param("department") String department);

    @Query("select COUNT(a) from Employee a where  a.status = 'Đang làm việc'")
    Long countEmployeeDLV();

    @Query("select a from Employee a where a.codeEmployee = :codeEmployee")
    Employee getByCode(@Param("codeEmployee") String codeEmployee);

    @Query("SELECT new CountEmployee( d.code, count(d.code)) FROM Employee e JOIN Department d on e.department = d.code group by d.code")
    List<CountEmployee> listAllEmployeesDepartmentNoPage();

    @Query(
        "select new Employee(a.id,a.codeEmployee,a.name,a.birthday, a.otherId, a.address, a.mobilePhone, a.workPhone, a.workEmail, a.privateEmail, t.department, a.startDate, a.closeDate,a.basicSalary, t.serviceType, t.serviceTypeName, a.region, a.rank, a.mucChiTraToiThieu,t.nhom,a.diaBan, t.status, a.note, a.ngayNghiSinh, a.ngayDieuChuyen) from Employee a join Transfer t on a.id = t.employeeId where LOWER(a.codeEmployee) LIKE %:searchCode% " +
        "AND LOWER(a.name) LIKE %:searchName% and a.department LIKE %:searchDepartment% " +
        "and t.nhom = :searchNhom and t.status LIKE %:searchStatus% and t.serviceTypeName LIKE %:searchService% and (t.startDate <= TO_DATE(:searchStartDate, 'YYYY-MM-DD') or a.startDate is null ) and t.closeDate > TO_DATE(:searchStartDate, 'YYYY-MM-DD')"
    )
    List<Employee> listAllEmployeesExport(
        @Param("searchCode") String searchCode,
        @Param("searchName") String searchName,
        @Param("searchDepartment") String searchDepartment,
        @Param("searchNhom") String searchNhom,
        @Param("searchStatus") String searchStatus,
        @Param("searchService") String searchService,
        @Param("searchStartDate") String searchStartDate
    );

    @Query(
        "select new Employee(a.id,a.codeEmployee,a.name,a.birthday, a.otherId, a.address, a.mobilePhone, a.workPhone, a.workEmail, a.privateEmail, t.department, a.startDate, a.closeDate,a.basicSalary, t.serviceType, t.serviceTypeName, a.region, a.rank, a.mucChiTraToiThieu,t.nhom,a.diaBan, t.status, a.note, a.ngayNghiSinh, a.ngayDieuChuyen) from Employee a join Transfer t on a.id = t.employeeId where LOWER(a.codeEmployee) LIKE %:searchCode% " +
        "AND LOWER(a.name) LIKE %:searchName% and a.department LIKE %:searchDepartment% " +
        " and t.status LIKE %:searchStatus% and t.serviceTypeName LIKE %:searchService% and (t.startDate <= TO_DATE(:searchStartDate, 'YYYY-MM-DD') or a.startDate is null ) and t.closeDate > TO_DATE(:searchStartDate, 'YYYY-MM-DD')"
    )
    List<Employee> listAllEmployeesExportNoNhom(
        @Param("searchCode") String searchCode,
        @Param("searchName") String searchName,
        @Param("searchDepartment") String searchDepartment,
        @Param("searchStatus") String searchStatus,
        @Param("searchService") String searchService,
        @Param("searchStartDate") String searchStartDate
    );

    @Query(
        "select new Employee(a.id,a.codeEmployee,a.name,t.department, a.startDate, a.closeDate, t.serviceType, t.serviceTypeName,t.nhom, t.status) from Employee a join Transfer t on a.id = t.employeeId where LOWER(a.codeEmployee) LIKE %:searchCode% " +
        "AND LOWER(a.name) LIKE %:searchName% and t.department = :department and t.nhom LIKE %:searchNhom% and t.status LIKE %:searchStatus% and a.serviceTypeName " +
        "LIKE %:searchService% and (t.startDate <= TO_DATE(:searchStartDate, 'YYYY-MM-DD') or a.startDate is null ) and t.closeDate > TO_DATE(:searchStartDate, 'YYYY-MM-DD') "
    )
    List<Employee> listAllEmployeesExporUser(
        @Param("searchCode") String searchCode,
        @Param("searchName") String searchName,
        @Param("department") String department,
        @Param("searchNhom") String searchNhom,
        @Param("searchStatus") String searchStatus,
        @Param("searchService") String searchService,
        @Param("searchStartDate") String searchStartDate
    );

    @Query(
        "select new Employee(a.id,a.codeEmployee,a.name,a.birthday, a.otherId, a.address, a.mobilePhone, a.workPhone, a.workEmail, a.privateEmail, a.department, a.startDate, a.closeDate,a.basicSalary, a.serviceType, a.serviceTypeName, a.region, a.rank, a.mucChiTraToiThieu,a.nhom,a.diaBan, a.status, a.note, a.ngayNghiSinh, a.ngayDieuChuyen) from Employee a  where a.codeEmployee = :codeEmployee"
    )
    List<Employee> listOne(@Param("codeEmployee") String codeEmployee);

    @Query("select a from Employee a where  a.nhom = :searchNhom")
    List<Employee> listAllEmployeesNhom(@Param("searchNhom") String searchNhom);

    @Query("select a from Employee a where  a.codeEmployee = :code")
    List<Employee> listAllEmployeesCode(@Param("code") String code);

    @Query("select d.name from Department d join Employee  a on d.code = a.department where a.codeEmployee = :code")
    String nameDepartmentByCode(@Param("code") String code);
}

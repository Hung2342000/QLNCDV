package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.repository.EmployeeRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EmployeeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
@Disabled
class EmployeeResourceIT {

    private static final Long DEFAULT_CODE_EMPLOYEE = 1L;
    private static final Long UPDATED_CODE_EMPLOYEE = 2L;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_BIRTHDAY = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTHDAY = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_OTHER_ID = "AAAAAAAAAA";
    private static final String UPDATED_OTHER_ID = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_MOBILE_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_MOBILE_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_WORK_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_WORK_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_WORK_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_WORK_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PRIVATE_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_PRIVATE_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_DEPARTMENT = "AAAAAAAAAA";
    private static final String UPDATED_DEPARTMENT = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/employees";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEmployeeMockMvc;

    private Employee employee;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employee createEntity(EntityManager em) {
        Employee employee = new Employee()
            .codeEmployee(DEFAULT_NAME)
            .name(DEFAULT_NAME)
            .birthday(DEFAULT_BIRTHDAY)
            .otherId(DEFAULT_OTHER_ID)
            .address(DEFAULT_ADDRESS)
            .mobilePhone(DEFAULT_MOBILE_PHONE)
            .workPhone(DEFAULT_WORK_PHONE)
            .workEmail(DEFAULT_WORK_EMAIL)
            .privateEmail(DEFAULT_PRIVATE_EMAIL)
            .department(DEFAULT_DEPARTMENT)
            .startDate(DEFAULT_START_DATE);
        return employee;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employee createUpdatedEntity(EntityManager em) {
        Employee employee = new Employee()
            .codeEmployee(DEFAULT_NAME)
            .name(UPDATED_NAME)
            .birthday(UPDATED_BIRTHDAY)
            .otherId(UPDATED_OTHER_ID)
            .address(UPDATED_ADDRESS)
            .mobilePhone(UPDATED_MOBILE_PHONE)
            .workPhone(UPDATED_WORK_PHONE)
            .workEmail(UPDATED_WORK_EMAIL)
            .privateEmail(UPDATED_PRIVATE_EMAIL)
            .department(UPDATED_DEPARTMENT)
            .startDate(UPDATED_START_DATE);
        return employee;
    }

    @BeforeEach
    public void initTest() {
        employee = createEntity(em);
    }

    @Test
    @Transactional
    void createEmployee() throws Exception {
        int databaseSizeBeforeCreate = employeeRepository.findAll().size();
        // Create the Employee
        restEmployeeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employee)))
            .andExpect(status().isCreated());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeCreate + 1);
        Employee testEmployee = employeeList.get(employeeList.size() - 1);
        assertThat(testEmployee.getCodeEmployee()).isEqualTo(DEFAULT_CODE_EMPLOYEE);
        assertThat(testEmployee.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEmployee.getBirthday()).isEqualTo(DEFAULT_BIRTHDAY);
        assertThat(testEmployee.getOtherId()).isEqualTo(DEFAULT_OTHER_ID);
        assertThat(testEmployee.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testEmployee.getMobilePhone()).isEqualTo(DEFAULT_MOBILE_PHONE);
        assertThat(testEmployee.getWorkPhone()).isEqualTo(DEFAULT_WORK_PHONE);
        assertThat(testEmployee.getWorkEmail()).isEqualTo(DEFAULT_WORK_EMAIL);
        assertThat(testEmployee.getPrivateEmail()).isEqualTo(DEFAULT_PRIVATE_EMAIL);
        assertThat(testEmployee.getDepartment()).isEqualTo(DEFAULT_DEPARTMENT);
        assertThat(testEmployee.getStartDate()).isEqualTo(DEFAULT_START_DATE);
    }

    @Test
    @Transactional
    void createEmployeeWithExistingId() throws Exception {
        // Create the Employee with an existing ID
        employee.setId(1L);

        int databaseSizeBeforeCreate = employeeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEmployeeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employee)))
            .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeEmployeeIsRequired() throws Exception {
        int databaseSizeBeforeTest = employeeRepository.findAll().size();
        // set the field null
        employee.setCodeEmployee(null);

        // Create the Employee, which fails.

        restEmployeeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employee)))
            .andExpect(status().isBadRequest());

        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEmployees() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList
        restEmployeeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employee.getId().intValue())))
            .andExpect(jsonPath("$.[*].codeEmployee").value(hasItem(DEFAULT_CODE_EMPLOYEE.intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].birthday").value(hasItem(DEFAULT_BIRTHDAY.toString())))
            .andExpect(jsonPath("$.[*].otherId").value(hasItem(DEFAULT_OTHER_ID)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].mobilePhone").value(hasItem(DEFAULT_MOBILE_PHONE)))
            .andExpect(jsonPath("$.[*].workPhone").value(hasItem(DEFAULT_WORK_PHONE)))
            .andExpect(jsonPath("$.[*].workEmail").value(hasItem(DEFAULT_WORK_EMAIL)))
            .andExpect(jsonPath("$.[*].privateEmail").value(hasItem(DEFAULT_PRIVATE_EMAIL)))
            .andExpect(jsonPath("$.[*].department").value(hasItem(DEFAULT_DEPARTMENT)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())));
    }

    @Test
    @Transactional
    void getEmployee() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get the employee
        restEmployeeMockMvc
            .perform(get(ENTITY_API_URL_ID, employee.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(employee.getId().intValue()))
            .andExpect(jsonPath("$.codeEmployee").value(DEFAULT_CODE_EMPLOYEE.intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.birthday").value(DEFAULT_BIRTHDAY.toString()))
            .andExpect(jsonPath("$.otherId").value(DEFAULT_OTHER_ID))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.mobilePhone").value(DEFAULT_MOBILE_PHONE))
            .andExpect(jsonPath("$.workPhone").value(DEFAULT_WORK_PHONE))
            .andExpect(jsonPath("$.workEmail").value(DEFAULT_WORK_EMAIL))
            .andExpect(jsonPath("$.privateEmail").value(DEFAULT_PRIVATE_EMAIL))
            .andExpect(jsonPath("$.department").value(DEFAULT_DEPARTMENT))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingEmployee() throws Exception {
        // Get the employee
        restEmployeeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewEmployee() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();

        // Update the employee
        Employee updatedEmployee = employeeRepository.findById(employee.getId()).get();
        // Disconnect from session so that the updates on updatedEmployee are not directly saved in db
        em.detach(updatedEmployee);
        updatedEmployee
            .codeEmployee(DEFAULT_NAME)
            .name(UPDATED_NAME)
            .birthday(UPDATED_BIRTHDAY)
            .otherId(UPDATED_OTHER_ID)
            .address(UPDATED_ADDRESS)
            .mobilePhone(UPDATED_MOBILE_PHONE)
            .workPhone(UPDATED_WORK_PHONE)
            .workEmail(UPDATED_WORK_EMAIL)
            .privateEmail(UPDATED_PRIVATE_EMAIL)
            .department(UPDATED_DEPARTMENT)
            .startDate(UPDATED_START_DATE);

        restEmployeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEmployee.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEmployee))
            )
            .andExpect(status().isOk());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);
        Employee testEmployee = employeeList.get(employeeList.size() - 1);
        assertThat(testEmployee.getCodeEmployee()).isEqualTo(UPDATED_CODE_EMPLOYEE);
        assertThat(testEmployee.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEmployee.getBirthday()).isEqualTo(UPDATED_BIRTHDAY);
        assertThat(testEmployee.getOtherId()).isEqualTo(UPDATED_OTHER_ID);
        assertThat(testEmployee.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testEmployee.getMobilePhone()).isEqualTo(UPDATED_MOBILE_PHONE);
        assertThat(testEmployee.getWorkPhone()).isEqualTo(UPDATED_WORK_PHONE);
        assertThat(testEmployee.getWorkEmail()).isEqualTo(UPDATED_WORK_EMAIL);
        assertThat(testEmployee.getPrivateEmail()).isEqualTo(UPDATED_PRIVATE_EMAIL);
        assertThat(testEmployee.getDepartment()).isEqualTo(UPDATED_DEPARTMENT);
        assertThat(testEmployee.getStartDate()).isEqualTo(UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void putNonExistingEmployee() throws Exception {
        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();
        employee.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, employee.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(employee))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEmployee() throws Exception {
        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();
        employee.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(employee))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEmployee() throws Exception {
        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();
        employee.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employee)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEmployeeWithPatch() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();

        // Update the employee using partial update
        Employee partialUpdatedEmployee = new Employee();
        partialUpdatedEmployee.setId(employee.getId());

        partialUpdatedEmployee
            .name(UPDATED_NAME)
            .birthday(UPDATED_BIRTHDAY)
            .workPhone(UPDATED_WORK_PHONE)
            .workEmail(UPDATED_WORK_EMAIL)
            .privateEmail(UPDATED_PRIVATE_EMAIL)
            .department(UPDATED_DEPARTMENT);

        restEmployeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmployee.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEmployee))
            )
            .andExpect(status().isOk());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);
        Employee testEmployee = employeeList.get(employeeList.size() - 1);
        assertThat(testEmployee.getCodeEmployee()).isEqualTo(DEFAULT_CODE_EMPLOYEE);
        assertThat(testEmployee.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEmployee.getBirthday()).isEqualTo(UPDATED_BIRTHDAY);
        assertThat(testEmployee.getOtherId()).isEqualTo(DEFAULT_OTHER_ID);
        assertThat(testEmployee.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testEmployee.getMobilePhone()).isEqualTo(DEFAULT_MOBILE_PHONE);
        assertThat(testEmployee.getWorkPhone()).isEqualTo(UPDATED_WORK_PHONE);
        assertThat(testEmployee.getWorkEmail()).isEqualTo(UPDATED_WORK_EMAIL);
        assertThat(testEmployee.getPrivateEmail()).isEqualTo(UPDATED_PRIVATE_EMAIL);
        assertThat(testEmployee.getDepartment()).isEqualTo(UPDATED_DEPARTMENT);
        assertThat(testEmployee.getStartDate()).isEqualTo(DEFAULT_START_DATE);
    }

    @Test
    @Transactional
    void fullUpdateEmployeeWithPatch() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();

        // Update the employee using partial update
        Employee partialUpdatedEmployee = new Employee();
        partialUpdatedEmployee.setId(employee.getId());

        partialUpdatedEmployee
            .codeEmployee(DEFAULT_NAME)
            .name(UPDATED_NAME)
            .birthday(UPDATED_BIRTHDAY)
            .otherId(UPDATED_OTHER_ID)
            .address(UPDATED_ADDRESS)
            .mobilePhone(UPDATED_MOBILE_PHONE)
            .workPhone(UPDATED_WORK_PHONE)
            .workEmail(UPDATED_WORK_EMAIL)
            .privateEmail(UPDATED_PRIVATE_EMAIL)
            .department(UPDATED_DEPARTMENT)
            .startDate(UPDATED_START_DATE);

        restEmployeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmployee.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEmployee))
            )
            .andExpect(status().isOk());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);
        Employee testEmployee = employeeList.get(employeeList.size() - 1);
        assertThat(testEmployee.getCodeEmployee()).isEqualTo(UPDATED_CODE_EMPLOYEE);
        assertThat(testEmployee.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEmployee.getBirthday()).isEqualTo(UPDATED_BIRTHDAY);
        assertThat(testEmployee.getOtherId()).isEqualTo(UPDATED_OTHER_ID);
        assertThat(testEmployee.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testEmployee.getMobilePhone()).isEqualTo(UPDATED_MOBILE_PHONE);
        assertThat(testEmployee.getWorkPhone()).isEqualTo(UPDATED_WORK_PHONE);
        assertThat(testEmployee.getWorkEmail()).isEqualTo(UPDATED_WORK_EMAIL);
        assertThat(testEmployee.getPrivateEmail()).isEqualTo(UPDATED_PRIVATE_EMAIL);
        assertThat(testEmployee.getDepartment()).isEqualTo(UPDATED_DEPARTMENT);
        assertThat(testEmployee.getStartDate()).isEqualTo(UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingEmployee() throws Exception {
        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();
        employee.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, employee.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(employee))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEmployee() throws Exception {
        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();
        employee.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(employee))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEmployee() throws Exception {
        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();
        employee.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(employee)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEmployee() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        int databaseSizeBeforeDelete = employeeRepository.findAll().size();

        // Delete the employee
        restEmployeeMockMvc
            .perform(delete(ENTITY_API_URL_ID, employee.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

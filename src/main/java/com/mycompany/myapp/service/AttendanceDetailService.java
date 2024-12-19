package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Attendance;
import com.mycompany.myapp.domain.AttendanceDetail;
import com.mycompany.myapp.repository.AttendanceDetailRepository;
import com.mycompany.myapp.repository.AttendanceRepository;
import com.mycompany.myapp.repository.EmployeeRepository;
import com.mycompany.myapp.repository.UserRepository;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails.
 * <p>
 * We use the {@link Async} annotation to send emails asynchronously.
 */
@Service
public class AttendanceDetailService {

    private final Logger log = LoggerFactory.getLogger(AttendanceDetailService.class);
    private UserRepository userRepository;
    private EmployeeRepository employeeRepository;
    private AttendanceRepository attendanceRepository;
    private AttendanceDetailRepository attendanceDetailRepository;

    public AttendanceDetailService(
        UserRepository userRepository,
        EmployeeRepository employeeRepository,
        AttendanceRepository attendanceRepository,
        AttendanceDetailRepository attendanceDetailRepository
    ) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.attendanceDetailRepository = attendanceDetailRepository;
    }

    public AttendanceDetail createAttendanceDetail(AttendanceDetail attendanceDetail) {
        attendanceDetailRepository.save(attendanceDetail);
        return attendanceDetail;
    }

    public void createAttendanceDetailAll(List<AttendanceDetail> attendanceDetails) {
        List<AttendanceDetail> attendanceDetailList = attendanceDetails;
        for (AttendanceDetail attendanceDetail : attendanceDetailList) {
            BigDecimal countDay = BigDecimal.ZERO;
            Field[] fields = attendanceDetail.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true); // Cho phép truy cập thuộc tính private
                try {
                    // Kiểm tra kiểu dữ liệu và cập nhật giá trị
                    if (field.get(attendanceDetail) != null && field.get(attendanceDetail).equals("+")) {
                        countDay = countDay.add(BigDecimal.valueOf(1));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            attendanceDetail.setPaidWorking(countDay);
            this.createAttendanceDetail(attendanceDetail);
        }
    }
}

package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Attendance;
import com.mycompany.myapp.domain.AttendanceDetail;
import com.mycompany.myapp.repository.AttendanceDetailRepository;
import com.mycompany.myapp.repository.AttendanceRepository;
import com.mycompany.myapp.repository.EmployeeRepository;
import com.mycompany.myapp.repository.UserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;
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
        LocalTime timeIn = LocalTime.of(8, 00);
        LocalTime timeOut = LocalTime.of(17, 00);

        if (
            (attendanceDetail.getInTime() == null || attendanceDetail.getOutTime() == null) &&
            attendanceDetail.getOutTime().isBefore(attendanceDetail.getInTime())
        ) {
            attendanceDetail.setCountTime((double) 0);
        } else {
            Duration duration;
            if (
                Duration.between(attendanceDetail.getOutTime(), timeOut).getSeconds() <= 0 &&
                Duration.between(attendanceDetail.getInTime(), timeIn).getSeconds() < 0
            ) {
                attendanceDetail.setStatus("Đi muộn");
                duration = Duration.between(attendanceDetail.getInTime(), timeOut);
            } else if (
                Duration.between(attendanceDetail.getOutTime(), timeOut).getSeconds() > 0 &&
                Duration.between(attendanceDetail.getInTime(), timeIn).getSeconds() >= 0
            ) {
                attendanceDetail.setStatus("Về sớm");
                duration = Duration.between(timeIn, attendanceDetail.getOutTime());
            } else if (attendanceDetail.getOutTime().isBefore(timeOut) && attendanceDetail.getInTime().isAfter(timeIn)) {
                duration = Duration.between(attendanceDetail.getInTime(), attendanceDetail.getOutTime());
                attendanceDetail.setStatus("Đi muộn, về sớm");
            } else {
                duration = Duration.between(timeIn, timeOut);
                attendanceDetail.setStatus("Hoàn thành");
            }
            long totalSeconds = duration.getSeconds() - 3600;
            double hours = totalSeconds / 3600.0000;
            attendanceDetail.setCountTime(hours);
        }
        AttendanceDetail attendanceDetailCreate = this.attendanceDetailRepository.save(attendanceDetail);

        Attendance attendance = this.attendanceRepository.findById(attendanceDetail.getAttendanceId()).get();
        long coutTime = this.attendanceDetailRepository.countAttendanceDetailByAttendanceId(attendanceDetail.getAttendanceId());
        long coutSuccessTime =
            this.attendanceDetailRepository.countSuccessAttendanceDetailByAttendanceId(attendanceDetail.getAttendanceId());
        Double coutTimeNotSuccess =
            this.attendanceDetailRepository.countTimeNotSuccessAttendanceDetailByAttendanceId(attendanceDetail.getAttendanceId());
        BigDecimal numberWorking;
        if (coutTimeNotSuccess != null) {
            numberWorking = new BigDecimal(coutTimeNotSuccess).divide(new BigDecimal(8)).add(new BigDecimal(coutSuccessTime));
        } else numberWorking = new BigDecimal(coutSuccessTime);

        attendance.setCount(coutTime);
        attendance.setNumberWorking(numberWorking);
        attendance.setCountNot(coutSuccessTime);
        attendanceRepository.save(attendance);
        return attendanceDetailCreate;
    }

    public void delete(Long id) {
        AttendanceDetail attendanceDetail = this.attendanceDetailRepository.findById(id).get();
        attendanceDetailRepository.deleteById(id);
        Attendance attendance = this.attendanceRepository.findById(attendanceDetail.getAttendanceId()).get();
        long coutTime = this.attendanceDetailRepository.countAttendanceDetailByAttendanceId(attendanceDetail.getAttendanceId());
        long coutSuccessTime =
            this.attendanceDetailRepository.countSuccessAttendanceDetailByAttendanceId(attendanceDetail.getAttendanceId());
        attendance.setCount(coutTime);
        attendance.setCountNot(coutSuccessTime);
        attendanceRepository.save(attendance);
    }

    public void createAttendanceDetailAll(List<AttendanceDetail> attendanceDetails) {
        List<AttendanceDetail> attendanceDetailList = attendanceDetails;
        for (AttendanceDetail attendanceDetailImport : attendanceDetailList) {
            this.createAttendanceDetail(attendanceDetailImport);
        }
    }
}

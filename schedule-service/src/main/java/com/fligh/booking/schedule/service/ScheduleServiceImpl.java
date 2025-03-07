package com.fligh.booking.schedule.service;

import com.fligh.booking.schedule.dto.*;
import com.fligh.booking.schedule.entity.Schedule;
import com.fligh.booking.schedule.exception.NoScheduleFoundException;
import com.fligh.booking.schedule.mapper.ScheduleMapper;
import com.fligh.booking.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService{

    @Autowired
    public ScheduleRepository scheduleRepository;

    private final ScheduleMapper scheduleMapper;

    @Override
    public ScheduleResponse createSchedule(ScheduleRequest schedule) {
        return scheduleMapper
                .mapToDTO(scheduleRepository.save(scheduleMapper.mapToModel(schedule)));

    }

    @Override
    public List<ScheduleResponse> getAllSchedules() {
        List<Schedule> schedules = scheduleRepository.findAll();
        if(schedules.isEmpty()){
            throw new NoScheduleFoundException("There are no flights yet!");
        }
        return  schedules.stream().map(scheduleMapper::mapToDTO).toList();
    }

    @Override
    public void deleteScheduleById(int id) {
        scheduleRepository.deleteById(id);
    }

    @Override
    public void deleteAllSchedules() {
        scheduleRepository.deleteAll();
    }

    @Override
    public ScheduleResponse getScheduleById(int id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(()-> new NoScheduleFoundException("Schedule Doesn't Exist"));
        return scheduleMapper.mapToDTO(schedule);
    }

    @Override
    public ScheduleResponse updateSchedule(ScheduleRequest scheduleRequest) {
        return null;
    }

    @Override
    public void deleteByAirportId(int id) {
        scheduleRepository.deleteBySourceAirportIdOrDestinationAirportId(id,id);
    }

    @Override
    public void deleteByFlightId(String id) {
        scheduleRepository.deleteByFlightId(id);
    }
}

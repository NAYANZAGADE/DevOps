package com.glidingpath.platform.shared.controllers;

import com.glidingpath.platform.shared.dto.MasterVestingScheduleDTO;
import com.glidingpath.platform.shared.dto.TenantVestingScheduleDTO;
import com.glidingpath.platform.shared.dto.VestingScheduleDetailDTO;
import com.glidingpath.platform.sponsor.service.VestingScheduleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/plan-sponsor/plan/vesting")
@Tag(name = "Plan-Sponsor Details", description = "APIs for managing retirement plan vesting schedules and employee vesting calculations")
@RequiredArgsConstructor
public class VestingScheduleController {

    private final VestingScheduleService vestingScheduleService;

    /**
     * Endpoint to retrieve all master vesting schedules.
     *
     * @return ResponseEntity with List of MasterVestingScheduleDTO containing all master schedules
     */
    @GetMapping("/master")
    @Operation(summary = "Get all master vesting schedules")
    public ResponseEntity<List<MasterVestingScheduleDTO>> getAllMasterVestingSchedules() {
        List<MasterVestingScheduleDTO> schedules = vestingScheduleService.getAllMasterVestingSchedules();
        return ResponseEntity.ok(schedules);
    }

    /**
     * Endpoint to retrieve tenant vesting schedule by ID.
     *
     * @param id the vesting schedule identifier
     * @return ResponseEntity with TenantVestingScheduleDTO containing vesting schedule details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get tenant vesting schedule by plan-type ID", description = "Retrieves specific tenant vesting schedule configuration and vesting calculation details by plan-type ID")
    public ResponseEntity<TenantVestingScheduleDTO> getTenantVestingSchedule(@PathVariable("id") UUID id) {
        TenantVestingScheduleDTO dto = vestingScheduleService.getTenantVestingScheduleById(id);
        return ResponseEntity.ok(dto);
    }


} 